package com.algorand.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.abi.Contract;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TransactionWithSigner;
import com.algorand.algosdk.transaction.AtomicTransactionComposer.ExecuteResult;
import com.algorand.algosdk.transaction.AtomicTransactionComposer.ReturnValue;
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
        List<Object> method_args = new ArrayList<Object>();
        method_args.add(1);
        method_args.add(1);

        MethodCallTransactionBuilder<?> mctb = MethodCallTransactionBuilder.Builder();

        MethodCallParams mcp = mctb.applicationId(appId).signer(acct.getTransactionSigner()).sender(acct.getAddress())
                .method(contract.getMethodByName("add")).methodArguments(method_args)
                .onComplete(Transaction.OnCompletion.NoOpOC).suggestedParams(sp).build();

        atc.addMethodCall(mcp);
        // example: ATC_ADD_METHOD_CALL
        // example: ATC_RESULTS
        ExecuteResult res = atc.execute(algodClient, 2);
        System.out.printf("App call (%s) confirmed in round %d\n", res.txIDs, res.confirmedRound);
        ReturnValue returnValue = res.methodResults.get(0);
        System.out.printf("Result from calling '%s' method: %s\n", returnValue.method.name, returnValue.value);
        // example: ATC_RESULTS
    }

    public static Long deployApp(AlgodClient algodClient, Account acct1) throws Exception {
        String approvalSource = Files.readString(Paths.get("calculator/approval.teal"));
        String clearSource = Files.readString(Paths.get("calculator/clear.teal"));

        CompileResponse approvalResponse = algodClient.TealCompile().source(approvalSource.getBytes()).execute().body();
        CompileResponse clearResponse = algodClient.TealCompile().source(clearSource.getBytes()).execute().body();

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
