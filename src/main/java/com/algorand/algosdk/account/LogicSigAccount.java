package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class LogicSigAccount {
    public final Ed25519PublicKey sigKey;
    public final LogicsigSignature lsig;

    /**
     * Creates a new escrow LogicSig Account
     * The address of the account is the hash of its program (logic)
     * @param logic the bytes of program
     * @param args the arguments of the program
     **/
    public LogicSigAccount(byte[] logic, List<byte[]> args) {
        this.lsig = new LogicsigSignature(logic, args);
        this.sigKey = null;
    }

    /**
     * Creates a new delegated LogicSigAccount.
     * This type of LogicSigAccount has the authority to sign transaction on behalf of another account,
     * or so to say that account delegate the transaction signing to the program.
     * If the delegating account is a multi-sig account, please pass in a multi-address for class construction
     * (do not use this constructor in that case)
     * @param logic the bytes of program
     * @param args the arguments of the program
     * @param privateKey the (only) private key of the delegating account
     **/
    public LogicSigAccount(byte[] logic, List<byte[]> args, PrivateKey privateKey)
            throws NoSuchAlgorithmException, IOException {
        this.lsig = new LogicsigSignature(logic, args);
        // Generate delegated Account from private keypair
        Account signerAccount = new Account(privateKey);
        // Sign program bytes from delegated account, and add to LogicSig
        signerAccount.signLogicsig(this.lsig);
        // set signing public key of the delegated account
        this.sigKey = signerAccount.getEd25519PublicKey();
    }

    /**
     * Creates a new delegated LogicSigAccount.
     * This type of LogicSigAccount has the authority to sign transaction on behalf of another account,
     * or so to say that account delegate the transaction signing to the program.
     * The delegated Account here is a multi-sig account.
     * To add additional signature from other member, use appendMultiSig method.
     * @param logic the bytes of program
     * @param args the arguments of the program
     * @param privateKey is the private key of one of the member of delegating multi-sig account
     * @param ma is the multi-sig-address of the delegating account.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public LogicSigAccount(byte[] logic, List<byte[]> args, PrivateKey privateKey, MultisigAddress ma)
            throws IOException, NoSuchAlgorithmException {
        this.lsig = new LogicsigSignature(logic, args);
        Account signerAccount = new Account(privateKey);
        signerAccount.signLogicsig(this.lsig, ma);
        this.sigKey = null;
    }

    /**
     * Adds a signature from the delegating multi-sig account
     * @param privateKey is the private key of one of the member of delegating multi-sig account
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void appendMultiSig(PrivateKey privateKey)
            throws IllegalArgumentException, IOException, NoSuchAlgorithmException {
        Account signerAccount = new Account(privateKey);
        signerAccount.appendToLogicsig(this.lsig);
    }

    /**
     * Creates a new delegated LogicSigAccount from existing LogicSig
     * @param lsig is an existing LogicSig.
     * @param signerPublicKey must be present if the LogicSig is delegated and the delegating account
     *                        is backed by a single private key (i.e. not a multi-sig account).
     *                        It must be the public key of the delegating account, or an error will be returned
     *                        if it is present.
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     */
    public LogicSigAccount(LogicsigSignature lsig, Ed25519PublicKey signerPublicKey)
            throws IllegalArgumentException, NoSuchAlgorithmException {
        boolean hasSig = lsig.sig != null;
        boolean hasMsig = lsig.msig != null;
        if (hasSig && hasMsig)
            throw new IllegalArgumentException("Logicsig has too many signatures, at most one of Sig or Msig may be defined");
        if (hasSig) {
            if (signerPublicKey == null)
                throw new IllegalArgumentException("Cannot generate LogicSigAccount from single-signed LogicSig and a null public key");
            if (!lsig.verify(new Address(signerPublicKey.getBytes())))
                throw new IllegalArgumentException("Cannot verify the sig in LogicSig with the public key provided");
            this.lsig = lsig;
            this.sigKey = signerPublicKey;
            return;
        }
        if (signerPublicKey != null)
            throw new IllegalArgumentException("Cannot generate LogicSigAccount from multi-sig LogicSig and a public key");
        this.lsig = lsig;
        this.sigKey = null;
    }

    /**
     * IsDelegated returns true iff. the LogicSig has been delegated to another account with a signature
     * @return boolean
     */
    public boolean isDelegated() {
        boolean hasSig = this.lsig.sig != null;
        boolean hasMsig = this.lsig.msig != null;
        return hasSig || hasMsig;
    }

    /**
     * getAddress returns the address of the LogicSigAccount
     * @return the address of the LogicSigAccount
     * @throws NoSuchAlgorithmException
     * @throws IllegalArgumentException
     */
    public Address getAddress() throws NoSuchAlgorithmException, IllegalArgumentException {
        boolean hasSig = this.lsig.sig != null;
        boolean hasMsig = this.lsig.msig != null;
        if (hasSig && hasMsig)
            throw new IllegalArgumentException("Logicsig has too many signatures, at most one of Sig or Msig may be defined");
        if (hasSig) {
            byte[] sigKeyRaw = this.sigKey.getBytes();
            return new Address(sigKeyRaw);
        }
        if (hasMsig) {
            List<Ed25519PublicKey> pkFromSubSig = new ArrayList<>();
            for (MultisigSignature.MultisigSubsig subSig : this.lsig.msig.subsigs)
                pkFromSubSig.add(subSig.key);
            MultisigAddress ma = new MultisigAddress(this.lsig.msig.version, this.lsig.msig.threshold, pkFromSubSig);
            return ma.toAddress();
        }
        return this.lsig.toAddress();
    }

    /**
     * Sign a transaction with this account
     * @param tx transaction
     * @return signed transaction from LogicSigAccount
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public SignedTransaction signLogicSigTransaction(Transaction tx)
            throws NoSuchAlgorithmException, IOException {
        return Account.signLogicTransactionWithAddress(this.lsig, this.getAddress(), tx);
    }

    public AtomicTransactionComposer.TxnSigner getTransactionSigner() {
        final LogicSigAccount self = this;

        return new AtomicTransactionComposer.TxnSigner() {
            @Override
            public SignedTransaction signTxn(Transaction txn) throws NoSuchAlgorithmException, IOException {
                return self.signLogicSigTransaction(txn);
            }

            @Override
            public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign)
                    throws NoSuchAlgorithmException, IOException {
                SignedTransaction[] sTxn = new SignedTransaction[indicesToSign.length];
                for (int i = 0; i < indicesToSign.length; i++)
                    sTxn[i] = self.signLogicSigTransaction(txnGroup[indicesToSign[i]]);
                return sTxn;
            }
        };
    }
}
