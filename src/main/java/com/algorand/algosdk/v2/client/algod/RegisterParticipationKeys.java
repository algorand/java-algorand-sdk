package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Generate (or renew) and register participation keys on the node for a given 
	account address. 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("v2");
		sb.append("/");
		sb.append("register-participation-keys");
		sb.append("/");
		sb.append(address);
		sb.append("?");

		boolean added = false;

		if (this.addressIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("address=");
			sb.append(address);
			added = true;
		}
		if (this.feeIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("fee=");
			sb.append(fee);
			added = true;
		}
		if (this.keyDilutionIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("keyDilution=");
			sb.append(keyDilution);
			added = true;
		}
		if (this.noWaitIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("noWait=");
			sb.append(noWait);
			added = true;
		}
		if (this.roundLastValidIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("roundLastValid=");
			sb.append(roundLastValid);
			added = true;
		}

		return sb.toString();
	}
}