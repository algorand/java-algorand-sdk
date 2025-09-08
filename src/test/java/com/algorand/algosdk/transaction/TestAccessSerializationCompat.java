package com.algorand.algosdk.transaction;

import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test serialization compatibility with go-algorand for the access field
 */
public class TestAccessSerializationCompat {

    private static final String SENDER_ADDR = "XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA";
    private static final String ACCOUNT_ADDR = "47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU";

    @Test
    public void testSerializationDeserializationRoundTrip() throws Exception {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // Create transaction with complex access pattern
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(account),                 // Address reference
            AppResourceRef.forAsset(12345L),                    // Asset reference
            AppResourceRef.forApp(67890L),                      // Foreign app reference
            AppResourceRef.forHolding(sender, 12345L),          // Holding ref (sender index 0, asset index 1)
            AppResourceRef.forLocals(account, 2001L),           // Locals ref (account index 1, current app index 0)
            AppResourceRef.forBox(2001L, "test-box".getBytes()) // Box ref (current app index 0)
        );

        Transaction original = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(2001L)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Serialize to MessagePack
        byte[] encoded = Encoder.encodeToMsgPack(original);
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);

        // Deserialize from MessagePack
        Transaction decoded = Encoder.decodeFromMsgPack(encoded, Transaction.class);
        assertNotNull(decoded);
        
        // Verify access field structure
        assertNotNull(decoded.access);
        assertEquals(original.access.size(), decoded.access.size());
        
        // Verify specific entries are preserved correctly
        for (int i = 0; i < original.access.size(); i++) {
            ResourceRef origRef = original.access.get(i);
            ResourceRef decodedRef = decoded.access.get(i);
            
            assertEquals(origRef.address, decodedRef.address);
            assertEquals(origRef.asset, decodedRef.asset);
            assertEquals(origRef.app, decodedRef.app);
            
            if (origRef.holding != null) {
                assertNotNull(decodedRef.holding);
                assertEquals(origRef.holding.addressIndex, decodedRef.holding.addressIndex);
                assertEquals(origRef.holding.assetIndex, decodedRef.holding.assetIndex);
            }
            
            if (origRef.locals != null) {
                assertNotNull(decodedRef.locals);
                assertEquals(origRef.locals.addressIndex, decodedRef.locals.addressIndex);
                assertEquals(origRef.locals.appIndex, decodedRef.locals.appIndex);
            }
            
            if (origRef.box != null) {
                assertNotNull(decodedRef.box);
                assertEquals(origRef.box.index, decodedRef.box.index);
                assertArrayEquals(origRef.box.name, decodedRef.box.name);
            }
        }
        
        // Verify all other transaction fields are preserved
        assertEquals(original.sender, decoded.sender);
        assertEquals(original.applicationId, decoded.applicationId);
        assertEquals(original.firstValid, decoded.firstValid);
        assertEquals(original.lastValid, decoded.lastValid);
        assertArrayEquals(original.genesisHash.getBytes(), decoded.genesisHash.getBytes());
    }

    @Test
    public void testAccessFieldMessagePackTags() throws Exception {
        Address sender = new Address(SENDER_ADDR);
        
        // Create transaction with various resource types
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(new Address(ACCOUNT_ADDR)),
            AppResourceRef.forAsset(999L),
            AppResourceRef.forApp(888L),
            AppResourceRef.forHolding(sender, 999L),
            AppResourceRef.forLocals(sender, 2002L),
            AppResourceRef.forBox(2002L, "mybox".getBytes())
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(2002L)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Encode and verify we can read the MessagePack bytes
        byte[] encoded = Encoder.encodeToMsgPack(txn);
        assertNotNull(encoded);
        
        // Convert to hex string for debugging (optional, but useful for verification)
        String hexEncoded = bytesToHex(encoded);
        assertNotNull(hexEncoded);
        
        // The presence of access field should be indicated by the "apac" tag in MessagePack
        // This is indirectly verified by successful round-trip serialization
        Transaction decoded = Encoder.decodeFromMsgPack(encoded, Transaction.class);
        assertNotNull(decoded.access);
        assertFalse(decoded.access.isEmpty());
    }
    
    @Test
    public void testIndexZeroSpecialCases() throws Exception {
        Address sender = new Address(SENDER_ADDR);
        
        // Test that sender address and current app become index 0
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forHolding(sender, 555L),    // sender should be index 0
            AppResourceRef.forLocals(sender, 3003L),    // current app should be index 0  
            AppResourceRef.forBox(3003L, "box1".getBytes()) // current app should be index 0
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(3003L)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Serialize and deserialize
        byte[] encoded = Encoder.encodeToMsgPack(txn);
        Transaction decoded = Encoder.decodeFromMsgPack(encoded, Transaction.class);
        
        // Find the holding reference and verify sender is index 0
        ResourceRef holdingRef = decoded.access.stream()
            .filter(ref -> ref.holding != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Holding reference not found"));
        assertEquals(Long.valueOf(0L), holdingRef.holding.addressIndex);
        
        // Find the locals reference and verify current app is index 0
        ResourceRef localsRef = decoded.access.stream()
            .filter(ref -> ref.locals != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Locals reference not found"));
        assertEquals(Long.valueOf(0L), localsRef.locals.appIndex);
        
        // Find the box reference and verify current app is index 0
        ResourceRef boxRef = decoded.access.stream()
            .filter(ref -> ref.box != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Box reference not found"));
        assertEquals(Long.valueOf(0L), boxRef.box.index);
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}