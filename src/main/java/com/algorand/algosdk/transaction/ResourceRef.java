package com.algorand.algosdk.transaction;

import com.algorand.algosdk.crypto.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 * ResourceRef is a reference to a resource in an application call transaction.
 * It can reference different types of resources like accounts, assets, applications, holdings, locals, or boxes.
 * Only one resource type should be set per ResourceRef instance.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResourceRef {
    @JsonProperty("d")
    public Address address;
    
    @JsonProperty("s")  
    public Long asset;
    
    @JsonProperty("p")
    public Long app;
    
    @JsonProperty("h")
    public HoldingRef holding;
    
    @JsonProperty("l")
    public LocalsRef locals;
    
    @JsonProperty("b")
    public BoxRef box;

    /**
     * Default constructor for ResourceRef.
     */
    public ResourceRef() {}

    /**
     * JsonCreator constructor for ResourceRef deserialization.
     */
    @JsonCreator
    public ResourceRef(
            @JsonProperty("d") byte[] address,
            @JsonProperty("s") Long asset,
            @JsonProperty("p") Long app,
            @JsonProperty("h") HoldingRef holding,
            @JsonProperty("l") LocalsRef locals,
            @JsonProperty("b") BoxRef box) {
        if (address != null) {
            this.address = new Address(address);
        }
        this.asset = asset;
        this.app = app;
        this.holding = holding;
        this.locals = locals;
        this.box = box;
    }

    /**
     * Create a ResourceRef for an account address.
     */
    public static ResourceRef forAddress(Address address) {
        ResourceRef ref = new ResourceRef();
        ref.address = address;
        return ref;
    }

    /**
     * Create a ResourceRef for an asset.
     */
    public static ResourceRef forAsset(long assetId) {
        ResourceRef ref = new ResourceRef();
        ref.asset = assetId;
        return ref;
    }

    /**
     * Create a ResourceRef for an application.
     */
    public static ResourceRef forApp(long appId) {
        ResourceRef ref = new ResourceRef();
        ref.app = appId;
        return ref;
    }

    /**
     * Create a ResourceRef for a holding reference.
     */
    public static ResourceRef forHolding(HoldingRef holdingRef) {
        ResourceRef ref = new ResourceRef();
        ref.holding = holdingRef;
        return ref;
    }

    /**
     * Create a ResourceRef for a locals reference.
     */
    public static ResourceRef forLocals(LocalsRef localsRef) {
        ResourceRef ref = new ResourceRef();
        ref.locals = localsRef;
        return ref;
    }

    /**
     * Create a ResourceRef for a box reference.
     */
    public static ResourceRef forBox(BoxRef boxRef) {
        ResourceRef ref = new ResourceRef();
        ref.box = boxRef;
        return ref;
    }

    /**
     * Check if this ResourceRef is empty (no resource type is set).
     */
    @JsonIgnore
    public boolean isEmpty() {
        return address == null && asset == null && app == null && 
               holding == null && locals == null && box == null;
    }

    /**
     * Validate that only one resource type is set.
     * @throws IllegalStateException if multiple resource types are set
     */
    @JsonIgnore
    public void validate() {
        int setCount = 0;
        if (address != null) setCount++;
        if (asset != null) setCount++;
        if (app != null) setCount++;
        if (holding != null) setCount++;
        if (locals != null) setCount++;
        if (box != null) setCount++;

        if (setCount > 1) {
            throw new IllegalStateException("ResourceRef can only have one resource type set");
        }
        if (setCount == 0) {
            throw new IllegalStateException("ResourceRef must have one resource type set");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceRef that = (ResourceRef) o;
        return Objects.equals(address, that.address) &&
               Objects.equals(asset, that.asset) &&
               Objects.equals(app, that.app) &&
               Objects.equals(holding, that.holding) &&
               Objects.equals(locals, that.locals) &&
               Objects.equals(box, that.box);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, asset, app, holding, locals, box);
    }

    @Override
    public String toString() {
        return "ResourceRef{" +
                "address=" + address +
                ", asset=" + asset +
                ", app=" + app +
                ", holding=" + holding +
                ", locals=" + locals +
                ", box=" + box +
                '}';
    }

    /**
     * HoldingRef represents a reference to an asset holding of an account.
     * Both fields are indices into the Access array, matching the Go implementation.
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class HoldingRef {
        @JsonProperty("d")
        public Long addressIndex;  // Index into Access array (0 = sender)
        
        @JsonProperty("s")
        public Long assetIndex;    // Index into Access array

        public HoldingRef() {}

        @JsonCreator
        public HoldingRef(
                @JsonProperty("d") Long addressIndex,
                @JsonProperty("s") Long assetIndex) {
            this.addressIndex = addressIndex;
            this.assetIndex = assetIndex;
        }

        public HoldingRef(long addressIndex, long assetIndex) {
            this.addressIndex = addressIndex;
            this.assetIndex = assetIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HoldingRef that = (HoldingRef) o;
            return Objects.equals(addressIndex, that.addressIndex) && Objects.equals(assetIndex, that.assetIndex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(addressIndex, assetIndex);
        }

        @Override
        public String toString() {
            return "HoldingRef{" +
                    "addressIndex=" + addressIndex +
                    ", assetIndex=" + assetIndex +
                    '}';
        }
    }

    /**
     * LocalsRef represents a reference to the local state of an account for an application.
     * Both fields are indices into the Access array, matching the Go implementation.
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class LocalsRef {
        @JsonProperty("d")
        public Long addressIndex;  // Index into Access array (0 = sender)
        
        @JsonProperty("p")
        public Long appIndex;      // Index into Access array (0 = current app)

        public LocalsRef() {}

        @JsonCreator
        public LocalsRef(
                @JsonProperty("d") Long addressIndex,
                @JsonProperty("p") Long appIndex) {
            this.addressIndex = addressIndex;
            this.appIndex = appIndex;
        }

        public LocalsRef(long addressIndex, long appIndex) {
            this.addressIndex = addressIndex;
            this.appIndex = appIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalsRef that = (LocalsRef) o;
            return Objects.equals(addressIndex, that.addressIndex) && Objects.equals(appIndex, that.appIndex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(addressIndex, appIndex);
        }

        @Override
        public String toString() {
            return "LocalsRef{" +
                    "addressIndex=" + addressIndex +
                    ", appIndex=" + appIndex +
                    '}';
        }
    }

    /**
     * BoxRef represents a reference to a box of an application.
     * The index field references the application in the Access array.
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class BoxRef {
        @JsonProperty("i")
        public Long index;        // Index into Access array (0 = current app)
        
        @JsonProperty("n")
        public byte[] name;

        public BoxRef() {}

        @JsonCreator
        public BoxRef(
                @JsonProperty("i") Long index,
                @JsonProperty("n") byte[] name) {
            this.index = index;
            this.name = name == null ? new byte[0] : Arrays.copyOf(name, name.length);
        }

        public BoxRef(long index, byte[] name) {
            this.index = index;
            this.name = name == null ? new byte[0] : Arrays.copyOf(name, name.length);
        }

        @JsonIgnore
        public byte[] getName() {
            return name == null ? new byte[0] : Arrays.copyOf(name, name.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxRef boxRef = (BoxRef) o;
            return Objects.equals(index, boxRef.index) && Arrays.equals(name, boxRef.name);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(index);
            result = 31 * result + Arrays.hashCode(name);
            return result;
        }

        @Override
        public String toString() {
            return "BoxRef{" +
                    "index=" + index +
                    ", name=" + Arrays.toString(name) +
                    '}';
        }
    }
}