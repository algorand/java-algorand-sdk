package com.algorand.sdkutils.generated;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import com.algorand.algosdk.v2.client.common.Utils;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.algod.*;
import com.algorand.algosdk.v2.client.indexer.*;
import com.algorand.algosdk.v2.client.model.Enums;
import com.algorand.algosdk.v2.client.common.*;

public class QueryMapper {

	public static Query getClass(String name, IndexerClient client, String args[]) throws NoSuchAlgorithmException {
		switch (name) {
		case "searchForAccounts":
			return client.searchForAccounts();
		case "lookupAccountByID":
			return client.lookupAccountByID(new Address(args[0]));
		case "lookupAccountTransactions":
			return client.lookupAccountTransactions(new Address(args[0]));
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

	public static Query getClass(String name, AlgodClient client, String args[]) throws NoSuchAlgorithmException {
		switch (name) {
		case "AccountInformation":
			return client.AccountInformation(new Address(args[0]));
		case "GetPendingTransactionsByAddress":
			return client.GetPendingTransactionsByAddress(new Address(args[0]));
		case "GetBlock":
			return client.GetBlock(Long.valueOf(args[0]));
		case "GetSupply":
			return client.GetSupply();
		case "GetStatus":
			return client.GetStatus();
		case "WaitForBlock":
			return client.WaitForBlock(Long.valueOf(args[0]));
		case "RawTransaction":
			return client.RawTransaction();
		case "TransactionParams":
			return client.TransactionParams();
		case "GetPendingTransactions":
			return client.GetPendingTransactions();
		case "PendingTransactionInformation":
			return client.PendingTransactionInformation(args[0]);
		}
		return null;
	}

	public static void setValue(Query q, String className, String property, String value) throws ParseException, NoSuchAlgorithmException {
		switch (className) {
		case "searchForAccounts":
			switch (property) {
			case "asset-id":
				((SearchForAccounts)q).assetId(Long.valueOf(value));
				break;
			case "currency-greater-than":
				((SearchForAccounts)q).currencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((SearchForAccounts)q).currencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((SearchForAccounts)q).limit(Long.valueOf(value));
				break;
			case "next":
				((SearchForAccounts)q).next(value);
				break;
			case "round":
				((SearchForAccounts)q).round(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAccountByID":
			switch (property) {
			case "round":
				((LookupAccountByID)q).round(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAccountTransactions":
			switch (property) {
			case "after-time":
				((LookupAccountTransactions)q).afterTime(Utils.parseDate(value));
				break;
			case "asset-id":
				((LookupAccountTransactions)q).assetId(Long.valueOf(value));
				break;
			case "before-time":
				((LookupAccountTransactions)q).beforeTime(Utils.parseDate(value));
				break;
			case "currency-greater-than":
				((LookupAccountTransactions)q).currencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAccountTransactions)q).currencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((LookupAccountTransactions)q).limit(Long.valueOf(value));
				break;
			case "max-round":
				((LookupAccountTransactions)q).maxRound(Long.valueOf(value));
				break;
			case "min-round":
				((LookupAccountTransactions)q).minRound(Long.valueOf(value));
				break;
			case "next":
				((LookupAccountTransactions)q).next(value);
				break;
			case "note-prefix":
				((LookupAccountTransactions)q).notePrefix(Encoder.decodeFromBase64(value));
				break;
			case "round":
				((LookupAccountTransactions)q).round(Long.valueOf(value));
				break;
			case "sig-type":
				((LookupAccountTransactions)q).sigType(getSigType(value));
				break;
			case "tx-type":
				((LookupAccountTransactions)q).txType(getTxType(value));
				break;
			case "txid":
				((LookupAccountTransactions)q).txid(value);
				break;
			}
			break;
		case "searchForAssets":
			switch (property) {
			case "asset-id":
				((SearchForAssets)q).assetId(Long.valueOf(value));
				break;
			case "creator":
				((SearchForAssets)q).creator(value);
				break;
			case "limit":
				((SearchForAssets)q).limit(Long.valueOf(value));
				break;
			case "name":
				((SearchForAssets)q).name(value);
				break;
			case "next":
				((SearchForAssets)q).next(value);
				break;
			case "unit":
				((SearchForAssets)q).unit(value);
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
				((LookupAssetBalances)q).currencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAssetBalances)q).currencyLessThan(Long.valueOf(value));
				break;
			case "limit":
				((LookupAssetBalances)q).limit(Long.valueOf(value));
				break;
			case "next":
				((LookupAssetBalances)q).next(value);
				break;
			case "round":
				((LookupAssetBalances)q).round(Long.valueOf(value));
				break;
			}
			break;
		case "lookupAssetTransactions":
			switch (property) {
			case "address":
				((LookupAssetTransactions)q).address(new Address(value));
				break;
			case "address-role":
				((LookupAssetTransactions)q).addressRole(getAddressRole(value));
				break;
			case "after-time":
				((LookupAssetTransactions)q).afterTime(Utils.parseDate(value));
				break;
			case "before-time":
				((LookupAssetTransactions)q).beforeTime(Utils.parseDate(value));
				break;
			case "currency-greater-than":
				((LookupAssetTransactions)q).currencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((LookupAssetTransactions)q).currencyLessThan(Long.valueOf(value));
				break;
			case "exclude-close-to":
				((LookupAssetTransactions)q).excludeCloseTo(Boolean.valueOf(value));
				break;
			case "limit":
				((LookupAssetTransactions)q).limit(Long.valueOf(value));
				break;
			case "max-round":
				((LookupAssetTransactions)q).maxRound(Long.valueOf(value));
				break;
			case "min-round":
				((LookupAssetTransactions)q).minRound(Long.valueOf(value));
				break;
			case "next":
				((LookupAssetTransactions)q).next(value);
				break;
			case "note-prefix":
				((LookupAssetTransactions)q).notePrefix(Encoder.decodeFromBase64(value));
				break;
			case "round":
				((LookupAssetTransactions)q).round(Long.valueOf(value));
				break;
			case "sig-type":
				((LookupAssetTransactions)q).sigType(getSigType(value));
				break;
			case "tx-type":
				((LookupAssetTransactions)q).txType(getTxType(value));
				break;
			case "txid":
				((LookupAssetTransactions)q).txid(value);
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
				((SearchForTransactions)q).address(new Address(value));
				break;
			case "address-role":
				((SearchForTransactions)q).addressRole(getAddressRole(value));
				break;
			case "after-time":
				((SearchForTransactions)q).afterTime(Utils.parseDate(value));
				break;
			case "asset-id":
				((SearchForTransactions)q).assetId(Long.valueOf(value));
				break;
			case "before-time":
				((SearchForTransactions)q).beforeTime(Utils.parseDate(value));
				break;
			case "currency-greater-than":
				((SearchForTransactions)q).currencyGreaterThan(Long.valueOf(value));
				break;
			case "currency-less-than":
				((SearchForTransactions)q).currencyLessThan(Long.valueOf(value));
				break;
			case "exclude-close-to":
				((SearchForTransactions)q).excludeCloseTo(Boolean.valueOf(value));
				break;
			case "limit":
				((SearchForTransactions)q).limit(Long.valueOf(value));
				break;
			case "max-round":
				((SearchForTransactions)q).maxRound(Long.valueOf(value));
				break;
			case "min-round":
				((SearchForTransactions)q).minRound(Long.valueOf(value));
				break;
			case "next":
				((SearchForTransactions)q).next(value);
				break;
			case "note-prefix":
				((SearchForTransactions)q).notePrefix(Encoder.decodeFromBase64(value));
				break;
			case "round":
				((SearchForTransactions)q).round(Long.valueOf(value));
				break;
			case "sig-type":
				((SearchForTransactions)q).sigType(getSigType(value));
				break;
			case "tx-type":
				((SearchForTransactions)q).txType(getTxType(value));
				break;
			case "txid":
				((SearchForTransactions)q).txid(value);
				break;
			}
			break;
		case "AccountInformation":
			switch (property) {
			}
			break;
		case "GetPendingTransactionsByAddress":
			switch (property) {
			case "format":
				((GetPendingTransactionsByAddress)q).format(getFormat(value));
				break;
			case "max":
				((GetPendingTransactionsByAddress)q).max(Long.valueOf(value));
				break;
			}
			break;
		case "GetBlock":
			switch (property) {
			case "format":
				((GetBlock)q).format(getFormat(value));
				break;
			}
			break;
		case "GetSupply":
			switch (property) {
			}
			break;
		case "GetStatus":
			switch (property) {
			}
			break;
		case "WaitForBlock":
			switch (property) {
			}
			break;
		case "RawTransaction":
			switch (property) {
			case "rawtxn":
				((RawTransaction)q).rawtxn(Encoder.decodeFromBase64(value));
				break;
			}
			break;
		case "TransactionParams":
			switch (property) {
			}
			break;
		case "GetPendingTransactions":
			switch (property) {
			case "format":
				((GetPendingTransactions)q).format(getFormat(value));
				break;
			case "max":
				((GetPendingTransactions)q).max(Long.valueOf(value));
				break;
			}
			break;
		case "PendingTransactionInformation":
			switch (property) {
			case "format":
				((PendingTransactionInformation)q).format(getFormat(value));
				break;
			}
			break;

		}
	}

	public static String lookup(Query q, String className) throws Exception {
		Response<?> resp = q.execute();
		if (resp.body() == null) {
			throw new RuntimeException(resp.message());
		}
		return resp.body().toString();
	}

	public static Enums.AddressRole getAddressRole(String val) {
		switch(val.toUpperCase()) {
		case "SENDER":
			return Enums.AddressRole.SENDER;
		case "RECEIVER":
			return Enums.AddressRole.RECEIVER;
		case "FREEZETARGET":
			return Enums.AddressRole.FREEZETARGET;
		default:
			throw new RuntimeException("Enum value not recognized: " + val +"!");
		}
	}
	public static Enums.Format getFormat(String val) {
		switch(val.toUpperCase()) {
		case "JSON":
			return Enums.Format.JSON;
		case "MSGPACK":
			return Enums.Format.MSGPACK;
		default:
			throw new RuntimeException("Enum value not recognized: " + val +"!");
		}
	}
	public static Enums.SigType getSigType(String val) {
		switch(val.toUpperCase()) {
		case "SIG":
			return Enums.SigType.SIG;
		case "MSIG":
			return Enums.SigType.MSIG;
		case "LSIG":
			return Enums.SigType.LSIG;
		default:
			throw new RuntimeException("Enum value not recognized: " + val +"!");
		}
	}
	public static Enums.TxType getTxType(String val) {
		switch(val.toUpperCase()) {
		case "PAY":
			return Enums.TxType.PAY;
		case "KEYREG":
			return Enums.TxType.KEYREG;
		case "ACFG":
			return Enums.TxType.ACFG;
		case "AXFER":
			return Enums.TxType.AXFER;
		case "AFRZ":
			return Enums.TxType.AFRZ;
		default:
			throw new RuntimeException("Enum value not recognized: " + val +"!");
		}
	}
}