package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * /v2/transactions 
 */
public class RawTransaction extends Query {

	public RawTransaction(Client client) {
		super(client, "post");
	}

	/**
	 * The byte encoded signed transaction to broadcast to network 
	 */
	public RawTransaction rawtxn(byte[] rawtxn) {
		addToBody(rawtxn);
		return this;
	}

	@Override
	public Response<String> execute() throws Exception {
		Response<String> resp = baseExecute();
		resp.setValueType(String.class);
		return resp;
	}

	protected QueryData getRequestString() {
		if (!qd.queries.containsKey("rawtxn")) {
			throw new RuntimeException("rawtxn is not set. It is a required parameter.");
		}
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}