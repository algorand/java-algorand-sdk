package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/*
	Lookup transactions for an asset. /assets/{asset-id}/transactions 
 */
public class LookupAssetTransactions extends Query {
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

	public LookupAssetTransactions(Client client) {
		super(client);
	}
	public LookupAssetTransactions setAddress(String address) {
		this.address = address;
		this.addressIsSet = true;
		return this;
	}
	public LookupAssetTransactions setAddressRole(String addressRole) {
		this.addressRole = addressRole;
		this.addressRoleIsSet = true;
		return this;
	}
	public LookupAssetTransactions setAfterTime(String afterTime) {
		this.afterTime = afterTime;
		this.afterTimeIsSet = true;
		return this;
	}
	public LookupAssetTransactions setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}
	public LookupAssetTransactions setBeforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
		this.beforeTimeIsSet = true;
		return this;
	}
	public LookupAssetTransactions setCurrencyGreaterThan(long currencyGreaterThan) {
		this.currencyGreaterThan = currencyGreaterThan;
		this.currencyGreaterThanIsSet = true;
		return this;
	}
	public LookupAssetTransactions setCurrencyLessThan(long currencyLessThan) {
		this.currencyLessThan = currencyLessThan;
		this.currencyLessThanIsSet = true;
		return this;
	}
	public LookupAssetTransactions setExcludeCloseTo(boolean excludeCloseTo) {
		this.excludeCloseTo = excludeCloseTo;
		this.excludeCloseToIsSet = true;
		return this;
	}
	public LookupAssetTransactions setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}
	public LookupAssetTransactions setMaxRound(long maxRound) {
		this.maxRound = maxRound;
		this.maxRoundIsSet = true;
		return this;
	}
	public LookupAssetTransactions setMinRound(long minRound) {
		this.minRound = minRound;
		this.minRoundIsSet = true;
		return this;
	}
	public LookupAssetTransactions setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}
	public LookupAssetTransactions setNotePrefix(String notePrefix) {
		this.notePrefix = notePrefix;
		this.notePrefixIsSet = true;
		return this;
	}
	public LookupAssetTransactions setRound(long round) {
		this.round = round;
		this.roundIsSet = true;
		return this;
	}
	public LookupAssetTransactions setSigType(String sigType) {
		this.sigType = sigType;
		this.sigTypeIsSet = true;
		return this;
	}
	public LookupAssetTransactions setTxId(String txId) {
		this.txId = txId;
		this.txIdIsSet = true;
		return this;
	}
	public LookupAssetTransactions setTxType(String txType) {
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
		if (this.addressIsSet) {
			qd.addQuery("address", String.valueOf(address));
		}
		if (this.addressRoleIsSet) {
			qd.addQuery("addressRole", String.valueOf(addressRole));
		}
		if (this.afterTimeIsSet) {
			qd.addQuery("afterTime", String.valueOf(afterTime));
		}
		if  (!this.assetIdIsSet) {
			throw new RuntimeException("assetId is not set, and it is a required parameter.");
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
		qd.addPathSegment(String.valueOf("assets"));
		qd.addPathSegment(String.valueOf(assetId));
		qd.addPathSegment(String.valueOf("transactions"));

		return qd;
	}
}