package com.algorand.examples;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

public class Example {
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
    }

    public static void createAccount() throws NoSuchAlgorithmException{
        // example: ACCOUNT_GENERATE
        Account acct = new Account();
        System.out.println("Address: " + acct.getAddress());
        System.out.println("Passphrase: " + acct.toMnemonic());
        // example: ACCOUNT_GENERATE
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

// Missing ATOMIC_CREATE_TXNS in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:71)
// Missing ATOMIC_GROUP_TXNS in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:154)
// Missing ATOMIC_GROUP_SIGN in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:222)
// Missing ATOMIC_GROUP_ASSEMBLE in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:284)
// Missing ATOMIC_GROUP_SEND in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:346)
// Missing MULTISIG_CREATE in JAVASDK examples (in
// ../docs/get-details/transactions/signatures.md:256)
// Missing MULTISIG_SIGN in JAVASDK examples (in
// ../docs/get-details/transactions/signatures.md:258)
// Missing TRANSACTION_FEE_OVERRIDE in JAVASDK examples (in
// ../docs/get-details/transactions/index.md:791)
// Missing CODEC_TRANSACTION_UNSIGNED in JAVASDK examples (in
// ../docs/get-details/transactions/offline_transactions.md:62)
// Missing CODEC_TRANSACTION_SIGNED in JAVASDK examples (in
// ../docs/get-details/transactions/offline_transactions.md:268)
// Missing KMD_CREATE_CLIENT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:115)
// Missing KMD_CREATE_WALLET in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:132)
// Missing KMD_CREATE_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:150)
// Missing KMD_RECOVER_WALLET in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:294)
// Missing KMD_EXPORT_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:488)
// Missing KMD_IMPORT_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:690)
// Missing ACCOUNT_GENERATE in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:894)
// Missing MULTISIG_CREATE in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:1030)
// Missing ACCOUNT_REKEY in JAVASDK examples (in
// ../docs/get-details/accounts/rekey.md:364)
// Missing CODEC_ADDRESS in JAVASDK examples (in
// ../docs/get-details/encoding.md:86)
// Missing CODEC_BASE64 in JAVASDK examples (in
// ../docs/get-details/encoding.md:138)
// Missing CODEC_UINT64 in JAVASDK examples (in
// ../docs/get-details/encoding.md:196)
// Missing CODEC_TRANSACTION_UNSIGNED in JAVASDK examples (in
// ../docs/get-details/encoding.md:337)
// Missing CODEC_TRANSACTION_SIGNED in JAVASDK examples (in
// ../docs/get-details/encoding.md:339)
// Missing CODEC_BLOCK in JAVASDK examples (in
// ../docs/get-details/encoding.md:363)
// Missing CONST_MIN_FEE in JAVASDK examples (in
// ../docs/get-details/dapps/smart-contracts/guidelines.md:62)
// Missing SP_MIN_FEE in JAVASDK examples (in
// ../docs/get-details/dapps/smart-contracts/guidelines.md:96)
// Missing ASSET_CREATE in JAVASDK examples (in ../docs/get-details/asa.md:203)
// Missing ASSET_CONFIG in JAVASDK examples (in ../docs/get-details/asa.md:429)
// Missing ASSET_OPTIN in JAVASDK examples (in ../docs/get-details/asa.md:628)
// Missing ASSET_XFER in JAVASDK examples (in ../docs/get-details/asa.md:809)
// Missing ASSET_FREEZE in JAVASDK examples (in ../docs/get-details/asa.md:1007)
// Missing ASSET_CLAWBACK in JAVASDK examples (in
// ../docs/get-details/asa.md:1193)
// Missing ASSET_DELETE in JAVASDK examples (in ../docs/get-details/asa.md:1386)
// Missing ASSET_INFO in JAVASDK examples (in ../docs/get-details/asa.md:1549)
// Missing ATC_CREATE in JAVASDK examples (in ../docs/get-details/atc.md:48)
// Missing ATC_ADD_TRANSACTION in JAVASDK examples (in
// ../docs/get-details/atc.md:142)
// Missing ATC_CONTRACT_INIT in JAVASDK examples (in
// ../docs/get-details/atc.md:307)
// Missing ATC_ADD_METHOD_CALL in JAVASDK examples (in
// ../docs/get-details/atc.md:325)
// Missing ATC_RESULTS in JAVASDK examples (in ../docs/get-details/atc.md:406)
// Missing CREATE_INDEXER_CLIENT in JAVASDK examples (in
// ../docs/get-details/indexer.md:42)
// Missing INDEXER_LOOKUP_ASSET in JAVASDK examples (in
// ../docs/get-details/indexer.md:131)
// Missing INDEXER_SEARCH_MIN_AMOUNT in JAVASDK examples (in
// ../docs/get-details/indexer.md:205)
// Missing INDEXER_PAGINATE_RESULTS in JAVASDK examples (in
// ../docs/get-details/indexer.md:322)
// Missing INDEXER_PREFIX_SEARCH in JAVASDK examples (in
// ../docs/get-details/indexer.md:477)
// Missing TRANSACTION_KEYREG_OFFLINE_CREATE in JAVASDK examples (in
// ../docs/run-a-node/participate/offline.md:41)
// Missing TRANSACTION_KEYREG_ONLINE_CREATE in JAVASDK examples (in
// ../docs/run-a-node/participate/online.md:42)
