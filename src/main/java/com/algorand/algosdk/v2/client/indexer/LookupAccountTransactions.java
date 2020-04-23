package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Lookup account transactions. 
 * /accounts/{account-id}/transactions 
 */
public class LookupAccountTransactions extends Query {

	private String accountId;

	/**
	 * @param accountId account string 
	 */
	public LookupAccountTransactions(Client client, String accountId) {
		super(client, "get");
		this.accountId = accountId;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAccountTransactions afterTime(java.util.Date afterTime) {
		addQuery("after-time", new java.text.SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(afterTime));
		return this;
	}

	/**
	 * Asset ID 
	 */
	public LookupAccountTransactions assetId(Long assetId) {
		addQuery("asset-id", String.valueOf(assetId));
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAccountTransactions beforeTime(java.util.Date beforeTime) {
		addQuery("before-time", new java.text.SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(beforeTime));
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAccountTransactions currencyGreaterThan(Long currencyGreaterThan) {
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAccountTransactions currencyLessThan(Long currencyLessThan) {
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAccountTransactions limit(Long limit) {
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public LookupAccountTransactions maxRound(Long maxRound) {
		addQuery("max-round", String.valueOf(maxRound));
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public LookupAccountTransactions minRound(Long minRound) {
		addQuery("min-round", String.valueOf(minRound));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAccountTransactions next(String next) {
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public LookupAccountTransactions notePrefix(String notePrefix) {
		addQuery("note-prefix", String.valueOf(notePrefix));
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAccountTransactions round(Long round) {
		addQuery("round", String.valueOf(round));
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: 
	 *   sig - Standard 
	 *   msig - MultiSig 
	 *   lsig - LogicSig 
	 */
	public LookupAccountTransactions sigType(SigType sigType) {
		addQuery("sig-type", String.valueOf(sigType));
		return this;
	}
	public enum SigType {
		SIG,
		MSIG,
		LSIG
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public LookupAccountTransactions txId(Address txId) {
		addQuery("tx-id", String.valueOf(txId));
		return this;
	}

	public LookupAccountTransactions txType(TxType txType) {
		addQuery("tx-type", String.valueOf(txType));
		return this;
	}
	public enum TxType {
		PAY,
		KEYREG,
		ACFG,
		AXFER,
		AFRZ
	}

	@Override
	public Response<TransactionsResponse> execute() throws Exception {
		Response<TransactionsResponse> resp = baseExecute();
		resp.setValueType(TransactionsResponse.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("accounts"));
		addPathSegment(String.valueOf(accountId));
		addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}