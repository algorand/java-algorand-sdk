package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * /v2/transactions 
 */
public class RawTransaction extends Query {
	private String rawtxn;

	private boolean rawtxnIsSet;

	public RawTransaction(Client client) {
		super(client, "post");
	}

	/**
	 * The byte encoded signed transaction to broadcast to network 
	 */
	public RawTransaction setRawtxn(String rawtxn) {
		this.rawtxn = rawtxn;
		this.rawtxnIsSet = true;
		return this;
	}

	@Override
	public Response<String> execute() throws Exception {
		Response<String> resp = baseExecute();
		resp.setValueType(String.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.rawtxnIsSet) {
			qd.addQuery("rawtxn", String.valueOf(rawtxn));
		}
		else {
			throw new RuntimeException("rawtxn is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}