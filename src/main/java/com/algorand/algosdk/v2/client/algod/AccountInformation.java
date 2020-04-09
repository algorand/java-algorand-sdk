package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.Account;


/*
	Given a specific account public key, this call returns the accounts status, 
	balance and spendable amounts /v2/accounts/{address} 
 */
public class AccountInformation extends Query {
	private String address;

	private boolean addressIsSet;

	public AccountInformation(Client client) {
		super(client);
	}
	public AccountInformation setAddress(String address) {
		this.address = address;
		this.addressIsSet = true;
		return this;
	}

	public Account lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		Account resp;
		try {
			resp = mapper.readValue(response, Account.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.addressIsSet) {
			throw new RuntimeException("address is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(address));

		return qd;
	}
}