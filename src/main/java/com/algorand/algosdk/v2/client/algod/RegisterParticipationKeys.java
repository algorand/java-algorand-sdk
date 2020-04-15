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
	private Long fee;
	private Long keyDilution;
	private Boolean noWait;
	private Long roundLastValid;


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
	public RegisterParticipationKeys setFee(Long fee) {
		this.fee = fee;
		return this;
	}

	/**
	 * value to use for two-level participation key. 
	 */
	public RegisterParticipationKeys setKeyDilution(Long keyDilution) {
		this.keyDilution = keyDilution;
		return this;
	}

	/**
	 * Don't wait for transaction to commit before returning response. 
	 */
	public RegisterParticipationKeys setNoWait(Boolean noWait) {
		this.noWait = noWait;
		return this;
	}

	/**
	 * The last round for which the generated participation keys will be valid. 
	 */
	public RegisterParticipationKeys setRoundLastValid(Long roundLastValid) {
		this.roundLastValid = roundLastValid;
		return this;
	}

	@Override
	public Response<String> execute() throws Exception {
		Response<String> resp = baseExecute();
		resp.setValueType(String.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.fee != null) {
			qd.addQuery("fee", String.valueOf(fee));
		}
		if (this.keyDilution != null) {
			qd.addQuery("keyDilution", String.valueOf(keyDilution));
		}
		if (this.noWait != null) {
			qd.addQuery("noWait", String.valueOf(noWait));
		}
		if (this.roundLastValid != null) {
			qd.addQuery("roundLastValid", String.valueOf(roundLastValid));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("register-participation-keys"));
		qd.addPathSegment(String.valueOf(address));

		return qd;
	}
}