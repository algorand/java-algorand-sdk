package com.algorand.algosdk.example;

import com.algorand.algosdk.algod.Client;
import com.algorand.algosdk.algod.ClientFactory;
import com.algorand.algosdk.algod.model.Supply;
import com.algorand.algosdk.kmd.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.model.CreateWalletRequest;
import retrofit2.Response;


public class Main {

    public static void main(String args[]) {
        final String algodApiAddr = "http://localhost:8080";
        final String algodApiToken = "d6f33a522f465ff12f0d263f2c3b707ac2f560bacad4d859914ada7e827902b3";
        final String kmdApiAddr = "http://localhost:7833";
        final String kmdApiToken = "1bce699faef65c80da8da6201bd0639b3ea4205c4fa05d24f94469efa2418f2d";


        // Get total number of algos circulating in the system
        Client aC = ClientFactory.create(algodApiAddr, algodApiToken);
        try {
            Supply s = aC.getSupply().execute().body();
            System.out.println("Total Algorand Supply: " + s.getTotalMoney());
            System.out.println("Online Algorand Supply: " + s.getOnlineMoney());
        } catch (Exception e) {
            System.out.println("Failed to fetch total supply: " + e);
            return;
        }

        // Create a wallet with kmd rest api
        com.algorand.algosdk.kmd.Client kC = com.algorand.algosdk.kmd.ClientFactory.create(kmdApiAddr, kmdApiToken);
        try {
            CreateWalletRequest req = new CreateWalletRequest()
                    .walletName("arbitrary-wallet-name")
                    .walletDriverName("sqlite");
            Response<APIV1POSTWalletResponse> r = kC.createWallet(req).execute();
            if (!r.isSuccessful()) {
                // print out error code, etc.
                System.out.println("Request failed: " + r + "\nerrorBody: " + r.errorBody().string());
            }
            System.out.println("Request failed: " + r + "\nerrorBody: " + r.errorBody().string());
            APIV1POSTWalletResponse res = kC.createWallet(req).execute().body();
            System.out.println("Created wallet: " + res.getWallet());
        } catch (Exception e) {
            System.out.println("Failed to create wallet: " + e);
            return;
        }
    }
}
