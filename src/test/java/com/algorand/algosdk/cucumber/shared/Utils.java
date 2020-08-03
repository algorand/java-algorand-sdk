package com.algorand.algosdk.cucumber.shared;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;

import java.time.Duration;

public class Utils {
    public static void waitForConfirmation(AlgodClient client, String txId) throws Exception {
        Duration timeout = Duration.ofSeconds(10);
        Long start = System.currentTimeMillis();
        Response<PendingTransactionResponse> r;
        // Keep checking until the timeout.
        do {
            r = client.PendingTransactionInformation(txId).execute();
            // If the transaction has been confirmed, exit.
            if (r.body().confirmedRound != null) {
                return;
            }
            Thread.sleep(250);
        } while ( (System.currentTimeMillis() - start) < timeout.toMillis());
    }
}
