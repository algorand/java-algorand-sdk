package com.algorand.examples;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.ExportKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ListKeysRequest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.kmd.client.ApiException;

public class ExampleUtils {

    private static String kmd_host = "http://localhost:4002";
    private static String kmd_token = "a".repeat(64);
    private static KmdApi kmd = null;

    private static String algod_host = "http://localhost";
    private static int algod_port = 4001;
    private static String algod_token = "a".repeat(64);

    private static String indexer_host = "http://localhost";
    private static int indexer_port = 8980;
    private static String indexer_token = "a".repeat(64);

    public static AlgodClient getAlgodClient() {
        return new AlgodClient(algod_host, algod_port, algod_token);
    }

    public static IndexerClient getIndexerClient() {
        return new IndexerClient(indexer_host, indexer_port, indexer_token);
    }

    public static List<Account> getSandboxAccounts() throws Exception {
        // Initialize KMD v1 client
        KmdClient kmdClient = new KmdClient();
        kmdClient.setBasePath(kmd_host);
        kmdClient.setApiKey(kmd_token);
        kmd = new KmdApi(kmdClient);

        // Get accounts from sandbox.
        String walletHandle = getDefaultWalletHandle();
        List<Address> addresses = getWalletAccounts(walletHandle);

        List<Account> accts = new ArrayList<>();
        for (Address addr : addresses) {
            byte[] pk = lookupPrivateKey(addr, walletHandle);
            accts.add(new Account(pk));
        }
        return accts;
    }

    public static byte[] lookupPrivateKey(Address addr, String walletHandle) throws ApiException {
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(addr.toString());
        req.setWalletHandleToken(walletHandle);
        req.setWalletPassword("");
        return kmd.exportKey(req).getPrivateKey();
    }

    public static String getDefaultWalletHandle() throws ApiException {
        for (APIV1Wallet w : kmd.listWallets().getWallets()) {
            if (w.getName().equals("unencrypted-default-wallet")) {
                InitWalletHandleTokenRequest tokenreq = new InitWalletHandleTokenRequest();
                tokenreq.setWalletId(w.getId());
                tokenreq.setWalletPassword("");
                return kmd.initWalletHandleToken(tokenreq).getWalletHandleToken();
            }
        }
        throw new RuntimeException("Default wallet not found.");
    }

    public static List<Address> getWalletAccounts(String walletHandle) throws ApiException, NoSuchAlgorithmException {
        List<Address> accounts = new ArrayList<>();
        ListKeysRequest keysRequest = new ListKeysRequest();
        keysRequest.setWalletHandleToken(walletHandle);
        for (String addr : kmd.listKeysInWallet(keysRequest).getAddresses()) {
            accounts.add(new Address(addr));
        }
        return accounts;
    }

    public static void sendPrint(AlgodClient algodClient, SignedTransaction stxn, String name) throws Exception {
        Response<PostTransactionsResponse> submitResult = algodClient.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(stxn)).execute();
        printTxnResults(algodClient, submitResult.body(), name);
    }

    public static void printTxnResults(AlgodClient client, PostTransactionsResponse ptr, String name) throws Exception {
        PendingTransactionResponse result = Utils.waitForConfirmation(client, ptr.txId, 4);
        System.out.printf("%s\ttransaction (%s) was confirmed in round %d\n", name, ptr.txId, result.confirmedRound);
    }
}