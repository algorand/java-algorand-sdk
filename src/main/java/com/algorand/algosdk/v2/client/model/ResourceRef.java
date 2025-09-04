package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ResourceRef names a single resource. Only one of the fields should be set.
 */
public class ResourceRef extends PathResponse {

    /**
     * (d) Account whose balance record is accessible by the executing ApprovalProgram
     * or ClearStateProgram.
     */
    @JsonProperty("address")
    public void address(String address) throws NoSuchAlgorithmException {
        this.address = new Address(address);
    }
    @JsonProperty("address")
    public String address() throws NoSuchAlgorithmException {
        if (this.address != null) {
            return this.address.encodeAsString();
        } else {
            return null;
        }
    }
    public Address address;

    /**
     * (p) Application id whose GlobalState may be read by the executing
     * ApprovalProgram or ClearStateProgram.
     */
    @JsonProperty("application-id")
    public Long applicationId;

    /**
     * (s) Asset whose AssetParams may be read by the executing
     * ApprovalProgram or ClearStateProgram.
     */
    @JsonProperty("asset-id")
    public Long assetId;

    /**
     * BoxReference names a box by its name and the application ID it belongs to.
     */
    @JsonProperty("box")
    public BoxReference box;

    /**
     * HoldingRef names a holding by referring to an Address and Asset it belongs to.
     */
    @JsonProperty("holding")
    public HoldingRef holding;

    /**
     * LocalsRef names a local state by referring to an Address and App it belongs to.
     */
    @JsonProperty("local")
    public LocalsRef local;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ResourceRef other = (ResourceRef) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.applicationId, other.applicationId)) return false;
        if (!Objects.deepEquals(this.assetId, other.assetId)) return false;
        if (!Objects.deepEquals(this.box, other.box)) return false;
        if (!Objects.deepEquals(this.holding, other.holding)) return false;
        if (!Objects.deepEquals(this.local, other.local)) return false;

        return true;
    }
}
