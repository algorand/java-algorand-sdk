package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;


/**
 * Special management endpoint to shutdown the node. Optionally provide a timeout 
 * parameter to indicate that the node should begin shutting down after a number of 
 * seconds. /v2/shutdown 
 */
public class ShutdownNode extends Query {
	private long timeout;

	private boolean timeoutIsSet;

	public ShutdownNode(Client client) {
		super(client, "post");
	}
	public ShutdownNode setTimeout(long timeout) {
		this.timeout = timeout;
		this.timeoutIsSet = true;
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
		if (this.timeoutIsSet) {
			qd.addQuery("timeout", String.valueOf(timeout));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("shutdown"));

		return qd;
	}
}