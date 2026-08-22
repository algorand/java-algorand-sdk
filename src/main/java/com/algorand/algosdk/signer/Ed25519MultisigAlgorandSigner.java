package com.algorand.algosdk.signer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.MultisigSignature.MultisigSubsig;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A multisig {@link TxnSigner} that fills each member's subsignature using an
 * {@link Ed25519AlgorandSigner} callback instead of a raw secret key: the
 * callback-based counterpart of
 * {@link com.algorand.algosdk.crypto.MultisigAddress#getTransactionSigner}.
 */
public class Ed25519MultisigAlgorandSigner implements TxnSigner {
    private final MultisigAddress msig;
    private final List<Ed25519AlgorandSigner> signers;

    /**
     * @param msig the multisig account
     * @param signers the members to sign with; each must wrap a public key
     *                present in the multisig. Only each member's public key and
     *                signing callback are used.
     */
    public Ed25519MultisigAlgorandSigner(MultisigAddress msig, List<Ed25519AlgorandSigner> signers) {
        Objects.requireNonNull(msig, "msig must not be null");
        Objects.requireNonNull(signers, "signers must not be null");

        List<Ed25519PublicKey> publicKeys = new ArrayList<>();
        for (Ed25519PublicKey publicKey : msig.publicKeys) {
            publicKeys.add(new Ed25519PublicKey(publicKey.getBytes()));
        }
        this.msig = new MultisigAddress(msig.version, msig.threshold, publicKeys);
        this.signers = Collections.unmodifiableList(new ArrayList<>(signers));
    }

    /**
     * Two multisig signers are equal when they sign for the same account with
     * the same ordered members.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ed25519MultisigAlgorandSigner)) return false;
        Ed25519MultisigAlgorandSigner actual = (Ed25519MultisigAlgorandSigner) obj;
        return this.msig.equals(actual.msig) && this.signers.equals(actual.signers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.msig, this.signers);
    }

    @Override
    public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign) throws Exception {
        SignedTransaction[] sTxn = new SignedTransaction[indicesToSign.length];
        for (int i = 0; i < indicesToSign.length; i++) {
            Transaction tx = txnGroup[indicesToSign[i]];
            MultisigSignature mSig = new MultisigSignature(msig.version, msig.threshold);
            for (int j = 0; j < msig.publicKeys.size(); j++) {
                mSig.subsigs.add(new MultisigSubsig(msig.publicKeys.get(j)));
            }
            for (Ed25519AlgorandSigner member : this.signers) {
                int memberIndex = msig.publicKeys.indexOf(member.getPublicKey());
                if (memberIndex == -1) {
                    throw new IllegalArgumentException("Multisig account does not contain this public key");
                }
                mSig.subsigs.set(memberIndex, new MultisigSubsig(member.getPublicKey(), member.rawSign(tx.bytesToSign())));
            }
            SignedTransaction stx = new SignedTransaction(tx, mSig, tx.txID());
            // if the transaction sender address is not the multisig address, set
            // the auth address to the multisig address
        Address msigAddr = msig.toAddress();
        stx.authAddr(msigAddr);
            sTxn[i] = stx;
        }
        return sTxn;
    }
}
