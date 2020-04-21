package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Lookup transactions for an asset. 
 * /assets/{asset-id}/transactions 
 */
public class LookupAssetTransactions extends Query {

	private Long assetId;

	public LookupAssetTransactions(Client client, Long assetId) {
		super(client, "get");
		this.assetId = assetId;
	}

	/**
	 * Only include transactions with this address in one of the transaction fields. 
	 */
	public LookupAssetTransactions address(java.util.Date address) {
		addQuery("address", new java.text.SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(address));
		return this;
	}

	/**
	 * Combine with the address parameter to define what type of address to search for. 
	 */
	public LookupAssetTransactions addressRole(String addressRole) {
		addQuery("address-role", String.valueOf(addressRole));
		return this;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAssetTransactions afterTime(java.util.Date afterTime) {
		addQuery("after-time", new java.text.SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(afterTime));
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAssetTransactions beforeTime(java.util.Date beforeTime) {
		addQuery("before-time", new java.text.SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(beforeTime));
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAssetTransactions currencyGreaterThan(Long currencyGreaterThan) {
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAssetTransactions currencyLessThan(Long currencyLessThan) {
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Combine with address and address-role parameters to define what type of address 
	 * to search for. The close to fields are normally treated as a receiver, if you 
	 * would like to exclude them set this parameter to true. 
	 */
	public LookupAssetTransactions excludeCloseTo(Boolean excludeCloseTo) {
		addQuery("exclude-close-to", String.valueOf(excludeCloseTo));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAssetTransactions limit(Long limit) {
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public LookupAssetTransactions maxRound(Long maxRound) {
		addQuery("max-round", String.valueOf(maxRound));
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public LookupAssetTransactions minRound(Long minRound) {
		addQuery("min-round", String.valueOf(minRound));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAssetTransactions next(String next) {
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public LookupAssetTransactions notePrefix(String notePrefix) {
		addQuery("note-prefix", String.valueOf(notePrefix));
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAssetTransactions round(Long round) {
		addQuery("round", String.valueOf(round));
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: 
	 *   sig - Standard 
	 *   msig - MultiSig 
	 *   lsig - LogicSig 
	 */
	public LookupAssetTransactions sigType(String sigType) {
		addQuery("sig-type", String.valueOf(sigType));
		return this;
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public LookupAssetTransactions txId(Address txId) {
		addQuery("tx-id", String.valueOf(txId));
		return this;
	}

	public LookupAssetTransactions txType(String txType) {
		addQuery("tx-type", String.valueOf(txType));
		return this;
	}

	@Override
	public Response<TransactionsResponse> execute() throws Exception {
		Response<TransactionsResponse> resp = baseExecute();
		resp.setValueType(TransactionsResponse.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("assets"));
		addPathSegment(String.valueOf(assetId));
		addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}