package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;

import java.util.Arrays;
import java.util.Objects;

/**
 * AppResourceRef represents a high-level resource reference that will be converted
 * to index-based ResourceRef entries. This provides a user-friendly API.
 * 
 * This follows the same pattern as AppBoxReference -> BoxReference conversion.
 */
public abstract class AppResourceRef {
    
    /**
     * Address reference.
     */
    public static class AddressRef extends AppResourceRef {
        private final Address address;
        
        public AddressRef(Address address) {
            this.address = address;
        }
        
        public Address getAddress() {
            return address;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AddressRef that = (AddressRef) o;
            return Objects.equals(address, that.address);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(address);
        }
        
        @Override
        public String toString() {
            return "AddressRef{address=" + address + '}';
        }
    }
    
    /**
     * Asset reference.
     */
    public static class AssetRef extends AppResourceRef {
        private final long assetId;
        
        public AssetRef(long assetId) {
            this.assetId = assetId;
        }
        
        public long getAssetId() {
            return assetId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssetRef assetRef = (AssetRef) o;
            return assetId == assetRef.assetId;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(assetId);
        }
        
        @Override
        public String toString() {
            return "AssetRef{assetId=" + assetId + '}';
        }
    }
    
    /**
     * Application reference.
     */
    public static class AppRef extends AppResourceRef {
        private final long appId;
        
        public AppRef(long appId) {
            this.appId = appId;
        }
        
        public long getAppId() {
            return appId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppRef appRef = (AppRef) o;
            return appId == appRef.appId;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(appId);
        }
        
        @Override
        public String toString() {
            return "AppRef{appId=" + appId + '}';
        }
    }
    
    /**
     * Holding reference (account + asset).
     */
    public static class HoldingRef extends AppResourceRef {
        private final Address address;
        private final long assetId;
        
        public HoldingRef(Address address, long assetId) {
            this.address = address;
            this.assetId = assetId;
        }
        
        public Address getAddress() {
            return address;
        }
        
        public long getAssetId() {
            return assetId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HoldingRef that = (HoldingRef) o;
            return assetId == that.assetId && Objects.equals(address, that.address);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(address, assetId);
        }
        
        @Override
        public String toString() {
            return "HoldingRef{address=" + address + ", assetId=" + assetId + '}';
        }
    }
    
    /**
     * Locals reference (account + app).
     */
    public static class LocalsRef extends AppResourceRef {
        private final Address address;
        private final long appId;
        
        public LocalsRef(Address address, long appId) {
            this.address = address;
            this.appId = appId;
        }
        
        public Address getAddress() {
            return address;
        }
        
        public long getAppId() {
            return appId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalsRef localsRef = (LocalsRef) o;
            return appId == localsRef.appId && Objects.equals(address, localsRef.address);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(address, appId);
        }
        
        @Override
        public String toString() {
            return "LocalsRef{address=" + address + ", appId=" + appId + '}';
        }
    }
    
    /**
     * Box reference.
     */
    public static class BoxRef extends AppResourceRef {
        private final long appId;
        private final byte[] name;
        
        public BoxRef(long appId, byte[] name) {
            this.appId = appId;
            this.name = name == null ? new byte[0] : Arrays.copyOf(name, name.length);
        }
        
        public long getAppId() {
            return appId;
        }
        
        public byte[] getName() {
            return Arrays.copyOf(name, name.length);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxRef boxRef = (BoxRef) o;
            return appId == boxRef.appId && Arrays.equals(name, boxRef.name);
        }
        
        @Override
        public int hashCode() {
            int result = Objects.hash(appId);
            result = 31 * result + Arrays.hashCode(name);
            return result;
        }
        
        @Override
        public String toString() {
            return "BoxRef{appId=" + appId + ", name=" + Arrays.toString(name) + '}';
        }
    }
    
    // Factory methods
    public static AddressRef forAddress(Address address) {
        return new AddressRef(address);
    }
    
    public static AssetRef forAsset(long assetId) {
        return new AssetRef(assetId);
    }
    
    public static AppRef forApp(long appId) {
        return new AppRef(appId);
    }
    
    public static HoldingRef forHolding(Address address, long assetId) {
        return new HoldingRef(address, assetId);
    }
    
    public static LocalsRef forLocals(Address address, long appId) {
        return new LocalsRef(address, appId);
    }
    
    public static BoxRef forBox(long appId, byte[] name) {
        return new BoxRef(appId, name);
    }
}