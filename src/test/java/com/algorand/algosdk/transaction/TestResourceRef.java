package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestResourceRef {

    @Test
    public void testResourceRefForAddress() throws NoSuchAlgorithmException {
        Address addr = new Address("XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA");
        ResourceRef ref = ResourceRef.forAddress(addr);
        
        assertNotNull(ref.address);
        assertEquals(addr, ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefForAsset() {
        ResourceRef ref = ResourceRef.forAsset(123L);
        
        assertNull(ref.address);
        assertEquals(Long.valueOf(123L), ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefForApp() {
        ResourceRef ref = ResourceRef.forApp(456L);
        
        assertNull(ref.address);
        assertNull(ref.asset);
        assertEquals(Long.valueOf(456L), ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefForHolding() {
        // Index-based approach: addressIndex=0 (sender), assetIndex=1 (first asset in access list)
        ResourceRef.HoldingRef holding = new ResourceRef.HoldingRef(0L, 1L);
        ResourceRef ref = ResourceRef.forHolding(holding);
        
        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNotNull(ref.holding);
        assertEquals(0L, ref.holding.addressIndex);
        assertEquals(1L, ref.holding.assetIndex);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefForLocals() {
        // Index-based approach: addressIndex=1, appIndex=0 (current app)
        ResourceRef.LocalsRef locals = new ResourceRef.LocalsRef(1L, 0L);
        ResourceRef ref = ResourceRef.forLocals(locals);

        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNotNull(ref.locals);
        assertEquals(1L, ref.locals.addressIndex);
        assertEquals(0L, ref.locals.appIndex);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testAllZeroCompoundRefsCollapseToEmpty() {
        // An all-zero holding/locals or a zero-index empty-name box is
        // canonically the fully-empty reference
        assertTrue(ResourceRef.forLocals(new ResourceRef.LocalsRef(0L, 0L)).isEmpty());
        assertTrue(ResourceRef.forHolding(new ResourceRef.HoldingRef(0L, 0L)).isEmpty());
        assertTrue(ResourceRef.forBox(new ResourceRef.BoxRef(0L, new byte[0])).isEmpty());
        assertTrue(ResourceRef.forBox(new ResourceRef.BoxRef(0L, null)).isEmpty());

        // Any non-zero component keeps the compound reference
        assertFalse(ResourceRef.forLocals(new ResourceRef.LocalsRef(1L, 0L)).isEmpty());
        assertFalse(ResourceRef.forHolding(new ResourceRef.HoldingRef(0L, 1L)).isEmpty());
        assertFalse(ResourceRef.forBox(new ResourceRef.BoxRef(0L, "n".getBytes())).isEmpty());
        assertFalse(ResourceRef.forBox(new ResourceRef.BoxRef(3L, new byte[0])).isEmpty());
    }

    @Test
    public void testResourceRefForBox() {
        byte[] boxName = "test-box".getBytes();
        ResourceRef.BoxRef box = new ResourceRef.BoxRef(0L, boxName); // Use index 0 for current app
        ResourceRef ref = ResourceRef.forBox(box);
        
        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNotNull(ref.box);
        assertEquals(0L, ref.box.index);
        assertArrayEquals(boxName, ref.box.name);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testEmptyResourceRefViaDefaultConstructor() {
        ResourceRef ref = new ResourceRef();

        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertTrue(ref.isEmpty());
    }

    @Test
    public void testEmptyResourceRefViaJsonCreatorStaysEmpty() {
        // Empty ResourceRef via JsonCreator (deserialization) stays empty so it
        // re-encodes canonically as an empty map
        ResourceRef ref = new ResourceRef(null, null, null, null, null, null);

        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertTrue(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testEmptyResourceRefIsValid() {
        // An empty ResourceRef is meaningful: it requests a box I/O quota bump
        // without naming a resource
        ResourceRef ref = ResourceRef.forEmpty();

        assertTrue(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefValidationFailsWhenMultipleSet() throws NoSuchAlgorithmException {
        ResourceRef ref = new ResourceRef();
        ref.address = new Address("XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA");
        ref.asset = 123L;
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, ref::validate);
        assertEquals("ResourceRef can only have one resource type set", exception.getMessage());
    }

    @Test
    public void testHoldingRefConstructorAndMethods() {
        // Index-based approach: addressIndex=2, assetIndex=3 (arbitrary indices)
        ResourceRef.HoldingRef holding = new ResourceRef.HoldingRef(2L, 3L);
        
        assertEquals(2L, holding.addressIndex);
        assertEquals(3L, holding.assetIndex);
        
        // Test equality
        ResourceRef.HoldingRef holding2 = new ResourceRef.HoldingRef(2L, 3L);
        assertEquals(holding, holding2);
        assertEquals(holding.hashCode(), holding2.hashCode());
        
        // Test toString
        assertTrue(holding.toString().contains("HoldingRef"));
    }

    @Test
    public void testLocalsRefConstructorAndMethods() {
        // Index-based approach: addressIndex=1, appIndex=2 (arbitrary indices)
        ResourceRef.LocalsRef locals = new ResourceRef.LocalsRef(1L, 2L);
        
        assertEquals(1L, locals.addressIndex);
        assertEquals(2L, locals.appIndex);
        
        // Test equality
        ResourceRef.LocalsRef locals2 = new ResourceRef.LocalsRef(1L, 2L);
        assertEquals(locals, locals2);
        assertEquals(locals.hashCode(), locals2.hashCode());
        
        // Test toString
        assertTrue(locals.toString().contains("LocalsRef"));
    }

    @Test
    public void testBoxRefConstructorAndMethods() {
        byte[] boxName = "my-box".getBytes();
        ResourceRef.BoxRef box = new ResourceRef.BoxRef(101L, boxName);
        
        assertEquals(101L, box.index);
        assertArrayEquals(boxName, box.name);
        assertArrayEquals(boxName, box.getName()); // Test getter makes defensive copy
        
        // Test equality
        ResourceRef.BoxRef box2 = new ResourceRef.BoxRef(101L, "my-box".getBytes());
        assertEquals(box, box2);
        assertEquals(box.hashCode(), box2.hashCode());
        
        // Test toString
        assertTrue(box.toString().contains("BoxRef"));
    }

    @Test
    public void testBoxRefWithNullName() {
        ResourceRef.BoxRef box = new ResourceRef.BoxRef(102L, null);

        assertEquals(102L, box.index);
        // Empty names are stored as null so the canonical encoding omits them
        assertNull(box.name);
        assertArrayEquals(new byte[0], box.getName());
    }

    @Test
    public void testResourceRefEquality() throws NoSuchAlgorithmException {
        Address addr = new Address("XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA");
        
        ResourceRef ref1 = ResourceRef.forAddress(addr);
        ResourceRef ref2 = ResourceRef.forAddress(addr);
        ResourceRef ref3 = ResourceRef.forAsset(123L);
        
        assertEquals(ref1, ref2);
        assertEquals(ref1.hashCode(), ref2.hashCode());
        assertNotEquals(ref1, ref3);
        assertNotEquals(ref1.hashCode(), ref3.hashCode());
        
        // Test toString
        assertTrue(ref1.toString().contains("ResourceRef"));
    }

    @Test
    public void testResourceRefEqualityWithNull() throws NoSuchAlgorithmException {
        ResourceRef ref = ResourceRef.forAsset(123L);

        assertNotEquals(ref, null);
        assertNotEquals(ref, "not a ResourceRef");
        assertEquals(ref, ref); // self equality
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Test
    public void testGoldenAccessRefEncodings() throws Exception {
        // Canonical msgpack encodings, matching go-algorand's omitempty structs
        // and the python SDK's golden vectors: zero indices and empty names are
        // never emitted.

        // empty reference -> empty map
        assertEquals("80", hex(Encoder.encodeToMsgPack(ResourceRef.forEmpty())));

        // holding(sender, asset at index 1) -> {"h":{"s":1}}
        assertEquals("81a16881a17301", hex(Encoder.encodeToMsgPack(
                ResourceRef.forHolding(new ResourceRef.HoldingRef(0L, 1L)))));

        // locals(address at index 1, current app) -> {"l":{"d":1}}
        assertEquals("81a16c81a16401", hex(Encoder.encodeToMsgPack(
                ResourceRef.forLocals(new ResourceRef.LocalsRef(1L, 0L)))));

        // locals(address at index 1, app at index 2) -> {"l":{"d":1,"p":2}}
        assertEquals("81a16c82a16401a17002", hex(Encoder.encodeToMsgPack(
                ResourceRef.forLocals(new ResourceRef.LocalsRef(1L, 2L)))));

        // box(current app, "name") -> {"b":{"n":"name"}}
        assertEquals("81a16281a16ec4046e616d65", hex(Encoder.encodeToMsgPack(
                ResourceRef.forBox(new ResourceRef.BoxRef(0L, "name".getBytes())))));

        // box(app at index 3, empty name) -> {"b":{"i":3}}
        assertEquals("81a16281a16903", hex(Encoder.encodeToMsgPack(
                ResourceRef.forBox(new ResourceRef.BoxRef(3L, new byte[0])))));

        // box(current app, empty name) collapses to the empty reference
        assertEquals("80", hex(Encoder.encodeToMsgPack(
                ResourceRef.forBox(new ResourceRef.BoxRef(0L, new byte[0])))));

        // Standalone zero values are omitted too: go-algorand's ResourceRef is
        // tagged omitempty, so a zero app/asset/address is the empty reference
        assertEquals("80", hex(Encoder.encodeToMsgPack(ResourceRef.forApp(0L))));
        assertEquals("80", hex(Encoder.encodeToMsgPack(ResourceRef.forAsset(0L))));
        assertEquals("80", hex(Encoder.encodeToMsgPack(ResourceRef.forAddress(new Address()))));
        assertEquals("80", hex(Encoder.encodeToMsgPack(ResourceRef.forAddress(null))));

        // Non-zero standalone values still encode
        assertEquals("81a17007", hex(Encoder.encodeToMsgPack(ResourceRef.forApp(7L))));
        assertEquals("81a17307", hex(Encoder.encodeToMsgPack(ResourceRef.forAsset(7L))));
    }

    @Test
    public void testAccessRefDecodeReEncodeIdentity() throws Exception {
        // Decoded references must re-encode to identical bytes
        for (String golden : new String[]{
                "80",                     // {}
                "81a16281a16903",         // {"b":{"i":3}}
                "81a16c81a16401",         // {"l":{"d":1}}
                "81a16881a17301",         // {"h":{"s":1}}
        }) {
            byte[] bytes = new byte[golden.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(golden.substring(2 * i, 2 * i + 2), 16);
            }
            ResourceRef decoded = Encoder.decodeFromMsgPack(
                    com.algorand.algosdk.util.Encoder.encodeToBase64(bytes), ResourceRef.class);
            assertEquals(golden, hex(Encoder.encodeToMsgPack(decoded)));
        }
    }

    @Test
    public void testHandleEmptyResourceReferencesInAccessList() throws Exception {
        // Create a transaction with empty ResourceRef objects in the access list
        Address from = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = com.algorand.algosdk.util.Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");

        Transaction tx = Transaction.ApplicationCallTransactionBuilder()
                .sender(from)
                .applicationId(111L)
                .firstValid(322575)
                .lastValid(322575)
                .genesisHash(gh)
                .build();

        // Add an empty ResourceRef to the access list
        java.util.List<ResourceRef> accessList = new java.util.ArrayList<>();
        accessList.add(ResourceRef.forAddress(from));
        accessList.add(ResourceRef.forEmpty());
        accessList.add(ResourceRef.forAsset(123L));
        tx.access = accessList;

        assertTrue(tx.access.get(1).isEmpty());

        // Encode and decode: the empty reference must survive the round trip and
        // re-encode to identical bytes (empty refs encode as an empty map)
        String encoded = com.algorand.algosdk.util.Encoder.encodeToBase64(
            com.algorand.algosdk.util.Encoder.encodeToMsgPack(tx));
        Transaction decoded = com.algorand.algosdk.util.Encoder.decodeFromMsgPack(
            encoded, Transaction.class);

        assertNotNull(decoded.access);
        assertEquals(3, decoded.access.size());
        assertTrue(decoded.access.get(1).isEmpty());

        String reEncoded = com.algorand.algosdk.util.Encoder.encodeToBase64(
            com.algorand.algosdk.util.Encoder.encodeToMsgPack(decoded));
        assertEquals(encoded, reEncoded);
    }
}