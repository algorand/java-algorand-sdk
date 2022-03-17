package com.algorand.algosdk.v2.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.Transaction.Type;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.DryrunRequest;
import com.algorand.algosdk.v2.client.model.Application;
import com.algorand.algosdk.v2.client.model.ApplicationParams;
import com.algorand.algosdk.v2.client.model.ApplicationStateSchema;
import com.algorand.algosdk.v2.client.model.Account;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;

public class Utils {

    // https://github.com/algorand/go-algorand/blob/e466aa18d4d963868d6d15279b1c881977fa603f/libgoal/libgoal.go#L1089-L1090
    private static final Long defaultAppId = 1380011588L;

    /**
     * Construct a DryruynRequest object from a set of transactions. 
     * A DryrunRequest is composed of static balance information.  This function uses the ApplicationCall transaction 
     * parameters to infer what Application State and Account balance information to query using the client and adds it to the DryrunRequest object. 
     * If foreign assets are passed, it will also add the creators balance information to the DryrunRequest. 
     * 
     * @param client           an Algod v2 client
     * @param txns             the array of SignedTransactions that should be used
     *                         to generate the DryrunRequest
     * @return DryrunRequest to be submitted to TealDryrun endpoint 
     * @throws Exception if transaction is rejected or the transaction is not
     *                   confirmed before wait round
     *
     */
    public static DryrunRequest createDryrun(AlgodClient client, List<SignedTransaction> txns) throws Exception {
        return Utils.createDryrun(client, txns, "", 0L, 0L);
    }

    /**
     * Construct a DryruynRequest object from a set of transactions. 
     * A DryrunRequest is composed of static balance information.  This function uses the ApplicationCall transaction 
     * parameters to infer what Application State and Account balance information to query using the client and adds it to the DryrunRequest object. 
     * If foreign assets are passed, it will also add the creators balance information to the DryrunRequest. 
     * 
     * @param client           an Algod v2 client
     * @param txns             the array of SignedTransactions that should be used
     *                         to generate the DryrunRequest
     * @param protocol_version The protocol version the dryrun should include
     * @param latest_timestamp The latest timestamp the dryrun should include
     * @param round            The agreement round or block height the dryrun should
     *                         include
     * @return DryrunRequest to be submitted to TealDryrun endpoint 
     * @throws Exception if transaction is rejected or the transaction is not
     *                   confirmed before wait round
     */
    public static DryrunRequest createDryrun(AlgodClient client, List<SignedTransaction> txns, String protocol_version,
            Long latest_timestamp, Long round)
            throws Exception {

        if (client == null || txns.size() == 0) {
            throw new IllegalArgumentException("Bad arguments for createDryrun.");
        }

        // The details we need to add to DryrunRequest object
        ArrayList<Application> app_infos = new ArrayList<>();
        ArrayList<Account> acct_infos = new ArrayList<>();

        // These are populated from the transactions passed
        Set<Long> apps = new HashSet<>();
        Set<Long> assets = new HashSet<>();
        Set<String> accts = new HashSet<>();

        for (SignedTransaction txn : txns) {
            Transaction tx = txn.tx;

            // We're only interested to pull state for app calls
            if (tx.type != Type.ApplicationCall) {
                continue;
            }

            // If this is a create transaction
            if (tx.applicationId == 0 || tx.applicationId == null) {
                // Prepare and set param fields for Application being created
                // from the transaction passed
                ApplicationParams params = new ApplicationParams();
                params.creator = tx.sender;
                params.approvalProgram = tx.approvalProgram.getBytes();
                params.clearStateProgram = tx.clearStateProgram.getBytes();

                ApplicationStateSchema localState = new ApplicationStateSchema();
                localState.numByteSlice = tx.localStateSchema.numByteSlice.longValue();
                localState.numUint = tx.localStateSchema.numUint.longValue();
                params.localStateSchema = localState;

                ApplicationStateSchema globalState = new ApplicationStateSchema();
                globalState.numByteSlice = tx.globalStateSchema.numByteSlice.longValue();
                globalState.numUint = tx.globalStateSchema.numUint.longValue();
                params.globalStateSchema = globalState;

                Application app = new Application();
                app.id = Utils.defaultAppId;
                app.params = params;

                app_infos.add(app);
            } else {
                apps.add(tx.applicationId);
                accts.add(Address.forApplication(tx.applicationId).toString());
            }

            if (tx.foreignApps.size() > 0) {
                apps.addAll(tx.foreignApps);
            }

            if (tx.foreignAssets.size() > 0) {
                assets.addAll(tx.foreignAssets);
            }

            if (tx.accounts.size() > 0) {
                for (Address acct : tx.accounts) {
                    accts.add(acct.toString());
                }
            }
        }

        for (Long asset : assets) {
            Response<Asset> ar = client.GetAssetByID(asset).execute();
            if(ar.isSuccessful()){
                Asset a = ar.body();
                accts.add(a.params.creator);
            }
        }

        for (Long app : apps) {
            Response<Application> ar = client.GetApplicationByID(app).execute();
            if(ar.isSuccessful()){
                app_infos.add(ar.body());
                accts.add(ar.body().params.creator.toString());
            }
        }

        for (String acct : accts) {
            Response<Account> ar = client.AccountInformation(new Address(acct)).execute();
            if(ar.isSuccessful()){
                acct_infos.add(ar.body());
            }
        }

        DryrunRequest drr = new DryrunRequest();
        drr.accounts = acct_infos;
        drr.apps = app_infos;
        drr.txns = txns;
        drr.protocolVersion = protocol_version;
        drr.latestTimestamp = latest_timestamp;
        drr.round = BigInteger.valueOf(round);
        return drr;
    }

