package com.algorand.examples;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.ExportKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ListKeysRequest;
import com.algorand.algosdk.kmd.client.ApiException;

public class Utils {

    private static String host = "http://localhost:4002";
    private static String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static KmdApi kmd = null;

    public static AlgodClient getAlgodClient() {

    }

    public static List<Account> getSandboxAccounts() throws Exception {
        // Initialize KMD v1 client
        KmdClient kmdClient = new KmdClient();
        kmdClient.setBasePath(host);
        kmdClient.setApiKey(token);
        kmd = new KmdApi(kmdClient);

        // Get accounts from sandbox.
        String walletHandle = getDefaultWalletHandle();
        List<Address> addresses  = getWalletAccounts(walletHandle);

        List<Account> accts = new ArrayList<>();
        for(Address addr: addresses){
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
}