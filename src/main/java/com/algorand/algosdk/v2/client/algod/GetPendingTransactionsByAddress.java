package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/*
	Get the list of pending transactions by address, sorted by priority, in 
	decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending 
	transactions. /v2/accounts/{address}/transactions/pending 
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
			response = request("get");
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
	protected QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.addressIsSet) {
			throw new RuntimeException("address is not set, and it is a required parameter.");
		}
		if (this.formatIsSet) {
			qd.addQuery("format", String.valueOf(format));
		}
		if (this.maxIsSet) {
			qd.addQuery("max", String.valueOf(max));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(address));
		qd.addPathSegment(String.valueOf("transactions"));
		qd.addPathSegment(String.valueOf("pending"));

		return qd;
	}
}