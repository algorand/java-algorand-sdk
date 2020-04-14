package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Lookup account transactions. /accounts/{account-id}/transactions 
 */
public class LookupAccountTransactions extends Query {
	private String accountId;
	private String afterTime;
	private long assetId;
	private String beforeTime;
	private long currencyGreaterThan;
	private long currencyLessThan;
	private long limit;
	private long maxRound;
	private long minRound;
	private String next;
	private String notePrefix;
	private long round;
	private String sigType;
	private String txId;
	private String txType;

	private boolean accountIdIsSet;
	private boolean afterTimeIsSet;
	private boolean assetIdIsSet;
	private boolean beforeTimeIsSet;
	private boolean currencyGreaterThanIsSet;
	private boolean currencyLessThanIsSet;
	private boolean limitIsSet;
	private boolean maxRoundIsSet;
	private boolean minRoundIsSet;
	private boolean nextIsSet;
	private boolean notePrefixIsSet;
	private boolean roundIsSet;
	private boolean sigTypeIsSet;
	private boolean txIdIsSet;
	private boolean txTypeIsSet;

	public LookupAccountTransactions(Client client, String accountId) {
		super(client, "get");
		this.accountId = accountId;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAccountTransactions setAfterTime(String afterTime) {
		this.afterTime = afterTime;
		this.afterTimeIsSet = true;
		return this;
	}

	/**
	 * Asset ID 
	 */
	public LookupAccountTransactions setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public LookupAccountTransactions setBeforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
		this.beforeTimeIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public LookupAccountTransactions setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public LookupAccountTransactions setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public LookupAccountTransactions setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public LookupAccountTransactions setMaxRound(long maxRound) {
		this.maxRound = maxRound;
		this.maxRoundIsSet = true;
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public LookupAccountTransactions setMinRound(long minRound) {
		this.minRound = minRound;
		this.minRoundIsSet = true;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public LookupAccountTransactions setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public LookupAccountTransactions setNotePrefix(String notePrefix) {
		this.notePrefix = notePrefix;
		this.notePrefixIsSet = true;
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public LookupAccountTransactions setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: * sig - 
	 * Standard * msig - MultiSig * lsig - LogicSig 
	 */
	public LookupAccountTransactions setSigType(String sigType) {
		this.sigType = sigType;
		this.sigTypeIsSet = true;
		return this;
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public LookupAccountTransactions setTxId(String txId) {
		this.txId = txId;
		this.txIdIsSet = true;
		return this;
	}
	public LookupAccountTransactions setTxType(String txType) {
		this.txType = txType;
		this.txTypeIsSet = true;
		return this;
	}

	@Override
	public Response<TransactionsResponse> execute() throws Exception {
		Response<TransactionsResponse> resp = baseExecute();
		resp.setValueType(TransactionsResponse.class);
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.afterTimeIsSet) {
			qd.addQuery("afterTime", String.valueOf(afterTime));
		}
		if (this.assetIdIsSet) {
			qd.addQuery("assetId", String.valueOf(assetId));
		}
		if (this.beforeTimeIsSet) {
			qd.addQuery("beforeTime", String.valueOf(beforeTime));
		}
		if (this.currencyGreaterThanIsSet) {
			qd.addQuery("currencyGreaterThan", String.valueOf(currencyGreaterThan));
		}
		if (this.currencyLessThanIsSet) {
			qd.addQuery("currencyLessThan", String.valueOf(currencyLessThan));
		}
		if (this.limitIsSet) {
			qd.addQuery("limit", String.valueOf(limit));
		}
		if (this.maxRoundIsSet) {
			qd.addQuery("maxRound", String.valueOf(maxRound));
		}
		if (this.minRoundIsSet) {
			qd.addQuery("minRound", String.valueOf(minRound));
		}
		if (this.nextIsSet) {
			qd.addQuery("next", String.valueOf(next));
		}
		if (this.notePrefixIsSet) {
			qd.addQuery("notePrefix", String.valueOf(notePrefix));
		}
		if (this.roundIsSet) {
			qd.addQuery("round", String.valueOf(round));
		}
		if (this.sigTypeIsSet) {
			qd.addQuery("sigType", String.valueOf(sigType));
		}
		if (this.txIdIsSet) {
			qd.addQuery("txId", String.valueOf(txId));
		}
		if (this.txTypeIsSet) {
			qd.addQuery("txType", String.valueOf(txType));
		}
		qd.addPathSegment(String.valueOf("accounts"));
		qd.addPathSegment(String.valueOf(accountId));
		qd.addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}