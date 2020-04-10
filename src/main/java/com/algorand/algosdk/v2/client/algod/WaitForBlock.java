package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;


/**
 * Waits for a block to appear after round {round} and returns the node's status at 
 * the time. /v2/status/wait-for-block-after/{round}/ 
 */
public class WaitForBlock extends Query {
	private long round;

	private boolean roundIsSet;

	public WaitForBlock(Client client) {
		super(client);
	}

	/**
	 * The round to wait until returning status 
	 */
	public WaitForBlock setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public NodeStatusResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		NodeStatusResponse resp;
		try {
			resp = mapper.readValue(response, NodeStatusResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.roundIsSet) {
			throw new RuntimeException("round is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("status"));
		qd.addPathSegment(String.valueOf("wait-for-block-after"));
		qd.addPathSegment(String.valueOf(round));

		return qd;
	}
}