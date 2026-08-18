package com.algorand.algosdk.transaction;

import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the SDK imposes no client-side size limits on notes, app args,
 * logic sig programs, app programs, or extra program pages. Absolute limits
 * are enforced by the node (note bytes 1024 -> 4096, total app args
 * 2048 -> 16384, extra program pages 3 -> 7 in the corresponding consensus
 * upgrade), and required fees and box refs should be determined via simulate.
 */
public class TestLargeTransactions {

    private static final String SENDER_ADDR = "XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA";
    private static final String RECEIVER_ADDR = "47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU";
    private static final String GENESIS_HASH = "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=";

    private static Transaction roundTrip(Transaction txn) {
        try {
            String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(txn));
            return Encoder.decodeFromMsgPack(encoded, Transaction.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLargeNoteAccepted() throws NoSuchAlgorithmException {
        // 4096 bytes is the new node-side limit; 8192 proves the client does not cap it at all
        for (int noteSize : new int[]{4096, 8192}) {
            byte[] note = new byte[noteSize];
            for (int i = 0; i < note.length; i++) {
                note[i] = (byte) i;
            }

            Transaction txn = PaymentTransactionBuilder.Builder()
                    .sender(new Address(SENDER_ADDR))
                    .receiver(new Address(RECEIVER_ADDR))
                    .amount(1000)
                    .note(note)
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                    .build();

            assertArrayEquals(note, txn.note);
            assertArrayEquals(note, roundTrip(txn).note);
        }
    }

    @Test
    public void testLargeAppArgsAccepted() throws NoSuchAlgorithmException {
        // 8 args x 2048 bytes = 16384 bytes total, the new node-side limit
        List<byte[]> args = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            byte[] arg = new byte[2048];
            for (int j = 0; j < arg.length; j++) {
                arg[j] = (byte) (i + j);
            }
            args.add(arg);
        }

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .applicationId(1001L)
                .args(args)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        assertEquals(8, txn.applicationArgs.size());
        Transaction decoded = roundTrip(txn);
        assertEquals(8, decoded.applicationArgs.size());
        for (int i = 0; i < 8; i++) {
            assertArrayEquals(args.get(i), decoded.applicationArgs.get(i));
        }
    }

    @Test
    public void testLargeAppProgramsAccepted() throws NoSuchAlgorithmException {
        // 7 extra pages allow (1 + 7) * 2048 = 16384 bytes of combined program size
        byte[] approval = new byte[14336];
        approval[0] = 0x06; // AVM version prefix; remaining zero bytes are non-printable
        byte[] clear = new byte[2048];
        clear[0] = 0x06;

        Transaction txn = ApplicationCreateTransactionBuilder.Builder()
                .sender(new Address(SENDER_ADDR))
                .approvalProgram(new TEALProgram(approval))
                .clearStateProgram(new TEALProgram(clear))
                .globalStateSchema(new StateSchema(1, 1))
                .localStateSchema(new StateSchema(0, 0))
                .extraPages(7L)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64(GENESIS_HASH))
                .build();

        assertEquals(Long.valueOf(7L), txn.extraPages);
        Transaction decoded = roundTrip(txn);
        assertEquals(Long.valueOf(7L), decoded.extraPages);
        assertArrayEquals(approval, decoded.approvalProgram.getBytes());
        assertArrayEquals(clear, decoded.clearStateProgram.getBytes());
    }

    @Test
    public void testExtraPagesHasNoClientSideCap() {
        // The maximum is enforced by the network, not the client
        assertDoesNotThrow(() -> ApplicationCreateTransactionBuilder.Builder().extraPages(7L));
        assertDoesNotThrow(() -> ApplicationCreateTransactionBuilder.Builder().extraPages(100L));

        // Malformed values are still rejected
        assertThrows(IllegalArgumentException.class,
                () -> ApplicationCreateTransactionBuilder.Builder().extraPages(-1L));
        assertThrows(IllegalArgumentException.class,
                () -> ApplicationCreateTransactionBuilder.Builder().extraPages(null));
    }

    @Test
    public void testLargeLogicSigAccepted() {
        // Well beyond the historical 1000-byte lsig limit once enforced client-side
        byte[] program = new byte[5000];
        program[0] = 0x06; // AVM version prefix; remaining zero bytes are non-printable

        List<byte[]> args = new ArrayList<>();
        args.add(new byte[2048]);
        args.add(new byte[2048]);

        LogicsigSignature lsig = assertDoesNotThrow(() -> new LogicsigSignature(program, args));
        assertArrayEquals(program, lsig.logic);
        assertEquals(2, lsig.args.size());

        assertDoesNotThrow(() -> {
            String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(lsig));
            LogicsigSignature decoded = Encoder.decodeFromMsgPack(encoded, LogicsigSignature.class);
            assertArrayEquals(program, decoded.logic);
        });
    }
}
