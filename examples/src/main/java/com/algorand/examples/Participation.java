package com.algorand.examples;

import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class Participation {

    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();

        // example: TRANSACTION_KEYREG_ONLINE_CREATE
        // get suggested parameters
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();

        String address = "EW64GC6F24M7NDSC5R3ES4YUVE3ZXXNMARJHDCCCLIHZU6TBEOC7XRSBG4";

        String votekey = "eXq34wzh2UIxCZaI1leALKyAvSz/+XOe0wqdHagM+bw=";
        String skey = "X84ReKTmp+yfgmMCbbokVqeFFFrKQeFZKEXG89SXwm4=";

        Long numRounds = 100000l;  // sets up keys for 100000 rounds
        Long keyDilution = (long) Math.sqrt(numRounds);  // dilution default is sqrt num rounds

        Transaction keyRegTxn = Transaction.KeyRegistrationTransactionBuilder().suggestedParams(sp)
                .sender(address)
                .selectionPublicKeyBase64(skey)
                .participationPublicKeyBase64(votekey)
                .voteFirst(sp.lastRound)
                .voteLast(sp.lastRound + numRounds)
                .voteKeyDilution(keyDilution)
                .build();
        // ... sign and send to network
        // example: TRANSACTION_KEYREG_ONLINE_CREATE

        // example: TRANSACTION_KEYREG_OFFLINE_CREATE
        // create keyreg transaction to take this account offline
        Transaction keyRegOfflineTxn = Transaction.KeyRegistrationTransactionBuilder().suggestedParams(sp)
                .sender(address)
                .build();
        // example: TRANSACTION_KEYREG_OFFLINE_CREATE
        System.out.println(keyRegOfflineTxn);
    }
    
}

