package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/**
 * Get the list of pending transactions, sorted by priority, in decreasing order, 
 * truncated at the end at MAX. If MAX = 0, returns all pending transactions. 
 * /v2/transactions/pending 
 */
public class GetPendingTransactions extends Query {
	private String format;
	private long max;

	private boolean formatIsSet;
	private boolean maxIsSet;

	public GetPendingTransactions(Client client) {
		super(client, "get");
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public GetPendingTransactions setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}

	/**
	 * Truncated number of transactions to display. If max=0, returns all pending txns. 
	 */
	public GetPendingTransactions setMax(long max) {
		this.max = max;
		this.maxIsSet = true;
		return this;
	}

	@Override
	public Response<PendingTransactionsResponse> execute() throws Exception {
		Response<PendingTransactionsResponse> resp = baseExecute();
		resp.setValueType(PendingTransactionsResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.formatIsSet) {
			qd.addQuery("format", String.valueOf(format));
		}
		if (this.maxIsSet) {
			qd.addQuery("max", String.valueOf(max));
		}
		qd.addPathSegment(String.valueOf("v2"));
		qd.addPathSegment(String.valueOf("transactions"));
		qd.addPathSegment(String.valueOf("pending"));

		return qd;
	}
}