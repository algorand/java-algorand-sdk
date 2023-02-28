package com.algorand.examples;

import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletInitResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;

public class KMDExamples {

    public static void main(String[] args) throws ApiException {
        // example: KMD_CREATE_CLIENT 
        String kmdHost = "http://localhost:4002";
        String kmdToken =  "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        KmdClient kmdClient = new KmdClient();
        kmdClient.setBasePath(kmdHost);
        kmdClient.setApiKey(kmdToken);

        KmdApi kmd = new KmdApi(kmdClient);
        // example: KMD_CREATE_CLIENT 


        // example: KMD_CREATE_WALLET 
        CreateWalletRequest cwr = new CreateWalletRequest();
        cwr.setWalletName("MyNewWallet");
        cwr.setWalletPassword("supersecretpassword");
        cwr.setWalletDriverName("sqlite"); // other option is `ledger`
        APIV1POSTWalletResponse result = kmd.createWallet(cwr);
        APIV1Wallet wallet = result.getWallet();
        System.out.printf("Wallet name: %s\n", wallet.getName());
        // example: KMD_CREATE_WALLET 

        // example: KMD_CREATE_ACCOUNT
        // First grab a handle that we can re-use
        InitWalletHandleTokenRequest tokenReq = new InitWalletHandleTokenRequest();
        tokenReq.setWalletId(wallet.getId());
        tokenReq.setWalletPassword("supersecretpassword");
        APIV1POSTWalletInitResponse handleTokenResp = kmd.initWalletHandleToken(tokenReq);

        GenerateKeyRequest gkr = new GenerateKeyRequest();
        gkr.setWalletHandleToken(handleTokenResp.getWalletHandleToken());
        APIV1POSTKeyResponse generatedKey = kmd.generateKey(gkr);
        System.out.printf("New account: %s\n", generatedKey.getAddress());
        // example: KMD_CREATE_ACCOUNT

        // /example: KMD_RECOVER_WALLET
        // /...
        // /example: KMD_RECOVER_WALLET

        // /example: KMD_EXPORT_ACCOUNT
        // /example: KMD_EXPORT_ACCOUNT

        // /example: KMD_IMPORT_ACCOUNT
        // /example: KMD_IMPORT_ACCOUNT

        // Get accounts from sandbox.
        // String walletHandle = getDefaultWalletHandle();
        // List<Address> addresses  = getWalletAccounts(walletHandle);

        // List<Account> accts = new ArrayList<>();
        // for(Address addr: addresses){
        //     byte[] pk = lookupPrivateKey(addr, walletHandle);
        //     accts.add(new Account(pk));
        // }
        // return accts;

    }
    
}
