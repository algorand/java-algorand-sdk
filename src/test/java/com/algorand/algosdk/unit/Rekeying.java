package com.algorand.algosdk.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Rekeying {

    @SuppressWarnings("rawtypes")
    private TransactionBuilder transactionBuilder;
    private SignedTransaction signedTransaction;
    public Account account;
    SignedTransaction stx;
    Address pk;
    Transaction txn;
    MultisigAddress msig;

    @Given("payment transaction parameters {int} {int} {int} {string} {string} {string} {int} {string} {string}")	
    public void payment_transaction_parameters(
            Integer fee, Integer fv, Integer lv, String gh,
            String to, String close, Integer amt, String gen, String note) {


        transactionBuilder = PaymentTransactionBuilder.Builder()
                .flatFee(fee)
                .firstValid(fv)
                .lastValid(lv)
                .genesisHashB64(gh)
                .receiver(to)
                .closeRemainderTo(close)
                .amount(amt)
                .genesisID(gen)
                .noteB64(note);
    }

    @Given("mnemonic for private key {string}")
    public void mnemonic_for_private_key(String mn) throws GeneralSecurityException {
        account = new Account(mn);
    }

    @When("I create the flat fee payment transaction")
    public void i_create_the_flat_fee_payment_transaction() {
        transactionBuilder.sender(account.getAddress());
    }

    @When("I add a rekeyTo field with address {string}")
    public void i_add_a_rekeyTo_field_with_address(String rekeyTo) {
        transactionBuilder.rekey(rekeyTo);
    }

    @When("I sign the transaction with the private key")
    public void i_sign_the_transaction_with_the_private_key() throws NoSuchAlgorithmException {
        signedTransaction = account.signTransaction(transactionBuilder.build());
    }

    @Then("the signed transaction should equal the golden {string}")
    public void the_signed_transaction_should_equal_the_golden(String golden) throws IOException {        
        byte[] signedTxBytes = Encoder.encodeToMsgPack(signedTransaction);
        assertThat(Encoder.encodeToBase64(signedTxBytes)).isEqualTo(golden);
    }

    @When("I set the from address to {string}")
    public void i_set_the_from_address_to(String fromAddress) {
        transactionBuilder.sender(fromAddress);
    }

    @Given("multisig addresses {string}")
    public void multisig_addresses(String addresses) throws NoSuchAlgorithmException{

        String[] addrs = addresses.split(" ");
        Ed25519PublicKey[] addrlist = new Ed25519PublicKey[addrs.length];
        for(int x = 0; x < addrs.length; x++){
            addrlist[x] = new Ed25519PublicKey((new Address(addrs[x])).getBytes());
        }
        msig = new MultisigAddress(1, 2, Arrays.asList(addrlist));
        pk = new Address(msig.toString());
    }

    @When("I create the multisig payment transaction")
    public void i_create_the_multisig_payment_transaction() {
        txn = transactionBuilder.sender(msig.toString()).build();
    }

    @When("I create the multisig payment transaction with zero fee")
    public void i_create_the_multisig_payment_transaction_with_zero_fee() {
        txn = transactionBuilder.sender(msig.toString()).build();
    }

    @When("I sign the multisig transaction with the private key")
    public void i_sign_the_multisig_transaction_with_the_private_key() throws NoSuchAlgorithmException {
        stx = account.signMultisigTransaction(msig, txn);
    }
}
