package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;


/*
	
 */
public class TransactionParams extends Query {


	public TransactionParams(Client client) {
		super(client);
	}

	public TransactionParametersResponse lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		TransactionParametersResponse resp;
		try {
			resp = mapper.readValue(response, TransactionParametersResponse.class);
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
		sb.append("transactions");
		sb.append("/");
		sb.append("params");
		sb.append("?");

		boolean added = false;


		return sb.toString();
	}
}