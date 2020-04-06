package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/*
	Get the list of pending transactions by address, sorted by priority, in 
	decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending 
	transactions. 
 */
public class GetPendingTransactionsByAddress extends Query {
	private String address;
	private String format;
	private long max;

	private boolean addressIsSet;
	private boolean formatIsSet;
	private boolean maxIsSet;

	public GetPendingTransactionsByAddress(Client client) {
		super(client);
	}
	public GetPendingTransactionsByAddress setAddress(String address) {
		this.address = address;
		this.addressIsSet = true;
		return this;
	}
	public GetPendingTransactionsByAddress setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}
	public GetPendingTransactionsByAddress setMax(long max) {
		this.max = max;
		this.maxIsSet = true;
		return this;
	}

	public PendingTransactionsResponse lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		PendingTransactionsResponse resp;
		try {
			resp = mapper.readValue(response, PendingTransactionsResponse.class);
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
		sb.append("accounts");
		sb.append("/");
		sb.append(address);
		sb.append("/");
		sb.append("transactions");
		sb.append("/");
		sb.append("pending");
		sb.append("?");

		boolean added = false;

		if (this.addressIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("address=");
			sb.append(address);
			added = true;
		}
		if (this.formatIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("format=");
			sb.append(format);
			added = true;
		}
		if (this.maxIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("max=");
			sb.append(max);
			added = true;
		}

		return sb.toString();
	}
}