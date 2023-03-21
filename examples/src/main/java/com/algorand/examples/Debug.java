package com.algorand.examples;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.abi.Contract;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.transaction.AtomicTransactionComposer;
import com.algorand.algosdk.transaction.MethodCallParams;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.DryrunRequest;
import com.algorand.algosdk.v2.client.model.DryrunResponse;
import com.algorand.algosdk.v2.client.model.DryrunTxnResult;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class Debug {
    

    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        Account acct = accts.get(0);

        Long appId = ATC.deployApp(algodClient, acct);

        String jsonContract = Files.readString(Paths.get("calculator/contract.json"));
        Contract contract = Encoder.decodeFromJson(jsonContract, Contract.class);

        // example: DEBUG_DRYRUN_DUMP
        // Set up the transactions we'd like to dryrun
        AtomicTransactionComposer atc = new AtomicTransactionComposer();

        List<Object> methodArgs = new ArrayList<Object>();
        methodArgs.add(1);
        methodArgs.add(1);

        TransactionParametersResponse sp = algodClient.TransactionParams().execute().body();

        MethodCallTransactionBuilder<?> mctb = MethodCallTransactionBuilder.Builder();
        MethodCallParams mcp = mctb.applicationId(appId).signer(acct.getTransactionSigner())
                        .suggestedParams(sp)
                        .sender(acct.getAddress())
                        .method(contract.getMethodByName("add"))
                        .methodArguments(methodArgs)
                        .onComplete(Transaction.OnCompletion.NoOpOC)
                        .build();
        atc.addMethodCall(mcp);

        DryrunRequest drr = Utils.createDryrun(algodClient, atc.gatherSignatures(), "", 0L, 0L);

        FileOutputStream outfile = new FileOutputStream("my-dryrun.msgpack");
        outfile.write(Encoder.encodeToMsgPack(drr));
        outfile.close();
        // example: DEBUG_DRYRUN_DUMP

        // example: DEBUG_DRYRUN_SUBMIT
        Response<DryrunResponse> resp = algodClient.TealDryrun().request(drr).execute();
        DryrunResponse drResp = resp.body();
        DryrunTxnResult dryrunTxnResult = drResp.txns.get(0);
        System.out.println(dryrunTxnResult.appCallMessages);
        System.out.println(Utils.appTrace(dryrunTxnResult));
        // example: DEBUG_DRYRUN_SUBMIT
    }
}
