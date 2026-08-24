package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * AccessConverter handles the conversion from high-level AppResourceRef instances
 * to index-based ResourceRef instances that go-algorand expects.
 * 
 * This follows the same pattern as BoxReference.fromAppBoxReference() method.
 */
public class AccessConverter {

    private static final Address ZERO_ADDRESS = new Address();

    
    /**
     * Convert a list of high-level AppResourceRef to index-based ResourceRef.
     * This handles index 0 special cases and ensures proper referencing.
     * 
     * Only a null or zero (empty) address means the sender (index 0); an explicit
     * address, including the sender's own, is listed and referenced by index.
     *
     * @param appRefs High-level resource references
     * @param sender Transaction sender (unused; kept for API stability)
     * @param currentAppId Current application ID (used for index 0 app references)
     * @return List of index-based ResourceRef for serialization
     */
    public static List<ResourceRef> convertToResourceRefs(
            List<AppResourceRef> appRefs, 
            Address sender, 
            Long currentAppId) {
        
        if (appRefs == null || appRefs.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ResourceRef> result = new ArrayList<>();
        
        // First pass: Create basic ResourceRef entries for addresses, assets, and
        // apps. Each resource is listed at most once (find-or-add, like the
        // compound passes below), so duplicates never waste access list slots.
        for (AppResourceRef appRef : appRefs) {
            if (appRef instanceof AppResourceRef.AddressRef) {
                AppResourceRef.AddressRef addrRef = (AppResourceRef.AddressRef) appRef;
                // The zero (empty) address means the sender and is never listed
                if (addrRef.getAddress() == null || addrRef.getAddress().equals(ZERO_ADDRESS)) {
                    continue;
                }
                if (!containsAddress(result, addrRef.getAddress())) {
                    result.add(ResourceRef.forAddress(addrRef.getAddress()));
                }
            } else if (appRef instanceof AppResourceRef.AssetRef) {
                AppResourceRef.AssetRef assetRef = (AppResourceRef.AssetRef) appRef;
                if (!containsAsset(result, assetRef.getAssetId())) {
                    result.add(ResourceRef.forAsset(assetRef.getAssetId()));
                }
            } else if (appRef instanceof AppResourceRef.AppRef) {
                AppResourceRef.AppRef appRefInner = (AppResourceRef.AppRef) appRef;
                if (!containsApp(result, appRefInner.getAppId())) {
                    result.add(ResourceRef.forApp(appRefInner.getAppId()));
                }
            }
        }
        
        // Second pass: Handle compound references (holding, locals, box) with proper indices
        for (AppResourceRef appRef : appRefs) {
            if (appRef instanceof AppResourceRef.HoldingRef) {
                AppResourceRef.HoldingRef holdingRef = (AppResourceRef.HoldingRef) appRef;
                long addressIndex = findOrAddAddressIndex(
                    holdingRef.getAddress(), result);
                long assetIndex = findOrAddAssetIndex(
                    holdingRef.getAssetId(), result);
                result.add(ResourceRef.forHolding(
                    new ResourceRef.HoldingRef(addressIndex, assetIndex)));
                    
            } else if (appRef instanceof AppResourceRef.LocalsRef) {
                AppResourceRef.LocalsRef localsRef = (AppResourceRef.LocalsRef) appRef;
                long addressIndex = findOrAddAddressIndex(
                    localsRef.getAddress(), result);
                long appIndex = findOrAddAppIndex(
                    localsRef.getAppId(), currentAppId, result);
                result.add(ResourceRef.forLocals(
                    new ResourceRef.LocalsRef(addressIndex, appIndex)));
                    
            } else if (appRef instanceof AppResourceRef.BoxRef) {
                AppResourceRef.BoxRef boxRef = (AppResourceRef.BoxRef) appRef;
                long appIndex = findOrAddAppIndex(
                    boxRef.getAppId(), currentAppId, result);
                result.add(ResourceRef.forBox(
                    new ResourceRef.BoxRef(appIndex, boxRef.getName())));

            } else if (appRef instanceof AppResourceRef.EmptyRef) {
                // An empty reference requests a box I/O quota bump without naming a resource
                result.add(ResourceRef.forEmpty());
            }
        }
        
        return result;
    }
    
    private static boolean containsAddress(List<ResourceRef> resources, Address address) {
        for (ResourceRef ref : resources) {
            if (ref.address != null && ref.address.equals(address)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsAsset(List<ResourceRef> resources, long assetId) {
        for (ResourceRef ref : resources) {
            if (ref.asset != null && ref.asset.equals(assetId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsApp(List<ResourceRef> resources, long appId) {
        for (ResourceRef ref : resources) {
            if (ref.app != null && ref.app.equals(appId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find or add an address to the resource list and return its index.
     * Only a null or zero (empty) address means the sender (index 0); an
     * explicit address, including the sender's own, is listed and referenced
     * by index, matching the py and js SDKs.
     */
    private static long findOrAddAddressIndex(Address address, List<ResourceRef> resources) {
        // Special case: index 0 = sender (null and the zero address are sender shorthands)
        if (address == null || address.equals(ZERO_ADDRESS)) {
            return 0;
        }
        
        // Look for existing address in the list
        for (int i = 0; i < resources.size(); i++) {
            ResourceRef ref = resources.get(i);
            if (ref.address != null && ref.address.equals(address)) {
                return i + 1; // 1-based indexing (0 is special)
            }
        }
        
        // Add address if not found
        resources.add(ResourceRef.forAddress(address));
        return resources.size(); // 1-based indexing
    }
    
    /**
     * Find or add an asset to the resource list and return its index.
     */
    private static long findOrAddAssetIndex(long assetId, List<ResourceRef> resources) {
        // Look for existing asset in the list
        for (int i = 0; i < resources.size(); i++) {
            ResourceRef ref = resources.get(i);
            if (ref.asset != null && ref.asset.equals(assetId)) {
                return i + 1; // 1-based indexing
            }
        }
        
        // Add asset if not found
        resources.add(ResourceRef.forAsset(assetId));
        return resources.size(); // 1-based indexing
    }
    
    /**
     * Find or add an app to the resource list and return its index.
     * Handles index 0 special case (current app). An appId of 0 always refers
     * to the currently executing app.
     */
    private static long findOrAddAppIndex(long appId, Long currentAppId, List<ResourceRef> resources) {
        // Special case: index 0 = current app (appId 0 is shorthand for the executing app)
        if (appId == 0 || (currentAppId != null && appId == currentAppId)) {
            return 0;
        }
        
        // Look for existing app in the list
        for (int i = 0; i < resources.size(); i++) {
            ResourceRef ref = resources.get(i);
            if (ref.app != null && ref.app.equals(appId)) {
                return i + 1; // 1-based indexing (0 is special)
            }
        }
        
        // Add app if not found
        resources.add(ResourceRef.forApp(appId));
        return resources.size(); // 1-based indexing
    }
}