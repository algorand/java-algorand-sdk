package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.SupplyResponse;


/*
	/v2/ledger/supply 
 */
public class GetSupply extends Query {


	public GetSupply(Client client) {
		super(client);
	}

	public SupplyResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		SupplyResponse resp;
		try {
			resp = mapper.readValue(response, SupplyResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("ledger"));
		qd.addPathSegment(String.valueOf("supply"));

		return qd;
	}
}