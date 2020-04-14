package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;


/**
 * Given a transaction id of a recently submitted transaction, it returns 
 * information about it. There are several cases when this might succeed: - 
 * transaction committed (committed round > 0) - transaction still in the pool 
 * (committed round = 0, pool error = "") - transaction removed from pool due to 
 * error (committed round = 0, pool error != "") Or the transaction may have 
 * happened sufficiently long ago that the node no longer remembers it, and this 
 * will return an error. /v2/transactions/pending/{txid} 
 */
public class PendingTransactionInformation extends Query {
	private String format;
	private String txid;

	private boolean formatIsSet;
	private boolean txidIsSet;

	public PendingTransactionInformation(Client client, String txid) {
		super(client, "get");
		this.txid = txid;
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public PendingTransactionInformation setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}

	@Override
	public Response<PendingTransactionResponse> execute() throws Exception {
		Response<PendingTransactionResponse> resp = baseExecute();
		resp.setValueType(PendingTransactionResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.formatIsSet) {
			qd.addQuery("format", String.valueOf(format));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("transactions"));
		qd.addPathSegment(String.valueOf("pending"));
		qd.addPathSegment(String.valueOf(txid));

		return qd;
	}
}