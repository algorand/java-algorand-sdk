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
	private long fee;
	private long keyDilution;
	private boolean noWait;
	private long roundLastValid;

	private boolean addressIsSet;
	private boolean feeIsSet;
	private boolean keyDilutionIsSet;
	private boolean noWaitIsSet;
	private boolean roundLastValidIsSet;

	public RegisterParticipationKeys(Client client, String address) {
		super(client, "post");
		this.address = address;
	}

	/**
	 * The fee to use when submitting key registration transactions. Defaults to the 
	 * suggested fee. 
	 */
	public RegisterParticipationKeys setFee(long fee) {
		this.fee = fee;
		this.feeIsSet = true;
		return this;
	}

	/**
	 * value to use for two-level participation key. 
	 */
	public RegisterParticipationKeys setKeyDilution(long keyDilution) {
		this.keyDilution = keyDilution;
		this.keyDilutionIsSet = true;
		return this;
	}

	/**
	 * Don't wait for transaction to commit before returning response. 
	 */
	public RegisterParticipationKeys setNoWait(boolean noWait) {
		this.noWait = noWait;
		this.noWaitIsSet = true;
		return this;
	}

	/**
	 * The last round for which the generated participation keys will be valid. 
	 */
	public RegisterParticipationKeys setRoundLastValid(long roundLastValid) {
		this.roundLastValid = roundLastValid;
		this.roundLastValidIsSet = true;
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
		if (this.feeIsSet) {
			qd.addQuery("fee", String.valueOf(fee));
		}
		if (this.keyDilutionIsSet) {
			qd.addQuery("keyDilution", String.valueOf(keyDilution));
		}
		if (this.noWaitIsSet) {
			qd.addQuery("noWait", String.valueOf(noWait));
		}
		if (this.roundLastValidIsSet) {
			qd.addQuery("roundLastValid", String.valueOf(roundLastValid));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("register-participation-keys"));
		qd.addPathSegment(String.valueOf(address));

		return qd;
	}
}