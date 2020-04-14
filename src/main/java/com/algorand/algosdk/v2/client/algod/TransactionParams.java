package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;


/**
 * /v2/transactions/params 
 */
public class TransactionParams extends Query {


	public TransactionParams(Client client) {
		super(client, "get");
	}

	@Override
	public Response<TransactionParametersResponse> execute() throws Exception {
		Response<TransactionParametersResponse> resp = baseExecute();
		resp.setValueType(TransactionParametersResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("transactions"));
		qd.addPathSegment(String.valueOf("params"));

		return qd;
	}
}