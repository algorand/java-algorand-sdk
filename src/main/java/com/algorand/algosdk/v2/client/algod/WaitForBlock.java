package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;


/**
 * Waits for a block to appear after round {round} and returns the node's status at 
 * the time. /v2/status/wait-for-block-after/{round}/ 
 */
public class WaitForBlock extends Query {
	private Long round;


	/**
	 * @param round The round to wait until returning status 
	 */
	public WaitForBlock(Client client, Long round) {
		super(client, "get");
		this.round = round;
	}

	@Override
	public Response<NodeStatusResponse> execute() throws Exception {
		Response<NodeStatusResponse> resp = baseExecute();
		resp.setValueType(NodeStatusResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("status"));
		qd.addPathSegment(String.valueOf("wait-for-block-after"));
		qd.addPathSegment(String.valueOf(round));

		return qd;
	}
}