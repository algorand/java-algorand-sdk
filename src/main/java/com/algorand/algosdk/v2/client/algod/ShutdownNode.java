package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Special management endpoint to shutdown the node. Optionally provide a timeout 
	parameter to indicate that the node should begin shutting down after a number of 
	seconds. /v2/shutdown 
 */
public class ShutdownNode extends Query {
	private long timeout;

	private boolean timeoutIsSet;

	public ShutdownNode(Client client) {
		super(client);
	}
	public ShutdownNode setTimeout(long timeout) {
		this.timeout = timeout;
		this.timeoutIsSet = true;
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
		if (this.timeoutIsSet) {
			qd.addQuery("timeout", String.valueOf(timeout));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("shutdown"));

		return qd;
	}
}