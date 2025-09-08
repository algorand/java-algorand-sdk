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
    private Transaction.OnCompletion onCompletion;
    private List<byte[]> applicationArgs;
    private List<Address> accounts;
    private List<Long> foreignApps;
    private List<Long> foreignAssets;
    private List<AppBoxReference> appBoxReferences;
    private List<ResourceRef> access;
    private List<AppResourceRef> appResourceRefs;
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

        // Handle access field conversion and validation based on useAccess flag
        boolean hasLegacyFields = (accounts != null && !accounts.isEmpty()) ||
                                 (foreignApps != null && !foreignApps.isEmpty()) ||
                                 (foreignAssets != null && !foreignAssets.isEmpty()) ||
                                 (appBoxReferences != null && !appBoxReferences.isEmpty());
        
        boolean hasAccessFields = (access != null && !access.isEmpty()) ||
                                 (appResourceRefs != null && !appResourceRefs.isEmpty());
        
        if (useAccess) {
            // Using access field mode - validate no legacy fields are set
            if (hasLegacyFields) {
                throw new IllegalArgumentException(
                    "Cannot use legacy accounts, foreignApps, foreignAssets, or boxReferences when useAccess=true");
            }
            
            // Convert AppResourceRef to ResourceRef if provided
            if (appResourceRefs != null && !appResourceRefs.isEmpty()) {
                if (access != null && !access.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Cannot use both AppResourceRef and ResourceRef access methods simultaneously");
                }
                access = AccessConverter.convertToResourceRefs(appResourceRefs, sender, applicationId);
            }
            
            // Validate AppResourceRef entries for null references
            if (appResourceRefs != null && !appResourceRefs.isEmpty()) {
                for (AppResourceRef ref : appResourceRefs) {
                    if (ref == null) {
                        throw new IllegalArgumentException("AppResourceRef cannot be null");
                    }
                }
            }
            
            // Validate ResourceRef entries
            if (access != null && !access.isEmpty()) {
                for (ResourceRef ref : access) {
                    if (ref != null) {
                        ref.validate();
                    }
                }
            }
            
            // Set access field and clear legacy fields
            if (access != null) txn.access = access;
        } else {
            // Using legacy fields mode - validate no access fields are set
            if (hasAccessFields) {
                throw new IllegalArgumentException(
                    "Cannot use access fields or appResourceRefs when useAccess=false. Set useAccess=true to use access field.");
            }
            
            // Use legacy fields (existing behavior)
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
     * Set the access list for this transaction using low-level ResourceRef objects.
     * The access list unifies accounts, foreignApps, foreignAssets, and boxReferences 
     * under a single list with explicit resource tracking.
     * 
     * Note: Using the access field is mutually exclusive with using the separate accounts,
     * foreignApps, foreignAssets, and boxReferences fields.
     */
    public T access(List<ResourceRef> access) {
        this.access = access;
        return (T) this;
    }

    /**
     * Set the access list for this transaction using high-level AppResourceRef objects.
     * This provides a user-friendly API that automatically handles index conversion.
     * 
     * Note: Using the access field is mutually exclusive with using the separate accounts,
     * foreignApps, foreignAssets, and boxReferences fields.
     */
    public T appResourceRefs(List<AppResourceRef> appResourceRefs) {
        this.appResourceRefs = appResourceRefs;
        return (T) this;
    }
    
    /**
     * Enable or disable the use of the access field in this transaction.
     * 
     * When useAccess=true:
     * - The transaction will use the new access field for resource references
     * - Legacy accounts, foreignApps, foreignAssets, and boxReferences fields are not allowed
     * - appResourceRefs() and access() methods are available for setting resources
     * 
     * When useAccess=false (default):
     * - The transaction will use legacy accounts, foreignApps, foreignAssets, and boxReferences fields
     * - The access field and appResourceRefs are not allowed
     * - This maintains backward compatibility with pre-consensus upgrade networks
     * 
     * @param useAccess true to use the access field, false to use legacy fields
     * @return this builder instance
     */
    public T useAccess(boolean useAccess) {
        this.useAccess = useAccess;
        return (T) this;
    }
}
