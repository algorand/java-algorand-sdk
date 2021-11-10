package com.algorand.algosdk.v2.client;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;

public class Utils {
    /**
     * Wait until a transaction has been confirmed or rejected by the network
     * or wait until waitRound fully elapsed
     * @param client an Algod v2 client
     * @param txID the transaction ID that we are waiting
     * @param waitRounds The maximum number of rounds to wait for.
     * @return TransactionResponse of the confirmed transaction
     * @throws Exception if transaction is rejected or the transaction is not confirmed before wait round
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
                        throw new Exception("The transaction has been rejected with a pool error: " + pendingInfo.poolError);
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
