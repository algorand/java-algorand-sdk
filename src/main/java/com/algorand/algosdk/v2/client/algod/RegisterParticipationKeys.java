package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Generate (or renew) and register participation keys on the node for a given 
 * account address. /v2/register-participation-keys/{address} 
 */
public class RegisterParticipationKeys extends Query {

	private String address;
	public String address() {
		return this.address;
	}
	private Long fee;
	public Long fee() {
		return this.fee;
	}
	private Long keyDilution;
	public Long keyDilution() {
		return this.keyDilution;
	}
	private Boolean noWait;
	public Boolean noWait() {
		return this.noWait;
	}
	private Long roundLastValid;
	public Long roundLastValid() {
		return this.roundLastValid;
	}

	/**
	 * @param address The `account-id` to update, or `all` to update all accounts. 
	 */
	public RegisterParticipationKeys(Client client, String address) {
		super(client, "post");
		this.address = address;
	}

	/**
	 * The fee to use when submitting key registration transactions. Defaults to the 
	 * suggested fee. 
	 */
	public RegisterParticipationKeys fee(Long fee) {
		this.fee = fee;
		addQuery("fee", String.valueOf(fee));
		return this;
	}

	/**
	 * value to use for two-level participation key. 
	 */
	public RegisterParticipationKeys keyDilution(Long keyDilution) {
		this.keyDilution = keyDilution;
		addQuery("key-dilution", String.valueOf(keyDilution));
		return this;
	}

	/**
	 * Don't wait for transaction to commit before returning response. 
	 */
	public RegisterParticipationKeys noWait(Boolean noWait) {
		this.noWait = noWait;
		addQuery("no-wait", String.valueOf(noWait));
		return this;
	}

	/**
	 * The last round for which the generated participation keys will be valid. 
	 */
	public RegisterParticipationKeys roundLastValid(Long roundLastValid) {
		this.roundLastValid = roundLastValid;
		addQuery("round-last-valid", String.valueOf(roundLastValid));
		return this;
	}

	@Override
	public Response<String> execute() throws Exception {
		Response<String> resp = baseExecute();
		resp.setValueType(String.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("register-participation-keys"));
		addPathSegment(String.valueOf(address));

		return qd;
	}
}