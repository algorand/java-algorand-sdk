package com.algorand.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationClearTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationCloseTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationDeleteTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationOptInTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationUpdateTransactionBuilder;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
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

public class AppExamples {

        public static void main(String[] args) throws Exception {
                AlgodClient algodClient = ExampleUtils.getAlgodClient();
                List<Account> accts = ExampleUtils.getSandboxAccounts();

                Account creator = accts.get(0);
                Account user = accts.get(1);

                // example: APP_SCHEMA
                int localInts = 1;
                int localBytes = 1;
                int globalInts = 1;
                int globalBytes = 0;
                StateSchema localSchema = new StateSchema(localInts, localBytes);
                StateSchema globalSchema = new StateSchema(globalInts, globalBytes);
                // example: APP_SCHEMA

                // example: APP_SOURCE
                // Read in the `teal` source files as a string
                String approvalSource = Files.readString(Paths.get("application/approval.teal"));
                String clearSource = Files.readString(Paths.get("application/clear.teal"));
                // example: APP_SOURCE

                // example: APP_COMPILE
                CompileResponse approvalResponse = algodClient.TealCompile().source(approvalSource.getBytes()).execute()
                                .body();
                CompileResponse clearResponse = algodClient.TealCompile().source(clearSource.getBytes()).execute()
                                .body();

                TEALProgram approvalProg = new TEALProgram(approvalResponse.result);
                TEALProgram clearProg = new TEALProgram(clearResponse.result);
                // example: APP_COMPILE

                // example: APP_CREATE
                Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
                TransactionParametersResponse sp = rsp.body();

                Transaction appCreate = ApplicationCreateTransactionBuilder.Builder()
                                .sender(creator.getAddress())
                                .suggestedParams(sp)
                                .approvalProgram(approvalProg)
                                .clearStateProgram(clearProg)
                                .localStateSchema(localSchema)
                                .globalStateSchema(globalSchema)
                                .build();

                SignedTransaction signedAppCreate = creator.signTransaction(appCreate);
                Response<PostTransactionsResponse> createResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedAppCreate)).execute();

                PendingTransactionResponse result = Utils.waitForConfirmation(algodClient, createResponse.body().txId,
                                4);
                Long appId = result.applicationIndex;
                System.out.printf("Created application with id: %d\n", appId);
                // example: APP_CREATE

                // example: APP_OPTIN
                Transaction optInTxn = ApplicationOptInTransactionBuilder.Builder()
                                .sender(user.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .build();

                SignedTransaction signedOptIn = user.signTransaction(optInTxn);
                Response<PostTransactionsResponse> optInResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedOptIn)).execute();

                PendingTransactionResponse optInResult = Utils.waitForConfirmation(algodClient,
                                optInResponse.body().txId, 4);
                assert optInResult.confirmedRound > 0;
                // example: APP_OPTIN

                // example: APP_NOOP
                Transaction noopTxn = ApplicationCallTransactionBuilder.Builder()
                                .sender(user.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .build();

                SignedTransaction signedNoop = user.signTransaction(noopTxn);
                Response<PostTransactionsResponse> noopResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedNoop)).execute();

                PendingTransactionResponse noopResult = Utils.waitForConfirmation(algodClient, noopResponse.body().txId,
                                4);
                assert noopResult.confirmedRound > 0;
                // example: APP_NOOP

                // example: APP_READ_STATE
                // example: APP_READ_STATE

                // example: APP_UPDATE
                String approvalSourceUpdated = Files.readString(Paths.get("application/approval_refactored.teal"));
                CompileResponse approvalUpdatedResponse = algodClient.TealCompile()
                                .source(approvalSourceUpdated.getBytes())
                                .execute()
                                .body();
                TEALProgram approvalProgUpdated = new TEALProgram(approvalUpdatedResponse.result);

                Transaction appUpdate = ApplicationUpdateTransactionBuilder.Builder()
                                .sender(creator.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .approvalProgram(approvalProgUpdated)
                                .clearStateProgram(clearProg)
                                .build();

                SignedTransaction signedAppUpdate = creator.signTransaction(appUpdate);
                Response<PostTransactionsResponse> updateResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedAppUpdate)).execute();
                PendingTransactionResponse updateResult = Utils.waitForConfirmation(algodClient,
                                updateResponse.body().txId, 4);
                assert updateResult.confirmedRound > 0;
                // example: APP_UPDATE

                // example: APP_CALL
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());

                List<byte[]> appArgs = new ArrayList<byte[]>();
                appArgs.add(formatter.format(date).toString().getBytes());

                // create unsigned transaction
                Transaction callTransaction = ApplicationCallTransactionBuilder.Builder()
                                .sender(user.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .args(appArgs)
                                .build();

                SignedTransaction signedCallTransaction = user.signTransaction(callTransaction);
                Response<PostTransactionsResponse> callResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedCallTransaction)).execute();

                PendingTransactionResponse callResult = Utils.waitForConfirmation(algodClient, callResponse.body().txId,
                                4);
                assert callResult.confirmedRound > 0;
                // display results
                if (callResult.globalStateDelta != null) {
                        System.out.printf("\tGlobal state: %s\n", callResult.globalStateDelta);
                }

                if (callResult.localStateDelta != null) {
                        System.out.printf("\tLocal state: %s\n", callResult.localStateDelta);
                }
                // example: APP_CALL

                // example: APP_CLOSEOUT
                Transaction closeOutTxn = ApplicationCloseTransactionBuilder.Builder()
                                .sender(user.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .build();

                SignedTransaction signedCloseOut = user.signTransaction(closeOutTxn);
                Response<PostTransactionsResponse> closeOutResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedCloseOut)).execute();

                PendingTransactionResponse closeOutResult = Utils.waitForConfirmation(algodClient,
                                closeOutResponse.body().txId,
                                4);
                assert closeOutResult.confirmedRound > 0;
                // example: APP_CLOSEOUT

                // example: APP_DELETE
                Transaction appDelete = ApplicationDeleteTransactionBuilder.Builder()
                                .sender(creator.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .build();

                SignedTransaction signedAppDelete = creator.signTransaction(appDelete);
                Response<PostTransactionsResponse> deleteResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedAppDelete)).execute();
                PendingTransactionResponse deleteResult = Utils.waitForConfirmation(algodClient,
                                deleteResponse.body().txId, 4);
                assert deleteResult.confirmedRound > 0;
                // example: APP_DELETE

                // example: APP_CLEAR
                Transaction clearTxn = ApplicationClearTransactionBuilder.Builder()
                                .sender(user.getAddress())
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .build();

                SignedTransaction signedClear = user.signTransaction(clearTxn);
                // ... sign, send, wait
                // example: APP_CLEAR
        }

}