package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.SupplyResponse;


/**
 * /v2/ledger/supply 
 */
public class GetSupply extends Query {

	public GetSupply(Client client) {
		super(client, "get");
	}

	@Override
	public Response<SupplyResponse> execute() throws Exception {
		Response<SupplyResponse> resp = baseExecute();
		resp.setValueType(SupplyResponse.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("ledger"));
		addPathSegment(String.valueOf("supply"));

		return qd;
	}
}