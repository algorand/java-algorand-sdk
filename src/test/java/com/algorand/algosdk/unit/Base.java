package com.algorand.algosdk.unit;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import io.cucumber.java.en.Given;
import org.assertj.core.api.Assertions;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Base {
    private Map<String, Account> signingAccounts = new HashMap<>();

    /**
     * Helper to sign a transaction with the correct account, or fail the test if the correct account isn't available.
     */
    public SignedTransaction signTransaction(Transaction tx) throws NoSuchAlgorithmException {
        String signingAddress = tx.sender.toString();
        if (tx.rekeyTo != null && !tx.rekeyTo.equals(new Address())) {
            signingAddress = tx.rekeyTo.toString();
        }

        if (!signingAccounts.containsKey(signingAddress)) {
            Assertions.fail("We don't have a signing account for '" + signingAddress + "'");
        }

        return signingAccounts.get(signingAddress).signTransaction(tx);
    }

    @Given("a signing account with address {string} and mnemonic {string}")
    public void a_signing_account_with_address_and_mnemonic(String address, String mnemonic) throws GeneralSecurityException {
        signingAccounts.put(address, new Account(mnemonic));
    }
}
