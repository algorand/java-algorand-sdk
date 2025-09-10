package com.algorand.algosdk.transaction;

import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestUseAccess {

    private static final String SENDER_ADDR = "XBYLS2E6YI6XXL5BWCAMOA4GTWHXWENZMX5UHXMRNWWUQ7BXCY5WC5TEPA";
    private static final String ACCOUNT_ADDR = "47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU";

    @Test
    public void testUseAccessDefaultIsFalse() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // Default behavior should be useAccess=false (legacy fields)
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                // No .useAccess() call - should default to false
                .accounts(Collections.singletonList(account))
                .foreignApps(Collections.singletonList(456L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Should use legacy fields
        assertEquals(1, txn.accounts.size());
        assertEquals(account, txn.accounts.get(0));
        assertEquals(1, txn.foreignApps.size());
        assertEquals(Long.valueOf(456L), txn.foreignApps.get(0));
        assertTrue(txn.access.isEmpty());
    }

    @Test
    public void testUseAccessFalseUsesLegacyFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // Explicit useAccess=false should use legacy fields
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(false)
                .accounts(Collections.singletonList(account))
                .foreignApps(Collections.singletonList(456L))
                .foreignAssets(Collections.singletonList(123L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Verify legacy fields are populated
        assertEquals(1, txn.accounts.size());
        assertEquals(account, txn.accounts.get(0));
        assertEquals(1, txn.foreignApps.size());
        assertEquals(Long.valueOf(456L), txn.foreignApps.get(0));
        assertEquals(1, txn.foreignAssets.size());
        assertEquals(Long.valueOf(123L), txn.foreignAssets.get(0));
        
        // Verify access field is empty
        assertTrue(txn.access.isEmpty());
    }

    @Test 
    public void testUseAccessTrueTranslatesFields() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);
        
        // useAccess=true should translate same method calls into access field
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)  // Translation mode
                .accounts(Collections.singletonList(account))
                .foreignApps(Collections.singletonList(456L))
                .foreignAssets(Collections.singletonList(123L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Verify legacy fields are empty (translated)
        assertTrue(txn.accounts.isEmpty());
        assertTrue(txn.foreignApps.isEmpty());
        assertTrue(txn.foreignAssets.isEmpty());
        
        // Verify access field has translated references
        assertNotNull(txn.access);
        assertEquals(3, txn.access.size());
        
        // Check each reference type exists in access field
        boolean foundAccount = false, foundApp = false, foundAsset = false;
        for (ResourceRef ref : txn.access) {
            if (ref.address != null && ref.address.equals(account)) foundAccount = true;
            if (ref.app != null && ref.app.equals(456L)) foundApp = true;
            if (ref.asset != null && ref.asset.equals(123L)) foundAsset = true;
        }
        assertTrue(foundAccount, "Account reference should be in access field");
        assertTrue(foundApp, "App reference should be in access field");
        assertTrue(foundAsset, "Asset reference should be in access field");
    }

    @Test
    public void testBoxReferencesWorkWithBothModes() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        AppBoxReference boxRef = new AppBoxReference(1001L, "test-box".getBytes());
        
        // Test with useAccess=false
        Transaction legacyTxn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(false)
                .boxReferences(Collections.singletonList(boxRef))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(1, legacyTxn.boxReferences.size());
        assertTrue(legacyTxn.access.isEmpty());
        
        // Test with useAccess=true 
        Transaction accessTxn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .boxReferences(Arrays.asList(boxRef))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertTrue(accessTxn.boxReferences.isEmpty());
        assertEquals(1, accessTxn.access.size());
        assertNotNull(accessTxn.access.get(0).box);
    }

    @Test
    public void testMixedReferencesTranslateCorrectly() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account1 = new Address(ACCOUNT_ADDR);
        Address account2 = new Address(SENDER_ADDR);
        
        // Test that multiple references of different types all get translated
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .accounts(Arrays.asList(account1, account2))  // Multiple accounts
                .foreignApps(Arrays.asList(456L, 789L))       // Multiple apps
                .foreignAssets(Arrays.asList(123L, 999L))     // Multiple assets
                .boxReferences(Arrays.asList(new AppBoxReference(1001L, "box1".getBytes())))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // All legacy fields should be empty
        assertTrue(txn.accounts.isEmpty());
        assertTrue(txn.foreignApps.isEmpty());
        assertTrue(txn.foreignAssets.isEmpty());
        assertTrue(txn.boxReferences.isEmpty());
        
        // Access field should contain all references
        assertNotNull(txn.access);
        assertEquals(7, txn.access.size()); // 2 accounts + 2 apps + 2 assets + 1 box = 7
        
        // Verify we have references of each type
        int addressCount = 0, appCount = 0, assetCount = 0, boxCount = 0;
        for (ResourceRef ref : txn.access) {
            if (ref.address != null) addressCount++;
            if (ref.app != null) appCount++;
            if (ref.asset != null) assetCount++;
            if (ref.box != null) boxCount++;
        }
        
        assertEquals(2, addressCount, "Should have 2 address references");
        assertEquals(2, appCount, "Should have 2 app references");  
        assertEquals(2, assetCount, "Should have 2 asset references");
        assertEquals(1, boxCount, "Should have 1 box reference");
    }

    @Test
    public void testEmptyReferencesWork() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        
        // Test both modes with no references
        Transaction legacyTxn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(false)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertTrue(legacyTxn.accounts.isEmpty());
        assertTrue(legacyTxn.access.isEmpty());
        
        Transaction accessTxn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertTrue(accessTxn.accounts.isEmpty());
        assertTrue(accessTxn.access.isEmpty());
    }
}