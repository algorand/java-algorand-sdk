package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.ApiClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.DefaultApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.Supply;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;


public class Main {

    public static void main(String args[]) throws Exception {
        final String ALGOD_API_ADDR = "http://localhost:8080";
        final String ALGOD_API_TOKEN = "d6f33a522f465ff12f0d263f2c3b707ac2f560bacad4d859914ada7e827902b3";
        final String SRC_ACCOUNT = "viable grain female caution grant mind cry mention pudding oppose orchard people forget similar social gossip marble fish guitar art morning ring west above concert";
        final String DEST_ADDR = "KV2XGKMXGYJ6PWYQA5374BYIQBL3ONRMSIARPCFCJEAMAHQEVYPB7PL3KU";

        ApiClient client = new ApiClient();
        client.setBasePath(ALGOD_API_ADDR);
        // Configure API key authorization: api_key
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(ALGOD_API_TOKEN);

        DefaultApi algodApiInstance = new DefaultApi(client);
        try {
            Supply supply = algodApiInstance.getSupply();
            System.out.println("Total Algorand Supply: " + supply.getTotalMoney());
            System.out.println("Online Algorand Supply: " + supply.getOnlineMoney());
        } catch (ApiException e) {
            System.err.println("Exception when calling algod#getSupply");
            e.printStackTrace();
        }

        long fee = 1;
        try {
            TransactionParams params = algodApiInstance.transactionParams();
            fee = params.getFee();
            System.out.println("Suggested Fee: " + fee);
        } catch (ApiException e) {
            System.err.println("Exception when calling algod#transactionParams");
            e.printStackTrace();
        }

        // set up crypto, in order to sign transactions
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // Generate a new transaction using randomly generated accounts (this is invalid, since src has no money...)
        Account src = new Account(SRC_ACCOUNT);
        long amount = 100;
        long firstRound = 300;
        long lastRound = 400;
        Transaction tx = new Transaction(src.getAddress(), new Address(DEST_ADDR), fee, amount, firstRound, lastRound);
        SignedTransaction signedTx = src.signTransaction(tx);
        System.out.println("Signed transaction with txid: " + signedTx.transactionID);

        // send the transaction to the network
        try {
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
            TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);
            System.out.println("Successfully sent tx with id: " + id);
        } catch (ApiException e) {
            // This is generally expected, but should give us an informative error message.
            System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
        }

        // now, demo kmd api
//        kmdApi();
    }

    public static void kmdApi() {
        final String KMD_API_ADDR = "http://localhost:7833";
        final String KMD_API_TOKEN = "1bce699faef65c80da8da6201bd0639b3ea4205c4fa05d24f94469efa2418f2d";

        // Create a wallet with kmd rest api
        com.algorand.algosdk.kmd.client.ApiClient client = new com.algorand.algosdk.kmd.client.ApiClient();
        client.setBasePath(KMD_API_ADDR);
        // Configure API key authorization: api_key
        com.algorand.algosdk.kmd.client.auth.ApiKeyAuth api_key = (com.algorand.algosdk.kmd.client.auth.ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(KMD_API_TOKEN);
        com.algorand.algosdk.kmd.client.api.DefaultApi kmdApiInstance = new com.algorand.algosdk.kmd.client.api.DefaultApi(client);

        APIV1POSTWalletResponse wallet;
        try {
            CreateWalletRequest req = new CreateWalletRequest()
                    .walletName("arbitrary-wallet-name-2")
                    .walletDriverName("sqlite");
            wallet = kmdApiInstance.createWallet(req);
            System.out.println("Created wallet id: " + wallet.getWallet().getId());
        } catch (com.algorand.algosdk.kmd.client.ApiException e) {
            System.out.println("Failed to create wallet: " + e.getResponseBody());
            e.printStackTrace();
        }

    }
}
