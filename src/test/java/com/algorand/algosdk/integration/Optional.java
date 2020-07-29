package com.algorand.algosdk.integration;

import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import io.cucumber.java.en.Then;

public class Optional {
    private final Clients clients;
    private final TransientAccount transientAccount;
    private final Applications applications;

    private Optional(Clients clients, TransientAccount transientAccount, Applications applications) {
        this.clients = clients;
        this.transientAccount = transientAccount;
        this.applications = applications;
    }

    @Then("the confirmed pending transaction by ID should have a <state-location> state change for \"foo\" to \"bar\"")
    public void checkConfirmedTransaction() throws Exception {
        Response<PendingTransactionResponse> r = clients.v2Client
                .PendingTransactionInformation(clients.v2Client.PendingTransactionInformation(applications.txId)
                .execute();
    }
}
