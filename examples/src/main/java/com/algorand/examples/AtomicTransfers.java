package com.algorand.examples;

import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class AtomicTransfers {
    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();

        Account acct1 = accts.get(0);
        Account acct2 = accts.get(1);

        // example: ATOMIC_CREATE_TXNS
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();

        // payment from account 1 to account 2
        Transaction ptxn1 = Transaction.PaymentTransactionBuilder().sender(acct1.getAddress())
                .amount(1000000).receiver(acct2.getAddress()).suggestedParams(rsp.body()).build();
        // txn_1 = transaction.PaymentTxn(addr1, suggested_params, addr2, 100000)

        // payment from account 2 to account 1
        Transaction ptxn2 = Transaction.PaymentTransactionBuilder().sender(acct2.getAddress())
                .amount(2000000).receiver(acct1.getAddress()).suggestedParams(rsp.body()).build();
        // example: ATOMIC_CREATE_TXNS

        // example: ATOMIC_GROUP_TXNS
        // Assign group id to the transactions (order matters!)
        Transaction[] txs = TxGroup.assignGroupID(ptxn1, ptxn2);

        // Or, equivalently
        // compute group id and assign it to transactions
        Digest gid = TxGroup.computeGroupID(txs);
        ptxn1.group = gid;
        ptxn2.group = gid;
        // example: ATOMIC_GROUP_TXNS

        // example: ATOMIC_GROUP_SIGN
        // sign transactions
        SignedTransaction signedPtxn1 = acct1.signTransaction(ptxn1);
        SignedTransaction signedPtxn2 = acct2.signTransaction(ptxn2);
        // example: ATOMIC_GROUP_SIGN

        // example: ATOMIC_GROUP_ASSEMBLE
        // combine the signed transactions into a single list
        SignedTransaction[] stxns = new SignedTransaction[] { signedPtxn1, signedPtxn2 };
        // example: ATOMIC_GROUP_ASSEMBLE

        // example: ATOMIC_GROUP_SEND
        // Only the first transaction id is returned
        Response<PostTransactionsResponse> txResponse = algodClient.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(stxns)).execute();
        String txid = txResponse.body().txId;

        // Wait for the transaction id to be confirmed
        // If the results from other transactions are needed, grab the txid from those
        // directly and
        // call waitForConfirmation on each
        PendingTransactionResponse txResult = Utils.waitForConfirmation(algodClient, txid, 4);
        System.out.printf("Transaction %s confirmed in round %d\n", txid, txResult.confirmedRound);
        // example: ATOMIC_GROUP_SEND
    }

}