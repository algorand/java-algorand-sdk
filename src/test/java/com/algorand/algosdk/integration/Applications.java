package com.algorand.algosdk.integration;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.ComparableBytes;
import com.algorand.algosdk.util.Digester;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.algorand.algosdk.util.ResourceUtils.loadTEALProgramFromFile;
import static com.algorand.algosdk.util.ConversionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class Applications {
    public Stepdefs base;
    public TransientAccount transientAccount;

    public Transaction transaction;
    public String txId = null;
    public Long lastTxConfirmedRound = 0L;
    public Long appId = 0L;
    public List<Long> rememberedAppIds = new ArrayList<>();

    public Applications(TransientAccount transientAccount, Stepdefs base) {
        this.transientAccount = transientAccount;
        this.base = base;
    }

    @Given("I build an application transaction with the transient account, the current application, suggested params, operation {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, extra-pages {long}, boxes {string}")
    public void buildAnApplicationTransactions(String operation, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long extraPages, String boxesStr) throws Exception {
        ApplicationBaseTransactionBuilder builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.base.aclv2))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.base.aclv2))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .extraPages(extraPages);
                break;
            case "create_optin":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.base.aclv2))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.base.aclv2))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .optIn(true);
                break;
            case "update":
                builder = Transaction.ApplicationUpdateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.base.aclv2))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.base.aclv2));
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
        if (StringUtils.isNotEmpty(boxesStr)) {
            builder.boxReferences(convertBoxes(boxesStr));
        }

        // Send with transient account, suggested params and current application
        builder.sender(this.transientAccount.transientAccount.getAddress());
        builder.lookupParams(this.base.aclv2);
        if (this.appId != 0 && !operation.equals("create")) {
            builder.applicationId(appId);
        }

        this.transaction = builder.build();
    }

    @Given("I sign and submit the transaction, saving the txid. If there is an error it is {string}.")
    public void sendTransactionWithTransientAccountAndCheckForError(String error) throws Exception {
        SignedTransaction stx = this.transientAccount.transientAccount.signTransaction(this.transaction);

        // Submit
        Response<PostTransactionsResponse> rPost = base.aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();

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
        base.txid = this.txId;
    }

    @Given("I wait for the transaction to be confirmed.")
    public void waitForTransactionToBeConfirmed() throws Exception {
        PendingTransactionResponse response = Utils.waitForConfirmation(base.aclv2, base.txid, 1);
        this.lastTxConfirmedRound = response.confirmedRound;
    }

    @Given("I remember the new application ID.")
    public void rememberTheNewApplicatoinId() throws Exception {
        PendingTransactionResponse r = base.aclv2.PendingTransactionInformation(txId).execute().body();
        this.appId = r.applicationIndex;
        this.rememberedAppIds.add(this.appId);
    }

    @Given("I reset the array of application IDs to remember.")
    public void i_reset_the_array_of_application_i_ds_to_remember() {
        this.rememberedAppIds = new ArrayList<>();
    }

    @Given("I fund the current application's address with {int} microalgos.")
    public void fundAppAccount(Integer amount) throws Exception {
        Address appAddress = Address.forApplication(this.appId);
        Address sender = base.getAddress(0);
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .receiver(appAddress)
                .amount(amount)
                .lookupParams(base.aclv2)
                .build();
        SignedTransaction stx = base.signWithAddress(tx, sender);

        Response<PostTransactionsResponse> rPost = base.aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();
        Utils.waitForConfirmation(base.aclv2, rPost.body().txId, 1);
    }

    @Then("I get the account address for the current application and see that it matches the app id's hash")
    public void compareApplicationIDHashWithAccountAddress() throws NoSuchAlgorithmException, IOException {
        Address accountAddress = Address.forApplication(this.appId);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(Address.APP_ID_PREFIX);
        buffer.write(Encoder.encodeUint64(this.appId));
        Address computedHash = new Address(Digester.digest(buffer.toByteArray()));
        assertThat(computedHash).isEqualTo(accountAddress);
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
        Response<com.algorand.algosdk.v2.client.model.Account> acctResponse = base.aclv2.AccountInformation(this.transientAccount.transientAccount.getAddress()).execute();

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

    @Then("according to {string}, the contents of the box with name {string} in the current application should be {string}. If there is an error it is {string}.")
    public void contentsOfBoxShouldBe(String fromClient, String encodedBoxName, String boxContents, String errStr) throws Exception {
        Response<Box> boxResp;
        if (fromClient.equals("algod"))
            boxResp = base.aclv2.GetApplicationBoxByName(this.appId).name(encodedBoxName).execute();
        else if (fromClient.equals("indexer"))
            boxResp = base.v2IndexerClient.lookupApplicationBoxByIDAndName(this.appId).name(encodedBoxName).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + fromClient);

        // If an error was expected, make sure it is set correctly.
        if (StringUtils.isNotEmpty(errStr)) {
            assertThat(boxResp.isSuccessful()).isFalse();
            assertThat(boxResp.message()).containsIgnoringCase(errStr);
            return;
        }

        assertThat(boxResp.body().value()).isEqualTo(boxContents);
    }

    private static void assertSetOfByteArraysEqual(Set<byte[]> expected, Set<byte[]> actual) {
        Set<ComparableBytes> expectedComparable = new HashSet<>();
        for (byte[] element : expected) {
            expectedComparable.add(new ComparableBytes(element));
        }

        Set<ComparableBytes> actualComparable = new HashSet<>();
        for (byte[] element : actual) {
            actualComparable.add(new ComparableBytes(element));
        }

        Assert.assertEquals(expectedComparable, actualComparable);
    }

    @Then("according to {string}, the current application should have the following boxes {string}.")
    public void checkAppBoxes(String fromClient, String encodedBoxesRaw) throws Exception {
        Response<BoxesResponse> r;
        if (fromClient.equals("algod"))
            r = base.aclv2.GetApplicationBoxes(this.appId).execute();
        else if (fromClient.equals("indexer"))
            r = base.v2IndexerClient.searchForApplicationBoxes(this.appId).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + fromClient);

        Assert.assertTrue(r.isSuccessful());

        final Set<byte[]> expectedNames = new HashSet<>();
        if (!encodedBoxesRaw.isEmpty()) {
            for (String s : encodedBoxesRaw.split(":")) {
                expectedNames.add(Encoder.decodeFromBase64(s));
            }
        }

        final Set<byte[]> actualNames = new HashSet<>();
        for (BoxDescriptor b : r.body().boxes) {
            actualNames.add(b.name);
        }

        assertSetOfByteArraysEqual(expectedNames, actualNames);
    }

    @Then("according to {string}, with {long} being the parameter that limits results, the current application should have {int} boxes.")
    public void checkAppBoxesNum(String fromClient, Long limit, int expected_num) throws Exception {
        Response<BoxesResponse> r;
        if (fromClient.equals("algod"))
            r = base.aclv2.GetApplicationBoxes(this.appId).max(limit).execute();
        else if (fromClient.equals("indexer"))
            r = base.v2IndexerClient.searchForApplicationBoxes(this.appId).limit(limit).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + fromClient);

        Assert.assertTrue(r.isSuccessful());
        Assert.assertEquals("expected " + expected_num + " boxes, actual " + r.body().boxes.size(),
                r.body().boxes.size(), expected_num);
    }

    @Then("according to indexer, with {long} being the parameter that limits results, and {string} being the parameter that sets the next result, the current application should have the following boxes {string}.")
    public void indexerCheckAppBoxesWithParams(Long limit, String next, String encodedBoxesRaw) throws Exception {
        Response<BoxesResponse> r = base.v2IndexerClient.searchForApplicationBoxes(this.appId).limit(limit).next(next).execute();
        final Set<byte[]> expectedNames = new HashSet<>();
        if (!encodedBoxesRaw.isEmpty()) {
            for (String s : encodedBoxesRaw.split(":")) {
                expectedNames.add(Encoder.decodeFromBase64(s));
            }
        }

        final Set<byte[]> actualNames = new HashSet<>();
        for (BoxDescriptor b : r.body().boxes) {
            actualNames.add(b.name);
        }

        assertSetOfByteArraysEqual(expectedNames, actualNames);
    }

    @Then("I wait for indexer to catch up to the round where my most recent transaction was confirmed.")
    public void sleepForNSecondsForIndexer() throws Exception {
        final int maxAttempts = 30;

        final long roundToWaitFor = this.lastTxConfirmedRound;
        long indexerRound = 0;
        int attempts = 0;

        while(true) {
            Response<HealthCheck> response = base.v2IndexerClient.makeHealthCheck().execute();
            Assert.assertTrue(response.isSuccessful());
            indexerRound = response.body().round;
            if (indexerRound >= roundToWaitFor) {
                // Success
                break;
            }

            // Sleep for 1 second and try again
            Thread.sleep(1000);
            attempts++;

            if (attempts > maxAttempts) {
                throw new Exception("Timeout waiting for indexer to catch up to round " + roundToWaitFor + ". It is currently on " + indexerRound);
            }
        }
    }
}
