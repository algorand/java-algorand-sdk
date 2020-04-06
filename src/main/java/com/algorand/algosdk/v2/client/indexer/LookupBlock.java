package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.Block;


/*
	Lookup block. 
 */
public class LookupBlock extends Query {
	private long roundNumber;

	private boolean roundNumberIsSet;

	public LookupBlock(Client client) {
		super(client);
	}
	public LookupBlock setRoundNumber(long roundNumber) {
		this.roundNumber = roundNumber;
		this.roundNumberIsSet = true;
		return this;
	}

	public Block lookup() {
		String response;
		try {
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("blocks");
		sb.append("/");
		sb.append(roundNumber);
		sb.append("?");

		boolean added = false;

		if (this.roundNumberIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("roundNumber=");
			sb.append(roundNumber);
			added = true;
		}

		return sb.toString();
	}
}