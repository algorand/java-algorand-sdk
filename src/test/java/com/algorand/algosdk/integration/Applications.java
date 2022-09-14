package com.algorand.algosdk.integration;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Digester;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.bouncycastle.util.Strings;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.algorand.algosdk.util.ResourceUtils.loadTEALProgramFromFile;
import static com.algorand.algosdk.util.ConversionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class Applications {
    public Clients clients;
    public Stepdefs base;
    public TransientAccount transientAccount;

    public Transaction transaction;
    public String txId = null;
    public Long appId = 0L;
    public List<Long> rememberedAppIds = new ArrayList<>();
    public TransactionParametersResponse sp = null;
    static final String zeroAddress = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY5HFKQ";

    public Applications(TransientAccount transientAccount, Clients clients, Stepdefs base) {
        this.transientAccount = transientAccount;
        this.clients = clients;
        this.base = base;
    }

    @Given("I build an application transaction with the transient account, the current application, suggested params, operation {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, foreign-assets {string}, app-accounts {string}, extra-pages {long}, boxes {string}")
    public void buildAnApplicationTransactions(String operation, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String foreignAssets, String appAccounts, Long extraPages, String boxesStr) throws Exception {
        ApplicationBaseTransactionBuilder builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.clients.v2Client))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.clients.v2Client))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .extraPages(extraPages);
                break;
            case "create_optin":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.clients.v2Client))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.clients.v2Client))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes))
                        .optIn(true);
                break;
            case "update":
                builder = Transaction.ApplicationUpdateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile, this.clients.v2Client))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile, this.clients.v2Client));
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
        builder.lookupParams(this.clients.v2Client);
        if (this.appId != 0 && !operation.equals("create")) {
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
        Utils.waitForConfirmation(clients.v2Client, txId, 1);
    }

    // TODO: Use V2 Pending Transaction endpoint when it is available.
    //       The initial implementation hacks into the v1 endpoint to manually extract the new data.
    @Given("I remember the new application ID.")
    public void rememberTheNewApplicatoinId() throws Exception {
        PendingTransactionResponse r = clients.v2Client.PendingTransactionInformation(txId).execute().body();
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
                .lookupParams(clients.v2Client)
                .build();
        SignedTransaction stx = base.signWithAddress(tx, sender);

        Response<PostTransactionsResponse> rPost = clients.v2Client.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();
        Utils.waitForConfirmation(clients.v2Client, rPost.body().txId, 1);
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

    @Then("according to {string}, the contents of the box with name {string} in the current application should be {string}. If there is an error it is {string}.")
    public void contentsOfBoxShouldBe(String from_client, String encodedBoxName, String boxContents, String errStr) throws Exception {
        Response<Box> boxResp;
        if (from_client.equals("algod"))
            boxResp = clients.v2Client.GetApplicationBoxByName(this.appId).name(encodedBoxName).execute();
        else if (from_client.equals("indexer"))
            boxResp = clients.v2IndexerClient.lookupApplicationBoxByIDandName(this.appId).name(encodedBoxName).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + from_client);

        // If an error was expected, make sure it is set correctly.
        if (StringUtils.isNotEmpty(errStr)) {
            assertThat(boxResp.isSuccessful()).isFalse();
            assertThat(boxResp.message()).containsIgnoringCase(errStr);
            return;
        }

        assertThat(boxResp.body().value()).isEqualTo(boxContents);
    }

    private static byte[] decodeBoxName(String encodedBoxName) {
        String[] split = Strings.split(encodedBoxName, ':');
        if (split.length != 2)
            throw new RuntimeException("encodedBoxName (" + encodedBoxName + ") does not match expected format");

        String encoding = split[0];
        String encoded = split[1];
        switch (encoding) {
            case "str":
                return encoded.getBytes(StandardCharsets.US_ASCII);
            case "b64":
                return Encoder.decodeFromBase64(encoded);
            default:
                throw new RuntimeException("Unsupported encoding = " + encoding);
        }
    }

    private static boolean contains(byte[] elem, List<byte[]> xs) {
        for (byte[] e : xs) {
            if (Arrays.equals(e, elem))
                return true;
        }
        return false;
    }

    @Then("according to {string}, the current application should have the following boxes {string}.")
    public void checkAppBoxes(String from_client, String encodedBoxesRaw) throws Exception {
        Response<BoxesResponse> r;
        if (from_client.equals("algod"))
            r = clients.v2Client.GetApplicationBoxes(this.appId).execute();
        else if (from_client.equals("indexer"))
            r = clients.v2IndexerClient.searchForApplicationBoxes(this.appId).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + from_client);

        Assert.assertTrue(r.isSuccessful());

        final List<byte[]> expectedNames = Lists.newArrayList();
        if (!encodedBoxesRaw.isEmpty()) {
            for (String s : Strings.split(encodedBoxesRaw, ':')) {
                expectedNames.add(Encoder.decodeFromBase64(s));
            }
        }

        final List<byte[]> actualNames = Lists.newArrayList();
        for (BoxDescriptor b : r.body().boxes) {
            actualNames.add(b.name);
        }

        Assert.assertEquals("expected and actual box names length do not match", expectedNames.size(), actualNames.size());
        for (byte[] e : expectedNames) {
            if (!contains(e, actualNames))
                throw new RuntimeException("expected and actual box names do not match: " + expectedNames + " != " + actualNames);
        }
    }

    @Then("according to {string}, with {long} being the parameter that limits results, the current application should have {int} boxes.")
    public void checkAppBoxesNum(String fromClient, Long limit, int expected_num) throws Exception {
        Response<BoxesResponse> r;
        if (fromClient.equals("algod"))
            r = clients.v2Client.GetApplicationBoxes(this.appId).max(limit).execute();
        else if (fromClient.equals("indexer"))
            r = clients.v2IndexerClient.searchForApplicationBoxes(this.appId).limit(limit).execute();
        else
            throw new IllegalArgumentException("expecting algod or indexer, got " + fromClient);

        Assert.assertTrue(r.isSuccessful());
        Assert.assertEquals("expected " + expected_num + " boxes, actual " + r.body().boxes.size(),
                r.body().boxes.size(), expected_num);
    }

    @Then("according to indexer, with {long} being the parameter that limits results, and {string} being the parameter that sets the next result, the current application should have the following boxes {string}.")
    public void indexerCheckAppBoxesWithParams(Long limit, String next, String encodedBoxesRaw) throws Exception {
        Response<BoxesResponse> r = clients.v2IndexerClient.searchForApplicationBoxes(this.appId).limit(limit).next(next).execute();
        final List<byte[]> expectedNames = Lists.newArrayList();
        if (!encodedBoxesRaw.isEmpty()) {
            for (String s : Strings.split(encodedBoxesRaw, ':')) {
                expectedNames.add(Encoder.decodeFromBase64(s));
            }
        }

        final List<byte[]> actualNames = Lists.newArrayList();
        for (BoxDescriptor b : r.body().boxes) {
            actualNames.add(b.name);
        }

        Assert.assertEquals("expected and actual box names length do not match", expectedNames.size(), actualNames.size());
        for (byte[] e : expectedNames) {
            if (!contains(e, actualNames))
                throw new RuntimeException("expected and actual box names do not match: " + expectedNames + " != " + actualNames);
        }
    }

    @Then("I sleep for {int} milliseconds for indexer to digest things down.")
    public void sleepForNSecondsForIndexer(int milliseconds) throws Exception {
        Thread.sleep(milliseconds);
    }
}
