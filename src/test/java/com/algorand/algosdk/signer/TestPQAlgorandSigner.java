package com.algorand.algosdk.signer;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the public generic PQAlgorandSigner base; the
 * scheme-specific behavior of its public Falcon-1024 subclass is covered by
 * {@link com.algorand.algosdk.crypto.TestPQ}.
 */
public class TestPQAlgorandSigner {
    @Test
    public void testHonorsCustomScheme() throws Exception {
        // the generic PQAlgorandSigner threads an arbitrary 2-byte scheme
        // into the wire signature (Falcon1024AlgorandSigner fixes it to "f1")
        byte[] scheme = "x1".getBytes(StandardCharsets.UTF_8);
        InputStream is = getClass().getClassLoader().getResourceAsStream("pq_test_data/pqPayment.json");
        assertThat(is).isNotNull();
        JsonNode fx = new ObjectMapper().readTree(is);
        Transaction txn = Encoder.decodeFromMsgPack(fx.get("txnBlob").asText(), Transaction.class);
        PQAlgorandSigner signer = new PQAlgorandSigner(
                new byte[]{(byte) 0xAB, (byte) 0xCD, (byte) 0xEF}, bytes -> new byte[1280], scheme);
        assertThat(signer.getScheme()).isEqualTo(scheme);
        SignedTransaction stxn = signer.signTxnGroup(new Transaction[]{txn}, new int[]{0})[0];
        assertThat(stxn.pqSig.scheme).isEqualTo(scheme);
    }
}
