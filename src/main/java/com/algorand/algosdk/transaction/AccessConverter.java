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
    
    /**
     * Convert a list of high-level AppResourceRef to index-based ResourceRef.
     * This handles index 0 special cases and ensures proper referencing.
     * 
     * @param appRefs High-level resource references
     * @param sender Transaction sender (used for index 0 address references)
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
        
        // First pass: Create basic ResourceRef entries for addresses, assets, and apps
        for (AppResourceRef appRef : appRefs) {
            if (appRef instanceof AppResourceRef.AddressRef) {
                AppResourceRef.AddressRef addrRef = (AppResourceRef.AddressRef) appRef;
                result.add(ResourceRef.forAddress(addrRef.getAddress()));
            } else if (appRef instanceof AppResourceRef.AssetRef) {
                AppResourceRef.AssetRef assetRef = (AppResourceRef.AssetRef) appRef;
                result.add(ResourceRef.forAsset(assetRef.getAssetId()));
            } else if (appRef instanceof AppResourceRef.AppRef) {
                AppResourceRef.AppRef appRefInner = (AppResourceRef.AppRef) appRef;
                result.add(ResourceRef.forApp(appRefInner.getAppId()));
            }
        }
        
        // Second pass: Handle compound references (holding, locals, box) with proper indices
        for (AppResourceRef appRef : appRefs) {
            if (appRef instanceof AppResourceRef.HoldingRef) {
                AppResourceRef.HoldingRef holdingRef = (AppResourceRef.HoldingRef) appRef;
                long addressIndex = findOrAddAddressIndex(
                    holdingRef.getAddress(), sender, result);
                long assetIndex = findOrAddAssetIndex(
                    holdingRef.getAssetId(), result);
                result.add(ResourceRef.forHolding(
                    new ResourceRef.HoldingRef(addressIndex, assetIndex)));
                    
            } else if (appRef instanceof AppResourceRef.LocalsRef) {
                AppResourceRef.LocalsRef localsRef = (AppResourceRef.LocalsRef) appRef;
                long addressIndex = findOrAddAddressIndex(
                    localsRef.getAddress(), sender, result);
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
            }
        }
        
        return result;
    }
    
    /**
     * Find or add an address to the resource list and return its index.
     * Handles index 0 special case (sender).
     */
    private static long findOrAddAddressIndex(Address address, Address sender, List<ResourceRef> resources) {
        // Special case: index 0 = sender
        if (address == null || address.equals(sender)) {
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
     * Handles index 0 special case (current app).
     */
    private static long findOrAddAppIndex(long appId, Long currentAppId, List<ResourceRef> resources) {
        // Special case: index 0 = current app
        if (currentAppId != null && appId == currentAppId) {
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