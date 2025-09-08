package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
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
        assertEquals(Long.valueOf(0L), ref.holding.addressIndex);
        assertEquals(Long.valueOf(1L), ref.holding.assetIndex);
        assertNull(ref.locals);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testResourceRefForLocals() {
        // Index-based approach: addressIndex=0 (sender), appIndex=0 (current app)
        ResourceRef.LocalsRef locals = new ResourceRef.LocalsRef(0L, 0L);
        ResourceRef ref = ResourceRef.forLocals(locals);
        
        assertNull(ref.address);
        assertNull(ref.asset);
        assertNull(ref.app);
        assertNull(ref.holding);
        assertNotNull(ref.locals);
        assertEquals(Long.valueOf(0L), ref.locals.addressIndex);
        assertEquals(Long.valueOf(0L), ref.locals.appIndex);
        assertNull(ref.box);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
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
        assertEquals(Long.valueOf(0L), ref.box.index);
        assertArrayEquals(boxName, ref.box.name);
        assertFalse(ref.isEmpty());
        assertDoesNotThrow(ref::validate);
    }

    @Test
    public void testEmptyResourceRef() {
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
    public void testResourceRefValidationFailsWhenEmpty() {
        ResourceRef ref = new ResourceRef();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, ref::validate);
        assertEquals("ResourceRef must have one resource type set", exception.getMessage());
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
        
        assertEquals(Long.valueOf(2L), holding.addressIndex);
        assertEquals(Long.valueOf(3L), holding.assetIndex);
        
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
        
        assertEquals(Long.valueOf(1L), locals.addressIndex);
        assertEquals(Long.valueOf(2L), locals.appIndex);
        
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
        
        assertEquals(Long.valueOf(101L), box.index);
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
        
        assertEquals(Long.valueOf(102L), box.index);
        assertArrayEquals(new byte[0], box.name);
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
}