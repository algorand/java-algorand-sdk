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

	private Long roundNumber;


	/**
	 * @param roundNumber Round number 
	 */
	public LookupBlock(Client client, Long roundNumber) {
		super(client, "get");
		this.roundNumber = roundNumber;
	}

	@Override
	public Response<Block> execute() throws Exception {
		Response<Block> resp = baseExecute();
		resp.setValueType(Block.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("blocks"));
		addPathSegment(String.valueOf(roundNumber));

		return qd;
	}
}