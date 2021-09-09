package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LogicSigAccount {
    // TODO some private config stuffs...
    private static final int PK_SIZE = 32;
    private static final int PK_X509_PREFIX_LENGTH = 12; // Ed25519 specific

    public final Ed25519PublicKey sigKey;
    public final LogicsigSignature lsig;

    // Create a new escrow LogicSigAccount.
    // The address of the account is the hash of its program
    public LogicSigAccount(byte[] logic, List<byte[]> args) {
        this.lsig = new LogicsigSignature(logic, args);
        this.sigKey = new Ed25519PublicKey();
    }

    // Create a new delegated LogicSigAccount.
    // This type of LogicSigAccount has the authority to sign transaction on behalf of another account,
    // or so to say that account delegate the transaction signing to the program.
    // If the delegating account is a multi-sig account, do not use this constructor.
    public LogicSigAccount(byte[] logic, List<byte[]> args, KeyPair privateKeyPair) throws NoSuchAlgorithmException {
        this.lsig = new LogicsigSignature(logic, args);
        // Generate delegated Account from private keypair
        Account signerAccount = new Account(privateKeyPair);
        // Sign program bytes from delegated account, and add to LogicSig
        this.lsig.sig = signerAccount.signBytes(this.lsig.bytesToSign());
        // set signing public key of the delegated account
        this.sigKey = signerAccount.getEd25519PublicKey();
    }

    // Create a new delegated LogicSigAccount.
    // The delegated Account here is a multi-sig account,
    public LogicSigAccount(byte[] logic, List<byte[]> args, MultisigAddress ma, KeyPair privateKeyPair) throws IOException {
        this.lsig = new LogicsigSignature(logic, args);
        Account signerAccount = new Account(privateKeyPair);
        signerAccount.signLogicsig(this.lsig, ma);
        this.sigKey = new Ed25519PublicKey();
    }

    public void appendMultiSig(KeyPair privateKeyPair) throws IllegalArgumentException, IOException {
        Account signerAccount = new Account(privateKeyPair);
        signerAccount.appendToLogicsig(this.lsig);
    }

    public LogicSigAccount(LogicsigSignature lsig, Ed25519PublicKey signerPk) throws IllegalArgumentException {
        boolean hasSig = !lsig.sig.equals(new Signature());
        boolean hasMsig = !lsig.msig.equals(new MultisigSignature(0, 0));
        if (hasSig && hasMsig) {
            throw new IllegalArgumentException("Logicsig has too many signatures, at most one of Sig or Msig may be defined");
        }
        if (hasMsig && !signerPk.equals(new Ed25519PublicKey())) {
            throw new IllegalArgumentException("Cannot generate LogicSigAccount from multi-sig LogicSig and a public key");
        }
        if (hasSig) {
            if (signerPk.equals(new Ed25519PublicKey())) {
                throw new IllegalArgumentException("Cannot generate LogicSigAccount from single-signed LogicSig and a null public key");
            }
            if (!lsig.verify(new Address(signerPk.getBytes()))) {
                throw new IllegalArgumentException("Cannot verify the sig in LogicSig with the public key provided");
            }
            this.lsig = lsig;
            this.sigKey = signerPk;
            return;
        }
        this.lsig = lsig;
        this.sigKey = new Ed25519PublicKey();
    }

    public boolean isDelegated() {
        boolean hasSig = !this.lsig.sig.equals(new Signature());
        boolean hasMsig = !this.lsig.msig.equals(new MultisigSignature(0, 0));
        return hasSig || hasMsig;
    }

    public Address getAddress() throws NoSuchAlgorithmException, IllegalArgumentException {
        boolean hasSig = !this.lsig.sig.equals(new Signature());
        boolean hasMsig = !this.lsig.msig.equals(new MultisigSignature(0, 0));
        if (hasSig && hasMsig) {
            throw new IllegalArgumentException("Logicsig has too many signatures, at most one of Sig or Msig may be defined");
        }
        if (hasSig) {
            byte[] sigKeyRaw = this.sigKey.getBytes();
            return new Address(sigKeyRaw);
        }
        if (hasMsig) {
            List<Ed25519PublicKey> pkFromSubSig = new ArrayList<Ed25519PublicKey>();
            for (MultisigSignature.MultisigSubsig subSig : this.lsig.msig.subsigs) {
                pkFromSubSig.add(subSig.key);
            }
            MultisigAddress ma = new MultisigAddress(this.lsig.msig.version, this.lsig.msig.threshold, pkFromSubSig);
            return ma.toAddress();
        }
        return this.lsig.toAddress();
    }
}