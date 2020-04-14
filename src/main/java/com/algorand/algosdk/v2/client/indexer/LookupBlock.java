package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Block;


/**
 * Lookup block. /blocks/{round-number} 
 */
public class LookupBlock extends Query {
	private long roundNumber;

	private boolean roundNumberIsSet;

	public LookupBlock(Client client, long roundNumber) {
		super(client, "get");
		this.roundNumber = roundNumber;
	}

	@Override
	public Response<Block> execute() throws Exception {
		Response<Block> resp = baseExecute();
		resp.setValueType(Block.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		qd.addPathSegment(String.valueOf("blocks"));
		qd.addPathSegment(String.valueOf(roundNumber));

		return qd;
	}
}