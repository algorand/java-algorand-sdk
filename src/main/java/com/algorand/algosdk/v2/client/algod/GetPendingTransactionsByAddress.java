package com.algorand.algosdk.v2.client.algod;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionsResponse;


/**
 * Get the list of pending transactions by address, sorted by priority, in 
 * decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending 
 * transactions. /v2/accounts/{address}/transactions/pending 
 */
public class GetPendingTransactionsByAddress extends Query {
	private String address;
	private String format;
	private long max;

	private boolean addressIsSet;
	private boolean formatIsSet;
	private boolean maxIsSet;

	public GetPendingTransactionsByAddress(Client client, String address) {
		super(client, "get");
		this.address = address;
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public GetPendingTransactionsByAddress setFormat(String format) {
		this.format = format;
		this.formatIsSet = true;
		return this;
	}

	/**
	 * Truncated number of transactions to display. If max=0, returns all pending txns. 
	 */
	public GetPendingTransactionsByAddress setMax(long max) {
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
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(address));
		qd.addPathSegment(String.valueOf("transactions"));
		qd.addPathSegment(String.valueOf("pending"));

		return qd;
	}
}