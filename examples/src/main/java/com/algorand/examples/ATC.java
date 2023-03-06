package com.algorand.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import com.algorand.algosdk.abi.Contract;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TransactionWithSigner;
import com.algorand.algosdk.transaction.AtomicTransactionComposer.ExecuteResult;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class ATC {

        public static void main(String[] args) throws Exception {
                AlgodClient algodClient = ExampleUtils.getAlgodClient();
                List<Account> accts = ExampleUtils.getSandboxAccounts();
                Account acct = accts.get(0);

                Long appId = deployApp(algodClient, acct);

                // Get suggested params from client
                Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
                TransactionParametersResponse sp = rsp.body();

                // example: ATC_CREATE
                AtomicTransactionComposer atc = new AtomicTransactionComposer();
                // example: ATC_CREATE

                // example: ATC_ADD_TRANSACTION
                // Create a transaction
                Transaction ptxn = PaymentTransactionBuilder.Builder().amount(10000).suggestedParams(sp)
                                .sender(acct.getAddress()).receiver(acct.getAddress()).build();

                // Construct TransactionWithSigner
                TransactionWithSigner tws = new TransactionWithSigner(ptxn,
                                acct.getTransactionSigner());

                // Pass TransactionWithSigner to atc
                atc.addTransaction(tws);
                // example: ATC_ADD_TRANSACTION

                // example: ATC_CONTRACT_INIT
                // Read the json from disk
                String jsonContract = Files.readString(Paths.get("calculator/contract.json"));
                // Create Contract from Json
                Contract contract = Encoder.decodeFromJson(jsonContract, Contract.class);
                // example: ATC_CONTRACT_INIT

                // example: ATC_ADD_METHOD_CALL
                // create methodCallParams by builder (or create by constructor) for add method
                List<Object> methodArgs = new ArrayList<Object>();
                methodArgs.add(1);
                methodArgs.add(1);

                MethodCallTransactionBuilder<?> mctb = MethodCallTransactionBuilder.Builder();

                MethodCallParams mcp = mctb.applicationId(appId).signer(acct.getTransactionSigner())
                                .sender(acct.getAddress())
                                .method(contract.getMethodByName("add")).methodArguments(methodArgs)
                                .onComplete(Transaction.OnCompletion.NoOpOC).suggestedParams(sp).build();

                atc.addMethodCall(mcp);
                // example: ATC_ADD_METHOD_CALL

                // example: ATC_RESULTS
                ExecuteResult res = atc.execute(algodClient, 2);
                System.out.printf("App call (%s) confirmed in round %d\n", res.txIDs, res.confirmedRound);
                res.methodResults.forEach(methodResult -> {
                        System.out.printf("Result from calling '%s' method: %s\n", methodResult.method.name,
                                        methodResult.value);
                });
                // example: ATC_RESULTS

                // example: ATC_BOX_REF
                MethodCallTransactionBuilder<?> mct_builder = MethodCallTransactionBuilder.Builder();

                List<AppBoxReference> boxRefs = new ArrayList<>();
                boxRefs.add(new AppBoxReference(appId.intValue(), "cool-box".getBytes()));
                MethodCallParams box_ref_mcp = mct_builder
                                .suggestedParams(sp)
                                .applicationId(appId)
                                .sender(acct.getAddress())
                                .method(contract.getMethodByName("add"))
                                .methodArguments(methodArgs)
                                .signer(acct.getTransactionSigner())
                                .onComplete(Transaction.OnCompletion.NoOpOC)
                                // Include reference to a box so the app logic may
                                // use it during evaluation
                                .boxReferences(boxRefs)
                                .build();
                // example: ATC_BOX_REF
        }

        public static void atcWithTws(AlgodClient algodClient, Account account) throws Exception {
                Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
                TransactionParametersResponse sp = rsp.body();

                Transaction ptxn = PaymentTransactionBuilder.Builder().amount(10000).suggestedParams(sp)
                                .sender(account.getAddress()).receiver(account.getAddress()).build();

                // Construct TransactionWithSigner
                TransactionWithSigner tws = new TransactionWithSigner(ptxn,
                                account.getTransactionSigner());

                MethodCallTransactionBuilder<?> mctb = MethodCallTransactionBuilder.Builder();

                Method m = new Method("doit(pay,bool)void");

                List<Object> methodArgs = new ArrayList<>();
                Boolean isHeads = true;
                methodArgs.add(tws);
                methodArgs.add(isHeads);
                MethodCallParams mcp = mctb.applicationId(123l).signer(account.getTransactionSigner())
                                .sender(account.getAddress())
                                .method(m).methodArguments(methodArgs)
                                .onComplete(Transaction.OnCompletion.NoOpOC).suggestedParams(sp).build();

                AtomicTransactionComposer atc = new AtomicTransactionComposer();
                atc.addMethodCall(mcp);

                List<SignedTransaction> stxns = atc.gatherSignatures();
                System.out.printf("Created %d transactions\n", stxns.size());
                // should be the app call
                SignedTransaction stxn = stxns.get(1);
                List<byte[]> args = stxn.tx.applicationArgs;
                for (int i = 0; i < args.size(); i++) {
                        System.out.printf("Arg %d is %s\n", i, Hex.encodeHexString(args.get(i)));
                }
        }

        public static Long deployApp(AlgodClient algodClient, Account acct1) throws Exception {
                String approvalSource = Files.readString(Paths.get("calculator/approval.teal"));
                String clearSource = Files.readString(Paths.get("calculator/clear.teal"));

                CompileResponse approvalResponse = algodClient.TealCompile().source(approvalSource.getBytes()).execute()
                                .body();
                CompileResponse clearResponse = algodClient.TealCompile().source(clearSource.getBytes()).execute()
                                .body();

                TEALProgram approvalProg = new TEALProgram(approvalResponse.result);
                TEALProgram clearProg = new TEALProgram(clearResponse.result);

                Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
                TransactionParametersResponse sp = rsp.body();

                StateSchema schema = new StateSchema(0, 0);

                Transaction appCreate = ApplicationCreateTransactionBuilder.Builder().sender(acct1.getAddress())
                                .approvalProgram(approvalProg).clearStateProgram(clearProg).localStateSchema(schema)
                                .globalStateSchema(schema)
                                .suggestedParams(sp).build();
                SignedTransaction signedAppCreate = acct1.signTransaction(appCreate);

                Response<PostTransactionsResponse> createResult = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedAppCreate)).execute();

                PendingTransactionResponse result = Utils.waitForConfirmation(algodClient, createResult.body().txId, 4);

                return result.applicationIndex;
        }

}
