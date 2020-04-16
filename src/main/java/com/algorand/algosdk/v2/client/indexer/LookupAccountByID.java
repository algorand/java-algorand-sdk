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
	public String accountId() {
		return this.accountId;
	}
	private Long round;
	public Long round() {
		return this.round;
	}

	/**
	 * @param accountId account string 
	 */
	public LookupAccountByID(Client client, String accountId) {
		super(client, "get");
		this.accountId = accountId;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAccountByID round(Long round) {
		this.round = round;
		addQuery("round", String.valueOf(round));
		return this;
	}

	@Override
	public Response<AccountResponse> execute() throws Exception {
		Response<AccountResponse> resp = baseExecute();
		resp.setValueType(AccountResponse.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("accounts"));
		addPathSegment(String.valueOf(accountId));

		return qd;
	}
}