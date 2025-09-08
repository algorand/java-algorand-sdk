package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AppResourceRef;
import com.algorand.algosdk.transaction.ResourceRef;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates the useAccess flag for handling consensus upgrade compatibility.
 * 
 * The useAccess flag allows applications to choose between:
 * - useAccess=false: Legacy fields (accounts, foreignApps, foreignAssets, boxReferences) 
 * - useAccess=true: New access field with unified resource references
 */
public class UseAccessFlagExample {
    
    public static void main(String[] args) throws Exception {
        Account sender = new Account();
        Account otherAccount = new Account();
        
        System.out.println("=== useAccess Flag Examples ===\n");
        
        // Example 1: Legacy mode (useAccess=false, default)
        demonstrateLegacyMode(sender, otherAccount);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 2: Access field mode (useAccess=true)
        demonstrateAccessFieldMode(sender, otherAccount);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 3: Error cases
        demonstrateValidationErrors(sender, otherAccount);
    }
    
    private static void demonstrateLegacyMode(Account sender, Account otherAccount) throws Exception {
        System.out.println("Example 1: Legacy Mode (useAccess=false)");
        System.out.println("-----------------------------------------");
        System.out.println("✓ Compatible with pre-consensus upgrade networks");
        System.out.println("✓ Uses separate accounts, foreignApps, foreignAssets fields");
        System.out.println("✗ Cannot use access field or appResourceRefs");
        
        // Build transaction using legacy fields (default behavior)
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender.getAddress())
                .applicationId(12345L)
                .useAccess(false)  // Explicitly set to false (though it's the default)
                .accounts(Arrays.asList(otherAccount.getAddress()))
                .foreignApps(Arrays.asList(67890L))
                .foreignAssets(Arrays.asList(999L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();
        
        // Verify legacy fields are used
        System.out.printf("Legacy fields populated:%n");
        System.out.printf("  - accounts: %d entries%n", txn.accounts.size());
        System.out.printf("  - foreignApps: %d entries%n", txn.foreignApps.size()); 
        System.out.printf("  - foreignAssets: %d entries%n", txn.foreignAssets.size());
        System.out.printf("  - access field: %d entries (empty)%n", txn.access.size());
        
        // Serialize to verify compatibility
        byte[] encoded = Encoder.encodeToMsgPack(txn);
        System.out.printf("MessagePack size: %d bytes%n", encoded.length);
        System.out.println("✓ Ready for pre-consensus upgrade networks");
    }
    
    private static void demonstrateAccessFieldMode(Account sender, Account otherAccount) throws Exception {
        System.out.println("Example 2: Access Field Mode (useAccess=true)");
        System.out.println("----------------------------------------------");
        System.out.println("✓ Compatible with post-consensus upgrade networks");
        System.out.println("✓ Uses unified access field for all resources");
        System.out.println("✓ Supports advanced features like holding/locals references");
        System.out.println("✗ Cannot use legacy separate fields");
        
        // Build transaction using access field with AppResourceRef API
        List<AppResourceRef> appResourceRefs = Arrays.asList(
            AppResourceRef.forAddress(otherAccount.getAddress()),
            AppResourceRef.forAsset(999L),
            AppResourceRef.forApp(67890L),
            AppResourceRef.forHolding(sender.getAddress(), 999L),      // Advanced: asset holding
            AppResourceRef.forLocals(sender.getAddress(), 12345L),     // Advanced: local state
            AppResourceRef.forBox(12345L, "shared-box".getBytes())     // Advanced: box reference
        );
        
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender.getAddress())
                .applicationId(12345L)
                .useAccess(true)  // Enable access field mode
                .appResourceRefs(appResourceRefs)
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();
        
        // Verify access field is used
        System.out.printf("Access field populated:%n");
        System.out.printf("  - access entries: %d%n", txn.access.size());
        System.out.printf("  - accounts: %d entries (empty)%n", txn.accounts.size());
        System.out.printf("  - foreignApps: %d entries (empty)%n", txn.foreignApps.size());
        System.out.printf("  - foreignAssets: %d entries (empty)%n", txn.foreignAssets.size());
        
        // Show access field details
        System.out.println("Access field contents:");
        for (int i = 0; i < txn.access.size(); i++) {
            System.out.printf("  [%d] %s%n", i, formatAccessEntry(txn.access.get(i)));
        }
        
        // Serialize to verify compatibility
        byte[] encoded = Encoder.encodeToMsgPack(txn);
        System.out.printf("MessagePack size: %d bytes%n", encoded.length);
        System.out.println("✓ Ready for post-consensus upgrade networks");
    }
    
    private static void demonstrateValidationErrors(Account sender, Account otherAccount) throws Exception {
        System.out.println("Example 3: Validation Error Cases");
        System.out.println("----------------------------------");
        
        // Error case 1: useAccess=true with legacy fields
        System.out.println("Error case 1: useAccess=true with legacy fields");
        try {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender.getAddress())
                    .applicationId(12345L)
                    .useAccess(true)
                    .accounts(Arrays.asList(otherAccount.getAddress()))  // ❌ Not allowed
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
            System.out.println("❌ Should have thrown exception!");
        } catch (IllegalArgumentException e) {
            System.out.printf("✓ Correctly rejected: %s%n", e.getMessage());
        }
        
        // Error case 2: useAccess=false with appResourceRefs
        System.out.println("Error case 2: useAccess=false with appResourceRefs");
        try {
            ApplicationCallTransactionBuilder.Builder()
                    .sender(sender.getAddress())
                    .applicationId(12345L)
                    .useAccess(false)
                    .appResourceRefs(Arrays.asList(AppResourceRef.forAsset(999L)))  // ❌ Not allowed
                    .firstValid(BigInteger.valueOf(1000))
                    .lastValid(BigInteger.valueOf(2000))
                    .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                    .build();
            System.out.println("❌ Should have thrown exception!");
        } catch (IllegalArgumentException e) {
            System.out.printf("✓ Correctly rejected: %s%n", e.getMessage());
        }
        
        System.out.println("\n✓ All validation rules working correctly");
    }
    
    private static String formatAccessEntry(ResourceRef ref) {
        if (ref.address != null) {
            return "Address: " + ref.address.toString().substring(0, 10) + "...";
        }
        if (ref.asset != null) {
            return "Asset: " + ref.asset;
        }
        if (ref.app != null) {
            return "App: " + ref.app;
        }
        if (ref.holding != null) {
            return String.format("Holding: addr_idx=%d, asset_idx=%d", 
                ref.holding.addressIndex, ref.holding.assetIndex);
        }
        if (ref.locals != null) {
            return String.format("Locals: addr_idx=%d, app_idx=%d",
                ref.locals.addressIndex, ref.locals.appIndex);
        }
        if (ref.box != null) {
            return String.format("Box: app_idx=%d, name='%s'",
                ref.box.index, new String(ref.box.name));
        }
        return "Empty";
    }
}