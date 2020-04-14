package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AccountResponse;


/**
 * Lookup account information. /accounts/{account-id} 
 */
public class LookupAccountByID extends Query {
	private String accountId;
	private long round;

	private boolean accountIdIsSet;
	private boolean roundIsSet;

	public LookupAccountByID(Client client, String accountId) {
		super(client, "get");
		this.accountId = accountId;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAccountByID setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	@Override
	public Response<AccountResponse> execute() throws Exception {
		Response<AccountResponse> resp = baseExecute();
		resp.setValueType(AccountResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.roundIsSet) {
			qd.addQuery("round", String.valueOf(round));
		}
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(accountId));

		return qd;
	}
}