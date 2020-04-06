package com.algorand.algosdk.v2.client.algod;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;


/*
	Given a transaction id of a recently submitted transaction, it returns 
	information about it. There are several cases when this might succeed: - 
	transaction committed (committed round > 0) - transaction still in the pool 
	(committed round = 0, pool error = "") - transaction removed from pool due to 
	error (committed round = 0, pool error != "") Or the transaction may have 
	happened sufficiently long ago that the node no longer remembers it, and this 
	will return an error. 
 */
public class PendingTransactionInformation extends Query {
	private String format;
	private String txid;

	private boolean formatIsSet;
	private boolean txidIsSet;

	public PendingTransactionInformation(Client client) {
		super(client);
	}
	public PendingTransactionInformation setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}
	public PendingTransactionInformation setTxid(String txid) {
		this.txid = txid;
		this.txidIsSet = true;
		return this;
	}

	public PendingTransactionResponse lookup() {
		String response;
		try {
			response = request();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		PendingTransactionResponse resp;
		try {
			resp = mapper.readValue(response, PendingTransactionResponse.class);
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
		sb.append("pending");
		sb.append("/");
		sb.append(txid);
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
		if (this.txidIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("txid=");
			sb.append(txid);
			added = true;
		}

		return sb.toString();
	}
}