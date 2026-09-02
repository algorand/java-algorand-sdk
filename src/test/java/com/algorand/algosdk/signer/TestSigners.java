package com.algorand.algosdk.signer;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;
import com.algorand.algosdk.util.Encoder;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Every callback signer must be byte-identical to the existing secret-key path.
 */
public class TestSigners {
    private static final byte[] PROGRAM = {0x01, 0x20, 0x01, 0x01, 0x22}; // int 1

    /**
     * A low-level ed25519 signer callback backed by a real secret key.
     */
    private static RawSigner rawSigner(Account account) throws Exception {
        Ed25519PrivateKeyParameters key = new Ed25519PrivateKeyParameters(account.toSeed(), 0);
        return bytes -> {
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, key);
            signer.update(bytes, 0, bytes.length);
            return signer.generateSignature();
        };
    }

    private static Ed25519AlgorandSigner signer(Account account) throws Exception {
        return new Ed25519AlgorandSigner(account.getEd25519PublicKey(), rawSigner(account));
    }

    private static Transaction payment(Address sender, Address receiver, long amount) {
        return Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .flatFee(1000)
                .firstValid(1)
                .lastValid(1000)
                .genesisHash(new Digest(new byte[32]))
                .build();
    }

    /* Ed25519AlgorandSigner */

    @Test
    public void testExposesDefaultAddress() throws Exception {
        Account account = new Account();
        assertThat(signer(account).getAddress()).isEqualTo(account.getAddress());
        assertThat(signer(account).getPublicKey()).isEqualTo(account.getEd25519PublicKey());
    }

    @Test
    public void testPublicKeyExposureCannotChangeSignerIdentity() throws Exception {
        Account account = new Account();
        Ed25519AlgorandSigner signer = signer(account);
        Address address = signer.getAddress();
        int hashCode = signer.hashCode();
        byte[] expectedKey = account.getClearTextPublicKey();

        byte[] exposed = signer.getPublicKey().getBytes();
        exposed[0] ^= 0xff;

        assertThat(signer.getPublicKey().getBytes()).isEqualTo(expectedKey);
        assertThat(signer.getAddress()).isEqualTo(address);
        assertThat(signer.hashCode()).isEqualTo(hashCode);
    }

    @Test
    public void testTransactionSignerEquivalence() throws Exception {
        Account account = new Account();
        Transaction txn = payment(account.getAddress(), account.getAddress(), 1000);
        SignedTransaction ref = account.signTransaction(txn);
        SignedTransaction got = signer(account).signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testSignsOnlyRequestedIndexes() throws Exception {
        // signTxnGroup returns exactly the requested indexes, in order
        Account account = new Account();
        Transaction[] txns = new Transaction[3];
        for (int i = 0; i < 3; i++) {
            txns[i] = payment(account.getAddress(), account.getAddress(), i);
        }
        SignedTransaction[] signed = signer(account).signTxnGroup(txns, new int[]{0, 2});
        assertThat(signed).hasSize(2);
        assertThat(signed[0].tx).isEqualTo(txns[0]);
        assertThat(signed[1].tx).isEqualTo(txns[2]);
    }

    @Test
    public void testTransactionSignerRekeyedEquivalence() throws Exception {
        // signer key differs from the txn sender (rekeyed account): the signer's
        // own address must be attached as the auth address
        Account account = new Account();
        Account other = new Account();
        Transaction txn = payment(other.getAddress(), other.getAddress(), 1000);
        SignedTransaction ref = account.signTransaction(txn);
        SignedTransaction got = signer(account).signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(got.authAddr).isEqualTo(account.getAddress());
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testSignBytesEquivalence() throws Exception {
        Account account = new Account();
        byte[] data = "a message to sign".getBytes();
        Signature got = signer(account).signBytes(data);
        // drop-in replacement for Account.signBytes: same signature, verifies
        // directly with Address.verifyBytes
        assertThat(got).isEqualTo(account.signBytes(data));
        assertThat(account.getAddress().verifyBytes(data, got)).isTrue();
    }

    @Test
    public void testSignBytesDoesNotVerifyOtherBytes() throws Exception {
        // negative check: the "MX" signature must not verify for other bytes
        Account account = new Account();
        Signature sig = signer(account).signBytes("hello world".getBytes());
        assertThat(account.getAddress().verifyBytes("goodbye world".getBytes(), sig)).isFalse();
    }

    @Test
    public void testTealSignEquivalence() throws Exception {
        Account account = new Account();
        LogicsigSignature lsig = new LogicsigSignature(PROGRAM);
        byte[] data = "program data".getBytes();
        assertThat(signer(account).tealSign(data, lsig.toAddress()))
                .isEqualTo(account.tealSign(data, lsig.toAddress()));
        assertThat(signer(account).tealSignFromProgram(data, PROGRAM))
                .isEqualTo(account.tealSignFromProgram(data, PROGRAM));
    }

    @Test
    public void testSignLogicsigSingleEquivalence() throws Exception {
        Account account = new Account();
        LogicsigSignature ref = account.signLogicsig(new LogicsigSignature(PROGRAM));
        LogicsigSignature got = signer(account).signLogicsig(new LogicsigSignature(PROGRAM));
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testSignLogicsigRejectsDoubleSign() throws Exception {
        Account account = new Account();
        Ed25519AlgorandSigner signer = signer(account);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(PROGRAM));
        assertThatThrownBy(() -> signer.signLogicsig(lsig))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testSignLogicsigMultisigEquivalence() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account a3 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey(), a3.getEd25519PublicKey()));
        LogicsigSignature ref = a1.signLogicsig(new LogicsigSignature(PROGRAM), msig);
        LogicsigSignature got = signer(a1).signLogicsig(new LogicsigSignature(PROGRAM), msig);
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testAppendToLogicsigEquivalence() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account a3 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey(), a3.getEd25519PublicKey()));
        // secret-key reference: signLogicsig then appendToLogicsig
        LogicsigSignature ref = a1.signLogicsig(new LogicsigSignature(PROGRAM), msig);
        a2.appendToLogicsig(ref);
        // callback path: signLogicsig (first member) then appendToLogicsig (each
        // additional member)
        LogicsigSignature got = signer(a1).signLogicsig(new LogicsigSignature(PROGRAM), msig);
        signer(a2).appendToLogicsig(got);
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
        assertThat(got.verify(msig.toAddress())).isTrue();
    }

    @Test
    public void testAppendToLogicsigRequiresDelegation() throws Exception {
        Account account = new Account();
        LogicsigSignature lsig = new LogicsigSignature(PROGRAM); // never multisig-delegated
        assertThatThrownBy(() -> signer(account).appendToLogicsig(lsig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAppendToLogicsigRejectsNonMember() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account outsider = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2,
                Arrays.asList(a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        LogicsigSignature lsig = signer(a1).signLogicsig(new LogicsigSignature(PROGRAM), msig);
        assertThatThrownBy(() -> signer(outsider).appendToLogicsig(lsig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testSignLogicsigRejectsNonMember() throws Exception {
        Account a1 = new Account();
        Account outsider = new Account();
        MultisigAddress msig = new MultisigAddress(1, 1, Collections.singletonList(a1.getEd25519PublicKey()));
        assertThatThrownBy(() -> signer(outsider).signLogicsig(new LogicsigSignature(PROGRAM), msig))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /* signer identity */

    @Test
    public void testSignersForTheSameKeyAreEqual() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        // the callback is not part of the identity, the signing key is
        assertThat(signer(a1)).isEqualTo(signer(a1));
        assertThat(signer(a1).hashCode()).isEqualTo(signer(a1).hashCode());
        assertThat(signer(a1)).isNotEqualTo(signer(a2));

        // which is what lets AtomicTransactionComposer group by signer
        Map<TxnSigner, String> group = new HashMap<>();
        group.put(signer(a1), "a");
        group.put(signer(a1), "b");
        group.put(signer(a2), "c");
        assertThat(group).hasSize(2);
    }

    @Test
    public void testMultisigSignersAreEqualForTheSameAccountAndMembers() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        MultisigAddress firstMsig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        MultisigAddress equivalentMsig = new MultisigAddress(1, 2, Arrays.asList(
                new Ed25519PublicKey(a1.getClearTextPublicKey()),
                new Ed25519PublicKey(a2.getClearTextPublicKey())));
        MultisigAddress other = new MultisigAddress(1, 1, Collections.singletonList(a1.getEd25519PublicKey()));

        Ed25519MultisigAlgorandSigner first =
                new Ed25519MultisigAlgorandSigner(firstMsig, Arrays.asList(signer(a1), signer(a2)));
        Ed25519MultisigAlgorandSigner equivalent =
                new Ed25519MultisigAlgorandSigner(equivalentMsig, Arrays.asList(signer(a1), signer(a2)));
        assertThat(first).isEqualTo(equivalent);
        assertThat(first.hashCode()).isEqualTo(equivalent.hashCode());

        Map<TxnSigner, String> group = new HashMap<>();
        group.put(first, "a");
        group.put(equivalent, "b");
        assertThat(group).hasSize(1);

        // a different account, or a different member set, is a different signer
        assertThat(first)
                .isNotEqualTo(new Ed25519MultisigAlgorandSigner(other, Collections.singletonList(signer(a1))));
        assertThat(first)
                .isNotEqualTo(new Ed25519MultisigAlgorandSigner(firstMsig, Collections.singletonList(signer(a1))));
    }

    @Test
    public void testMultisigSignerSnapshotsConstructorInputs() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        List<Ed25519AlgorandSigner> members = new ArrayList<>(Arrays.asList(signer(a1), signer(a2)));
        Ed25519MultisigAlgorandSigner signer = new Ed25519MultisigAlgorandSigner(msig, members);
        int hashCode = signer.hashCode();

        msig.publicKeys.clear();
        members.clear();

        MultisigAddress original = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        Ed25519MultisigAlgorandSigner expected =
                new Ed25519MultisigAlgorandSigner(original, Arrays.asList(signer(a1), signer(a2)));
        assertThat(signer).isEqualTo(expected);
        assertThat(signer.hashCode()).isEqualTo(hashCode);
    }

    /* single-member multisig signing and appending */

    @Test
    public void testSignMultisigTransactionEquivalence() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        Transaction txn = payment(msig.toAddress(), a1.getAddress(), 1000);
        SignedTransaction ref = a1.signMultisigTransaction(msig, txn);
        SignedTransaction got = signer(a1).signMultisigTransaction(msig, txn);
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testAppendToMultisigTransactionEquivalence() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account a3 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey(), a3.getEd25519PublicKey()));
        Transaction txn = payment(msig.toAddress(), a1.getAddress(), 1000);
        SignedTransaction partial = a1.signMultisigTransaction(msig, txn);
        SignedTransaction ref = a2.appendMultisigTransaction(msig, partial);
        SignedTransaction got = signer(a2).appendToMultisigTransaction(msig, partial);
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testAppendToMultisigTransactionUnknownMemberRaises() throws Exception {
        Account a1 = new Account();
        Account outsider = new Account();
        MultisigAddress msig = new MultisigAddress(1, 1, Collections.singletonList(a1.getEd25519PublicKey()));
        Transaction txn = payment(msig.toAddress(), a1.getAddress(), 1000);
        SignedTransaction partial = a1.signMultisigTransaction(msig, txn);
        assertThatThrownBy(() -> signer(outsider).appendToMultisigTransaction(msig, partial))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not contain this public key");
    }

    /* Ed25519MultisigAlgorandSigner */

    @Test
    public void testMultisigEquivalenceWithSecretKeyPath() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account a3 = new Account();
        MultisigAddress msig = new MultisigAddress(1, 2, Arrays.asList(
                a1.getEd25519PublicKey(), a2.getEd25519PublicKey(), a3.getEd25519PublicKey()));
        Transaction txn = payment(msig.toAddress(), a1.getAddress(), 1000);
        SignedTransaction ref = a2.appendMultisigTransaction(msig, a1.signMultisigTransaction(msig, txn));
        List<Ed25519AlgorandSigner> members = Arrays.asList(signer(a1), signer(a2));
        SignedTransaction got = new Ed25519MultisigAlgorandSigner(msig, members)
                .signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testMultisigRekeyedEquivalence() throws Exception {
        // sender differs from the multisig address: the multisig address must be
        // attached as the auth address, matching the secret-key path
        Account a1 = new Account();
        Account other = new Account();
        MultisigAddress msig = new MultisigAddress(1, 1, Collections.singletonList(a1.getEd25519PublicKey()));
        Transaction txn = payment(other.getAddress(), other.getAddress(), 1000);
        SignedTransaction ref = a1.signMultisigTransaction(msig, txn);
        SignedTransaction got = new Ed25519MultisigAlgorandSigner(msig, Collections.singletonList(signer(a1)))
                .signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(got.authAddr).isEqualTo(msig.toAddress());
        assertThat(Encoder.encodeToMsgPack(got)).isEqualTo(Encoder.encodeToMsgPack(ref));
    }

    @Test
    public void testMultisigUnknownMemberRaises() throws Exception {
        Account a1 = new Account();
        Account a2 = new Account();
        Account outsider = new Account();
        MultisigAddress msig = new MultisigAddress(1, 1,
                Arrays.asList(a1.getEd25519PublicKey(), a2.getEd25519PublicKey()));
        Transaction txn = payment(msig.toAddress(), a1.getAddress(), 1000);
        Ed25519MultisigAlgorandSigner signer =
                new Ed25519MultisigAlgorandSigner(msig, Collections.singletonList(signer(outsider)));
        assertThatThrownBy(() -> signer.signTxnGroup(new Transaction[]{txn}, new int[]{0}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testMalformedMultisigRejected() {
        // a malformed multisig fails fast (threshold larger than member count)
        assertThatThrownBy(() -> new MultisigAddress(1, 2, Collections.singletonList(new Account().getEd25519PublicKey())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /* TxnSigner.signTransaction convenience */

    @Test
    public void testSignTransactionConvenience() throws Exception {
        // the single-transaction helper matches signing the txn directly, for
        // both the account-backed and the callback-backed signer
        Account account = new Account();
        Transaction txn = payment(account.getAddress(), account.getAddress(), 1000);
        byte[] ref = Encoder.encodeToMsgPack(account.signTransaction(txn));
        assertThat(Encoder.encodeToMsgPack(account.getTransactionSigner().signTransaction(txn))).isEqualTo(ref);
        assertThat(Encoder.encodeToMsgPack(signer(account).signTransaction(txn))).isEqualTo(ref);
    }
}
