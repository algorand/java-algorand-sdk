package com.algorand.algosdk.builder.transaction;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AccessConverter;
import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.AppResourceRef;
import com.algorand.algosdk.transaction.ResourceRef;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class ApplicationBaseTransactionBuilder<T extends ApplicationBaseTransactionBuilder<T>> extends TransactionBuilder<T> implements ApplicationCallReferencesSetter<T> {
    
    /**
     * Represents a holding reference for asset holdings of an account.
     */
    public static class HoldingReference {
        public final Address address;
        public final long assetId;
        
        public HoldingReference(Address address, long assetId) {
            this.address = address;
            this.assetId = assetId;
        }
    }
    
    /**
     * Represents a locals reference for local state of an account in an app.
     */
    public static class LocalsReference {
        public final Address address;
        public final long appId;
        
        public LocalsReference(Address address, long appId) {
            this.address = address;
            this.appId = appId;
        }
    }
    private Transaction.OnCompletion onCompletion;
    private List<byte[]> applicationArgs;
    private List<Address> accounts;
    private List<Long> foreignApps;
    private List<Long> foreignAssets;
    private List<AppBoxReference> appBoxReferences;
    private List<HoldingReference> holdings;
    private List<LocalsReference> locals;
    private Long applicationId;
    private boolean useAccess = false;

    /**
     * All application calls use this type, so no need to make this private. This constructor should always be called.
     */
    protected ApplicationBaseTransactionBuilder() {
        super(Transaction.Type.ApplicationCall);
    }

    @Override
    protected void applyTo(Transaction txn) {
        // Global requirements
        Objects.requireNonNull(onCompletion, "OnCompletion is required, please file a bug report.");
        Objects.requireNonNull(applicationId);

        // Check if advanced features are being used
        boolean hasAdvancedFeatures = (holdings != null && !holdings.isEmpty()) ||
                                     (locals != null && !locals.isEmpty());
        
        if (useAccess) {
            // Using access field mode - translate all references into access list
            List<AppResourceRef> allRefs = new ArrayList<>();
            
            // Convert basic foreign references
            if (accounts != null && !accounts.isEmpty()) {
                for (Address account : accounts) {
                    allRefs.add(AppResourceRef.forAddress(account));
                }
            }
            
            if (foreignApps != null && !foreignApps.isEmpty()) {
                for (Long appId : foreignApps) {
                    allRefs.add(AppResourceRef.forApp(appId));
                }
            }
            
            if (foreignAssets != null && !foreignAssets.isEmpty()) {
                for (Long assetId : foreignAssets) {
                    allRefs.add(AppResourceRef.forAsset(assetId));
                }
            }
            
            if (appBoxReferences != null && !appBoxReferences.isEmpty()) {
                for (AppBoxReference boxRef : appBoxReferences) {
                    allRefs.add(AppResourceRef.forBox(boxRef.getAppId(), boxRef.getName()));
                }
            }
            
            // Convert advanced features
            if (holdings != null && !holdings.isEmpty()) {
                for (HoldingReference holdingRef : holdings) {
                    allRefs.add(AppResourceRef.forHolding(holdingRef.address, holdingRef.assetId));
                }
            }
            
            if (locals != null && !locals.isEmpty()) {
                for (LocalsReference localsRef : locals) {
                    allRefs.add(AppResourceRef.forLocals(localsRef.address, localsRef.appId));
                }
            }

            txn.access = AccessConverter.convertToResourceRefs(allRefs, sender, applicationId);
            
        } else {
            // Using legacy fields mode
            if (hasAdvancedFeatures) {
                throw new IllegalArgumentException(
                    "Holdings and locals references require useAccess=true as they cannot be represented in legacy transaction format"
                );
            }
            
            // Use legacy fields directly
            if (accounts != null) txn.accounts = accounts;
            if (foreignApps != null) txn.foreignApps = foreignApps;
            if (foreignAssets != null) txn.foreignAssets = foreignAssets;
            if (appBoxReferences != null) txn.boxReferences = convertBoxes(appBoxReferences, foreignApps, applicationId);
        }

        // Set common fields
        if (applicationId != null) txn.applicationId = applicationId;
        if (onCompletion != null) txn.onCompletion = onCompletion;
        if (applicationArgs != null) txn.applicationArgs = applicationArgs;
    }

    @Override
    public T applicationId(Long applicationId) {
        this.applicationId = applicationId;
        return (T) this;
    }

    /**
     * This is the faux application type used to distinguish different application actions. Specifically, OnCompletion
     * specifies what side effects this transaction will have if it successfully makes it into a block.
     */
    protected T onCompletion(Transaction.OnCompletion onCompletion) {
        this.onCompletion = onCompletion;
        return (T) this;
    }

    /**
     * ApplicationArgs lists some transaction-specific arguments accessible from application logic.
     */
    public T args(List<byte[]> args) {
        this.applicationArgs = args;
        return (T) this;
    }

    /**
     * ApplicationArgs lists some transaction-specific arguments accessible from application logic.
     *
     * @param args List of Base64 encoded strings.
     */
    public T argsBase64Encoded(List<String> args) {
        List<byte[]> decodedArgs = new ArrayList<>();
        for (String arg : args) {
            decodedArgs.add(Encoder.decodeFromBase64(arg));
        }
        return this.args(decodedArgs);
    }

    @Override
    public T accounts(List<Address> accounts) {
        this.accounts = accounts;
        return (T) this;
    }

    @Override
    public T foreignApps(List<Long> foreignApps) {
        this.foreignApps = foreignApps;
        return (T) this;
    }

    @Override
    public T foreignAssets(List<Long> foreignAssets) {
        this.foreignAssets = foreignAssets;
        return (T) this;
    }

    private List<Transaction.BoxReference> convertBoxes(List<AppBoxReference> abrs, List<Long> foreignApps, Long curApp) {
        ArrayList<Transaction.BoxReference> xs = new ArrayList<>();
        for (AppBoxReference abr : abrs) {
            xs.add(Transaction.BoxReference.fromAppBoxReference(abr, foreignApps, curApp));
        }
        return xs;
    }

    public T boxReferences(List<AppBoxReference> boxReferences) {
        this.appBoxReferences = boxReferences;
        return (T) this;
    }

    /**
     * Set asset holding references that need to be accessible in this transaction.
     * Holdings references allow the transaction to access asset balances of specific accounts.
     * 
     * Note: Holdings references are only available when useAccess=true as they cannot be 
     * represented in legacy transaction format.
     */
    public T holdings(List<HoldingReference> holdings) {
        this.holdings = holdings;
        return (T) this;
    }

    /**
     * Set local state references that need to be accessible in this transaction.
     * Locals references allow the transaction to access local state of specific accounts in specific apps.
     * 
     * Note: Locals references are only available when useAccess=true as they cannot be 
     * represented in legacy transaction format.
     */
    public T locals(List<LocalsReference> locals) {
        this.locals = locals;
        return (T) this;
    }

    /**
     * Enable or disable translation of foreign references into the access field.
     * 
     * When useAccess=true:
     * - All foreign references (accounts, foreignApps, foreignAssets, boxReferences) are translated
     *   into a unified access field instead of using separate legacy fields
     * - You can still use the same methods (accounts(), foreignApps(), etc.) - they will be translated
     * - Advanced features (holdings(), locals()) are also available
     * - Compatible with networks that support the access field consensus upgrade
     * 
     * When useAccess=false (default):
     * - Uses legacy separate fields (accounts, foreignApps, foreignAssets, boxReferences)
     * - No translation occurs - references are placed directly in their respective fields
     * - Maintains backward compatibility with pre-consensus upgrade networks
     * - Advanced features (holdings(), locals()) are not allowed
     * 
     * This design allows easy migration - just add .useAccess(true) to enable access field mode
     * while keeping your existing foreign reference method calls.
     * 
     * @param useAccess true to translate references to access field, false to use legacy fields
     * @return this builder instance
     */
    public T useAccess(boolean useAccess) {
        this.useAccess = useAccess;
        return (T) this;
    }
}
