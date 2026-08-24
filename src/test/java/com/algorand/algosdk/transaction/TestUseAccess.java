package com.algorand.algosdk.transaction;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
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

    @Test
    public void testZeroLocalAppRefMeansCurrentApp() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // appId 0 in a locals reference refers to the currently executing app
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .locals(Collections.singletonList(
                        new ApplicationBaseTransactionBuilder.LocalsReference(account, 0L)))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Access list: the account plus the locals ref - no app entry should be added for app 0
        assertEquals(2, txn.access.size());
        assertEquals(account, txn.access.get(0).address);
        assertNotNull(txn.access.get(1).locals);
        assertEquals(1L, txn.access.get(1).locals.addressIndex);
        assertEquals(0L, txn.access.get(1).locals.appIndex);
        for (ResourceRef ref : txn.access) {
            assertNull(ref.app, "App 0 must not be added as a standalone app reference");
        }
    }

    @Test
    public void testExplicitSenderAddressIsListedAndReferenced() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // An explicit address, even the sender's own, is listed and referenced
        // by index — only null/zero addresses collapse to index 0 (py/js contract)
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .locals(Collections.singletonList(
                        new ApplicationBaseTransactionBuilder.LocalsReference(sender, 0L)))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(2, txn.access.size());
        assertEquals(sender, txn.access.get(0).address);
        assertNotNull(txn.access.get(1).locals);
        assertEquals(1L, txn.access.get(1).locals.addressIndex);
        assertEquals(0L, txn.access.get(1).locals.appIndex);
    }

    @Test
    public void testNullAddressLocalsCollapsesToSender() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // null address + appId 0 collapses to indices (0, 0), canonically the
        // fully-empty reference, matching go-algorand's omitempty encoding
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .locals(Collections.singletonList(
                        new ApplicationBaseTransactionBuilder.LocalsReference(null, 0L)))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(1, txn.access.size());
        assertTrue(txn.access.get(0).isEmpty());
    }

    @Test
    public void testZeroAddressMeansSender() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // The zero (empty) address means the sender: it collapses to index 0 in
        // compound references and is never listed as a standalone entry
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .accounts(Collections.singletonList(new Address()))
                .locals(Collections.singletonList(
                        new ApplicationBaseTransactionBuilder.LocalsReference(new Address(), 0L)))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // The zero address collapses to sender (index 0), and locals(sender,
        // current app) is all-zero, canonically the fully-empty reference
        assertEquals(1, txn.access.size());
        assertNull(txn.access.get(0).address, "The zero address must not be listed");
        assertTrue(txn.access.get(0).isEmpty());
    }

    @Test
    public void testDuplicateReferencesAreDeduplicated() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Each resource is listed at most once, matching the python SDK's ensure()
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .accounts(Arrays.asList(account, account))
                .foreignAssets(Arrays.asList(55L, 55L))
                .foreignApps(Arrays.asList(77L, 77L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        // Exact order should match the other SDKs (py, go, ts): accounts, assets, apps
        assertEquals(3, txn.access.size());
        assertEquals(account, txn.access.get(0).address);
        assertEquals(Long.valueOf(55L), txn.access.get(1).asset);
        assertEquals(Long.valueOf(77L), txn.access.get(2).app);
    }

    @Test
    public void testHoldingWithZeroAddressReferencesSender() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // A zero-address holding collapses the address to index 0 (sender) and
        // never lists the zero address as a standalone entry
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .holdings(Collections.singletonList(
                        new ApplicationBaseTransactionBuilder.HoldingReference(new Address(), 55L)))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(2, txn.access.size());
        assertEquals(Long.valueOf(55L), txn.access.get(0).asset);
        assertNotNull(txn.access.get(1).holding);
        assertEquals(0L, txn.access.get(1).holding.addressIndex);
        assertEquals(1L, txn.access.get(1).holding.assetIndex);
    }

    @Test
    public void testStandaloneForeignAppZeroCollapsesToEmpty() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // A standalone entry with a zero app id is canonically the fully-empty
        // reference (a box I/O quota bump): go-algorand's ResourceRef omits zero
        // fields, so emitting {"p":0} would break signature verification
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .foreignApps(Collections.singletonList(0L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(1, txn.access.size());
        assertTrue(txn.access.get(0).isEmpty());
    }

    @Test
    public void testEmptyRefsAddedToAccessList() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);
        Address account = new Address(ACCOUNT_ADDR);

        // Empty references request box I/O quota bumps without naming a resource
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .accounts(Collections.singletonList(account))
                .emptyRefs(2)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(3, txn.access.size());
        assertEquals(account, txn.access.get(0).address);
        assertTrue(txn.access.get(1).isEmpty());
        assertTrue(txn.access.get(2).isEmpty());
    }

    @Test
    public void testEmptyRefsRequireUseAccess() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                ApplicationCallTransactionBuilder.Builder()
                        .sender(sender)
                        .applicationId(1001L)
                        .useAccess(false)
                        .emptyRefs(1)
                        .firstValid(BigInteger.valueOf(1000))
                        .lastValid(BigInteger.valueOf(2000))
                        .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                        .build());
        assertTrue(e.getMessage().contains("require useAccess=true"));

        assertThrows(IllegalArgumentException.class, () ->
                ApplicationCallTransactionBuilder.Builder().emptyRefs(-1));
    }

    @Test
    public void testZeroBoxAppRefMeansCurrentApp() throws NoSuchAlgorithmException {
        Address sender = new Address(SENDER_ADDR);

        // appId 0 in a box reference also refers to the currently executing app in access mode
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender)
                .applicationId(1001L)
                .useAccess(true)
                .boxReferences(Collections.singletonList(new AppBoxReference(0L, "box1".getBytes())))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();

        assertEquals(1, txn.access.size());
        assertNotNull(txn.access.get(0).box);
        assertEquals(0L, txn.access.get(0).box.index);
        assertNull(txn.access.get(0).app);
    }
}
