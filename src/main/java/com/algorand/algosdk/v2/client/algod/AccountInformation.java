package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.Account;


/*
	Given a specific account public key, this call returns the accounts status, 
	balance and spendable amounts 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("v2");
		sb.append("/");
		sb.append("accounts");
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

		return sb.toString();
	}
}