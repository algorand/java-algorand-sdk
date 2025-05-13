package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * These are resources that were accessed by this group that would normally have
 * caused failure, but were allowed in simulation. Depending on where this object
 * is in the response, the unnamed resources it contains may or may not qualify for
 * group resource sharing. If this is a field in SimulateTransactionGroupResult,
 * the resources do qualify, but if this is a field in SimulateTransactionResult,
 * they do not qualify. In order to make this group valid for actual submission,
 * resources that qualify for group sharing can be made available by any
 * transaction of the group; otherwise, resources must be placed in the same
 * transaction which accessed them.
 */
public class SimulateUnnamedResourcesAccessed extends PathResponse {

    /**
     * The unnamed accounts that were referenced. The order of this array is arbitrary.
     */
    @JsonProperty("accounts")
    public void accounts(List<String> accounts) throws NoSuchAlgorithmException {
        this.accounts = new ArrayList<Address>();
        for (String val : accounts) {
            this.accounts.add(new Address(val));
        }
    }
    @JsonProperty("accounts")
    public List<String> accounts() throws NoSuchAlgorithmException {
        ArrayList<String> ret = new ArrayList<String>();
        for (Address val : this.accounts) {
            ret.add(val.encodeAsString());
        }
        return ret;
    }
    public List<Address> accounts = new ArrayList<Address>();

    /**
     * The unnamed application local states that were referenced. The order of this
     * array is arbitrary.
     */
    @JsonProperty("app-locals")
    public List<ApplicationLocalReference> appLocals = new ArrayList<ApplicationLocalReference>();

    /**
     * The unnamed applications that were referenced. The order of this array is
     * arbitrary.
     */
    @JsonProperty("apps")
    public List<java.math.BigInteger> apps = new ArrayList<java.math.BigInteger>();

    /**
     * The unnamed asset holdings that were referenced. The order of this array is
     * arbitrary.
     */
    @JsonProperty("asset-holdings")
    public List<AssetHoldingReference> assetHoldings = new ArrayList<AssetHoldingReference>();

    /**
     * The unnamed assets that were referenced. The order of this array is arbitrary.
     */
    @JsonProperty("assets")
    public List<java.math.BigInteger> assets = new ArrayList<java.math.BigInteger>();

    /**
     * The unnamed boxes that were referenced. The order of this array is arbitrary.
     */
    @JsonProperty("boxes")
    public List<BoxReference> boxes = new ArrayList<BoxReference>();

    /**
     * The number of extra box references used to increase the IO budget. This is in
     * addition to the references defined in the input transaction group and any
     * referenced to unnamed boxes.
     */
    @JsonProperty("extra-box-refs")
    public Long extraBoxRefs;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        SimulateUnnamedResourcesAccessed other = (SimulateUnnamedResourcesAccessed) o;
        if (!Objects.deepEquals(this.accounts, other.accounts)) return false;
        if (!Objects.deepEquals(this.appLocals, other.appLocals)) return false;
        if (!Objects.deepEquals(this.apps, other.apps)) return false;
        if (!Objects.deepEquals(this.assetHoldings, other.assetHoldings)) return false;
        if (!Objects.deepEquals(this.assets, other.assets)) return false;
        if (!Objects.deepEquals(this.boxes, other.boxes)) return false;
        if (!Objects.deepEquals(this.extraBoxRefs, other.extraBoxRefs)) return false;

        return true;
    }
}
