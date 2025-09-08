package com.algorand.algosdk.transaction;

import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransactionAccess {

    private static final String SENDER_ADDR = "XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA";
    private static final String ACCOUNT_ADDR = "47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU";

    @Test
    public void testTransactionWithAppResourceRefs() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // Create high-level AppResourceRef list (using current app for box reference)
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(account),
            AppResourceRef.forAsset(123L),
            AppResourceRef.forApp(456L),
            AppResourceRef.forBox(1001L, "test-box".getBytes()) // Use current app ID
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        assertEquals(4, txn.access.size());
        
        // Verify each access entry has been converted to index-based format
        assertEquals(account, txn.access.get(0).address);
        assertEquals(Long.valueOf(123L), txn.access.get(1).asset);
        assertEquals(Long.valueOf(456L), txn.access.get(2).app);
        assertEquals(Long.valueOf(0L), txn.access.get(3).box.index); // Box references current app (index 0)
        assertArrayEquals("test-box".getBytes(), txn.access.get(3).box.name);
        
        // Verify traditional fields are empty
        assertTrue(txn.accounts.isEmpty());
        assertTrue(txn.foreignApps.isEmpty());
        assertTrue(txn.foreignAssets.isEmpty());
        assertTrue(txn.boxReferences.isEmpty());
    }

    @Test
    public void testTransactionAccessWithHoldingAndLocals() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // Create high-level AppResourceRef for holding and locals
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forHolding(account, 555L),
            AppResourceRef.forLocals(account, 1002L) // Use current app ID
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1002L)
                .useAccess(true)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        // Should have address, asset, holding, locals entries due to conversion
        assertTrue(txn.access.size() >= 2);
        
        // Find holding and locals references in the converted access list
        ResourceRef holdingRef = txn.access.stream()
            .filter(ref -> ref.holding != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Holding reference not found"));
            
        ResourceRef localsRef = txn.access.stream()
            .filter(ref -> ref.locals != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Locals reference not found"));
        
        // Verify holding reference uses indices (account might not be sender)
        assertNotNull(holdingRef.holding);
        // The address index should be 1 since the account is added to the access list
        assertEquals(Long.valueOf(1L), holdingRef.holding.addressIndex);
        assertTrue(holdingRef.holding.assetIndex > 0); // asset should have non-zero index
        
        // Verify locals reference uses indices
        assertNotNull(localsRef.locals);
        assertEquals(Long.valueOf(1L), localsRef.locals.addressIndex); // account is index 1  
        assertEquals(Long.valueOf(0L), localsRef.locals.appIndex); // current app is index 0
    }

    @Test
    public void testTransactionBuilderRejectsAppResourceRefsWithLegacyFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(account)
        );

        // Should throw when trying to use appResourceRefs with accounts and useAccess=true
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1003L)
                    .useAccess(true)
                    .appResourceRefs(appResourceRefs)
                    .accounts(Arrays.asList(account)) // This should conflict
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertTrue(exception.getMessage().contains("Cannot use legacy accounts, foreignApps, foreignAssets, or boxReferences when useAccess=true"));
    }

    @Test
    public void testTransactionBuilderRejectsAppResourceRefsWithForeignApps() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forApp(777L)
        );

        // Should throw when trying to use appResourceRefs with foreignApps and useAccess=true
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1004L)
                    .useAccess(true)
                    .appResourceRefs(appResourceRefs)
                    .foreignApps(Arrays.asList(777L)) // This should conflict
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertTrue(exception.getMessage().contains("Cannot use legacy accounts, foreignApps, foreignAssets, or boxReferences when useAccess=true"));
    }

    @Test
    public void testTransactionBuilderRejectsInvalidResourceRef() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        // Create invalid ResourceRef with multiple fields set
        ResourceRef invalidRef = new ResourceRef();
        invalidRef.asset = 123L;
        invalidRef.app = 456L; // Should not be allowed
        
        List<ResourceRef> accessList = Arrays.asList(invalidRef);

        // Should throw when trying to build with invalid ResourceRef
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1005L)
                    .useAccess(true)
                    .access(accessList)
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertEquals("ResourceRef can only have one resource type set", exception.getMessage());
    }

    @Test
    public void testTransactionSerializationWithAppResourceRefs() throws Exception {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(account),
            AppResourceRef.forAsset(888L)
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1006L)
                .useAccess(true)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Test that transaction can be serialized (encoding works)
        byte[] encoded = Encoder.encodeToMsgPack(txn);
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);

        // Verify access field is set correctly in transaction with converted values
        assertNotNull(txn.access);
        assertEquals(2, txn.access.size());
        assertEquals(account, txn.access.get(0).address);
        assertEquals(Long.valueOf(888L), txn.access.get(1).asset);
        
        // Verify deserialization works
        Transaction decoded = Encoder.decodeFromMsgPack(encoded, Transaction.class);
        assertNotNull(decoded);
        assertNotNull(decoded.access);
        assertEquals(2, decoded.access.size());
        assertEquals(account, decoded.access.get(0).address);
        assertEquals(Long.valueOf(888L), decoded.access.get(1).asset);
    }

    @Test
    public void testEmptyAppResourceRefsList() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // Empty appResourceRefs list should work fine
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1007L)
                .useAccess(true)
                .appResourceRefs(Arrays.asList()) // Empty list
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        assertTrue(txn.access.isEmpty());
    }

    @Test
    public void testLegacyFieldsStillWorkWithoutAccess() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Traditional approach should still work when access is not used
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1008L)
                .accounts(Arrays.asList(account))
                .foreignApps(Arrays.asList(999L))
                .foreignAssets(Arrays.asList(111L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Verify traditional fields work
        assertEquals(1, txn.accounts.size());
        assertEquals(account, txn.accounts.get(0));
        assertEquals(1, txn.foreignApps.size());
        assertEquals(Long.valueOf(999L), txn.foreignApps.get(0));
        assertEquals(1, txn.foreignAssets.size());
        assertEquals(Long.valueOf(111L), txn.foreignAssets.get(0));
        
        // Access field should be empty
        assertTrue(txn.access.isEmpty());
    }

    @Test
    public void testLowLevelResourceRefAPI() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Test low-level ResourceRef API with index-based references
        List<ResourceRef> accessList = Arrays.asList(
            ResourceRef.forAddress(account),
            ResourceRef.forAsset(123L),
            ResourceRef.forApp(456L),
            ResourceRef.forBox(new ResourceRef.BoxRef(0L, "test-box".getBytes())),
            ResourceRef.forHolding(new ResourceRef.HoldingRef(0L, 1L)), // sender holding asset at index 1
            ResourceRef.forLocals(new ResourceRef.LocalsRef(0L, 0L))    // sender locals for current app
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1009L)
                .useAccess(true)
                .access(accessList)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        assertEquals(6, txn.access.size());
        
        // Verify each access entry maintains index-based format
        assertEquals(account, txn.access.get(0).address);
        assertEquals(Long.valueOf(123L), txn.access.get(1).asset);
        assertEquals(Long.valueOf(456L), txn.access.get(2).app);
        
        // Verify box reference
        assertEquals(Long.valueOf(0L), txn.access.get(3).box.index);
        assertArrayEquals("test-box".getBytes(), txn.access.get(3).box.name);
        
        // Verify holding reference indices
        assertEquals(Long.valueOf(0L), txn.access.get(4).holding.addressIndex);
        assertEquals(Long.valueOf(1L), txn.access.get(4).holding.assetIndex);
        
        // Verify locals reference indices
        assertEquals(Long.valueOf(0L), txn.access.get(5).locals.addressIndex);
        assertEquals(Long.valueOf(0L), txn.access.get(5).locals.appIndex);
    }

    @Test
    public void testIndexConversionSpecialCases() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address otherAccount = new Address(ACCOUNT_ADDR);
        
        // Test index 0 special cases: sender for address, current app for app
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forHolding(sender, 999L),        // sender address should become index 0
            AppResourceRef.forLocals(otherAccount, 1010L),  // current app should become index 0
            AppResourceRef.forBox(1010L, "special-box".getBytes()) // current app box
        );

        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1010L) // This is the current app
                .useAccess(true)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        
        // Find the holding reference - sender should be index 0
        ResourceRef holdingRef = txn.access.stream()
            .filter(ref -> ref.holding != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Holding reference not found"));
        assertEquals(Long.valueOf(0L), holdingRef.holding.addressIndex); // sender is index 0
        
        // Find the locals reference - current app should be index 0
        ResourceRef localsRef = txn.access.stream()
            .filter(ref -> ref.locals != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Locals reference not found"));
        assertEquals(Long.valueOf(0L), localsRef.locals.appIndex); // current app is index 0
        
        // Find the box reference - current app should be index 0
        ResourceRef boxRef = txn.access.stream()
            .filter(ref -> ref.box != null)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Box reference not found"));
        assertEquals(Long.valueOf(0L), boxRef.box.index); // current app is index 0
    }

    @Test
    public void testMixedAccessAndAppResourceRefsRejection() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        List<ResourceRef> accessList = Arrays.asList(ResourceRef.forAsset(111L));
        List<AppResourceRef> appResourceRefs = Arrays.asList(AppResourceRef.forAsset(222L));

        // Should throw when trying to use both access and appResourceRefs
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1011L)
                    .useAccess(true)
                    .access(accessList)
                    .appResourceRefs(appResourceRefs) // This should conflict
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertTrue(exception.getMessage().contains("Cannot use both AppResourceRef and ResourceRef access methods"));
    }

    @Test
    public void testUseAccessTrueWithAppResourceRefs() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(account),
            AppResourceRef.forAsset(999L)
        );

        // Should work when useAccess=true
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1012L)
                .useAccess(true)
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        assertEquals(2, txn.access.size());
        
        // Verify legacy fields are empty
        assertTrue(txn.accounts.isEmpty());
        assertTrue(txn.foreignApps.isEmpty());
        assertTrue(txn.foreignAssets.isEmpty());
        assertTrue(txn.boxReferences.isEmpty());
    }

    @Test
    public void testUseAccessFalseWithLegacyFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Should work when useAccess=false (default)
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1013L)
                .useAccess(false)
                .accounts(Arrays.asList(account))
                .foreignApps(Arrays.asList(999L))
                .foreignAssets(Arrays.asList(777L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Verify legacy fields are set
        assertEquals(1, txn.accounts.size());
        assertEquals(account, txn.accounts.get(0));
        assertEquals(1, txn.foreignApps.size());
        assertEquals(Long.valueOf(999L), txn.foreignApps.get(0));
        assertEquals(1, txn.foreignAssets.size());
        assertEquals(Long.valueOf(777L), txn.foreignAssets.get(0));
        
        // Verify access field is empty
        assertTrue(txn.access.isEmpty());
    }

    @Test
    public void testUseAccessDefaultIsFalse() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Default behavior (no useAccess call) should be useAccess=false
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1014L)
                .accounts(Arrays.asList(account))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Should work like useAccess=false
        assertEquals(1, txn.accounts.size());
        assertTrue(txn.access.isEmpty());
    }

    @Test
    public void testUseAccessTrueRejectsLegacyFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Should throw when trying to use legacy fields with useAccess=true
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1015L)
                    .useAccess(true)
                    .accounts(Arrays.asList(account)) // This should conflict
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertTrue(exception.getMessage().contains("Cannot use legacy accounts, foreignApps, foreignAssets, or boxReferences when useAccess=true"));
    }

    @Test
    public void testUseAccessFalseRejectsAccessFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAsset(123L)
        );

        // Should throw when trying to use access fields with useAccess=false
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender)
                    .applicationId(1016L)
                    .useAccess(false)
                    .appResourceRefs(appResourceRefs) // This should conflict
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
        });
        
        assertTrue(exception.getMessage().contains("Cannot use access fields or appResourceRefs when useAccess=false"));
    }

    @Test
    public void testUseAccessTrueWithDirectResourceRefs() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        List<ResourceRef> accessList = Arrays.asList(
            ResourceRef.forAddress(account),
            ResourceRef.forAsset(555L)
        );

        // Should work when useAccess=true with direct ResourceRef
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1017L)
                .useAccess(true)
                .access(accessList)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertNotNull(txn.access);
        assertEquals(2, txn.access.size());
        assertEquals(account, txn.access.get(0).address);
        assertEquals(Long.valueOf(555L), txn.access.get(1).asset);
        
        // Verify legacy fields are empty
        assertTrue(txn.accounts.isEmpty());
        assertTrue(txn.foreignApps.isEmpty());
        assertTrue(txn.foreignAssets.isEmpty());
    }
}