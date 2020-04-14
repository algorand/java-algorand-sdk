package com.algorand.algosdk.v2.client.model;

import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Account information at a given round. Definition: data/basics/userBalance.go : 
 * AccountData 
 */
public class Account extends PathResponse {

	/**
	 * the account public key 
	 */
	private String address;
	private boolean addressIsSet;
	@JsonProperty("address")
	public void setAddress(String address){
		this.address = address;
		addressIsSet = true;
	}
	@JsonProperty("address")
	public String getAddress(){
		return addressIsSet ? address : null;
	}
	/**
	 * Check if has a value for address 
	 */	@JsonIgnore
	public boolean hasAddress(){
		return addressIsSet;
	}

	/**
	 * (algo) total number of MicroAlgos in the account 
	 */
	private long amount;
	private boolean amountIsSet;
	@JsonProperty("amount")
	public void setAmount(long amount){
		this.amount = amount;
		amountIsSet = true;
	}
	@JsonProperty("amount")
	public Long getAmount(){
		return amountIsSet ? amount : null;
	}
	/**
	 * Check if has a value for amount 
	 */	@JsonIgnore
	public boolean hasAmount(){
		return amountIsSet;
	}

	/**
	 * specifies the amount of MicroAlgos in the account, without the pending rewards. 
	 */
	private long amountWithoutPendingRewards;
	private boolean amountWithoutPendingRewardsIsSet;
	@JsonProperty("amount-without-pending-rewards")
	public void setAmountWithoutPendingRewards(long amountWithoutPendingRewards){
		this.amountWithoutPendingRewards = amountWithoutPendingRewards;
		amountWithoutPendingRewardsIsSet = true;
	}
	@JsonProperty("amount-without-pending-rewards")
	public Long getAmountWithoutPendingRewards(){
		return amountWithoutPendingRewardsIsSet ? amountWithoutPendingRewards : null;
	}
	/**
	 * Check if has a value for amountWithoutPendingRewards 
	 */	@JsonIgnore
	public boolean hasAmountWithoutPendingRewards(){
		return amountWithoutPendingRewardsIsSet;
	}

	/**
	 * (asset) assets held by this account. Note the raw object uses `map[int] -> 
	 * AssetHolding` for this type. 
	 */
	private List<AssetHolding> assets;
	private boolean assetsIsSet;
	@JsonProperty("assets")
	public void setAssets(List<AssetHolding> assets){
		this.assets = assets;
		assetsIsSet = true;
	}
	@JsonProperty("assets")
	public List<AssetHolding> getAssets(){
		return assetsIsSet ? assets : null;
	}
	/**
	 * Check if has a value for assets 
	 */	@JsonIgnore
	public boolean hasAssets(){
		return assetsIsSet;
	}

	/**
	 * (apar) parameters of assets created by this account. Note: the raw account uses 
	 * `map[int] -> Asset` for this type. 
	 */
	private List<Asset> createdAssets;
	private boolean createdAssetsIsSet;
	@JsonProperty("created-assets")
	public void setCreatedAssets(List<Asset> createdAssets){
		this.createdAssets = createdAssets;
		createdAssetsIsSet = true;
	}
	@JsonProperty("created-assets")
	public List<Asset> getCreatedAssets(){
		return createdAssetsIsSet ? createdAssets : null;
	}
	/**
	 * Check if has a value for createdAssets 
	 */	@JsonIgnore
	public boolean hasCreatedAssets(){
		return createdAssetsIsSet;
	}

	private AccountParticipation participation;
	private boolean participationIsSet;
	@JsonProperty("participation")
	public void setParticipation(AccountParticipation participation){
		this.participation = participation;
		participationIsSet = true;
	}
	@JsonProperty("participation")
	public AccountParticipation getParticipation(){
		return participationIsSet ? participation : null;
	}
	/**
	 * Check if has a value for participation 
	 */	@JsonIgnore
	public boolean hasParticipation(){
		return participationIsSet;
	}

