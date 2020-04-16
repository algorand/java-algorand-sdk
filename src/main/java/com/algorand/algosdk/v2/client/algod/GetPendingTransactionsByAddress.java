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
	public String address() {
		return this.address;
	}
	private String format;
	public String format() {
		return this.format;
	}
	private Long max;
	public Long max() {
		return this.max;
	}

	/**
	 * @param address An account public key 
	 */
	public GetPendingTransactionsByAddress(Client client, String address) {
		super(client, "get");
		this.address = address;
	}

	/**
	 * Configures whether the response object is JSON or MessagePack encoded. 
	 */
	public GetPendingTransactionsByAddress format(String format) {
		this.format = format;
		addQuery("format", String.valueOf(format));
		return this;
	}

	/**
	 * Truncated number of transactions to display. If max=0, returns all pending txns. 
	 */
	public GetPendingTransactionsByAddress max(Long max) {
		this.max = max;
		addQuery("max", String.valueOf(max));
		return this;
	}

	@Override
	public Response<PendingTransactionsResponse> execute() throws Exception {
		Response<PendingTransactionsResponse> resp = baseExecute();
		resp.setValueType(PendingTransactionsResponse.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("v2"));
		addPathSegment(String.valueOf("accounts"));
		addPathSegment(String.valueOf(address));
		addPathSegment(String.valueOf("transactions"));
		addPathSegment(String.valueOf("pending"));

		return qd;
	}
}