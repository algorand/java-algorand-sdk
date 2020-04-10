package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AccountResponse;


/**
 * Lookup account information. /accounts/{account-id} 
 */
public class LookupAccountByID extends Query {
	private String accountId;
	private long round;

	private boolean accountIdIsSet;
	private boolean roundIsSet;

	public LookupAccountByID(Client client) {
		super(client);
	}

	/**
	 * account string 
	 */
	public LookupAccountByID setAccountId(String accountId) {
		this.accountId = accountId;
		this.accountIdIsSet = true;
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAccountByID setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public AccountResponse lookup() {
		String response;
		try {
			response = request("get");
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
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.accountIdIsSet) {
			throw new RuntimeException("accountId is not set, and it is a required parameter.");
		}
		if (this.roundIsSet) {
			qd.addQuery("round", String.valueOf(round));
		}
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(accountId));

		return qd;
	}
}