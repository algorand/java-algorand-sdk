package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;


/*
	Waits for a block to appear after round {round} and returns the node's status at 
	the time. 
 */
public class WaitForBlock extends Query {
	private long round;

	private boolean roundIsSet;

	public WaitForBlock(Client client) {
		super(client);
	}
	public WaitForBlock setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public NodeStatusResponse lookup() {
		String response;
		try {
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("v2");
		sb.append("/");
		sb.append("status");
		sb.append("/");
		sb.append("wait-for-block-after");
		sb.append("/");
		sb.append(round);
		sb.append("?");

		boolean added = false;

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