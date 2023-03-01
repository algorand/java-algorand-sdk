package com.algorand.examples;

import org.bouncycastle.util.Arrays;

import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyExportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyImportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTMasterKeyExportResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletInitResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.ExportKeyRequest;
import com.algorand.algosdk.kmd.client.model.ExportMasterKeyRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.ImportKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.mnemonic.Mnemonic;

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
        // create a new CreateWalletRequest and set parameters 
        CreateWalletRequest cwr = new CreateWalletRequest();
        cwr.setWalletName(walletName);
        cwr.setWalletPassword(password);
        cwr.setWalletDriverName("sqlite"); // other option is `ledger`
        // using our client, pass the request
        APIV1POSTWalletResponse result = kmd.createWallet(cwr);
        APIV1Wallet wallet = result.getWallet();
        System.out.printf("Wallet name: %s\n", wallet.getName());
        // example: KMD_CREATE_WALLET 

        String handleToken = getHandle(kmd, wallet, password);

        // example: KMD_BACKUP_WALLET
        ExportMasterKeyRequest mker = new ExportMasterKeyRequest();
        mker.setWalletHandleToken(handleToken);
        mker.setWalletPassword(password);
        APIV1POSTMasterKeyExportResponse masterKeyResp = kmd.exportMasterKey(mker);
        byte[] backupKey = masterKeyResp.getMasterDerivationKey();
        // example: KMD_BACKUP_WALLET

        // example: KMD_RECOVER_WALLET
        // create a new CreateWalletRequest and set parameters 
        CreateWalletRequest recoverRequest = new CreateWalletRequest();
        recoverRequest.setWalletName("Recovered:"+walletName);
        recoverRequest.setWalletPassword(password);
        recoverRequest.setWalletDriverName("sqlite");
        // Pass the specific derivation key we want to use
        // to recover the wallet
        recoverRequest.setMasterDerivationKey(backupKey);
        APIV1POSTWalletResponse recoverResponse = kmd.createWallet(recoverRequest);
        APIV1Wallet recoveredWallet = recoverResponse.getWallet();
        System.out.printf("Wallet name: %s\n", recoveredWallet.getName());
        // example: KMD_RECOVER_WALLET

        // example: KMD_CREATE_ACCOUNT
        // create a request to generate a new key, using the handle token
        GenerateKeyRequest gkr = new GenerateKeyRequest();
        gkr.setWalletHandleToken(handleToken);
        APIV1POSTKeyResponse generatedKey = kmd.generateKey(gkr);
        String addr = generatedKey.getAddress();
        System.out.printf("New account: %s\n", addr);
        // example: KMD_CREATE_ACCOUNT

        // example: KMD_EXPORT_ACCOUNT
        ExportKeyRequest ekr = new ExportKeyRequest();
        ekr.setAddress(addr);
        ekr.setWalletHandleToken(handleToken);
        ekr.setWalletPassword(password);
        APIV1POSTKeyExportResponse exportedKeyResp = kmd.exportKey(ekr);
        byte[] exportedKey = exportedKeyResp.getPrivateKey();
        String mn = Mnemonic.fromKey(Arrays.copyOfRange(exportedKey, 0, 32));
        System.out.printf("Exported mnemonic: %s\n", mn);
        // example: KMD_EXPORT_ACCOUNT

        String recoveredWalletHandleToken = getHandle(kmd, recoveredWallet, password);

        // example: KMD_IMPORT_ACCOUNT
        ImportKeyRequest ikr = new ImportKeyRequest();
        ikr.setPrivateKey(Arrays.copyOfRange(exportedKey, 0, 32));
        ikr.setWalletHandleToken(recoveredWalletHandleToken);
        APIV1POSTKeyImportResponse importResp = kmd.importKey(ikr);
        System.out.printf("Imported account: %s\n", importResp.getAddress());
        // /example: KMD_IMPORT_ACCOUNT
    }

    public static String getHandle(KmdApi kmd, APIV1Wallet wallet, String password) throws ApiException {
        // example: KMD_CREATE_HANDLE_TOKEN
        // grab a handle that we can re-use
        InitWalletHandleTokenRequest tokenReq = new InitWalletHandleTokenRequest();
        tokenReq.setWalletId(wallet.getId());
        tokenReq.setWalletPassword(password);
        APIV1POSTWalletInitResponse handleTokenResp = kmd.initWalletHandleToken(tokenReq);
        String handleToken = handleTokenResp.getWalletHandleToken();
        // example: KMD_CREATE_HANDLE_TOKEN
        return handleToken;
    }

}
