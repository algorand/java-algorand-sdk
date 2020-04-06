package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AccountResponse;


/*
	Lookup account information. 
 */
public class LookupAccountByID extends Query {
	private String accountId;
	private long round;

	private boolean accountIdIsSet;
	private boolean roundIsSet;

	public LookupAccountByID(Client client) {
		super(client);
	}
	public LookupAccountByID setAccountId(String accountId) {
		this.accountId = accountId;
		this.accountIdIsSet = true;
		return this;
	}
	public LookupAccountByID setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public AccountResponse lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		AccountResponse resp;
		try {
			resp = mapper.readValue(response, AccountResponse.class);
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
		sb.append("accounts");
		sb.append("/");
		sb.append(accountId);
		sb.append("?");

		boolean added = false;

		if (this.accountIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("accountId=");
			sb.append(accountId);
			added = true;
		}
		if (this.roundIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("round=");
			sb.append(round);
			added = true;
		}

		return sb.toString();
	}
}