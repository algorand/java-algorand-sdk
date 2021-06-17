package com.algorand.algosdk.unit;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.model.AssetParams;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.auction.Bid;
import com.algorand.algosdk.auction.SignedBid;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.CompileResponse;
import com.algorand.algosdk.v2.client.model.DryrunResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FeeTest {
    public static String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    Transaction txn;
    BigInteger fv;
    BigInteger lv;
    Digest gh;
    Address to;
    Address close;
    BigInteger amt;
    String gen;
    byte[] note;
    MultisigAddress msig;


    @When("I create the multisig payment transaction with zero fee")
    public void createMsigTxnZeroFee() throws NoSuchAlgorithmException {
        txn = Transaction.PaymentTransactionBuilder()
                .sender(msig.toString())
                .fee(0)
                .firstValid(fv)
                .lastValid(lv)
                .note(note)
                .genesisID(gen)
                .genesisHash(gh)
                .amount(amt)
                .receiver(to)
                .closeRemainderTo(close)
                .build();
    }

    @Given("multisig addresses {string}")
    public void multisig_addresses(String string) {
    }

    @When("I create the multisig payment transaction")
    public void i_create_the_multisig_payment_transaction() {
    }

    @When("I sign the multisig transaction with the private key")
    public void i_sign_the_multisig_transaction_with_the_private_key() {
    }

}
