package com.algorand.algosdk.integration;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.cucumber.shared.Utils;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.algorand.algosdk.cucumber.shared.TransactionSteps.loadTEALProgramFromFile;
import static com.algorand.algosdk.util.ConversionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class Applications {
    private final Clients clients;
    private final Stepdefs base;
    private final TransientAccount transientAccount;

    public Transaction transaction;
    public String txId = null;
    public Long appId = 0L;

    public Applications(TransientAccount transientAccount, Clients clients, Stepdefs base) {
        this.transientAccount = transientAccount;
        this.clients = clients;
        this.base = base;
    }

    @Given("I build an application transaction with the transient account, the current application, suggested params, operation {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, extra-pages {long}")
    public void buildAnApplicationTransactions(String operation, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long extraPages) throws Exception {
        ApplicationBaseTransactionBuilder builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .extraPages(extraPages);
                break;
            case "create_optin":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .optIn(true);
                break;
            case "update":
                builder = Transaction.ApplicationUpdateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile));
                break;
            case "call":
                builder = Transaction.ApplicationCallTransactionBuilder();
                break;
            case "optin":
                builder = Transaction.ApplicationOptInTransactionBuilder();
                break;
            case "clear":
                builder = Transaction.ApplicationClearTransactionBuilder();
                break;
            case "closeout":
                builder = Transaction.ApplicationCloseTransactionBuilder();
                break;
            case "delete":
                builder = Transaction.ApplicationDeleteTransactionBuilder();
                break;
            default:
                Assertions.fail("Need an option to build: " + operation);
        }

        // Shared base fields
        if (StringUtils.isNotEmpty(appArgs)) {
            builder.args(convertArgs(appArgs));
        }
        if (StringUtils.isNotEmpty(foreignApps)) {
            builder.foreignApps(convertForeignApps(foreignApps));
        }
        if (StringUtils.isNotEmpty(foreignAssets)) {
            builder.foreignAssets(convertForeignAssets(foreignAssets));
        }
        if (StringUtils.isNotEmpty(appAccounts)) {
            builder.accounts(convertAccounts(appAccounts));
        }

        // Send with transient account, suggested params and current application
        builder.sender(this.transientAccount.transientAccount.getAddress());
        builder.lookupParams(this.clients.v2Client);
        if (this.appId != 0) {
            builder.applicationId(appId);
        }

        this.transaction = builder.build();
    }

    @Given("I sign and submit the transaction, saving the txid. If there is an error it is {string}.")
    public void sendTransactionWithTransientAccountAndCheckForError(String error) throws Exception {
        SignedTransaction stx = this.transientAccount.transientAccount.signTransaction(this.transaction);

        // Submit
        Response<PostTransactionsResponse> rPost = clients.v2Client.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();

        // If an error was expected, make sure it is set correctly.
        if (StringUtils.isNotEmpty(error)) {
            assertThat(rPost.isSuccessful()).isFalse();
            assertThat(rPost.message()).containsIgnoringCase(error);
            return;
        }

        // Otherwise make sure the transaction was submitted successfully.
        assertThat(rPost.isSuccessful()).isTrue();

        // And save the txId for later
        this.txId = rPost.body().txId;
    }

    @Given("I wait for the transaction to be confirmed.")
    public void waitForTransactionToBeConfirmed() throws Exception {
        Utils.waitForConfirmation(clients.v2Client, txId);
    }

    // TODO: Use V2 Pending Transaction endpoint when it is available.
    //       The initial implementation hacks into the v1 endpoint to manually extract the new data.
    @Given("I remember the new application ID.")
    public void rememberTheNewApplicatoinId() throws Exception {
        PendingTransactionResponse r = clients.v2Client.PendingTransactionInformation(txId).execute().body();
        this.appId = r.applicationIndex;
    }

    @Then("The transient account should have the created app {string} and total schema byte-slices {long} and uints {long}, the application {string} state contains key {string} with value {string}")
    public void checkAccountData(
            String created,
            Long numByteSlices,
            Long numUints,
            String stateLocation,
            String hasKey,
            String keyValue
    ) throws Exception {
        Response<com.algorand.algosdk.v2.client.model.Account> acctResponse = clients.v2Client.AccountInformation(transientAccount.transientAccount.getAddress()).execute();

        com.algorand.algosdk.v2.client.model.Account acct = acctResponse.body();

        // Check the total schema sizes
        assertThat(acct.appsTotalSchema.numByteSlice).isEqualTo(numByteSlices);
        assertThat(acct.appsTotalSchema.numUint).isEqualTo(numUints);

        // If we don't expect the app to exist, verify that it isn't there and exit.
        if (Boolean.parseBoolean(created) == false) {
            assertThat(acct.createdApps).extracting("id").doesNotContain(appId);
            return;
        }

        // Top level assertions.
        assertThat(acct.createdApps).extracting("id").contains(appId);

        // If there is no key to check, we're done.
        if (StringUtils.isEmpty(hasKey)) {
            return;
        }

        // Verify the key-value is set
        boolean found = false;

        List<TealKeyValue> keyValues = null;

        // Find global or local key-values
        if (StringUtils.equalsIgnoreCase(stateLocation, "local")) {
            List<ApplicationLocalState> matches = acct.appsLocalState.stream()
                    .filter(app -> app.id.equals(appId))
                    .collect(Collectors.toList());
            assertThat(matches).hasSize(1);
            keyValues = matches.get(0).keyValue;
        }
        if (StringUtils.equalsIgnoreCase(stateLocation, "global")) {
            List<ApplicationParams> matches = acct.createdApps.stream()
                    .filter(app -> app.id.equals(appId))
                    .map(app -> app.params)
                    .collect(Collectors.toList());
            assertThat(matches).hasSize(1);
            keyValues = matches.get(0).globalState;
        }

        // Check for expected key-value
        assertThat(keyValues).hasSizeGreaterThan(0);
        for (TealKeyValue kv : keyValues) {
            if (StringUtils.equals(kv.key, hasKey)) {
                if (kv.value.type.equals(1)) {
                    assertThat(kv.value.bytes).isEqualTo(keyValue);
                } else if (kv.value.type.equals(0)) {
                    assertThat(kv.value.uint).isEqualTo(Long.parseLong(keyValue));
                }
                // Check that the values are equal.
                found = true;
            }
        }

        assertThat(found).as("Couldn't find key '%s'", hasKey).isTrue();
    }
}
