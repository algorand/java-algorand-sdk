package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Generate (or renew) and register participation keys on the node for a given 
	account address. /v2/register-participation-keys/{address} 
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

	public RegisterParticipationKeys(Client client) {
		super(client);
	}
	public RegisterParticipationKeys setAddress(String address) {
		this.address = address;
		this.addressIsSet = true;
		return this;
	}
	public RegisterParticipationKeys setFee(long fee) {
		this.fee = fee;
		this.feeIsSet = true;
		return this;
	}
	public RegisterParticipationKeys setKeyDilution(long keyDilution) {
		this.keyDilution = keyDilution;
		this.keyDilutionIsSet = true;
		return this;
	}
	public RegisterParticipationKeys setNoWait(boolean noWait) {
		this.noWait = noWait;
		this.noWaitIsSet = true;
		return this;
	}
	public RegisterParticipationKeys setRoundLastValid(long roundLastValid) {
		this.roundLastValid = roundLastValid;
		this.roundLastValidIsSet = true;
		return this;
	}

	public String lookup() {
		String response;
		try {
			response = request("post");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		String resp;
		try {
			resp = mapper.readValue(response, String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	protected QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.addressIsSet) {
			throw new RuntimeException("address is not set, and it is a required parameter.");
		}
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