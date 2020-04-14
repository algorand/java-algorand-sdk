package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/**
 * Search for transactions. /transactions 
 */
public class SearchForTransactions extends Query {
	private String address;
	private String addressRole;
	private String afterTime;
	private long assetId;
	private String beforeTime;
	private long currencyGreaterThan;
	private long currencyLessThan;
	private boolean excludeCloseTo;
	private long limit;
	private long maxRound;
	private long minRound;
	private String next;
	private String notePrefix;
	private long round;
	private String sigType;
	private String txId;
	private String txType;

	private boolean addressIsSet;
	private boolean addressRoleIsSet;
	private boolean afterTimeIsSet;
	private boolean assetIdIsSet;
	private boolean beforeTimeIsSet;
	private boolean currencyGreaterThanIsSet;
	private boolean currencyLessThanIsSet;
	private boolean excludeCloseToIsSet;
	private boolean limitIsSet;
	private boolean maxRoundIsSet;
	private boolean minRoundIsSet;
	private boolean nextIsSet;
	private boolean notePrefixIsSet;
	private boolean roundIsSet;
	private boolean sigTypeIsSet;
	private boolean txIdIsSet;
	private boolean txTypeIsSet;

	public SearchForTransactions(Client client) {
		super(client, "get");
	}

	/**
	 * Only include transactions with this address in one of the transaction fields. 
	 */
	public SearchForTransactions setAddress(String address) {
		this.address = address;
		this.addressIsSet = true;
		return this;
	}

	/**
	 * Combine with the address parameter to define what type of address to search for. 
	 */
	public SearchForTransactions setAddressRole(String addressRole) {
		this.addressRole = addressRole;
		this.addressRoleIsSet = true;
		return this;
	}

	/**
	 * Include results after the given time. Must be an RFC 3339 formatted string. 
	 */
	public SearchForTransactions setAfterTime(String afterTime) {
		this.afterTime = afterTime;
		this.afterTimeIsSet = true;
		return this;
	}

	/**
	 * Asset ID 
	 */
	public SearchForTransactions setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}

	/**
	 * Include results before the given time. Must be an RFC 3339 formatted string. 
	 */
	public SearchForTransactions setBeforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
		this.beforeTimeIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount greater than this value. MicroAlgos are the 
	 * default currency unless an asset-id is provided, in which case the asset will be 
	 * used. 
	 */
	public SearchForTransactions setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}

	/**
	 * Results should have an amount less than this value. MicroAlgos are the default 
	 * currency unless an asset-id is provided, in which case the asset will be used. 
	 */
	public SearchForTransactions setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}

	/**
	 * Combine with address and address-role parameters to define what type of address 
	 * to search for. The close to fields are normally treated as a receiver, if you 
	 * would like to exclude them set this parameter to true. 
	 */
	public SearchForTransactions setExcludeCloseTo(boolean excludeCloseTo) {
		this.excludeCloseTo = excludeCloseTo;
		this.excludeCloseToIsSet = true;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForTransactions setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}

	/**
	 * Include results at or before the specified max-round. 
	 */
	public SearchForTransactions setMaxRound(long maxRound) {
		this.maxRound = maxRound;
		this.maxRoundIsSet = true;
		return this;
	}

	/**
	 * Include results at or after the specified min-round. 
	 */
	public SearchForTransactions setMinRound(long minRound) {
		this.minRound = minRound;
		this.minRoundIsSet = true;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForTransactions setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}

	/**
	 * Specifies a prefix which must be contained in the note field. 
	 */
	public SearchForTransactions setNotePrefix(String notePrefix) {
		this.notePrefix = notePrefix;
		this.notePrefixIsSet = true;
		return this;
	}

	/**
	 * Include results for the specified round. 
	 */
	public SearchForTransactions setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}

	/**
	 * SigType filters just results using the specified type of signature: * sig - 
	 * Standard * msig - MultiSig * lsig - LogicSig 
	 */
	public SearchForTransactions setSigType(String sigType) {
		this.sigType = sigType;
		this.sigTypeIsSet = true;
		return this;
	}

	/**
	 * Lookup the specific transaction by ID. 
	 */
	public SearchForTransactions setTxId(String txId) {
		this.txId = txId;
		this.txIdIsSet = true;
		return this;
	}
	public SearchForTransactions setTxType(String txType) {
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
		if (this.addressIsSet) {
			qd.addQuery("address", String.valueOf(address));
		}
		if (this.addressRoleIsSet) {
			qd.addQuery("addressRole", String.valueOf(addressRole));
		}
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
		if (this.excludeCloseToIsSet) {
			qd.addQuery("excludeCloseTo", String.valueOf(excludeCloseTo));
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
		qd.addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}