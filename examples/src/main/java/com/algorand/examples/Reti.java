package com.algorand.examples;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

import com.algorand.algosdk.abi.Contract;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.*;
import com.algorand.algosdk.transaction.AtomicTransactionComposer.ExecuteResult;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;

public class Reti {
    static Long RETI_APP_ID_TESTNET = 673404372L;

    public static void main(String[] args) throws Exception {
        // Get valid Reti testnet account
        Account acct = recoverFromMnemonic();
        System.out.println("account address " + acct.getAddress().toString());

        // Get # of Reti Validators in testnet
        System.out.println();
        System.out.println("getNumberOfValidators");
        getNumberOfValidators(acct);

        // Get staked pools on account, there should be at least one
        System.out.println();
        System.out.println("getStakedPoolsForAccount");
        getStakedPoolsForAccount(acct);

        // Add Stake to Validator Pool ID
        System.out.println();
        System.out.println("addStake");
        addStake(acct);

        // Remove Stake from Validator Pool ID
        System.out.println();
        System.out.println("removeStake");
        removeStake(acct);
    }

    public static Account recoverFromMnemonic() throws GeneralSecurityException {
        // example: ACCOUNT_RECOVER_MNEMONIC
        // Space delimited 25 word mnemonic
        String part1 = "panda course account pact six same";
        String part2 = "antique shed slender finger lab dose";
        String part3 = "reveal escape amateur since power left";
        String part4 = "trust update soup neck tuition about meadow";
        String mn = part1 + " " + part2 + " " + part3 + " " + part4;
        return new Account(mn);
    }

