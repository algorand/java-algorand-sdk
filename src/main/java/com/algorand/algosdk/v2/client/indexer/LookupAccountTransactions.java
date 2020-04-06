package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;


/*
	Lookup account transactions. 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("accounts");
		sb.append("/");
		sb.append(accountId);
		sb.append("/");
		sb.append("transactions");
		sb.append("?");

		boolean added = false;

		if (this.accountIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("accountId=");
			sb.append(accountId);
			added = true;
		}
		if (this.afterTimeIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("afterTime=");
			sb.append(afterTime);
			added = true;
		}
		if (this.assetIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("assetId=");
			sb.append(assetId);
			added = true;
		}
		if (this.beforeTimeIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("beforeTime=");
			sb.append(beforeTime);
			added = true;
		}
		if (this.currencyGreaterThanIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyGreaterThan=");
			sb.append(currencyGreaterThan);
			added = true;
		}
		if (this.currencyLessThanIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("currencyLessThan=");
			sb.append(currencyLessThan);
			added = true;
		}
		if (this.limitIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("limit=");
			sb.append(limit);
			added = true;
		}
		if (this.maxRoundIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("maxRound=");
			sb.append(maxRound);
			added = true;
		}
		if (this.minRoundIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("minRound=");
			sb.append(minRound);
			added = true;
		}
		if (this.nextIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("next=");
			sb.append(next);
			added = true;
		}
		if (this.notePrefixIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("notePrefix=");
			sb.append(notePrefix);
			added = true;
		}
		if (this.roundIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("round=");
			sb.append(round);
			added = true;
		}
		if (this.sigTypeIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("sigType=");
			sb.append(sigType);
			added = true;
		}
		if (this.txIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("txId=");
			sb.append(txId);
			added = true;
		}
		if (this.txTypeIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("txType=");
			sb.append(txType);
			added = true;
		}

		return sb.toString();
	}
}