	/**
	 * amount of MicroAlgos of pending rewards in this account. 
	 */
	private long pendingRewards;
	private boolean pendingRewardsIsSet;
	@JsonProperty("pending-rewards")
	public void setPendingRewards(long pendingRewards){
		this.pendingRewards = pendingRewards;
		pendingRewardsIsSet = true;
	}
	@JsonProperty("pending-rewards")
	public Long getPendingRewards(){
		return pendingRewardsIsSet ? pendingRewards : null;
	}
	/**
	 * Check if has a value for pendingRewards 
	 */	@JsonIgnore
	public boolean hasPendingRewards(){
		return pendingRewardsIsSet;
	}

	/**
	 * (ebase) used as part of the rewards computation. Only applicable to accounts 
	 * which are participating. 
	 */
	private long rewardBase;
	private boolean rewardBaseIsSet;
	@JsonProperty("reward-base")
	public void setRewardBase(long rewardBase){
		this.rewardBase = rewardBase;
		rewardBaseIsSet = true;
	}
	@JsonProperty("reward-base")
	public Long getRewardBase(){
		return rewardBaseIsSet ? rewardBase : null;
	}
	/**
	 * Check if has a value for rewardBase 
	 */	@JsonIgnore
	public boolean hasRewardBase(){
		return rewardBaseIsSet;
	}

	/**
	 * (ern) total rewards of MicroAlgos the account has received, including pending 
	 * rewards. 
	 */
	private long rewards;
	private boolean rewardsIsSet;
	@JsonProperty("rewards")
	public void setRewards(long rewards){
		this.rewards = rewards;
		rewardsIsSet = true;
	}
	@JsonProperty("rewards")
	public Long getRewards(){
		return rewardsIsSet ? rewards : null;
	}
	/**
	 * Check if has a value for rewards 
	 */	@JsonIgnore
	public boolean hasRewards(){
		return rewardsIsSet;
	}

	/**
	 * The round for which this information is relevant. 
	 */
	private long round;
	private boolean roundIsSet;
	@JsonProperty("round")
	public void setRound(long round){
		this.round = round;
		roundIsSet = true;
	}
	@JsonProperty("round")
	public Long getRound(){
		return roundIsSet ? round : null;
	}
	/**
	 * Check if has a value for round 
	 */	@JsonIgnore
	public boolean hasRound(){
		return roundIsSet;
	}

	/**
	 * (onl) delegation status of the account's MicroAlgos * Offline - indicates that 
	 * the associated account is delegated. * Online - indicates that the associated 
	 * account used as part of the delegation pool. * NotParticipating - indicates that 
	 * the associated account is neither a delegator nor a delegate. 
	 */
	private String status;
	private boolean statusIsSet;
	@JsonProperty("status")
	public void setStatus(String status){
		this.status = status;
		statusIsSet = true;
	}
	@JsonProperty("status")
	public String getStatus(){
		return statusIsSet ? status : null;
	}
	/**
	 * Check if has a value for status 
	 */	@JsonIgnore
	public boolean hasStatus(){
		return statusIsSet;
	}

	/**
	 * Indicates what type of signature is used by this account, must be one of: * sig 
	 * * msig * lsig 
	 */
	private String type;
	private boolean typeIsSet;
	@JsonProperty("type")
	public void setType(String type){
		this.type = type;
		typeIsSet = true;
	}
	@JsonProperty("type")
	public String getType(){
		return typeIsSet ? type : null;
	}
	/**
	 * Check if has a value for type 
	 */	@JsonIgnore
	public boolean hasType(){
		return typeIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		Account other = (Account) o;
		if (!Objects.deepEquals(this.address, other.address)) return false;
		if (!Objects.deepEquals(this.amount, other.amount)) return false;
		if (!Objects.deepEquals(this.amountWithoutPendingRewards, other.amountWithoutPendingRewards)) return false;
		if (!Objects.deepEquals(this.assets, other.assets)) return false;
		if (!Objects.deepEquals(this.createdAssets, other.createdAssets)) return false;
		if (!Objects.deepEquals(this.participation, other.participation)) return false;
		if (!Objects.deepEquals(this.pendingRewards, other.pendingRewards)) return false;
		if (!Objects.deepEquals(this.rewardBase, other.rewardBase)) return false;
		if (!Objects.deepEquals(this.rewards, other.rewards)) return false;
		if (!Objects.deepEquals(this.round, other.round)) return false;
		if (!Objects.deepEquals(this.status, other.status)) return false;
		if (!Objects.deepEquals(this.type, other.type)) return false;

		return true;
	}
}
