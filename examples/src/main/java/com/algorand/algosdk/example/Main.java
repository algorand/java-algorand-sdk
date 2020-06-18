package com.algorand.algosdk.example;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.Supply;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.SupplyResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Main {
    /**
     * Test driver.
     */
    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
            System.err.println("Required parameters: ALGOD_API_ADDR ALGOD_API_PORT ALGOD_API_TOKEN");
            System.err.println("The parameter values can be found in algod.net and algod.token files within the data directory of your algod install.");
            System.exit(1);
        }

        String algodApiAddrTmp = args[0];
        // Don't include the protocol.
        algodApiAddrTmp.replaceFirst("http[s]?://", "");

        final String ALGOD_API_ADDR = algodApiAddrTmp;
        final int ALGOD_API_PORT = Integer.parseInt(args[1]);
        final String ALGOD_API_TOKEN = args[2];

        //v1Client("http://" + ALGOD_API_ADDR + ":" + ALGOD_API_PORT, ALGOD_API_TOKEN);
        v2Client(ALGOD_API_ADDR, ALGOD_API_PORT, ALGOD_API_TOKEN);
    }

    /**
     * Updated tests using V2 client and TransactionBuilders.
     */
    public static void v2Client(String addr, int port, String token) throws Exception {
        final String SRC_ACCOUNT = "viable grain female caution grant mind cry mention pudding oppose orchard people forget similar social gossip marble fish guitar art morning ring west above concert";
        final String DEST_ADDR = "KV2XGKMXGYJ6PWYQA5374BYIQBL3ONRMSIARPCFCJEAMAHQEVYPB7PL3KU";

        AlgodClient client = new AlgodClient(addr, port, token);

        /////////////////////////////////
        // Query for state information //
        /////////////////////////////////

        Response<SupplyResponse> supplyResponse = client.GetSupply().execute();
        if (supplyResponse.isSuccessful()) {
            SupplyResponse supply = supplyResponse.body();
            System.out.println("Total Algorand Supply: " + supply.totalMoney);
            System.out.println("Online Algorand Supply: " + supply.onlineMoney);
        } else {
            System.out.println("There was a problem getting the supply: " + supplyResponse.message());
            System.exit(1);
        }

        Account src = new Account(SRC_ACCOUNT);
        System.out.println("My Address: " + src.getAddress());

        Response<TransactionParametersResponse> paramsResponse = client.TransactionParams().execute();
        TransactionParametersResponse params = null;
        if (supplyResponse.isSuccessful()) {
            params = paramsResponse.body();
            System.out.println("Suggested Fee: " + params.fee);
            System.out.println("Current Round: " + params.lastRound);
        } else {
            System.out.println("There was a problem getting the transaction parameters: " + paramsResponse.message());
            System.exit(1);
        }

        /////////////////////////
        // Payment transaction //
        /////////////////////////
        // Because we are using randomly generated accounts an overspend error is expected.

        System.out.println("Attempting an invalid transaction: overspending using randomly generated accounts.");
        System.out.println("Expecting overspend exception.");

        long amount = 100000;
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(src.getAddress())
                .receiver(new Address(DEST_ADDR))
                .amount(amount)
                .suggestedParams(params)
                .build();
        SignedTransaction signedTx = src.signTransaction(tx);
        System.out.println("Signed transaction with txid: " + signedTx.transactionID);

        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
        Response<PostTransactionsResponse> postResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute();
        if (postResponse.isSuccessful()) {
            System.out.println("This shouldn't have succeeded: " + postResponse.body().txId);
            System.exit(1);
        } else {
            System.out.println("We expect an ovserspend exception, actual message: " + postResponse.message());
        }

        ///////////////////////////////
        // Transaction Group Example //
        ///////////////////////////////
        // For example purposes the same transaction is being added twice.

        Transaction[] transactions = TxGroup.assignGroupID(tx, tx);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Sign all transactions and add them to a byte array.
        for (Transaction transaction : transactions) {
            // Because we have added the group field, the fee is recomputed.
            SignedTransaction stx = src.signTransactionWithFeePerByte(transaction, BigInteger.valueOf(params.fee));
            baos.write(Encoder.encodeToMsgPack(stx));
        }

        postResponse = client.RawTransaction().rawtxn(baos.toByteArray()).execute();
        if (postResponse.isSuccessful()) {
            System.out.println("This shouldn't have succeeded, txid is: " + postResponse.body().txId);
            System.exit(1);
        } else {
            System.out.println("The group transaction fail (as expected) with message: " + postResponse.message());
        }

        /////////////////////
        // Logic Signature //
        /////////////////////

        // format and send logic sig
        byte[] program = {
            0x01, 0x20, 0x01, 0x00, 0x22  // int 0
        };

        LogicsigSignature lsig = new LogicsigSignature(program, null);
        System.out.println("Escrow address: " + lsig.toAddress().toString());

        tx = Transaction.PaymentTransactionBuilder()
                .sender(lsig.toAddress())
                .receiver(new Address(DEST_ADDR))
                .amount(amount)
                .lookupParams(client)
                .build();

        if (!lsig.verify(tx.sender)) {
            System.err.println("Verification failed");
            System.exit(1);
        } else {
            SignedTransaction stx = Account.signLogicsigTransaction(lsig, tx);
            encodedTxBytes = Encoder.encodeToMsgPack(stx);
            postResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute();
            if (postResponse.isSuccessful()) {
                System.out.println("This shouldn't have succeeded, txid is: " + postResponse.body().txId);
                System.exit(1);
            }
            else {
                System.out.println("The logicsig transaction fail (as expected) with message: " + postResponse.message());
            }
        }

        //////////////////////////////
        // Other Transactions Types //
        //////////////////////////////


        // Create an asset
        Transaction assetCreate = Transaction.AssetCreateTransactionBuilder()
                .sender(src.getAddress())
                .assetName("MyCoin")
                .assetUnitName("mc")
                .assetTotal(Long.MAX_VALUE)
                .assetDecimals(6)
                .clawback(src.getAddress())
                .freeze(src.getAddress())
                .manager(src.getAddress())
                .reserve(src.getAddress())
                .lookupParams(client)
                .build();

        // Create an application
        Transaction applicationCreate = Transaction.ApplicationCreateTransactionBuilder()
                .approvalProgram(new TEALProgram(program))
                .clearStateProgram(new TEALProgram(program))
                .globalStateSchema(new StateSchema(0, 1))
                .localStateSchema(new StateSchema(0, 1));
                .lookupParams(client)
                .build();
    }

    /**
     * Examples using the legacy v1 client and transaction constructors.
     */
    public static void v1Client(String addr, String token) throws Exception {
        final String SRC_ACCOUNT = "viable grain female caution grant mind cry mention pudding oppose orchard people forget similar social gossip marble fish guitar art morning ring west above concert";
        final String DEST_ADDR = "KV2XGKMXGYJ6PWYQA5374BYIQBL3ONRMSIARPCFCJEAMAHQEVYPB7PL3KU";

        com.algorand.algosdk.algod.client.AlgodClient client = new com.algorand.algosdk.algod.client.AlgodClient();
        client.setBasePath(addr);
        // Configure API key authorization: api_key
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(token);

        AlgodApi algodApiInstance = new AlgodApi(client);
        try {
            Supply supply = algodApiInstance.getSupply();
            System.out.println("Total Algorand Supply: " + supply.getTotalMoney());
            System.out.println("Online Algorand Supply: " + supply.getOnlineMoney());
        } catch (ApiException e) {
            System.err.println("Exception when calling algod#getSupply");
            e.printStackTrace();
        }

        Account src = new Account(SRC_ACCOUNT);
        System.out.println("My Address: " + src.getAddress());

        BigInteger feePerByte;
        String genesisID;
        Digest genesisHash;
        long firstRound = 301;
        try {
            TransactionParams params = algodApiInstance.transactionParams();
            feePerByte = params.getFee();
            genesisHash = new Digest(params.getGenesishashb64());
            genesisID = params.getGenesisID();
            System.out.println("Suggested Fee: " + feePerByte);
            NodeStatus s = algodApiInstance.getStatus();
            firstRound = s.getLastRound().longValue();
            System.out.println("Current Round: " + firstRound);
        } catch (ApiException e) {
            throw new RuntimeException("Could not get params", e);
        }

        // Generate a new transaction using randomly generated accounts (this is invalid, since src has no money...)
        System.out.println("Attempting an invalid transaction: overspending using randomly generated accounts.");
        System.out.println("Expecting overspend exception.");

        long amount = 100000;
        long lastRound = firstRound + 1000; // 1000 is the max tx window
        Transaction tx = new Transaction(src.getAddress(), new Address(DEST_ADDR), amount, firstRound, lastRound, genesisID, genesisHash);
        SignedTransaction signedTx = src.signTransactionWithFeePerByte(tx, feePerByte);
        System.out.println("Signed transaction with txid: " + signedTx.transactionID);

        // send the transaction to the network
        try {
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
            TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);
            System.out.println("Successfully sent tx with id: " + id);
        } catch (ApiException e) {
            // This is generally expected, but should give us an informative error message.
            System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
        }
        // Let's create a transaction group, for example purposes the same transaction is being added twice.
        Digest gid = TxGroup.computeGroupID(tx, tx);
        tx.assignGroupID(gid);
        signedTx = src.signTransactionWithFeePerByte(tx, feePerByte);
        try {
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
            byte[] concat = Arrays.copyOf(encodedTxBytes, encodedTxBytes.length + encodedTxBytes.length);
            System.arraycopy(encodedTxBytes, 0, concat, encodedTxBytes.length, encodedTxBytes.length);
            TransactionID id = algodApiInstance.rawTransaction(concat);
            System.out.println("Successfully sent tx group with first tx id: " + id);
        } catch (ApiException e) {
            // This is generally expected, but should give us an informative error message.
            System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
        }

        // format and send logic sig
        byte[] program = {
            0x01, 0x20, 0x01, 0x00, 0x22  // int 0
        };

        LogicsigSignature lsig = new LogicsigSignature(program, null);
        System.out.println("Escrow address: " + lsig.toAddress().toString());

        tx = new Transaction(lsig.toAddress(), new Address(DEST_ADDR), amount, firstRound, lastRound, genesisID, genesisHash);
        if (!lsig.verify(tx.sender)) {
            String msg = "Verification failed";
            System.err.println(msg);
        } else {
            try {
                SignedTransaction stx = Account.signLogicsigTransaction(lsig, tx);
                byte[] encodedTxBytes = Encoder.encodeToMsgPack(stx);
                TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);
                System.out.println("Successfully sent tx logic sig tx id: " + id);
            } catch (ApiException e) {
                // This is generally expected, but should give us an informative error message.
                System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
            }
        }
    }

    /**
     * Using the kmd API to create a wallet.
     */
    public static void kmdApi() {
        final String KMD_API_ADDR = "http://localhost:7833";
        final String KMD_API_TOKEN = "1bce699faef65c80da8da6201bd0639b3ea4205c4fa05d24f94469efa2418f2d";

        // Create a wallet with kmd rest api
        KmdClient client = new KmdClient();
        client.setBasePath(KMD_API_ADDR);
        // Configure API key authorization: api_key
        com.algorand.algosdk.kmd.client.auth.ApiKeyAuth api_key = (com.algorand.algosdk.kmd.client.auth.ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(KMD_API_TOKEN);
        KmdApi kmdApiInstance = new KmdApi(client);

        APIV1POSTWalletResponse wallet;
        try {
            CreateWalletRequest req = new CreateWalletRequest()
                    .walletName("arbitrary-wallet-name-3")
                    .walletDriverName("sqlite");
            wallet = kmdApiInstance.createWallet(req);
            System.out.println("Created wallet id: " + wallet.getWallet().getId());
        } catch (com.algorand.algosdk.kmd.client.ApiException e) {
            System.out.println("Failed to create wallet: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
