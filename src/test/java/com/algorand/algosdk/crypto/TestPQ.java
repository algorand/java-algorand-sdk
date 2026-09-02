package com.algorand.algosdk.crypto;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.account.LogicSigAccount;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.signer.Falcon1024AlgorandSigner;
import com.algorand.algosdk.signer.PQAlgorandSigner;
import com.algorand.algosdk.signer.RawSigner;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxnSigner;
import com.algorand.algosdk.util.Digester;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Post-quantum (Falcon-1024) tests, driven by the shared test vectors under
 * src/test/resources/pq_test_data: the signing vectors carry real Falcon-1024
 * signatures over the fixed seed 0..31 account and match the shared cross-SDK
 * golden blobs byte-for-byte, and the ed25519
 * point-check known-answer vectors come from
 * algorandfoundation/falcon-signatures. All are vendored byte-identically
 * from js-algorand-sdk PR #1102.
 */
public class TestPQ {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final byte[] PROGRAM = {0x01, 0x20, 0x01, 0x01, 0x22}; // int 1
    private static final byte[] DUMMY_FALCON_SIG = new byte[1280];

    private static JsonNode load(String name) throws Exception {
        InputStream is = TestPQ.class.getClassLoader().getResourceAsStream("pq_test_data/" + name);
        assertThat(is).as("fixture %s", name).isNotNull();
        return JSON.readTree(is);
    }

    private static byte[] hex(String s) throws Exception {
        return Encoder.decodeFromHexStr(s);
    }

    private static byte[] b64(JsonNode node) {
        return Encoder.decodeFromBase64(node.asText());
    }

    /* ed25519 point check */

    @Test
    public void testEd25519PointCheckKatVectors() throws Exception {
        // The broad point check must match every falcon-signatures KAT vector,
        // including the small-order / non-canonical cases that a narrow
        // (libsodium is_valid_point) predicate would get wrong.
        JsonNode kat = load("lsig_address_kat.json");
        int cases = 0;
        for (JsonNode c : kat.get("edwards25519_decode_cases")) {
            assertThat(PQAddress.isEd25519Point(hex(c.get("encoding_hex").asText())))
                    .as("point check mismatch for %s", c.get("name").asText())
                    .isEqualTo(c.get("decodes_to_edwards25519_point").asBoolean());
            cases++;
        }
        for (JsonNode c : kat.get("lsig_derivation").get("counter_cases")) {
            assertThat(PQAddress.isEd25519Point(hex(c.get("address_hex").asText())))
                    .as("point check mismatch for counter %d", c.get("counter").asInt())
                    .isEqualTo(c.get("decodes_to_edwards25519_point").asBoolean());
            cases++;
        }
        assertThat(cases).isEqualTo(7);
    }

    @Test
    public void testWrongLengthIsNotAPoint() {
        assertThat(PQAddress.isEd25519Point(new byte[31])).isFalse();
        assertThat(PQAddress.isEd25519Point(new byte[33])).isFalse();
        assertThat(PQAddress.isEd25519Point(null)).isFalse();
    }

    /* address derivation */

    @Test
    public void testAddressMatchesGoAlgorandFixture() throws Exception {
        JsonNode fx = load("pqMnemonic.json");
        PQAddress derived = PQAddress.derive(PQSignature.falcon1024Scheme(), b64(fx.get("publicKey")));
        assertThat(derived.getAddress().encodeAsString()).isEqualTo(fx.get("address").asText());
        assertThat(derived.getSalt()).isBetween(0, 255);
    }

    @Test
    public void testDerivedAddressIsWellFormed() throws Exception {
        // A PQ-derived address is a normal 58-char Algorand address that
        // round-trips through decode/encode.
        JsonNode fx = load("pqMnemonic.json");
        Address address = PQAddress.derive(PQSignature.falcon1024Scheme(), b64(fx.get("publicKey"))).getAddress();
        String encoded = address.encodeAsString();
        assertThat(encoded).hasSize(58);
        assertThat(new Address(encoded)).isEqualTo(address);
    }

