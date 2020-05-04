package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Special management endpoint to shutdown the node. Optionally provide a timeout 
 * parameter to indicate that the node should begin shutting down after a number of 
 * seconds. 
 * /v2/shutdown 
 */
public class ShutdownNode extends Query {

	public ShutdownNode(Client client) {
		super(client, new HttpMethod("post"));
	}

	public ShutdownNode timeout(Long timeout) {
		addQuery("timeout", String.valueOf(timeout));
		return this;
	}

	@Override
	public Response<String> execute() throws Exception {
		Response<String> resp = baseExecute();
		resp.setValueType(String.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("shutdown"));

		return qd;
	}
}