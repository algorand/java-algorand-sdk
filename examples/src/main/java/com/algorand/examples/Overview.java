package com.algorand.examples;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

public class Overview {
    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        // grab the first one from the sandbox kmd
        Account acct = accts.get(0);
        Account acct2 = accts.get(1);

        // example: TRANSACTION_PAYMENT_CREATE
        Response<TransactionParametersResponse> suggestedParams = algodClient.TransactionParams().execute();
        Integer amount = 1000000; // 1 Algo
        Transaction ptxn = Transaction.PaymentTransactionBuilder()
            .sender(acct.getAddress())
            .amount(amount)
            .receiver(acct2.getAddress())
            .suggestedParams(suggestedParams.body()).build();
        // example: TRANSACTION_PAYMENT_CREATE

        
        // example: TRANSACTION_PAYMENT_SIGN
        SignedTransaction sptxn = acct.signTransaction(ptxn);
        // example: TRANSACTION_PAYMENT_SIGN

        // example: TRANSACTION_PAYMENT_SUBMIT
        // encode the transaction
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(sptxn);
        // submit the transaction to the algod server
        Response<PostTransactionsResponse> resp = algodClient.RawTransaction().rawtxn(encodedTxBytes).execute();
        // wait for the transaction to be confirmed
        String txid = resp.body().txId;
        PendingTransactionResponse result = Utils.waitForConfirmation(algodClient, txid, 4);
        System.out.printf("Transaction %s confirmed in round %d\n", txid, result.confirmedRound);
        // example: TRANSACTION_PAYMENT_SUBMIT


        // example: SP_MIN_FEE
        Response<TransactionParametersResponse> tpr = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = tpr.body();
        System.out.printf("Min fee from suggested params: %d\n", sp.minFee);
        // example: SP_MIN_FEE

        // example: TRANSACTION_FEE_OVERRIDE
        Transaction feeOverrideTxn = Transaction.PaymentTransactionBuilder()
            .sender(acct.getAddress())
            .receiver(acct2.getAddress())
            .suggestedParams(suggestedParams.body())
            // override the fee given by suggested params
            // to set a flat fee of 2x minfee to cover another transaction
            // in the same group
            .flatFee(2*suggestedParams.body().minFee).build();
        // example: TRANSACTION_FEE_OVERRIDE

        // example: CONST_MIN_FEE
        System.out.printf("Min fee from const: %d\n", Account.MIN_TX_FEE_UALGOS);
        // example: CONST_MIN_FEE
    }

    public static void createAccount() throws NoSuchAlgorithmException{
        // example: ACCOUNT_GENERATE
        Account acct = new Account();
        System.out.println("Address: " + acct.getAddress());
        System.out.println("Passphrase: " + acct.toMnemonic());
        // example: ACCOUNT_GENERATE
    }

    public static void getAccountInfo(AlgodClient algodClient, Account acct) throws Exception {
        // example: ALGOD_FETCH_ACCOUNT_INFO
        Response<com.algorand.algosdk.v2.client.model.Account> acctInfoResp = algodClient.AccountInformation(acct.getAddress()).execute();
        com.algorand.algosdk.v2.client.model.Account acctInfo = acctInfoResp.body();
        // print one of the fields in the account info response
        System.out.printf("Current balance: %d", acctInfo.amount);
        // example: ALGOD_FETCH_ACCOUNT_INFO
    }

    public static AlgodClient createClient() {
        // example: ALGOD_CREATE_CLIENT
        String algodHost = "http://localhost";
        int algodPort = 4001;
        String algodToken = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        AlgodClient algodClient = new AlgodClient(algodHost, algodPort, algodToken);

        // OR if the API provider requires a specific header key for the token
        String tokenHeader = "X-API-Key";
        AlgodClient otherAlgodClient = new AlgodClient(algodHost, algodPort, algodToken, tokenHeader);
        // example: ALGOD_CREATE_CLIENT
        return algodClient;
    }


}