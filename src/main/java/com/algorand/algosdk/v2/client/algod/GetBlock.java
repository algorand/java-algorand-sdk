package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BlockResponse;


/**
 * /v2/blocks/{round} 
 */
public class GetBlock extends Query {
	private String format;
	private long round;

	private boolean formatIsSet;
	private boolean roundIsSet;

	public GetBlock(Client client, long round) {
		super(client, "get");
		this.round = round;
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public GetBlock setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}

	@Override
	public Response<BlockResponse> execute() throws Exception {
		Response<BlockResponse> resp = baseExecute();
		resp.setValueType(BlockResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.formatIsSet) {
			qd.addQuery("format", String.valueOf(format));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("blocks"));
		qd.addPathSegment(String.valueOf(round));

		return qd;
	}
}