    /**
     * Wait until a transaction has been confirmed or rejected by the network
     * or wait until waitRound fully elapsed
     * 
     * @param client     an Algod v2 client
     * @param txID       the transaction ID that we are waiting
     * @param waitRounds The maximum number of rounds to wait for.
     * @return TransactionResponse of the confirmed transaction
     * @throws Exception if transaction is rejected or the transaction is not
     *                   confirmed before wait round
     */
    public static PendingTransactionResponse waitForConfirmation(AlgodClient client, String txID, int waitRounds)
            throws Exception {
        if (client == null || txID == null || waitRounds < 0) {
            throw new IllegalArgumentException("Bad arguments for waitForConfirmation.");
        }
        Response<NodeStatusResponse> resp = client.GetStatus().execute();
        if (!resp.isSuccessful()) {
            throw new Exception(resp.message());
        }
        NodeStatusResponse nodeStatusResponse = resp.body();
        long startRound = nodeStatusResponse.lastRound + 1;
        long currentRound = startRound;
        while (currentRound < (startRound + waitRounds)) {
            // Check the pending transactions
            Response<PendingTransactionResponse> resp2 = client.PendingTransactionInformation(txID).execute();
            if (resp2.isSuccessful()) {
                PendingTransactionResponse pendingInfo = resp2.body();
                if (pendingInfo != null) {
                    if (pendingInfo.confirmedRound != null && pendingInfo.confirmedRound > 0) {
                        // Got the completed Transaction
                        return pendingInfo;
                    }
                    if (pendingInfo.poolError != null && pendingInfo.poolError.length() > 0) {
                        // If there was a pool error, then the transaction has been rejected!
                        throw new Exception(
                                "The transaction has been rejected with a pool error: " + pendingInfo.poolError);
                    }
                }
            }
            resp = client.WaitForBlock(currentRound).execute();
            if (!resp.isSuccessful()) {
                throw new Exception(resp.message());
            }
            currentRound++;
        }
        throw new Exception("Transaction not confirmed after " + waitRounds + " rounds!");
    }
}
