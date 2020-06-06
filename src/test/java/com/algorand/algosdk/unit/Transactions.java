package com.algorand.algosdk.unit;

import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.bytebuddy.pool.TypePool;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.bouncycastle.util.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class Transactions {
    private final Base base;
    private Transaction applicationTransaction = null;
    private SignedTransaction signedTransaction = null;

    public Transactions(Base b) {
        this.base = b;
    }

    private TEALProgram loadTEALProgramFromFile(String file) {
        InputStream fis = getClass().getClassLoader().getResourceAsStream("com/algorand/algosdk/unit/" + file);
        try {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return new TEALProgram(data);
        } catch (Exception e) {
            Assertions.fail("Unable to read file ('"+file+"') required by test: " + e.getMessage(), e);
        }

        throw new RuntimeException("Unknown error.");
    }

    /**
     * Convert an array of arguments like "str:arg1,str:arg2" into a properly converted byte array.
     */
    private List<byte[]> convertArgs(String args) {
        // Hell yeah, I missed this unreadable Java streams functional magic!
        return Arrays.stream(Strings.split(args, ','))
                .map(s -> {
                    String[] parts = Strings.split(s, ':');
                    byte[] converted = null;
                    switch(parts[0]) {
                        case "str":
                            converted = parts[1].getBytes();
                            break;
                        default:
                            Assertions.fail("Doesn't currently support '" + parts[0] + "' convertion.");
                    }
                    return converted;
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert list of numbers to list of longs.
     */
    private List<Long> convertForeignApps(String foreignApps) {
        return Arrays.stream(Strings.split(foreignApps, ','))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private Address convertOrFailTest(String addrString) {
        try {
            return new Address(addrString);
        } catch (NoSuchAlgorithmException e) {
            Assertions.fail("Failed to parse address: " + e.getMessage(), e);
        }
        return null;
    }

    private List<Address> convertAccounts(String accounts) {
        return Arrays.stream(Strings.split(accounts, ','))
                .map(this::convertOrFailTest)
                .collect(Collectors.toList());
    }

    @When("I build an application transaction with operation {string}, application-id {long}, sender {string}, approval-program {string}, clear-program {string}, global-bytes {long}, global-ints {long}, local-bytes {long}, local-ints {long}, app-args {string}, foreign-apps {string}, app-accounts {string}, fee {long}, first-valid {long}, last-valid {long}, genesis-hash {string}")
    public void buildApplicationTransactions(String operation, Long applicationId, String sender, String approvalProgramFile, String clearProgramFile, Long globalBytes, Long globalInts, Long localBytes, Long localInts, String appArgs, String foreignApps, String appAccounts, Long fee, Long firstValid, Long lastValid, String genesisHash) {
        ApplicationBaseTransactionBuilder builder = null;

        // Create builder and apply builder-specific parameters
        switch (operation) {
            case "create":
                builder = Transaction.ApplicationCreateTransactionBuilder()
                        .approvalProgram(loadTEALProgramFromFile(approvalProgramFile))
                        .clearStateProgram(loadTEALProgramFromFile(clearProgramFile))
                        .globalStateSchema(new StateSchema(globalInts, globalBytes))
                        .localStateSchema(new StateSchema(localInts, localBytes));
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

        builder.sender(sender);

        // Shared base fields
        if (applicationId != 0) {
            builder.applicationId(applicationId);
        }
        if (StringUtils.isNotEmpty(appArgs)) {
            builder.args(convertArgs(appArgs));
        }
        if (StringUtils.isNotEmpty(foreignApps)) {
            builder.foreignApps(convertForeignApps(foreignApps));
        }
        if (StringUtils.isNotEmpty(appAccounts)) {
            builder.accounts(convertAccounts(appAccounts));
        }
        if (fee != 0) {
            builder.flatFee(fee);
        }
        if (firstValid != 0) {
            builder.firstValid(firstValid);
        }
        if (lastValid != 0) {
            builder.lastValid(lastValid);
        }
        if (StringUtils.isNotEmpty(genesisHash)) {
            builder.genesisHashB64(genesisHash);
        }

        applicationTransaction = builder.build();
    }
    @When("sign the transaction")
    public void sign_the_transaction() throws NoSuchAlgorithmException {
        signedTransaction = base.signTransaction(applicationTransaction);
    }

    @Then("the base64 encoded signed transaction should equal {string}")
    public void the_base64_encoded_signed_transaction_should_equal(String golden) throws JsonProcessingException {
        String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(signedTransaction));
        assertThat(encoded).isEqualTo(golden);
    }
}
