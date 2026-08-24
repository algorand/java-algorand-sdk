package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationUpdateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the global state schema and extraPages can be changed by an
 * application update, allowed since consensus v42. The local state schema
 * remains immutable after creation.
 */
public class TestUpdateApplication {

    private static final String SENDER_ADDR = "XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA";
    private static final String GENESIS_HASH = "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=";

    private static TEALProgram program() {
        byte[] bytes = new byte[64];
        bytes[0] = 0x06; // AVM version prefix; remaining zero bytes are non-printable
        return new TEALProgram(bytes);
    }

    @Test
    public void testUpdateAllowsGlobalSchemaAndExtraPages() throws Exception {
        Transaction txn = ApplicationUpdateTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .applicationId(1001L)
                .approvalProgram(program())
                .clearStateProgram(program())
                .globalStateSchema(new StateSchema(2, 3))
                .extraPages(2L)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        assertEquals(Transaction.OnCompletion.UpdateApplicationOC, txn.onCompletion);
        assertEquals(BigInteger.valueOf(2), txn.globalStateSchema.numUint);
        assertEquals(BigInteger.valueOf(3), txn.globalStateSchema.numByteSlice);
        assertEquals(Long.valueOf(2L), txn.extraPages);

        String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(txn));
        Transaction decoded = Encoder.decodeFromMsgPack(encoded, Transaction.class);
        assertEquals(BigInteger.valueOf(2), decoded.globalStateSchema.numUint);
        assertEquals(BigInteger.valueOf(3), decoded.globalStateSchema.numByteSlice);
        assertEquals(Long.valueOf(2L), decoded.extraPages);
    }

    @Test
    public void testUpdateWithoutSchemaKeepsDefaults() throws NoSuchAlgorithmException {
        Transaction txn = ApplicationUpdateTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .applicationId(1001L)
                .approvalProgram(program())
                .clearStateProgram(program())
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        assertEquals(Long.valueOf(0L), txn.extraPages);
        assertEquals(new StateSchema(), txn.globalStateSchema);
    }

    @Test
    public void testSchemalessCreateOmitsSchemaKeys() throws Exception {
        // A create without schemas/extraPages must omit apls/apgs/apep entirely;
        // encoding them as msgpack nil breaks signature verification on the node
        Transaction unset = ApplicationCreateTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .approvalProgram(program())
                .clearStateProgram(program())
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        byte[] enc = Encoder.encodeToMsgPack(unset);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> fields = Encoder.decodeFromMsgPack(
                Encoder.encodeToBase64(enc), java.util.Map.class);
        assertFalse(fields.containsKey("apls"), "apls must be omitted, not nil");
        assertFalse(fields.containsKey("apgs"), "apgs must be omitted, not nil");
        assertFalse(fields.containsKey("apep"), "apep must be omitted, not nil");

        // Explicit zero schemas and extraPages(0) encode byte-identically to unset
        Transaction explicitZero = ApplicationCreateTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .approvalProgram(program())
                .clearStateProgram(program())
                .localStateSchema(new StateSchema(0, 0))
                .globalStateSchema(new StateSchema(0, 0))
                .extraPages(0L)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        assertEquals(Encoder.encodeToBase64(enc),
                Encoder.encodeToBase64(Encoder.encodeToMsgPack(explicitZero)));
    }

    @Test
    public void testMethodCallUpdateAllowsGlobalSchemaAndExtraPages() throws NoSuchAlgorithmException {
        Account account = new Account();

        MethodCallTransactionBuilder<?> builder = MethodCallTransactionBuilder.Builder()
                .onComplete(Transaction.OnCompletion.UpdateApplicationOC)
                .sender(account.getAddress())
                .signer(account.getTransactionSigner())
                .applicationId(1001L)
                .method(new Method("foo()void"))
                .approvalProgram(program())
                .clearStateProgram(program())
                .globalStateSchema(new StateSchema(2, 3))
                .extraPages(2L)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .genesisID("testnet-v1.0")
                .flatFee(BigInteger.valueOf(1000));

        MethodCallParams params = assertDoesNotThrow(builder::build);
        Transaction txn = params.createTransactions().get(0).txn;
        assertEquals(Transaction.OnCompletion.UpdateApplicationOC, txn.onCompletion);
        assertEquals(BigInteger.valueOf(2), txn.globalStateSchema.numUint);
        assertEquals(BigInteger.valueOf(3), txn.globalStateSchema.numByteSlice);
        assertEquals(Long.valueOf(2L), txn.extraPages);
    }

    @Test
    public void testMethodCallUpdateRejectsLocalSchema() throws NoSuchAlgorithmException {
        Account account = new Account();

        MethodCallTransactionBuilder<?> builder = MethodCallTransactionBuilder.Builder()
                .onComplete(Transaction.OnCompletion.UpdateApplicationOC)
                .sender(account.getAddress())
                .signer(account.getTransactionSigner())
                .applicationId(1001L)
                .method(new Method("foo()void"))
                .approvalProgram(program())
                .clearStateProgram(program())
                .localStateSchema(new StateSchema(1, 1))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .genesisID("testnet-v1.0")
                .flatFee(BigInteger.valueOf(1000));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertTrue(e.getMessage().contains("local state schema cannot be changed"));
    }
}
