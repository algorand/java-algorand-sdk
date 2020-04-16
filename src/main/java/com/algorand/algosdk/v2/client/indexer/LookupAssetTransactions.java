package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Lookup transactions for an asset. /assets/{asset-id}/transactions 
 */
public class LookupAssetTransactions extends Query {

	private String address;
	public String address() {
		return this.address;
	}
	private String addressRole;
	public String addressRole() {
		return this.addressRole;
	}
	private String afterTime;
	public String afterTime() {
		return this.afterTime;
	}
	private Long assetId;
	public Long assetId() {
		return this.assetId;
	}
	private String beforeTime;
	public String beforeTime() {
		return this.beforeTime;
	}
	private Long currencyGreaterThan;
	public Long currencyGreaterThan() {
		return this.currencyGreaterThan;
	}
	private Long currencyLessThan;
	public Long currencyLessThan() {
		return this.currencyLessThan;
	}
	private Boolean excludeCloseTo;
	public Boolean excludeCloseTo() {
		return this.excludeCloseTo;
	}
	private Long limit;
	public Long limit() {
		return this.limit;
	}
	private Long maxRound;
	public Long maxRound() {
		return this.maxRound;
	}
	private Long minRound;
	public Long minRound() {
		return this.minRound;
	}
	private String next;
	public String next() {
		return this.next;
	}
	private String notePrefix;
	public String notePrefix() {
		return this.notePrefix;
	}
	private Long round;
	public Long round() {
		return this.round;
	}
	private String sigType;
	public String sigType() {
		return this.sigType;
	}
	private String txId;
	public String txId() {
		return this.txId;
	}
	private String txType;
	public String txType() {
		return this.txType;
	}

	public LookupAssetTransactions(Client client, Long assetId) {
		super(client, "get");
		this.assetId = assetId;
	}

	/**
	 * Only include transactions with this address in one of the transaction fields. 
	 */
	public LookupAssetTransactions address(String address) {
		this.address = address;
		addQuery("address", String.valueOf(address));
		return this;
	}

	/**
	 * Combine with the address parameter to define what type of address to search for. 
	 */
	public LookupAssetTransactions addressRole(String addressRole) {
		this.addressRole = addressRole;
		addQuery("address-role", String.valueOf(addressRole));
		return this;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAssetTransactions afterTime(String afterTime) {
		this.afterTime = afterTime;
		addQuery("after-time", String.valueOf(afterTime));
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAssetTransactions beforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
		addQuery("before-time", String.valueOf(beforeTime));
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAssetTransactions currencyGreaterThan(Long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAssetTransactions currencyLessThan(Long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		addQuery("currency-less-than", String.valueOf(currencyLessThan));
		return this;
	}

	/**
	 * Combine with address and address-role parameters to define what type of address 
	 * to search for. The close to fields are normally treated as a receiver, if you 
	 * would like to exclude them set this parameter to true. 
	 */
	public LookupAssetTransactions excludeCloseTo(Boolean excludeCloseTo) {
		this.excludeCloseTo = excludeCloseTo;
		addQuery("exclude-close-to", String.valueOf(excludeCloseTo));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAssetTransactions limit(Long limit) {
		this.limit = limit;
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public LookupAssetTransactions maxRound(Long maxRound) {
		this.maxRound = maxRound;
		addQuery("max-round", String.valueOf(maxRound));
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public LookupAssetTransactions minRound(Long minRound) {
		this.minRound = minRound;
		addQuery("min-round", String.valueOf(minRound));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAssetTransactions next(String next) {
		this.next = next;
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public LookupAssetTransactions notePrefix(String notePrefix) {
		this.notePrefix = notePrefix;
		addQuery("note-prefix", String.valueOf(notePrefix));
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAssetTransactions round(Long round) {
		this.round = round;
		addQuery("round", String.valueOf(round));
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: * sig - 
	 * Standard * msig - MultiSig * lsig - LogicSig 
	 */
	public LookupAssetTransactions sigType(String sigType) {
		this.sigType = sigType;
		addQuery("sig-type", String.valueOf(sigType));
		return this;
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public LookupAssetTransactions txId(String txId) {
		this.txId = txId;
		addQuery("tx-id", String.valueOf(txId));
		return this;
	}
	public LookupAssetTransactions txType(String txType) {
		this.txType = txType;
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