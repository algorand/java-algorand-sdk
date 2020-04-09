package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	/v2/transactions 
 */
public class RawTransaction extends Query {
	private String rawtxn;

	private boolean rawtxnIsSet;

	public RawTransaction(Client client) {
		super(client);
	}
	public RawTransaction setRawtxn(String rawtxn) {
		this.rawtxn = rawtxn;
		this.rawtxnIsSet = true;
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
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.rawtxnIsSet) {
			qd.addQuery("rawtxn", String.valueOf(rawtxn));
		}
		else {
			throw new RuntimeException("rawtxn is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}