    public static void getNumberOfValidators(Account acct) {
        try {
            AlgodClient algodClient = ExampleUtils.getAlgodTestnetClient();
            Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
            TransactionParametersResponse sp = rsp.body();

            String jsonContract = Files.readString(Paths.get("reti/ValidatorRegistry.arc4.json"));
            Contract contract = Encoder.decodeFromJson(jsonContract, Contract.class);

            List<Object> methodArgs = new ArrayList<Object>();

            MethodCallParams mcp = MethodCallTransactionBuilder.Builder()
                .applicationId(RETI_APP_ID_TESTNET)
                .signer(acct.getTransactionSigner())
                .sender(acct.getAddress())
                .method(contract.getMethodByName("getNumValidators"))
                .methodArguments(methodArgs)
                .onComplete(Transaction.OnCompletion.NoOpOC)
                .suggestedParams(sp)
                .build();

            AtomicTransactionComposer atc = new AtomicTransactionComposer();
            atc.addMethodCall(mcp);

            ExecuteResult res = atc.execute(algodClient, 250);
            System.out.printf("App call (%s) confirmed in round %d\n", res.txIDs, res.confirmedRound);
            res.methodResults.forEach(methodResult -> {
                    System.out.printf("Result from calling '%s' method: %s\n", methodResult.method.name,
                                methodResult.value);
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void getStakedPoolsForAccount(Account acct) {
        try {
            AlgodClient algodClient = ExampleUtils.getAlgodTestnetClient();
            Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
            TransactionParametersResponse sp = rsp.body();

            String jsonContract = Files.readString(Paths.get("reti/ValidatorRegistry.arc4.json"));
            Contract contract = Encoder.decodeFromJson(jsonContract, Contract.class);

            List<Object> methodArgs = new ArrayList<Object>();
            methodArgs.add(acct.getAddress());

            MethodCallParams mcp = MethodCallTransactionBuilder.Builder()
                .applicationId(RETI_APP_ID_TESTNET)
                .signer(new EmptyTransactionSigner(acct.getAddress().toString()))
                .sender(acct.getAddress())
                .method(contract.getMethodByName("getStakedPoolsForAccount"))
                .methodArguments(methodArgs)
                .onComplete(Transaction.OnCompletion.NoOpOC)
                .suggestedParams(sp)
                .build();

            AtomicTransactionComposer atc = new AtomicTransactionComposer();
            atc.addMethodCall(mcp);

            SimulateRequest request = new SimulateRequest();
            request.allowEmptySignatures = true;
            request.allowUnnamedResources = true;

            AtomicTransactionComposer.SimulateResult simulateResult = atc.simulate(algodClient, request);
            Object resultsObj = simulateResult.getMethodResults().get(0).value;
            List<Object> results = Arrays.stream(((Object[]) resultsObj)).toList();

            for (Object o : results) {
                List<Object> result = Arrays.stream(((Object[]) o)).toList();
                BigInteger validatorId = ((BigInteger) result.get(0));
                // BigInteger poolNum = ((BigInteger) result.get(1));
                BigInteger poolAppId = ((BigInteger) result.get(2));
                System.out.println("validator = " + validatorId + ", poolAppID = " + poolAppId);
                getStakerInfo(acct, poolAppId);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getStakerInfo(Account acct, BigInteger poolAppID) {
        String feeSink = "A7NMWS3NT3IUDMLVO26ULGXGIIOUQ3ND2TXSER6EBGRZNOBOUIQXHIBGDE";
        try {
            AlgodClient algodClient = ExampleUtils.getAlgodTestnetClient();
            Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
            TransactionParametersResponse sp = rsp.body();

            String jsonContract = Files.readString(Paths.get("reti/StakingPool.arc4.json"));
            Contract contract = Encoder.decodeFromJson(jsonContract, Contract.class);

            List<Object> methodArgs = new ArrayList<Object>();
            methodArgs.add(acct.getAddress());

            MethodCallParams mcp = MethodCallTransactionBuilder.Builder()
                .applicationId(poolAppID.longValue())
                .signer(new EmptyTransactionSigner(acct.getAddress().toString()))
                .sender(acct.getAddress())
                .method(contract.getMethodByName("getStakerInfo"))
                .methodArguments(methodArgs)
                .onComplete(Transaction.OnCompletion.NoOpOC)
                .suggestedParams(sp)
                .build();

            AtomicTransactionComposer atc = new AtomicTransactionComposer();
            atc.addMethodCall(mcp);

            SimulateRequest request = new SimulateRequest();
            request.allowEmptySignatures = true;
            request.allowUnnamedResources = true;

             AtomicTransactionComposer.SimulateResult simulateResult = atc.simulate(algodClient, request);
             Object resultsObj = simulateResult.getMethodResults().get(0).value;
             List<Object> results = Arrays.stream(((Object[]) resultsObj)).toList();
            System.out.println("balance = " + results.get(1) + " microALGOS, poolAppID = " + poolAppID);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void addStake(Account acct)  {
        Long validatorId = 1L;
        Long poolAppId = 673421623L;
        Long amount = 8000000L;

        try {
            AlgodClient algodClient = ExampleUtils.getAlgodTestnetClient();
            Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
            TransactionParametersResponse sp = rsp.body();

            String jsonContract = Files.readString(Paths.get("reti/ValidatorRegistry.arc4.json"));
            Contract validatorContract = Encoder.decodeFromJson(jsonContract, Contract.class);

            AtomicTransactionComposer atc = new AtomicTransactionComposer();

            //Gas Call
            List<AppBoxReference> boxRefsGas = new ArrayList<>();
            boxRefsGas.add(new AppBoxReference(0L, getValidatorListBoxName(validatorId)));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, getStakerPoolSetBoxName(acct.getAddress())));

            MethodCallParams mcpGas = MethodCallTransactionBuilder.Builder()
                    .applicationId(RETI_APP_ID_TESTNET)
                    .signer(acct.getTransactionSigner())
                    .sender(acct.getAddress())
                    .method(validatorContract.getMethodByName("gas"))
                    .onComplete(Transaction.OnCompletion.NoOpOC)
                    .boxReferences(boxRefsGas)
                    .suggestedParams(sp)
                    .build();
            atc.addMethodCall(mcpGas);

            // addStake call
            Transaction ptxn = PaymentTransactionBuilder.Builder()
            .amount(amount) // 8 algos
            .suggestedParams(sp)
            .sender(acct.getAddress())
            .receiver(new Address().forApplication(RETI_APP_ID_TESTNET))
            .build();

            // Construct TransactionWithSigner
            TransactionWithSigner tws = new TransactionWithSigner(ptxn,
                                acct.getTransactionSigner());

            List<Object> methodArgs = new ArrayList<Object>();
            methodArgs.add(tws);
            methodArgs.add(BigInteger.valueOf(validatorId));
            methodArgs.add(BigInteger.ZERO);

            List<Long> foreignApps = new ArrayList<Long>();
            foreignApps.add(poolAppId);

            List<AppBoxReference> boxRefs = new ArrayList<>();
            boxRefs.add(new AppBoxReference(poolAppId, getStakerLedgerBoxName()));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));

            sp.fee = 1000L * 2;

            MethodCallParams mcp = MethodCallTransactionBuilder.Builder()
                .applicationId(RETI_APP_ID_TESTNET)
                .signer(acct.getTransactionSigner())
                .sender(acct.getAddress())
                .method(validatorContract.getMethodByName("addStake"))
                .methodArguments(methodArgs)
                .foreignApps(foreignApps)
                .boxReferences(boxRefs)
                .onComplete(Transaction.OnCompletion.NoOpOC)
                .suggestedParams(sp)
                .build();
            atc.addMethodCall(mcp);

            ExecuteResult res = atc.execute(algodClient, 250);
            System.out.printf("App call (%s) confirmed in round %d\n", res.txIDs, res.confirmedRound);
            res.methodResults.forEach(methodResult -> {
                switch (methodResult.method.name) {
                    case "addStake":
                        List<Object> results = Arrays.stream(((Object[]) methodResult.value)).toList();
                        System.out.println(amount + " additional microAlgos staked to poolAppId = " + results.get(2));
                        break;
                    default: break;
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void removeStake(Account acct)  {
        Long validatorId = 1L;
        Long poolAppId = 673421623L;

        try {
            AlgodClient algodClient = ExampleUtils.getAlgodTestnetClient();
            Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
            TransactionParametersResponse sp = rsp.body();

            String jsonContract = Files.readString(Paths.get("reti/ValidatorRegistry.arc4.json"));
            Contract validatorContract = Encoder.decodeFromJson(jsonContract, Contract.class);
            String stakingContractStr = Files.readString(Paths.get("reti/StakingPool.arc4.json"));
            Contract stakingContract = Encoder.decodeFromJson(stakingContractStr, Contract.class);

            AtomicTransactionComposer atc = new AtomicTransactionComposer();

            //Gas Call
            List<AppBoxReference> boxRefsGas = new ArrayList<>();
            boxRefsGas.add(new AppBoxReference(RETI_APP_ID_TESTNET, getValidatorListBoxName(validatorId)));
            boxRefsGas.add(new AppBoxReference(RETI_APP_ID_TESTNET, new byte[0]));
            boxRefsGas.add(new AppBoxReference(RETI_APP_ID_TESTNET, getStakerPoolSetBoxName(acct.getAddress())));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));
            boxRefsGas.add(new AppBoxReference(0L, new byte[0]));

            MethodCallParams mcpGas = MethodCallTransactionBuilder.Builder()
                    .applicationId(RETI_APP_ID_TESTNET)
                    .signer(acct.getTransactionSigner())
                    .sender(acct.getAddress())
                    .method(validatorContract.getMethodByName("gas"))
                    .onComplete(Transaction.OnCompletion.NoOpOC)
                    .boxReferences(boxRefsGas)
                    .suggestedParams(sp)
                    .build();
            atc.addMethodCall(mcpGas);

            List<Object> methodArgs = new ArrayList<Object>();
            methodArgs.add(acct.getAddress());
            methodArgs.add(BigInteger.valueOf(0L)); // 0 Removes all stake

            List<Long> foreignApps = new ArrayList<Long>();
            foreignApps.add(poolAppId);

            List<AppBoxReference> boxRefs = new ArrayList<>();
            boxRefs.add(new AppBoxReference(poolAppId, getStakerLedgerBoxName()));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));
            boxRefs.add(new AppBoxReference(0L, new byte[0]));

            sp.fee = 1000L * 2;

            MethodCallParams mcp = MethodCallTransactionBuilder.Builder()
                    .applicationId(poolAppId)
                    .signer(acct.getTransactionSigner())
                    .sender(acct.getAddress())
                    .method(stakingContract.getMethodByName("removeStake"))
                    .methodArguments(methodArgs)
                    .foreignApps(foreignApps)
                    .boxReferences(boxRefs)
                    .onComplete(Transaction.OnCompletion.NoOpOC)
                    .suggestedParams(sp)
                    .build();
            atc.addMethodCall(mcp);

            ExecuteResult res = atc.execute(algodClient, 250);
            System.out.printf("App call (%s) confirmed in round %d\n", res.txIDs, res.confirmedRound);
            res.methodResults.forEach(methodResult -> {
                switch (methodResult.method.name) {
                    case "removeStake":
                        System.out.println("All staked algos removed from poolAppId = " + poolAppId);
                        break;
                    default: break;
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static byte[] getStakerLedgerBoxName() {
        String name = "stakers";
        return name.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] getStakerPoolSetBoxName(Address stakerAccount) {
        byte[] prefix = "sps".getBytes();
        byte[] combinedArray = new byte[prefix.length + stakerAccount.getBytes().length];

        System.arraycopy(prefix, 0, combinedArray, 0, prefix.length);
        System.arraycopy(stakerAccount.getBytes(), 0, combinedArray, prefix.length, stakerAccount.getBytes().length);

        return combinedArray;
    }

    public static byte[] getValidatorListBoxName(long id) {
        int EIGHT = 8;

        byte[] prefix = new byte[]{(byte) 'v'};
        ByteBuffer buffer = ByteBuffer.allocate(EIGHT).order(ByteOrder.BIG_ENDIAN).putLong(id);
        byte[] ibytes = buffer.array();
        byte[] result = new byte[prefix.length + ibytes.length];

        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(ibytes, 0, result, prefix.length, ibytes.length);

        return result;
    }
}
