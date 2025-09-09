package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.ResourceRef;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates the useAccess flag for handling consensus upgrade compatibility.
 * 
 * The useAccess flag controls how foreign references are handled:
 * - useAccess=false: Uses legacy fields (accounts, foreignApps, foreignAssets, boxReferences) 
 * - useAccess=true: Translates the same references into a unified access field
 * 
 * Key insight: You use the SAME builder methods in both modes - just add .useAccess(true)!
 * No API changes needed for migration, making upgrades simple and safe.
 */
public class UseAccessFlagExample {
    
    public static void main(String[] args) throws Exception {
        Account sender = new Account();
        Account otherAccount = new Account();
        
        System.out.println("=== useAccess Flag Examples ===\n");
        System.out.println("Shows how easy it is to migrate to access field mode:");
        System.out.println("Just add .useAccess(true) - no other code changes needed!\n");
        
        // Example 1: Legacy mode (useAccess=false, default)
        demonstrateLegacyMode(sender, otherAccount);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 2: Same code with useAccess=true - easy migration!
        demonstrateEasyMigration(sender, otherAccount);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        System.out.println("\n🎉 That's it! Migration to access field mode is just one line.");
        System.out.println("Advanced features like holdings() and locals() are also available with useAccess=true.");
    }
    
    private static void demonstrateLegacyMode(Account sender, Account otherAccount) throws Exception {
        System.out.println("Example 1: Legacy Mode (useAccess=false)");
        System.out.println("-----------------------------------------");
        System.out.println("Using standard foreign reference methods with legacy field output");
        
        // Build transaction using foreign reference methods (default behavior)
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender.getAddress())
                .applicationId(12345L)
                .useAccess(false)  // Default mode - puts references in separate fields
                .accounts(Arrays.asList(otherAccount.getAddress()))
                .foreignApps(Arrays.asList(67890L))
                .foreignAssets(Arrays.asList(999L))
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();
        
        // Show how references are stored in separate legacy fields
        System.out.printf("Result: References stored in separate fields%n");
        System.out.printf("  - accounts: %d entries%n", txn.accounts.size());
        System.out.printf("  - foreignApps: %d entries%n", txn.foreignApps.size()); 
        System.out.printf("  - foreignAssets: %d entries%n", txn.foreignAssets.size());
        System.out.printf("  - access field: %d entries (empty)%n", txn.access.size());
        System.out.println("✓ Compatible with pre-consensus upgrade networks");
    }
    
    private static void demonstrateEasyMigration(Account sender, Account otherAccount) throws Exception {
        System.out.println("Example 2: Easy Migration (useAccess=true)");
        System.out.println("-------------------------------------------");
        System.out.println("SAME CODE as Example 1 - just add .useAccess(true)!");
        
        // Build transaction using the EXACT SAME builder method calls as Example 1
        // The only difference is useAccess=true, which translates these into access field
        Transaction txn = ApplicationCallTransactionBuilder.Builder()
                .sender(sender.getAddress())
                .applicationId(12345L)
                .useAccess(true)  // 🔥 ONLY CHANGE: Add this one line!
                .accounts(Arrays.asList(otherAccount.getAddress()))  // Same as Example 1
                .foreignApps(Arrays.asList(67890L))                  // Same as Example 1
                .foreignAssets(Arrays.asList(999L))                  // Same as Example 1
                .firstValid(BigInteger.valueOf(1000))
                .lastValid(BigInteger.valueOf(2000))
                .genesisHash(Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI="))
                .build();
        
        // Show how the same references are now translated to access field
        System.out.printf("Result: Same references, different internal format%n");
        System.out.printf("  - accounts: %d entries (empty - translated to access)%n", txn.accounts.size());
        System.out.printf("  - foreignApps: %d entries (empty - translated to access)%n", txn.foreignApps.size());
        System.out.printf("  - foreignAssets: %d entries (empty - translated to access)%n", txn.foreignAssets.size());
        System.out.printf("  - access field: %d entries (contains all references)%n", txn.access.size());
        
        System.out.println("\n🚀 Migration is just one line: .useAccess(true)");
        System.out.println("✓ No API changes required");
        System.out.println("✓ Same builder methods work in both modes");  
        System.out.println("✓ Ready for post-consensus upgrade networks");
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