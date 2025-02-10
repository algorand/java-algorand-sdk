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
     * total number of MicroAlgos in the account
     */
    @JsonProperty("amount")
    public Long amount;

    /**
     * specifies the amount of MicroAlgos in the account, without the pending rewards.
     */
    @JsonProperty("amount-without-pending-rewards")
    public Long amountWithoutPendingRewards;

    /**
     * application local data stored in this account.
     * Note the raw object uses `map[int] -> AppLocalState` for this type.
     */
    @JsonProperty("apps-local-state")
    public List<ApplicationLocalState> appsLocalState = new ArrayList<ApplicationLocalState>();

    /**
     * the sum of all extra application program pages for this account.
     */
    @JsonProperty("apps-total-extra-pages")
    public Long appsTotalExtraPages;

    /**
     * the sum of all of the local schemas and global schemas in this account.
     * Note: the raw account uses `StateSchema` for this type.
     */
    @JsonProperty("apps-total-schema")
    public ApplicationStateSchema appsTotalSchema;

    /**
     * assets held by this account.
     * Note the raw object uses `map[int] -> AssetHolding` for this type.
     */
    @JsonProperty("assets")
    public List<AssetHolding> assets = new ArrayList<AssetHolding>();

    /**
     * The address against which signing should be checked. If empty, the address of
     * the current account is used. This field can be updated in any transaction by
     * setting the RekeyTo field.
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
     * Round during which this account was most recently closed.
     */
    @JsonProperty("closed-at-round")
    public java.math.BigInteger closedAtRound;

    /**
     * parameters of applications created by this account including app global data.
     * Note: the raw account uses `map[int] -> AppParams` for this type.
     */
    @JsonProperty("created-apps")
    public List<Application> createdApps = new ArrayList<Application>();

    /**
     * parameters of assets created by this account.
     * Note: the raw account uses `map[int] -> Asset` for this type.
     */
    @JsonProperty("created-assets")
    public List<Asset> createdAssets = new ArrayList<Asset>();

    /**
     * Round during which this account first appeared in a transaction.
     */
    @JsonProperty("created-at-round")
    public java.math.BigInteger createdAtRound;

    /**
     * Whether or not this account is currently closed.
     */
    @JsonProperty("deleted")
    public Boolean deleted;

    /**
     * can the account receive block incentives if its balance is in range at proposal
     * time.
     */
    @JsonProperty("incentive-eligible")
    public Boolean incentiveEligible;

    /**
     * The round in which this account last went online, or explicitly renewed their
     * online status.
     */
    @JsonProperty("last-heartbeat")
    public Long lastHeartbeat;

    /**
     * The round in which this account last proposed the block.
     */
    @JsonProperty("last-proposed")
    public Long lastProposed;

    /**
     * MicroAlgo balance required by the account.
     * The requirement grows based on asset and application usage.
     */
    @JsonProperty("min-balance")
    public Long minBalance;

    /**
     * AccountParticipation describes the parameters used by this account in consensus
     * protocol.
     */
    @JsonProperty("participation")
    public AccountParticipation participation;

    /**
     * amount of MicroAlgos of pending rewards in this account.
     */
    @JsonProperty("pending-rewards")
    public Long pendingRewards;

    /**
     * used as part of the rewards computation. Only applicable to accounts which are
     * participating.
     */
    @JsonProperty("reward-base")
    public Long rewardBase;

    /**
     * total rewards of MicroAlgos the account has received, including pending rewards.
     */
    @JsonProperty("rewards")
    public Long rewards;

    /**
     * The round for which this information is relevant.
     */
    @JsonProperty("round")
    public Long round;

    /**
     * the type of signature used by this account, must be one of:
     *   sig
     *   msig
     *   lsig
     *   or null if unknown
     */
    @JsonProperty("sig-type")
    public Enums.SigType sigType;

    /**
     * voting status of the account's MicroAlgos
     *   Offline - indicates that the associated account is delegated.
     *   Online - indicates that the associated account used as part of the delegation
     * pool.
     *   NotParticipating - indicates that the associated account is neither a
     * delegator nor a delegate.
     */
    @JsonProperty("status")
    public String status;

    /**
     * The count of all applications that have been opted in, equivalent to the count
     * of application local data (AppLocalState objects) stored in this account.
     */
    @JsonProperty("total-apps-opted-in")
    public Long totalAppsOptedIn;

    /**
     * The count of all assets that have been opted in, equivalent to the count of
     * AssetHolding objects held by this account.
     */
    @JsonProperty("total-assets-opted-in")
    public Long totalAssetsOptedIn;

    /**
     * For app-accounts only. The total number of bytes allocated for the keys and
     * values of boxes which belong to the associated application.
     */
    @JsonProperty("total-box-bytes")
    public Long totalBoxBytes;

    /**
     * For app-accounts only. The total number of boxes which belong to the associated
     * application.
     */
    @JsonProperty("total-boxes")
    public Long totalBoxes;

    /**
     * The count of all apps (AppParams objects) created by this account.
     */
    @JsonProperty("total-created-apps")
    public Long totalCreatedApps;

    /**
     * The count of all assets (AssetParams objects) created by this account.
     */
    @JsonProperty("total-created-assets")
    public Long totalCreatedAssets;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        Account other = (Account) o;
        if (!Objects.deepEquals(this.address, other.address)) return false;
        if (!Objects.deepEquals(this.amount, other.amount)) return false;
        if (!Objects.deepEquals(this.amountWithoutPendingRewards, other.amountWithoutPendingRewards)) return false;
        if (!Objects.deepEquals(this.appsLocalState, other.appsLocalState)) return false;
        if (!Objects.deepEquals(this.appsTotalExtraPages, other.appsTotalExtraPages)) return false;
        if (!Objects.deepEquals(this.appsTotalSchema, other.appsTotalSchema)) return false;
        if (!Objects.deepEquals(this.assets, other.assets)) return false;
        if (!Objects.deepEquals(this.authAddr, other.authAddr)) return false;
        if (!Objects.deepEquals(this.closedAtRound, other.closedAtRound)) return false;
        if (!Objects.deepEquals(this.createdApps, other.createdApps)) return false;
        if (!Objects.deepEquals(this.createdAssets, other.createdAssets)) return false;
        if (!Objects.deepEquals(this.createdAtRound, other.createdAtRound)) return false;
        if (!Objects.deepEquals(this.deleted, other.deleted)) return false;
        if (!Objects.deepEquals(this.incentiveEligible, other.incentiveEligible)) return false;
        if (!Objects.deepEquals(this.lastHeartbeat, other.lastHeartbeat)) return false;
        if (!Objects.deepEquals(this.lastProposed, other.lastProposed)) return false;
        if (!Objects.deepEquals(this.minBalance, other.minBalance)) return false;
        if (!Objects.deepEquals(this.participation, other.participation)) return false;
        if (!Objects.deepEquals(this.pendingRewards, other.pendingRewards)) return false;
        if (!Objects.deepEquals(this.rewardBase, other.rewardBase)) return false;
        if (!Objects.deepEquals(this.rewards, other.rewards)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;
        if (!Objects.deepEquals(this.sigType, other.sigType)) return false;
        if (!Objects.deepEquals(this.status, other.status)) return false;
        if (!Objects.deepEquals(this.totalAppsOptedIn, other.totalAppsOptedIn)) return false;
        if (!Objects.deepEquals(this.totalAssetsOptedIn, other.totalAssetsOptedIn)) return false;
        if (!Objects.deepEquals(this.totalBoxBytes, other.totalBoxBytes)) return false;
        if (!Objects.deepEquals(this.totalBoxes, other.totalBoxes)) return false;
        if (!Objects.deepEquals(this.totalCreatedApps, other.totalCreatedApps)) return false;
        if (!Objects.deepEquals(this.totalCreatedAssets, other.totalCreatedAssets)) return false;

        return true;
    }
}
