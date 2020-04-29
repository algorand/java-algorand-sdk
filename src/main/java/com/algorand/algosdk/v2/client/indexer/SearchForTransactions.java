package com.algorand.algosdk.v2.client.indexer;

import java.util.Date;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Enums;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Search for transactions. 
 * /transactions 
 */
public class SearchForTransactions extends Query {

	public SearchForTransactions(Client client) {
		super(client, "get");
	}

	/**
	 * Only include transactions with this address in one of the transaction fields. 
	 */
	public SearchForTransactions address(Address address) {
		addQuery("address", String.valueOf(address));
		return this;
	}

	/**
	 * Combine with the address parameter to define what type of address to search for. 
	 */
	public SearchForTransactions addressRole(Enums.AddressRole addressRole) {
		addQuery("address-role", String.valueOf(addressRole));
		return this;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public SearchForTransactions afterTime(Date afterTime) {
		addQuery("after-time", afterTime.toInstant().atZone(java.time.ZoneOffset.ofHours(0)).toString());
		return this;
	}

	/**
	 * Asset ID 
	 */
	public SearchForTransactions assetId(Long assetId) {
		addQuery("asset-id", String.valueOf(assetId));
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public SearchForTransactions beforeTime(Date beforeTime) {
		addQuery("before-time", beforeTime.toInstant().atZone(java.time.ZoneOffset.ofHours(0)).toString());
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public SearchForTransactions currencyGreaterThan(Long currencyGreaterThan) {
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public SearchForTransactions currencyLessThan(Long currencyLessThan) {
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Combine with address and address-role parameters to define what type of address 
	 * to search for. The close to fields are normally treated as a receiver, if you 
	 * would like to exclude them set this parameter to true. 
	 */
	public SearchForTransactions excludeCloseTo(Boolean excludeCloseTo) {
		addQuery("exclude-close-to", String.valueOf(excludeCloseTo));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForTransactions limit(Long limit) {
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public SearchForTransactions maxRound(Long maxRound) {
		addQuery("max-round", String.valueOf(maxRound));
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public SearchForTransactions minRound(Long minRound) {
		addQuery("min-round", String.valueOf(minRound));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForTransactions next(String next) {
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public SearchForTransactions notePrefix(byte[] notePrefix) {
		addQuery("note-prefix", Encoder.encodeToBase64(notePrefix));
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public SearchForTransactions round(Long round) {
		addQuery("round", String.valueOf(round));
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: 
	 *   sig - Standard 
	 *   msig - MultiSig 
	 *   lsig - LogicSig 
	 */
	public SearchForTransactions sigType(Enums.SigType sigType) {
		addQuery("sig-type", String.valueOf(sigType));
		return this;
	}

	public SearchForTransactions txType(Enums.TxType txType) {
		addQuery("tx-type", String.valueOf(txType));
		return this;
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public SearchForTransactions txid(String txid) {
		addQuery("txid", String.valueOf(txid));
		return this;
	}

	@Override
	public Response<TransactionsResponse> execute() throws Exception {
		Response<TransactionsResponse> resp = baseExecute();
		resp.setValueType(TransactionsResponse.class);
		return resp;
	}

	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}