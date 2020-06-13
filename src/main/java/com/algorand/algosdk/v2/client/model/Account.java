package com.algorand.algosdk.v2.client.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account information at a given round.
 * Definition:
 * data/basics/userBalance.go : AccountData
 *
 */
public class Account extends PathResponse {

    /**
     * the account public key
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
     * (algo) total number of MicroAlgos in the account
     */
    @JsonProperty("amount")
    public Long amount;

    /**
     * specifies the amount of MicroAlgos in the account, without the pending rewards.
     */
    @JsonProperty("amount-without-pending-rewards")
    public Long amountWithoutPendingRewards;

    /**
     * (asset) assets held by this account.
     * Note the raw object uses `map[int] -> AssetHolding` for this type.
     */
    @JsonProperty("assets")
    public List<AssetHolding> assets = new ArrayList<AssetHolding>();
    /**
     * (spend) the address against which signing should be checked. If empty, the
     * address of the current account is used. This field can be updated in any
     * transaction by setting the RekeyTo field.
     */
    @JsonProperty("auth-addr")
    public void authAddr(String authAddr) throws NoSuchAlgorithmException {
        this.authAddr = new Address(authAddr);
    }
    @JsonProperty("auth-addr")
    public String authAddr() throws NoSuchAlgorithmException {
        if (this.authAddr != null) {
            return this.authAddr.encodeAsString();
        } else {
            return null;
        }
    }
    public Address authAddr;

    /**
     * (apar) parameters of assets created by this account.
     * Note: the raw account uses `map[int] -> Asset` for this type.
     */
    @JsonProperty("created-assets")
    public List<Asset> createdAssets = new ArrayList<Asset>();

    @JsonProperty("participation")
    public AccountParticipation participation;

    /**
     * amount of MicroAlgos of pending rewards in this account.
     */
    @JsonProperty("pending-rewards")
    public Long pendingRewards;

    /**
     * (ebase) used as part of the rewards computation. Only applicable to accounts
     * which are participating.
     */
    @JsonProperty("reward-base")
    public Long rewardBase;

    /**
     * (ern) total rewards of MicroAlgos the account has received, including pending
     * rewards.
     */
    @JsonProperty("rewards")
    public Long rewards;

    /**
     * The round for which this information is relevant.
     */
    @JsonProperty("round")
    public Long round;

    /**
     * Indicates what type of signature is used by this account, must be one of:
     *   sig
     *   msig
     *   lsig
     */
    @JsonProperty("sig-type")
    public Enums.SigType sigType;

    /**
     * (onl) delegation status of the account's MicroAlgos
     *   Offline - indicates that the associated account is delegated.
     *   Online - indicates that the associated account used as part of the delegation
     * pool.
     *   NotParticipating - indicates that the associated account is neither a
     * delegator nor a delegate.
     */
    @JsonProperty("status")
    public String status;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Account other = (Account) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.amountWithoutPendingRewards, other.amountWithoutPendingRewards)) return false;
        if (!Objects.deepEquals(this.assets, other.assets)) return false;
        if (!Objects.deepEquals(this.authAddr, other.authAddr)) return false;
        if (!Objects.deepEquals(this.createdAssets, other.createdAssets)) return false;
        if (!Objects.deepEquals(this.participation, other.participation)) return false;
        if (!Objects.deepEquals(this.pendingRewards, other.pendingRewards)) return false;
        if (!Objects.deepEquals(this.rewardBase, other.rewardBase)) return false;
        if (!Objects.deepEquals(this.rewards, other.rewards)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.sigType, other.sigType)) return false;
        if (!Objects.deepEquals(this.status, other.status)) return false;

        return true;
    }
}
