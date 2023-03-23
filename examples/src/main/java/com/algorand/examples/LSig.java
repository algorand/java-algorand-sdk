package com.algorand.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.account.LogicSigAccount;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class LSig {

        public static void main(String[] args) throws Exception {
                AlgodClient algodClient = ExampleUtils.getAlgodClient();
                List<Account> accts = ExampleUtils.getSandboxAccounts();
                Account seedAcct = accts.get(0);

                byte[] tealBinary = compileLsig(algodClient, "lsig/simple.teal");
                // example: LSIG_INIT
                LogicSigAccount lsig = new LogicSigAccount(tealBinary, null);
                // example: LSIG_INIT

                byte[] tealBinaryWithArgs = compileLsig(algodClient, "lsig/simple.teal");
                // example: LSIG_PASS_ARGS
                List<byte[]> tealArgs = new ArrayList<byte[]>();
                // The arguments _must_ be byte arrays
                byte[] arg1 = Encoder.encodeUint64(123l);
                tealArgs.add(arg1);
                LogicSigAccount lsigWithArgs = new LogicSigAccount(tealBinaryWithArgs, tealArgs);
                // example: LSIG_PASS_ARGS

                // Create a transaction to seed the lsig address
                TransactionParametersResponse seedParams = algodClient.TransactionParams().execute().body();
                Transaction seedTxn = Transaction.PaymentTransactionBuilder()
                                .suggestedParams(seedParams)
                                .sender(seedAcct.getAddress())
                                .amount(10000000)
                                .receiver(lsigWithArgs.getAddress())
                                .build();
                ExampleUtils.sendPrint(algodClient, seedAcct.signTransaction(seedTxn), "seed lsig");

                // example: LSIG_SIGN_FULL
                TransactionParametersResponse params = algodClient.TransactionParams().execute().body();
                // create a transaction
                Transaction txn = Transaction.PaymentTransactionBuilder()
                                .sender(lsig.getAddress())
                                .amount(100000)
                                .receiver(seedAcct.getAddress())
                                .suggestedParams(params)
                                .build();
                // create the LogicSigTransaction with contract account LogicSigAccount
                SignedTransaction stx = Account.signLogicsigTransaction(lsig.lsig, txn);
                // send raw LogicSigTransaction to network
                Response<PostTransactionsResponse> submitResult = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(stx)).execute();
                String txid = submitResult.body().txId;
                // Wait for transaction confirmation
                PendingTransactionResponse pTrx = Utils.waitForConfirmation(algodClient, txid, 4);
                System.out.printf("Transaction %s confirmed in round %d\n", txid, pTrx.confirmedRound);
                // example: LSIG_SIGN_FULL

                // example: LSIG_DELEGATE_FULL
                // account signs the logic, and now the logic may be passed instead
                // of a signature for a transaction
                LogicsigSignature delegateLsig = seedAcct.signLogicsig(lsigWithArgs.lsig);
                params = algodClient.TransactionParams().execute().body();
                // create a transaction where the sender is the signer of the lsig
                txn = Transaction.PaymentTransactionBuilder()
                                .sender(seedAcct.getAddress())
                                .amount(100000)
                                .receiver(delegateLsig.toAddress())
                                .suggestedParams(params)
                                .build();
                // Sign the transaction with the delegate lsig
                stx = Account.signLogicsigTransaction(delegateLsig, txn);
                // send raw LogicSigTransaction to network
                submitResult = algodClient.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();
                txid = submitResult.body().txId;
                // Wait for transaction confirmation
                PendingTransactionResponse delegatResponse = Utils.waitForConfirmation(algodClient, txid, 4);
                System.out.printf("Transaction %s confirmed in round %d\n", txid, delegatResponse.confirmedRound);
                // example: LSIG_DELEGATE_FULL
        }

        public static byte[] compileLsig(AlgodClient algodClient, String path) throws Exception {
                // example: LSIG_COMPILE
                String tealsrc = Files.readString(Paths.get("lsig/simple.teal"));
                Response<CompileResponse> compileResp = algodClient.TealCompile().source(tealsrc.getBytes()).execute();
                System.out.printf("Program: %s\n", compileResp.body().result);
                System.out.printf("Address: %s\n", compileResp.body().hash);
                byte[] tealBinary = Encoder.decodeFromBase64(compileResp.body().result);
                // example: LSIG_COMPILE
                return tealBinary;
        }

}
