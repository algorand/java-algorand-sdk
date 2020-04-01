package com.algorand.indexer.schemas;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Account information at a given round. Definition: data/basics/userBalance.go : 
	AccountData 
 */
public class Account {

	/*
		the account public key 
	 */
	@JsonProperty("address")
	public String address;

	/*
		(algo) total number of MicroAlgos in the account 
	 */
	@JsonProperty("amount")
	public long amount;

	/*
		specifies the amount of MicroAlgos in the account, without the pending rewards. 
	 */
	@JsonProperty("amount-without-pending-rewards")
	public long amountWithoutPendingRewards;

	/*
		(asset) assets held by this account. Note the raw object uses `map[int] -> 
		AssetHolding` for this type. 
	 */
	@JsonProperty("assets")
	public List<AssetHolding> assets;

	/*
		(apar) parameters of assets created by this account. Note: the raw account uses 
		`map[int] -> Asset` for this type. 
	 */
	@JsonProperty("created-assets")
	public List<Asset> createdAssets;

	@JsonProperty("participation")
	public AccountParticipation participation;

	/*
		amount of MicroAlgos of pending rewards in this account. 
	 */
	@JsonProperty("pending-rewards")
	public long pendingRewards;

	/*
		(ebase) used as part of the rewards computation. Only applicable to accounts 
		which are participating. 
	 */
	@JsonProperty("reward-base")
	public long rewardBase;

	/*
		(ern) total rewards of MicroAlgos the account has received, including pending 
		rewards. 
	 */
	@JsonProperty("rewards")
	public long rewards;

	/*
		The round for which this information is relevant. 
	 */
	@JsonProperty("round")
	public long round;

	/*
		(onl) delegation status of the account's MicroAlgos * Offline - indicates that 
		the associated account is delegated. * Online - indicates that the associated 
		account used as part of the delegation pool. * NotParticipating - indicates that 
		the associated account is neither a delegator nor a delegate. 
	 */
	@JsonProperty("status")
	public String status;

	/*
		Indicates what type of signature is used by this account, must be one of: * sig 
		* msig * lsig 
	 */
	@JsonProperty("type")
	public String type;

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

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