    @Test
    public void testSchemeLengthValidation() {
        for (byte[] bad : new byte[][]{new byte[0], new byte[1], new byte[3], null}) {
            assertThatThrownBy(() -> PQAddress.derive(bad, new byte[32]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("scheme");
        }
    }

    @Test
    public void testArbitrarySchemeAndKeyIsDeterministic() throws Exception {
        // Port of go-algorand's
        // TestCanonicalPQAddressSaltDoesNotRequireRegisteredSchemeOrValidatedKey:
        // derivation needs neither a registered scheme nor a validated key, and
        // is deterministic.
        byte[] scheme = "x1".getBytes(StandardCharsets.UTF_8);
        byte[] publicKey = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        PQAddress first = PQAddress.derive(scheme, publicKey);
        PQAddress again = PQAddress.derive(scheme, publicKey);
        assertThat(first.getAddress()).isEqualTo(again.getAddress());
        assertThat(first.getSalt()).isEqualTo(again.getSalt());
    }

    @Test
    public void testNonzeroSaltDerivationAndWire() throws Exception {
        byte[] scheme = PQSignature.falcon1024Scheme();
        // find a public key whose salt-0 candidate lands ON the curve, forcing
        // salt>0, and check the salt is genuinely the canonical (lowest) one
        byte[] pk = null;
        int salt = 0;
        for (int i = 1; i < 2000 && pk == null; i++) {
            byte[] candidate = Encoder.encodeUintToBytes(BigInteger.valueOf(i), 32);
            PQAddress derived = PQAddress.derive(scheme, candidate);
            if (derived.getSalt() > 0) {
                pk = candidate;
                salt = derived.getSalt();
            }
        }
        assertThat(pk).isNotNull();
        assertThat(salt).isGreaterThan(0);
        // the chosen salt is genuinely the LOWEST off-curve salt
        for (int lower = 0; lower <= salt; lower++) {
            byte[] preimage = concat("PQA".getBytes(StandardCharsets.UTF_8), scheme, new byte[]{(byte) lower}, pk);
            boolean onCurve = PQAddress.isEd25519Point(Digester.digest(preimage));
            assertThat(onCurve).isEqualTo(lower < salt);
        }
        // end-to-end: the nonzero salt threads into the wire "slt" field
        Transaction txn = Encoder.decodeFromMsgPack(load("pqPayment.json").get("txnBlob").asText(), Transaction.class);
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        assertThat(signer.getSalt()).isEqualTo(salt);
        byte[] blob = Encoder.encodeToMsgPack(signer.signTxnGroup(new Transaction[]{txn}, new int[]{0})[0]);
        Map<?, ?> raw = Encoder.decodeFromMsgPack(blob, Map.class);
        assertThat(((Map<?, ?>) raw.get("pqsig")).get("slt")).isEqualTo(salt);
        assertThat(Encoder.decodeFromMsgPack(blob, SignedTransaction.class).pqSig.salt).isEqualTo(salt);
    }

    /* mnemonic seed */

    @Test
    public void testSeedMatchesGoAlgorandFixture() throws Exception {
        JsonNode fx = load("pqMnemonic.json");
        byte[] seed = Mnemonic.toPQSeed(fx.get("mnemonic").asText(), PQSignature.falcon1024Scheme());
        assertThat(Encoder.encodeToBase64(seed)).isEqualTo(fx.get("seed").asText());
    }

    @Test
    public void testSeedSchemeLengthValidation() throws Exception {
        // toPQSeed validates the scheme length just like PQAddress.derive.
        String mnemonic = load("pqMnemonic.json").get("mnemonic").asText();
        for (byte[] bad : new byte[][]{new byte[0], new byte[1], new byte[3], null}) {
            assertThatThrownBy(() -> Mnemonic.toPQSeed(mnemonic, bad))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("scheme");
        }
    }

    /* PQSignature encoding */

    @Test
    public void testPQSignatureRoundtrip() throws Exception {
        PQSignature pqsig = new PQSignature(
                PQSignature.falcon1024Scheme(), 3,
                "pk-bytes".getBytes(StandardCharsets.UTF_8),
                "sig-bytes".getBytes(StandardCharsets.UTF_8));
        byte[] encoded = Encoder.encodeToMsgPack(pqsig);
        assertThat(Encoder.decodeFromMsgPack(encoded, PQSignature.class)).isEqualTo(pqsig);
    }

    @Test
    public void testZeroSaltIsOmitted() throws Exception {
        PQSignature pqsig = new PQSignature(
                PQSignature.falcon1024Scheme(), 0,
                "pk".getBytes(StandardCharsets.UTF_8),
                "sig".getBytes(StandardCharsets.UTF_8));
        @SuppressWarnings("unchecked")
        Map<String, Object> raw = Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(pqsig), Map.class);
        assertThat(raw.keySet()).containsExactlyInAnyOrder("pk", "sch", "sig");
        // and it round-trips back to 0
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(pqsig), PQSignature.class).salt).isZero();
    }

    /* transaction signing against the go-algorand fixtures */

    private static class SignResult {
        JsonNode fx;
        SignedTransaction stxn;
        byte[] blob;
        byte[] capturedPreimage;
        Falcon1024AlgorandSigner signer;
    }

    private SignResult runTransactionFixture(String fixtureName) throws Exception {
        JsonNode fx = load(fixtureName);
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] expectedSig = b64(fx.get("stxn").get("pqsig").get("sig"));
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        // sanity: the unsigned txn re-encodes to the fixture byte-for-byte
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(txn))).isEqualTo(fx.get("txnBlob").asText());

        AtomicReference<byte[]> captured = new AtomicReference<>();
        RawSigner fake = bytes -> {
            captured.set(bytes);
            return expectedSig;
        };
        SignResult result = new SignResult();
        result.fx = fx;
        result.signer = new Falcon1024AlgorandSigner(pk, fake);
        result.stxn = result.signer.signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        result.blob = Encoder.encodeToMsgPack(result.stxn);
        result.capturedPreimage = captured.get();
        return result;
    }

    @Test
    public void testDirectPayment() throws Exception {
        SignResult r = runTransactionFixture("pqPayment.json");
        // Falcon signs the raw preimage "TX" + txn, not a digest of it
        assertThat(r.capturedPreimage).isEqualTo(r.stxn.tx.bytesToSign());
        // byte-exact vs the shared golden blob (wire key "pqsig")
        assertThat(r.blob).isEqualTo(b64(r.fx.get("stxnBlob")));
        assertThat(r.stxn.authAddr).isEqualTo(new Address());
        // decode round-trip
        assertThat(Encoder.decodeFromMsgPack(r.blob, SignedTransaction.class)).isEqualTo(r.stxn);
    }

    @Test
    public void testRekeyedPayment() throws Exception {
        SignResult r = runTransactionFixture("pqRekeyedPayment.json");
        assertThat(r.blob).isEqualTo(b64(r.fx.get("stxnBlob")));
        assertThat(r.stxn.authAddr.encodeAsString()).isEqualTo(r.fx.get("stxn").get("sgnr").asText());
        assertThat(Encoder.decodeFromMsgPack(r.blob, SignedTransaction.class)).isEqualTo(r.stxn);
    }

    @Test
    public void testSignsOnlyRequestedIndexes() throws Exception {
        JsonNode fx = load("pqPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        Transaction t0 = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        Transaction t1 = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        Transaction t2 = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        t1.note = new byte[]{'1'};
        t2.note = new byte[]{'2'};
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        SignedTransaction[] stxns = signer.signTxnGroup(new Transaction[]{t0, t1, t2}, new int[]{0, 2});
        // only the requested indexes are signed, in order
        assertThat(stxns).hasSize(2);
        assertThat(stxns[0].tx).isEqualTo(t0);
        assertThat(stxns[1].tx).isEqualTo(t2);
        assertThat(stxns[1].tx).isNotEqualTo(t1);
    }

    /* delegated logic signatures against the go-algorand fixtures */

    private static class DelegatedResult {
        JsonNode fx;
        LogicsigSignature lsig;
        SignedTransaction stxn;
        byte[] blob;
        byte[] capturedPreimage;
        Address pqAddress;
    }

    private DelegatedResult runDelegatedFixture(String fixtureName) throws Exception {
        JsonNode fx = load(fixtureName);
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        byte[] expectedSig = b64(fx.get("stxn").get("lsig").get("pqsig").get("sig"));
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);

        AtomicReference<byte[]> captured = new AtomicReference<>();
        RawSigner fake = bytes -> {
            captured.set(bytes);
            return expectedSig;
        };
        DelegatedResult result = new DelegatedResult();
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, fake);
        result.fx = fx;
        result.lsig = signer.signLogicsig(new LogicsigSignature(program));
        result.stxn = Account.signLogicsigTransaction(result.lsig, txn);
        result.blob = Encoder.encodeToMsgPack(result.stxn);
        result.capturedPreimage = captured.get();
        result.pqAddress = signer.getAddress();
        return result;
    }

    @Test
    public void testDelegatedPayment() throws Exception {
        DelegatedResult r = runDelegatedFixture("pqDelegatedPayment.json");
        // Falcon signs the raw preimage "PQProgram" + address + program, built
        // here independently of bytesToSignPQ so a regression in it cannot hide
        assertThat(r.capturedPreimage).isEqualTo(concat(
                "PQProgram".getBytes(StandardCharsets.UTF_8),
                r.pqAddress.getBytes(),
                b64(r.fx.get("signer").get("lsig"))));
        assertThat(r.blob).isEqualTo(b64(r.fx.get("stxnBlob")));
        assertThat(r.stxn.authAddr).isEqualTo(new Address());
        assertThat(Encoder.decodeFromMsgPack(r.blob, SignedTransaction.class)).isEqualTo(r.stxn);
    }

    @Test
    public void testRekeyedDelegatedPayment() throws Exception {
        DelegatedResult r = runDelegatedFixture("pqRekeyedDelegatedPayment.json");
        assertThat(r.blob).isEqualTo(b64(r.fx.get("stxnBlob")));
        assertThat(r.stxn.authAddr.encodeAsString()).isEqualTo(r.fx.get("stxn").get("sgnr").asText());
        assertThat(Encoder.decodeFromMsgPack(r.blob, SignedTransaction.class)).isEqualTo(r.stxn);
    }

    @Test
    public void testPQLogicsigIsDelegatedSingleSig() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));
        LogicSigAccount lsigAccount = new LogicSigAccount(lsig, null);
        assertThat(lsigAccount.isDelegated()).isTrue();
        assertThat(lsig.sigCount()).isEqualTo(1);
        // the delegated address is the derived PQ address, not the escrow hash
        assertThat(lsigAccount.getAddress()).isEqualTo(signer.getAddress());
        assertThat(lsigAccount.getAddress()).isNotEqualTo(lsig.toAddress());
    }

    @Test
    public void testDelegatedFalcon1024Factory() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        byte[] expectedSig = b64(fx.get("stxn").get("lsig").get("pqsig").get("sig"));
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);

        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> expectedSig);
        LogicSigAccount account = LogicSigAccount.delegatedFalcon1024(program, null, signer);

        assertThat(account.isDelegated()).isTrue();
        // the delegating address is the derived PQ address, not the escrow hash
        assertThat(account.getAddress()).isEqualTo(signer.getAddress());
        assertThat(account.lsig.sigCount()).isEqualTo(1);
        // the one-call factory produces the same canonical signed transaction as
        // the manual signLogicsig + signLogicsigTransaction path
        byte[] blob = Encoder.encodeToMsgPack(account.signLogicSigTransaction(txn));
        assertThat(blob).isEqualTo(b64(fx.get("stxnBlob")));
    }

    @Test
    public void testDelegatedFalcon1024FactoryRejectsNullSigner() {
        assertThatThrownBy(() -> LogicSigAccount.delegatedFalcon1024(new byte[]{1}, null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSignPQRejectsDoubleSign() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));
        assertThatThrownBy(() -> signer.signLogicsig(lsig))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testPQLogicsigVerify() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));
        // verify() re-derives the delegating address from the PQ public key and
        // confirms it matches
        assertThat(lsig.verify(signer.getAddress())).isTrue();
        // a mismatched delegating address must not verify
        assertThat(lsig.verify(new Address())).isFalse();
    }

    @Test
    public void testSignPQThenEd25519SignRejected() throws Exception {
        // a post-quantum-signed logicsig must reject a subsequent ed25519
        // secret-key signing, or the pqsig and the sig would both go on the wire
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));
        Account account = new Account();
        assertThatThrownBy(() -> account.signLogicsig(lsig))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> account.signLogicsig(lsig,
                new MultisigAddress(1, 1, Arrays.asList(account.getEd25519PublicKey()))))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testLogicsigEqualsDistinguishesPQsig() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        LogicsigSignature signed = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG)
                .signLogicsig(new LogicsigSignature(program));
        assertThat(signed).isNotEqualTo(new LogicsigSignature(program));
        // a different post-quantum public key must not compare equal
        byte[] reversed = new byte[pk.length];
        for (int i = 0; i < pk.length; i++) reversed[i] = pk[pk.length - 1 - i];
        LogicsigSignature other = new Falcon1024AlgorandSigner(reversed, bytes -> DUMMY_FALCON_SIG)
                .signLogicsig(new LogicsigSignature(program));
        assertThat(signed).isNotEqualTo(other);
    }

    /* decoding go-algorand fixture blobs directly */

    @Test
    public void testDecodeDirectFixture() throws Exception {
        JsonNode fx = load("pqPayment.json");
        SignedTransaction stxn = Encoder.decodeFromMsgPack(fx.get("stxnBlob").asText(), SignedTransaction.class);
        assertThat(Encoder.encodeToBase64(stxn.pqSig.signature)).isEqualTo(fx.get("stxn").get("pqsig").get("sig").asText());
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stxn))).isEqualTo(fx.get("stxnBlob").asText());
    }

    @Test
    public void testDecodeDelegatedFixture() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        SignedTransaction stxn = Encoder.decodeFromMsgPack(fx.get("stxnBlob").asText(), SignedTransaction.class);
        assertThat(stxn.lSig.pqsig).isNotNull();
        assertThat(Encoder.encodeToBase64(stxn.lSig.pqsig.signature))
                .isEqualTo(fx.get("stxn").get("lsig").get("pqsig").get("sig").asText());
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stxn))).isEqualTo(fx.get("stxnBlob").asText());
    }

    /* non-canonical salts: the carried salt resolves the authorizer */

    /** A public key whose canonical salt is greater than zero, with that salt. */
    private static byte[] keyWithNonzeroCanonicalSalt() throws Exception {
        byte[] scheme = PQSignature.falcon1024Scheme();
        for (int i = 1; i < 2000; i++) {
            byte[] candidate = Encoder.encodeUintToBytes(BigInteger.valueOf(i), 32);
            if (PQAddress.derive(scheme, candidate).getSalt() > 0) {
                return candidate;
            }
        }
        throw new IllegalStateException("no key with a nonzero canonical salt found");
    }

    @Test
    public void testDeriveWithExplicitSaltUsesThatSalt() throws Exception {
        byte[] scheme = PQSignature.falcon1024Scheme();
        byte[] pk = keyWithNonzeroCanonicalSalt();
        PQAddress canonical = PQAddress.derive(scheme, pk);

        // the explicit-salt overload agrees with the search at the canonical salt
        assertThat(PQAddress.derive(scheme, canonical.getSalt(), pk)).isEqualTo(canonical.getAddress());
        // and gives a different, still valid address for every other salt
        for (int salt = 0; salt <= 0xff; salt++) {
            if (salt == canonical.getSalt()) continue;
            assertThat(PQAddress.derive(scheme, salt, pk)).isNotEqualTo(canonical.getAddress());
        }
        for (int bad : new int[]{-1, 256, 1000}) {
            assertThatThrownBy(() -> PQAddress.derive(scheme, bad, pk))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("salt");
        }
    }

    @Test
    public void testFromSignatureHonoursANonCanonicalSalt() throws Exception {
        byte[] scheme = PQSignature.falcon1024Scheme();
        byte[] pk = keyWithNonzeroCanonicalSalt();
        PQAddress canonical = PQAddress.derive(scheme, pk);
        // a salt above the canonical one whose address is still off-curve, i.e.
        // one consensus would accept as a post-quantum authorizer
        int otherSalt = -1;
        for (int salt = canonical.getSalt() + 1; salt <= 0xff; salt++) {
            if (!PQAddress.isEd25519Point(PQAddress.derive(scheme, salt, pk).getBytes())) {
                otherSalt = salt;
                break;
            }
        }
        assertThat(otherSalt).isGreaterThan(canonical.getSalt());

        PQSignature pqsig = new PQSignature(scheme, otherSalt, pk, DUMMY_FALCON_SIG);
        // the address comes from the salt the signature carries, not the canonical one
        assertThat(PQAddress.fromSignature(pqsig)).isEqualTo(PQAddress.derive(scheme, otherSalt, pk));
        assertThat(PQAddress.fromSignature(pqsig)).isNotEqualTo(canonical.getAddress());

        // and it threads through the LogicSig entry points
        LogicsigSignature lsig = new LogicsigSignature(b64(load("pqDelegatedPayment.json").get("signer").get("lsig")));
        lsig.pqsig = pqsig;
        Address authorizer = PQAddress.derive(scheme, otherSalt, pk);
        assertThat(new LogicSigAccount(lsig, null).getAddress()).isEqualTo(authorizer);
        assertThat(lsig.verify(authorizer)).isTrue();
        assertThat(lsig.verify(canonical.getAddress())).isFalse();
    }

    /* delegated LogicSig safety checks (ported from js-algorand-sdk) */

    /** A self-consistent pqsig over a one-byte key, at that key's canonical salt. */
    private static PQSignature samplePqsig() throws Exception {
        byte[] pk = new byte[]{1};
        PQAddress derived = PQAddress.derive(PQSignature.falcon1024Scheme(), pk);
        return new PQSignature(PQSignature.falcon1024Scheme(), derived.getSalt(), pk, new byte[]{2});
    }

    @Test
    public void testRejectsALogicsigCarryingBothAnEd25519SigAndAPqsig() throws Exception {
        Transaction txn = Encoder.decodeFromMsgPack(load("pqPayment.json").get("txnBlob").asText(), Transaction.class);
        LogicsigSignature lsig = new LogicsigSignature(PROGRAM);
        lsig.sig = new Signature(new byte[64]);
        lsig.pqsig = samplePqsig();

        assertThat(lsig.sigCount()).isEqualTo(2);
        assertThat(lsig.verify(txn.sender)).isFalse();
        assertThatThrownBy(() -> Account.signLogicsigTransaction(lsig, txn))
                .hasStackTraceContaining("verification failed on logic sig");
    }

    @Test
    public void testRejectsAPqDelegatedLogicsigWhoseProgramIsNotTeal() throws Exception {
        // js catches this at signing time, because there the program can be
        // replaced after construction. In Java `logic` is final and every
        // constructor -- including the one msgpack decoding uses -- sanity-checks
        // it, so such a LogicSig cannot be built at all.
        byte[] notTeal = "#pragma version 12\nint 1".getBytes(StandardCharsets.UTF_8);
        PQSignature pqsig = samplePqsig();
        assertThatThrownBy(() -> new LogicsigSignature(notTeal, null, null, null, null, pqsig))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not looking like Teal byte code");

        // and a wire blob carrying such a program cannot be decoded into one
        byte[] blob = b64(load("pqDelegatedPayment.json").get("stxnBlob"));
        @SuppressWarnings("unchecked")
        Map<String, Object> raw = Encoder.decodeFromMsgPack(blob, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> lsigMap = (Map<String, Object>) raw.get("lsig");
        lsigMap.put("l", notTeal);
        byte[] tampered = Encoder.encodeToMsgPack(raw);
        assertThatThrownBy(() -> Encoder.decodeFromMsgPack(tampered, SignedTransaction.class))
                .hasStackTraceContaining("not looking like Teal byte code");
    }

    @Test
    public void testRejectsALogicSigAccountWhoseSigKeyContradictsItsPqsig() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));

        // a pqsig names its own delegating account, so pairing one with an
        // ed25519 signing key is contradictory by construction and refused
        assertThatThrownBy(() -> new LogicSigAccount(lsig, new Ed25519PublicKey(new byte[32])))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("post-quantum LogicSig and a public key");
        // the delegating account still comes from the signature itself
        assertThat(new LogicSigAccount(lsig, null).getAddress()).isEqualTo(signer.getAddress());
    }

    @Test
    public void testBareLogicsigDerivesItsDelegatorFromThePqsig() throws Exception {
        // Unlike an ed25519 sig, a pqsig identifies its own delegating account, so
        // a bare LogicSig can authorize a transaction whose sender was rekeyed to
        // that account; sgnr must name the delegating account, not the sender.
        JsonNode fx = load("pqRekeyedDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));
        byte[] sig = b64(fx.get("stxn").get("lsig").get("pqsig").get("sig"));
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);

        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(pk, bytes -> sig);
        LogicsigSignature lsig = signer.signLogicsig(new LogicsigSignature(program));
        assertThat(txn.sender).isNotEqualTo(signer.getAddress());

        // passing the bare LogicSig must produce the fixture blob, the same one
        // the LogicSigAccount path produces
        SignedTransaction stx = Account.signLogicsigTransaction(lsig, txn);
        assertThat(stx.authAddr.encodeAsString()).isEqualTo(fx.get("stxn").get("sgnr").asText());
        assertThat(Encoder.encodeToMsgPack(stx)).isEqualTo(b64(fx.get("stxnBlob")));
    }

    /* salted (non-canonical) signers */

    @Test
    public void testSaltedSignerOnlyDiffersInSaltAndAddress() throws Exception {
        JsonNode fx = load("pqPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] expectedSig = b64(fx.get("stxn").get("pqsig").get("sig"));
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);

        Falcon1024AlgorandSigner canonical = new Falcon1024AlgorandSigner(pk, bytes -> expectedSig);
        Falcon1024AlgorandSigner salted = new Falcon1024AlgorandSigner(pk, bytes -> expectedSig, 99);

        assertThat(salted.getSalt()).isEqualTo(99);
        assertThat(canonical.getSalt()).isNotEqualTo(99);
        // everything but the salt and the address it implies is unchanged
        assertThat(salted.getPublicKey()).isEqualTo(canonical.getPublicKey());
        assertThat(salted.getScheme()).isEqualTo(canonical.getScheme());
        assertThat(salted.getAddress()).isNotEqualTo(canonical.getAddress());
        assertThat(salted.getAddress()).isEqualTo(PQAddress.derive(PQSignature.falcon1024Scheme(), 99, pk));

        SignedTransaction stx = salted.signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(stx.pqSig.salt).isEqualTo(99);
        // the signature covers the transaction, not the salt, so the bytes are
        // the same ones the canonical signer would have produced
        assertThat(stx.pqSig.signature).isEqualTo(expectedSig);
        // the fixture's sender is the canonical account, so signing for another
        // salt makes this a rekeyed authorization
        assertThat(stx.authAddr).isEqualTo(salted.getAddress());
        assertThat(PQAddress.fromSignature(stx.pqSig)).isEqualTo(salted.getAddress());
    }

    @Test
    public void testSaltedSignerRejectsAnOutOfRangeSalt() throws Exception {
        byte[] pk = b64(load("pqPayment.json").get("signer").get("pqSigner").get("pk"));
        for (int bad : new int[]{-1, 256}) {
            assertThatThrownBy(() -> new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG, bad))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("salt");
        }
    }

    @Test
    public void testSaltedSignerDelegatesALogicsigToItsOwnAddress() throws Exception {
        JsonNode fx = load("pqDelegatedPayment.json");
        byte[] pk = b64(fx.get("signer").get("pqSigner").get("pk"));
        byte[] program = b64(fx.get("signer").get("lsig"));

        AtomicReference<byte[]> captured = new AtomicReference<>();
        Falcon1024AlgorandSigner salted = new Falcon1024AlgorandSigner(pk, bytes -> {
            captured.set(bytes);
            return DUMMY_FALCON_SIG;
        }, 99);
        LogicsigSignature lsig = salted.signLogicsig(new LogicsigSignature(program));

        // the delegation is bound to the salted address, not the canonical one;
        // the preimage is built independently of bytesToSignPQ so a regression
        // in it cannot hide
        assertThat(captured.get()).isEqualTo(concat(
                "PQProgram".getBytes(StandardCharsets.UTF_8),
                salted.getAddress().getBytes(),
                program));
        assertThat(new LogicSigAccount(lsig, null).getAddress()).isEqualTo(salted.getAddress());
        assertThat(lsig.verify(salted.getAddress())).isTrue();
    }

    /* PQSignature validation and immutability */

    @Test
    public void testPQSignatureRejectsBadSchemeAndSalt() {
        byte[] pk = "pk".getBytes(StandardCharsets.UTF_8);
        for (byte[] bad : new byte[][]{new byte[0], new byte[1], new byte[3]}) {
            assertThatThrownBy(() -> new PQSignature(bad, 0, pk, pk))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("scheme");
        }
        for (int bad : new int[]{-1, 256}) {
            assertThatThrownBy(() -> new PQSignature(PQSignature.falcon1024Scheme(), bad, pk, pk))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("salt");
        }
        // a blank signature decodes with no scheme at all
        assertThat(new PQSignature(null, 0, null, null).scheme).isNull();
    }

    @Test
    public void testDecodingToleratesAnUnderivablePqAddress() throws Exception {
        // Decoding does not derive the delegating address, so a scheme no address
        // can be derived from still decodes for inspection; the check happens
        // where the address is derived. Ported from py-algorand-sdk's
        // test_decoding_tolerates_an_underivable_pq_address. js-algorand-sdk
        // rejects such a blob outright instead.
        byte[] badScheme = "f123".getBytes(StandardCharsets.UTF_8);
        for (String name : new String[]{"pqPayment.json", "pqDelegatedPayment.json"}) {
            boolean delegated = name.startsWith("pqDelegated");
            @SuppressWarnings("unchecked")
            Map<String, Object> raw = Encoder.decodeFromMsgPack(b64(load(name).get("stxnBlob")), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> holder = delegated ? (Map<String, Object>) raw.get("lsig") : raw;
            @SuppressWarnings("unchecked")
            Map<String, Object> pqsig = (Map<String, Object>) holder.get("pqsig");
            pqsig.put("sch", badScheme);

            SignedTransaction stx = Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(raw), SignedTransaction.class);
            assertThat(stx).as("%s decodes", name).isNotNull();
            final PQSignature decoded = delegated ? stx.lSig.pqsig : stx.pqSig;
            assertThat(decoded.scheme).isEqualTo(badScheme);
            // ... but no address can be derived from it
            assertThatThrownBy(() -> PQAddress.fromSignature(decoded))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("scheme");
        }
    }

    /* signer identity */

    @Test
    public void testSignersForTheSameAccountAreEqualAndSaltIsPartOfIdentity() throws Exception {
        byte[] pk = b64(load("pqPayment.json").get("signer").get("pqSigner").get("pk"));
        Falcon1024AlgorandSigner one = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG);
        Falcon1024AlgorandSigner another = new Falcon1024AlgorandSigner(pk, bytes -> new byte[7]);
        Falcon1024AlgorandSigner salted = new Falcon1024AlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG, 99);
        PQAlgorandSigner generic = new PQAlgorandSigner(
                pk, bytes -> DUMMY_FALCON_SIG, PQSignature.falcon1024Scheme());

        // the callback is not part of the identity, the authorized account is
        assertThat(one).isEqualTo(another);
        assertThat(one.hashCode()).isEqualTo(another.hashCode());
        assertThat(one).isEqualTo(generic);
        assertThat(generic).isEqualTo(one);
        assertThat(one.hashCode()).isEqualTo(generic.hashCode());
        // signing for another salt is signing for another account
        assertThat(one).isNotEqualTo(salted);

        // which is what lets AtomicTransactionComposer group by signer
        Map<TxnSigner, String> group = new HashMap<>();
        group.put(one, "a");
        group.put(another, "b");
        group.put(generic, "generic");
        group.put(salted, "c");
        assertThat(group).hasSize(2);
    }

    @Test
    public void testGenericPqSignerIsUsableFromOutsideItsPackage() throws Exception {
        // the generic base is public, so a scheme the protocol registers later can
        // be driven without waiting for a scheme-specific subclass
        byte[] scheme = "x1".getBytes(StandardCharsets.UTF_8);
        byte[] pk = b64(load("pqPayment.json").get("signer").get("pqSigner").get("pk"));
        PQAlgorandSigner signer = new PQAlgorandSigner(pk, bytes -> DUMMY_FALCON_SIG, scheme);
        assertThat(signer.getScheme()).isEqualTo(scheme);

        LogicSigAccount lsa = LogicSigAccount.delegatedPQ(PROGRAM, null, signer);
        assertThat(lsa.isDelegated()).isTrue();
        assertThat(lsa.getAddress()).isEqualTo(signer.getAddress());
        assertThat(lsa.lsig.pqsig.scheme).isEqualTo(scheme);
    }

    @Test
    public void testSchemeConstantCannotBeMutated() throws Exception {
        byte[] scheme = PQSignature.falcon1024Scheme();
        scheme[0] = 'x';
        assertThat(PQSignature.falcon1024Scheme()).isEqualTo("f1".getBytes(StandardCharsets.UTF_8));

        // and a signature keeps its own copy of the array it was handed
        byte[] mine = PQSignature.falcon1024Scheme();
        PQSignature pqsig = new PQSignature(mine, 0, "pk".getBytes(StandardCharsets.UTF_8), DUMMY_FALCON_SIG);
        mine[0] = 'x';
        assertThat(pqsig.scheme).isEqualTo("f1".getBytes(StandardCharsets.UTF_8));
    }

    /* wire-format constants */

    @Test
    public void testWireFormatConstants() {
        assertThat(PQSignature.falcon1024Scheme()).isEqualTo("f1".getBytes(StandardCharsets.UTF_8));
        assertThat(PQSignature.SCHEME_LEN_BYTES).isEqualTo(2);
    }

    private static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] part : parts) len += part.length;
        byte[] out = new byte[len];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, out, offset, part.length);
            offset += part.length;
        }
        return out;
    }
}
