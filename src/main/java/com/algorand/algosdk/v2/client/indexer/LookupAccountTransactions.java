package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/*
	Lookup account transactions. /accounts/{account-id}/transactions 
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

	public LookupAccountTransactions(Client client) {
		super(client);
	}
	public LookupAccountTransactions setAccountId(String accountId) {
		this.accountId = accountId;
		this.accountIdIsSet = true;
		return this;
	}
	public LookupAccountTransactions setAfterTime(String afterTime) {
		this.afterTime = afterTime;
		this.afterTimeIsSet = true;
		return this;
	}
	public LookupAccountTransactions setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}
	public LookupAccountTransactions setBeforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
		this.beforeTimeIsSet = true;
		return this;
	}
	public LookupAccountTransactions setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}
	public LookupAccountTransactions setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}
	public LookupAccountTransactions setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}
	public LookupAccountTransactions setMaxRound(long maxRound) {
		this.maxRound = maxRound;
		this.maxRoundIsSet = true;
		return this;
	}
	public LookupAccountTransactions setMinRound(long minRound) {
		this.minRound = minRound;
		this.minRoundIsSet = true;
		return this;
	}
	public LookupAccountTransactions setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}
	public LookupAccountTransactions setNotePrefix(String notePrefix) {
		this.notePrefix = notePrefix;
		this.notePrefixIsSet = true;
		return this;
	}
	public LookupAccountTransactions setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}
	public LookupAccountTransactions setSigType(String sigType) {
		this.sigType = sigType;
		this.sigTypeIsSet = true;
		return this;
	}
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

	public TransactionsResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		TransactionsResponse resp;
		try {
			resp = mapper.readValue(response, TransactionsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	protected QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.accountIdIsSet) {
			throw new RuntimeException("accountId is not set, and it is a required parameter.");
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