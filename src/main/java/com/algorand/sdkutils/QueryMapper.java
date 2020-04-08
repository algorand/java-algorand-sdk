package com.algorand.sdkutils;

import com.algorand.algosdk.v2.client.indexer.*;
import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;

public class QueryMapper {

	static Query getClass(String name, Client client) {
		switch (name) {
		case "searchAccounts":
			return new SearchAccounts(client);
		case "lookupAccountByID":
			return new LookupAccountByID(client);
		case "lookupAccountTransactions":
			return new LookupAccountTransactions(client);
		case "searchForAssets":
			return new SearchForAssets(client);
		case "lookupAssetByID":
			return new LookupAssetByID(client);
		case "lookupAssetBalances":
			return new LookupAssetBalances(client);
		case "lookupAssetTransactions":
			return new LookupAssetTransactions(client);
		case "lookupBlock":
			return new LookupBlock(client);
		case "searchForTransactions":
			return new SearchForTransactions(client);
		}
		return null;
	}

	static void setValue(Query q, String className, String property, String value) {
		switch (className) {
		case "searchAccounts":
			switch (property) {
			case "assetId":
				((SearchAccounts)q).setAssetId(value);
			break;
			case "currencyGreaterThan":
				((SearchAccounts)q).setCurrencyGreaterThan(Long.valueOf(value));
			break;
			case "currencyLessThan":
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
		case "lookupAccountByID":
			switch (property) {
			case "accountId":
				((LookupAccountByID)q).setAccountId(value);
			break;
			case "round":
				((LookupAccountByID)q).setRound(Long.valueOf(value));
			break;
			}
		case "lookupAccountTransactions":
			switch (property) {
			case "accountId":
				((LookupAccountTransactions)q).setAccountId(value);
			break;
			case "afterTime":
				((LookupAccountTransactions)q).setAfterTime(value);
			break;
			case "assetId":
				((LookupAccountTransactions)q).setAssetId(Long.valueOf(value));
			break;
			case "beforeTime":
				((LookupAccountTransactions)q).setBeforeTime(value);
			break;
			case "currencyGreaterThan":
				((LookupAccountTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
			break;
			case "currencyLessThan":
				((LookupAccountTransactions)q).setCurrencyLessThan(Long.valueOf(value));
			break;
			case "limit":
				((LookupAccountTransactions)q).setLimit(Long.valueOf(value));
			break;
			case "maxRound":
				((LookupAccountTransactions)q).setMaxRound(Long.valueOf(value));
			break;
			case "minRound":
				((LookupAccountTransactions)q).setMinRound(Long.valueOf(value));
			break;
			case "next":
				((LookupAccountTransactions)q).setNext(value);
			break;
			case "notePrefix":
				((LookupAccountTransactions)q).setNotePrefix(value);
			break;
			case "round":
				((LookupAccountTransactions)q).setRound(Long.valueOf(value));
			break;
			case "sigType":
				((LookupAccountTransactions)q).setSigType(value);
			break;
			case "txId":
				((LookupAccountTransactions)q).setTxId(value);
			break;
			case "txType":
				((LookupAccountTransactions)q).setTxType(value);
			break;
			}
		case "searchForAssets":
			switch (property) {
			case "assetId":
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
		case "lookupAssetByID":
			switch (property) {
			case "assetId":
				((LookupAssetByID)q).setAssetId(Long.valueOf(value));
			break;
			}
		case "lookupAssetBalances":
			switch (property) {
			case "assetId":
				((LookupAssetBalances)q).setAssetId(Long.valueOf(value));
			break;
			case "currencyGreaterThan":
				((LookupAssetBalances)q).setCurrencyGreaterThan(Long.valueOf(value));
			break;
			case "currencyLessThan":
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
		case "lookupAssetTransactions":
			switch (property) {
			case "address":
				((LookupAssetTransactions)q).setAddress(value);
			break;
			case "addressRole":
				((LookupAssetTransactions)q).setAddressRole(value);
			break;
			case "afterTime":
				((LookupAssetTransactions)q).setAfterTime(value);
			break;
			case "assetId":
				((LookupAssetTransactions)q).setAssetId(Long.valueOf(value));
			break;
			case "beforeTime":
				((LookupAssetTransactions)q).setBeforeTime(value);
			break;
			case "currencyGreaterThan":
				((LookupAssetTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
			break;
			case "currencyLessThan":
				((LookupAssetTransactions)q).setCurrencyLessThan(Long.valueOf(value));
			break;
			case "excludeCloseTo":
				((LookupAssetTransactions)q).setExcludeCloseTo(Boolean.valueOf(value));
			break;
			case "limit":
				((LookupAssetTransactions)q).setLimit(Long.valueOf(value));
			break;
			case "maxRound":
				((LookupAssetTransactions)q).setMaxRound(Long.valueOf(value));
			break;
			case "minRound":
				((LookupAssetTransactions)q).setMinRound(Long.valueOf(value));
			break;
			case "next":
				((LookupAssetTransactions)q).setNext(value);
			break;
			case "notePrefix":
				((LookupAssetTransactions)q).setNotePrefix(value);
			break;
			case "round":
				((LookupAssetTransactions)q).setRound(Long.valueOf(value));
			break;
			case "sigType":
				((LookupAssetTransactions)q).setSigType(value);
			break;
			case "txId":
				((LookupAssetTransactions)q).setTxId(value);
			break;
			case "txType":
				((LookupAssetTransactions)q).setTxType(value);
			break;
			}
		case "lookupBlock":
			switch (property) {
			case "roundNumber":
				((LookupBlock)q).setRoundNumber(Long.valueOf(value));
			break;
			}
		case "searchForTransactions":
			switch (property) {
			case "address":
				((SearchForTransactions)q).setAddress(value);
			break;
			case "addressRole":
				((SearchForTransactions)q).setAddressRole(value);
			break;
			case "afterTime":
				((SearchForTransactions)q).setAfterTime(value);
			break;
			case "assetId":
				((SearchForTransactions)q).setAssetId(Long.valueOf(value));
			break;
			case "beforeTime":
				((SearchForTransactions)q).setBeforeTime(value);
			break;
			case "currencyGreaterThan":
				((SearchForTransactions)q).setCurrencyGreaterThan(Long.valueOf(value));
			break;
			case "currencyLessThan":
				((SearchForTransactions)q).setCurrencyLessThan(Long.valueOf(value));
			break;
			case "excludeCloseTo":
				((SearchForTransactions)q).setExcludeCloseTo(Boolean.valueOf(value));
			break;
			case "limit":
				((SearchForTransactions)q).setLimit(Long.valueOf(value));
			break;
			case "maxRound":
				((SearchForTransactions)q).setMaxRound(Long.valueOf(value));
			break;
			case "minRound":
				((SearchForTransactions)q).setMinRound(Long.valueOf(value));
			break;
			case "next":
				((SearchForTransactions)q).setNext(value);
			break;
			case "notePrefix":
				((SearchForTransactions)q).setNotePrefix(value);
			break;
			case "round":
				((SearchForTransactions)q).setRound(Long.valueOf(value));
			break;
			case "sigType":
				((SearchForTransactions)q).setSigType(value);
			break;
			case "txId":
				((SearchForTransactions)q).setTxId(value);
			break;
			case "txType":
				((SearchForTransactions)q).setTxType(value);
			break;
			}

		}
	}

	static String lookup(Query q, String className) {
		switch (className) {
		case "searchAccounts":
			return ((SearchAccounts)q).lookup().toString();
		case "lookupAccountByID":
			return ((LookupAccountByID)q).lookup().toString();
		case "lookupAccountTransactions":
			return ((LookupAccountTransactions)q).lookup().toString();
		case "searchForAssets":
			return ((SearchForAssets)q).lookup().toString();
		case "lookupAssetByID":
			return ((LookupAssetByID)q).lookup().toString();
		case "lookupAssetBalances":
			return ((LookupAssetBalances)q).lookup().toString();
		case "lookupAssetTransactions":
			return ((LookupAssetTransactions)q).lookup().toString();
		case "lookupBlock":
			return ((LookupBlock)q).lookup().toString();
		case "searchForTransactions":
			return ((SearchForTransactions)q).lookup().toString();
		}
		return null;
	}
}