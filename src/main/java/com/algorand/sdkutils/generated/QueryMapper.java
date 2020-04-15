package com.algorand.sdkutils.generated;

import com.algorand.algosdk.v2.client.indexer.*;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;

public class QueryMapper {

	public static Query getClass(String name, Client client, String args[]) {
		switch (name) {
		case "searchAccounts":
			return client.searchAccounts();
		case "lookupAccountByID":
			return client.lookupAccountByID(args[0]);
		case "lookupAccountTransactions":
			return client.lookupAccountTransactions(args[0]);
		case "searchForAssets":
			return client.searchForAssets();
		case "lookupAssetByID":
			return client.lookupAssetByID(Long.valueOf(args[0]));
		case "lookupAssetBalances":
			return client.lookupAssetBalances(Long.valueOf(args[0]));
		case "lookupAssetTransactions":
			return client.lookupAssetTransactions(Long.valueOf(args[0]));
		case "lookupBlock":
			return client.lookupBlock(Long.valueOf(args[0]));
		case "searchForTransactions":
			return client.searchForTransactions();
		}
		return null;
	}

	public static void setValue(Query q, String className, String property, String value) {
		switch (className) {
		case "searchAccounts":
			switch (property) {
			case "asset-id":
				((SearchAccounts)q).setAssetId(Long.valueOf(value));
				break;
			case "currency-greater-than":
				((SearchAccounts)q).setCurrencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((SearchAccounts)q).setCurrencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((SearchAccounts)q).setLimit(Long.valueOf(value));
				break;
			case "next":
				((SearchAccounts)q).setNext(value);
				break;
			case "round":
				((SearchAccounts)q).setRound(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAccountByID":
			switch (property) {
			case "round":
				((LookupAccountByID)q).setRound(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAccountTransactions":
			switch (property) {
			case "after-time":
				((LookupAccountTransactions)q).setAfterTime(value);
				break;
			case "asset-id":
				((LookupAccountTransactions)q).setAssetId(Long.valueOf(value));
				break;
			case "before-time":
				((LookupAccountTransactions)q).setBeforeTime(value);
				break;
			case "currency-greater-than":
				((LookupAccountTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAccountTransactions)q).setCurrencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((LookupAccountTransactions)q).setLimit(Long.valueOf(value));
				break;
			case "max-round":
				((LookupAccountTransactions)q).setMaxRound(Long.valueOf(value));
				break;
			case "min-round":
				((LookupAccountTransactions)q).setMinRound(Long.valueOf(value));
				break;
			case "next":
				((LookupAccountTransactions)q).setNext(value);
				break;
			case "note-prefix":
				((LookupAccountTransactions)q).setNotePrefix(value);
				break;
			case "round":
				((LookupAccountTransactions)q).setRound(Long.valueOf(value));
				break;
			case "sig-type":
				((LookupAccountTransactions)q).setSigType(value);
				break;
			case "tx-id":
				((LookupAccountTransactions)q).setTxId(value);
				break;
			case "tx-type":
				((LookupAccountTransactions)q).setTxType(value);
				break;
			}
			break;
		case "searchForAssets":
			switch (property) {
			case "asset-id":
				((SearchForAssets)q).setAssetId(Long.valueOf(value));
				break;
			case "creator":
				((SearchForAssets)q).setCreator(value);
				break;
			case "limit":
				((SearchForAssets)q).setLimit(Long.valueOf(value));
				break;
			case "name":
				((SearchForAssets)q).setName(value);
				break;
			case "next":
				((SearchForAssets)q).setNext(value);
				break;
			case "unit":
				((SearchForAssets)q).setUnit(value);
				break;
			}
			break;
		case "lookupAssetByID":
			switch (property) {
			}
			break;
		case "lookupAssetBalances":
			switch (property) {
			case "currency-greater-than":
				((LookupAssetBalances)q).setCurrencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAssetBalances)q).setCurrencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((LookupAssetBalances)q).setLimit(Long.valueOf(value));
				break;
			case "next":
				((LookupAssetBalances)q).setNext(value);
				break;
			case "round":
				((LookupAssetBalances)q).setRound(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAssetTransactions":
			switch (property) {
			case "address":
				((LookupAssetTransactions)q).setAddress(value);
				break;
			case "address-role":
				((LookupAssetTransactions)q).setAddressRole(value);
				break;
			case "after-time":
				((LookupAssetTransactions)q).setAfterTime(value);
				break;
			case "before-time":
				((LookupAssetTransactions)q).setBeforeTime(value);
				break;
			case "currency-greater-than":
				((LookupAssetTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAssetTransactions)q).setCurrencyLessThan(Long.valueOf(value));
				break;
			case "exclude-close-to":
				((LookupAssetTransactions)q).setExcludeCloseTo(Boolean.valueOf(value));
				break;
			case "limit":
				((LookupAssetTransactions)q).setLimit(Long.valueOf(value));
				break;
			case "max-round":
				((LookupAssetTransactions)q).setMaxRound(Long.valueOf(value));
				break;
			case "min-round":
				((LookupAssetTransactions)q).setMinRound(Long.valueOf(value));
				break;
			case "next":
				((LookupAssetTransactions)q).setNext(value);
				break;
			case "note-prefix":
				((LookupAssetTransactions)q).setNotePrefix(value);
				break;
			case "round":
				((LookupAssetTransactions)q).setRound(Long.valueOf(value));
				break;
			case "sig-type":
				((LookupAssetTransactions)q).setSigType(value);
				break;
			case "tx-id":
				((LookupAssetTransactions)q).setTxId(value);
				break;
			case "tx-type":
				((LookupAssetTransactions)q).setTxType(value);
				break;
			}
			break;
		case "lookupBlock":
			switch (property) {
			}
			break;
		case "searchForTransactions":
			switch (property) {
			case "address":
				((SearchForTransactions)q).setAddress(value);
				break;
			case "address-role":
				((SearchForTransactions)q).setAddressRole(value);
				break;
			case "after-time":
				((SearchForTransactions)q).setAfterTime(value);
				break;
			case "asset-id":
				((SearchForTransactions)q).setAssetId(Long.valueOf(value));
				break;
			case "before-time":
				((SearchForTransactions)q).setBeforeTime(value);
				break;
			case "currency-greater-than":
				((SearchForTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((SearchForTransactions)q).setCurrencyLessThan(Long.valueOf(value));
				break;
			case "exclude-close-to":
				((SearchForTransactions)q).setExcludeCloseTo(Boolean.valueOf(value));
				break;
			case "limit":
				((SearchForTransactions)q).setLimit(Long.valueOf(value));
				break;
			case "max-round":
				((SearchForTransactions)q).setMaxRound(Long.valueOf(value));
				break;
			case "min-round":
				((SearchForTransactions)q).setMinRound(Long.valueOf(value));
				break;
			case "next":
				((SearchForTransactions)q).setNext(value);
				break;
			case "note-prefix":
				((SearchForTransactions)q).setNotePrefix(value);
				break;
			case "round":
				((SearchForTransactions)q).setRound(Long.valueOf(value));
				break;
			case "sig-type":
				((SearchForTransactions)q).setSigType(value);
				break;
			case "tx-id":
				((SearchForTransactions)q).setTxId(value);
				break;
			case "tx-type":
				((SearchForTransactions)q).setTxType(value);
				break;
			}
			break;

		}
	}

	public static String lookup(Query q, String className) throws Exception {
		switch (className) {
		case "searchAccounts":
			return ((SearchAccounts)q).execute().body().toString();
		case "lookupAccountByID":
			return ((LookupAccountByID)q).execute().body().toString();
		case "lookupAccountTransactions":
			return ((LookupAccountTransactions)q).execute().body().toString();
		case "searchForAssets":
			return ((SearchForAssets)q).execute().body().toString();
		case "lookupAssetByID":
			return ((LookupAssetByID)q).execute().body().toString();
		case "lookupAssetBalances":
			return ((LookupAssetBalances)q).execute().body().toString();
		case "lookupAssetTransactions":
			return ((LookupAssetTransactions)q).execute().body().toString();
		case "lookupBlock":
			return ((LookupBlock)q).execute().body().toString();
		case "searchForTransactions":
			return ((SearchForTransactions)q).execute().body().toString();
		}
		return null;
	}
}