package com.algorand.algosdk.integration;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import io.cucumber.java.en.Given;

public class TransientAccount {
    public Stepdefs base;

    public Account transientAccount = null;

    public TransientAccount(Stepdefs base) {
        this.base = base;
    }

    @Given("I create a new transient account and fund it with {long} microalgos.")
    public void createAndFundTransientAccount(Long amount) throws Exception {
        // Create a new account.
        this.transientAccount = new Account();

        // Fund it with one of the base wallets.
        Address sender = base.getAddress(1);

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .receiver(this.transientAccount.getAddress())
                .amount(amount)
                .lookupParams(base.aclv2)
                .build();
        SignedTransaction stx = base.signWithAddress(tx, sender);

        Response<PostTransactionsResponse> rPost = base.aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();
        Utils.waitForConfirmation(base.aclv2, rPost.body().txId, 1);
    }

}
