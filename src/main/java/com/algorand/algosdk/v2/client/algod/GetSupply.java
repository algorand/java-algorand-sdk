package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.SupplyResponse;


/*
	
 */
public class GetSupply extends Query {


	public GetSupply(Client client) {
		super(client);
	}

	public SupplyResponse lookup() {
		String response;
		try {
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("v2");
		sb.append("/");
		sb.append("ledger");
		sb.append("/");
		sb.append("supply");
		sb.append("?");

		boolean added = false;


		return sb.toString();
	}
}