package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	
 */
public class GetBlock extends Query {
	private String format;
	private long round;

	private boolean formatIsSet;
	private boolean roundIsSet;

	public GetBlock(Client client) {
		super(client);
	}
	public GetBlock setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}
	public GetBlock setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	public String lookup() {
		String response;
		try {
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("v2");
		sb.append("/");
		sb.append("blocks");
		sb.append("/");
		sb.append(round);
		sb.append("?");

		boolean added = false;

		if (this.formatIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("format=");
			sb.append(format);
			added = true;
		}
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