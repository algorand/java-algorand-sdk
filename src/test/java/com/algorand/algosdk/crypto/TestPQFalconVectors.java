package com.algorand.algosdk.crypto;

import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.signer.Falcon1024AlgorandSigner;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.falcon.Falcon1024;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The half of the post-quantum story {@link TestPQ} cannot reach: it drives real
 * Falcon-1024 key generation and signing through the falcon-det1024 test
 * dependency, closing the seed-to-public-key link that the fixtures otherwise
 * take on faith.
 * <p>
 * The SDK itself bundles no Falcon implementation, so this class needs the
 * vendored native library; where it is unavailable for the platform the whole
 * class is skipped rather than failed.
 */
public class TestPQFalconVectors {
    private static final ObjectMapper JSON = new ObjectMapper();

    @BeforeAll
    public static void requireFalconNative() {
        boolean available;
        try {
            Falcon1024.Signer.generate(new byte[]{1});
            available = true;
        } catch (Throwable t) {
            available = false;
        }
        Assumptions.assumeTrue(available, "falcon-det1024 native library unavailable on this platform");
    }

    private static JsonNode load(String name) throws Exception {
        InputStream is = TestPQFalconVectors.class.getClassLoader().getResourceAsStream("pq_test_data/" + name);
        assertThat(is).as("fixture %s", name).isNotNull();
        return JSON.readTree(is);
    }

    @Test
    public void testMnemonicSeedDerivesTheGoldenKeypairAndAddress() throws Exception {
        JsonNode fx = load("pqMnemonic.json");
        byte[] seed = Mnemonic.toPQSeed(fx.get("mnemonic").asText(), PQSignature.falcon1024Scheme());
        assertThat(Encoder.encodeToBase64(seed)).isEqualTo(fx.get("seed").asText());

        // the step the fixtures alone cannot check: seed -> Falcon public key
        Falcon1024.Signer falcon = Falcon1024.Signer.generate(seed);
        assertThat(Encoder.encodeToBase64(falcon.getPublicKey())).isEqualTo(fx.get("publicKey").asText());
        assertThat(PQAddress.derive(PQSignature.falcon1024Scheme(), falcon.getPublicKey()).getAddress().encodeAsString())
                .isEqualTo(fx.get("address").asText());
    }

    @Test
    public void testKeygenIsDeterministicAndTheAddressIsOffCurve() throws Exception {
        byte[] seed = Mnemonic.toPQSeed(load("pqMnemonic.json").get("mnemonic").asText(),
                PQSignature.falcon1024Scheme());
        // the same seed always yields the same account (deterministic keygen + salt)
        Falcon1024.Signer first = Falcon1024.Signer.generate(seed);
        Falcon1024.Signer second = Falcon1024.Signer.generate(seed);
        assertThat(first.getPublicKey()).isEqualTo(second.getPublicKey());
        assertThat(first.getPrivateKey()).isEqualTo(second.getPrivateKey());

        // a post-quantum address must not double as a valid ed25519 point
        Address addr = PQAddress.derive(PQSignature.falcon1024Scheme(), first.getPublicKey()).getAddress();
        assertThat(PQAddress.isEd25519Point(addr.getBytes())).isFalse();
    }

    @Test
    public void testGeneratedAccountIsWellFormed() throws Exception {
        byte[] seed = new byte[32];
        new SecureRandom().nextBytes(seed);
        Falcon1024.Signer falcon = Falcon1024.Signer.generate(seed);
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(falcon.getPublicKey(), falcon::sign);

        assertThat(signer.getAddress()).isNotEqualTo(new Address());
        assertThat(signer.getSalt()).isBetween(0, 255);
        assertThat(PQAddress.isEd25519Point(signer.getAddress().getBytes())).isFalse();
        // round-trips through its own string form
        assertThat(new Address(signer.getAddress().encodeAsString())).isEqualTo(signer.getAddress());
    }

    @Test
    public void testRealSignatureVerifiesOverThePreimageTheSdkHandsTheCallback() throws Exception {
        JsonNode fx = load("pqPayment.json");
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        byte[] seed = Mnemonic.toPQSeed(load("pqMnemonic.json").get("mnemonic").asText(),
                PQSignature.falcon1024Scheme());
        Falcon1024.Signer falcon = Falcon1024.Signer.generate(seed);

        AtomicReference<byte[]> preimage = new AtomicReference<>();
        Falcon1024AlgorandSigner signer = new Falcon1024AlgorandSigner(falcon.getPublicKey(), bytes -> {
            preimage.set(bytes);
            return falcon.sign(bytes);
        });
        SignedTransaction stx = signer.signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];

        // the callback is handed the raw "TX" || txn preimage, and a real Falcon
        // signature over exactly those bytes verifies
        assertThat(preimage.get()).isEqualTo(txn.bytesToSign());
        assertThat(falcon.verifyingKey().isValid(preimage.get(), stx.pqSig.signature)).isTrue();
        assertThat(PQAddress.fromSignature(stx.pqSig)).isEqualTo(signer.getAddress());
        // and the whole thing survives the wire
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(stx), SignedTransaction.class)).isEqualTo(stx);
    }
}
