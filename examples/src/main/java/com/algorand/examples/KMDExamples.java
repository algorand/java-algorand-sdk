package com.algorand.examples;

import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTMasterKeyExportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletInitResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.ExportMasterKeyRequest;
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


        String walletName =  "MyNewWallet";
        String password = "supersecretpassword";
        // example: KMD_CREATE_WALLET 
        //create a new request
        CreateWalletRequest cwr = new CreateWalletRequest();
        cwr.setWalletName(walletName);
        cwr.setWalletPassword(password);
        cwr.setWalletDriverName("sqlite"); // other option is `ledger`
        APIV1POSTWalletResponse result = kmd.createWallet(cwr);
        APIV1Wallet wallet = result.getWallet();
        System.out.printf("Wallet name: %s\n", wallet.getName());
        // example: KMD_CREATE_WALLET 

        // example: KMD_CREATE_ACCOUNT
        // First grab a handle that we can re-use
        InitWalletHandleTokenRequest tokenReq = new InitWalletHandleTokenRequest();
        tokenReq.setWalletId(wallet.getId());
        tokenReq.setWalletPassword(password);
        APIV1POSTWalletInitResponse handleTokenResp = kmd.initWalletHandleToken(tokenReq);

        String handleToken = handleTokenResp.getWalletHandleToken()
        GenerateKeyRequest gkr = new GenerateKeyRequest();
        gkr.setWalletHandleToken(handleToken);
        APIV1POSTKeyResponse generatedKey = kmd.generateKey(gkr);
        System.out.printf("New account: %s\n", generatedKey.getAddress());
        // example: KMD_CREATE_ACCOUNT

        ExportMasterKeyRequest mker = new ExportMasterKeyRequest();
        mker.setWalletHandleToken(handleToken);
        APIV1POSTMasterKeyExportResponse masterKeyResp = kmd.exportMasterKey(mker);
        byte[] backupKey = masterKeyResp.getMasterDerivationKey();

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
