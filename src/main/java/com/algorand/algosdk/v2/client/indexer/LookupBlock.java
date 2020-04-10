package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.Block;


/**
 * Lookup block. /blocks/{round-number} 
 */
public class LookupBlock extends Query {
	private long roundNumber;

	private boolean roundNumberIsSet;

	public LookupBlock(Client client) {
		super(client);
	}

	/**
	 * Round number 
	 */
	public LookupBlock setRoundNumber(long roundNumber) {
		this.roundNumber = roundNumber;
		this.roundNumberIsSet = true;
		return this;
	}

	public Block lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		Block resp;
		try {
			resp = mapper.readValue(response, Block.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.roundNumberIsSet) {
			throw new RuntimeException("roundNumber is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("blocks"));
		qd.addPathSegment(String.valueOf(roundNumber));

		return qd;
	}
}