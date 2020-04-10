package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.BlockResponse;


/**
 * /v2/blocks/{round} 
 */
public class GetBlock extends Query {
	private String format;
	private long round;

	private boolean formatIsSet;
	private boolean roundIsSet;

	public GetBlock(Client client) {
		super(client);
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public GetBlock setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}

	/**
	 * The round from which to fetch block information. 
	 */
	public GetBlock setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public BlockResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		BlockResponse resp;
		try {
			resp = mapper.readValue(response, BlockResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.formatIsSet) {
			qd.addQuery("format", String.valueOf(format));
		}
		if  (!this.roundIsSet) {
			throw new RuntimeException("round is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("blocks"));
		qd.addPathSegment(String.valueOf(round));

		return qd;
	}
}