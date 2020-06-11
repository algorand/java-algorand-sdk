package com.algorand.algosdk.integration;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationCreateTransactionBuilder;
import com.algorand.algosdk.builder.transaction.ApplicationUpdateTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.ConversionUtils;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.ResourceUtils;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Applications {
    private final Stepdefs base;

    private Applications() {
        this.base = new Stepdefs();
    }

    /*
    private Applications(Stepdefs base) {
        this.base = base;
    }
     */

    public void waitForConfirmation(AlgodClient client, String txid, Duration timeout) throws Exception {
        Long start = System.currentTimeMillis();

        Response<PendingTransactionResponse> r;
        // Keep checking until the timeout.
        do {
            r = client.PendingTransactionInformation(txid).execute();
            // If the transaction has been confirmed, exit.
            if (r.body().confirmedRound != null) {
                return;
            }
            Thread.sleep(250);
        } while ( (System.currentTimeMillis() - start) < timeout.toMillis());
    }

    public Account createAndFundAccount(AlgodClient client, Long amount) throws Exception {
        Account acct = new Account();
        Address sender = base.getAddress(1);

        Transaction fundingTransaction = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .receiver(acct.getAddress())
                .amount(amount)
                .suggestedParams(client.TransactionParams().execute().body())
                .build();
        SignedTransaction stx = base.signWithAddress(fundingTransaction, sender);
        Response<PostTransactionsResponse> r = client.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();

        waitForConfirmation(client, r.body().txId, Duration.ofSeconds(10));

        //Response<com.algorand.algosdk.v2.client.model.Account> acctResponse = client.AccountInformation(acct.getAddress()).execute();
        //System.out.println(acctResponse.body());

        return acct;
    }

    /**
     *
     Given I create and fund a new account with 100000 microalgos.
     I build an application transaction with operation "create", application-id 0, sender "", approval-program "programs/one.teal.tok", clear-program "programs/one.teal.tok", global-bytes 0, global-ints 0, local-bytes 0, local-ints 0, app-args "", foreign-apps "", app-accounts "", fee 0, first-valid 0, last-valid 0, genesis-hash ""
     I update the transaction with my account.
     I sign and submit the transaction.
     I wait for the transaction to be confirmed.
     I remember the new application ID.
     I build an application transaction with operation "update", application-id 0, sender "", approval-program "programs/loccheck.teal.tok", clear-program "programs/one.teal.tok", global-bytes 0, global-ints 0, local-bytes 0, local-ints 0, app-args "", foreign-apps "", app-accounts "", fee 0, first-valid 0, last-valid 0, genesis-hash ""
     I update the transaction with my account.
     I update the transaction with the application ID.
     */
    @Test
    public void Stub() throws Exception {
        base.aClient();
        base.kClient();
        base.walletInfo();

        AlgodClient v2Client = new AlgodClient("localhost", Stepdefs.algodPort, Stepdefs.token);

        TransactionParametersResponse params = v2Client.TransactionParams().execute().body();

        Account creator = createAndFundAccount(v2Client, 100000L);

        TEALProgram one = new TEALProgram(ResourceUtils.readResource("programs/one.teal.tok"));
        TEALProgram loccheck = new TEALProgram(ResourceUtils.readResource("programs/loccheck.teal.tok"));

        // CREATE
        Transaction tx = ApplicationCreateTransactionBuilder.Builder()
                .sender(creator.getAddress())
                .suggestedParams(params)
                .approvalProgram(one)
                .clearStateProgram(one)
                .globalStateSchema(new StateSchema(0, 0))
                .localStateSchema(new StateSchema(0, 0))
                .args(ConversionUtils.convertArgs("str:hello"))
                .build();

        SignedTransaction stx = creator.signTransaction(tx);
        Response<PostTransactionsResponse> r = v2Client.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(stx))
                .execute();

        waitForConfirmation(v2Client, r.body().txId, Duration.ofSeconds(10));

        // UPDATE
        tx = ApplicationUpdateTransactionBuilder.Builder()
                .sender(creator.getAddress())
                .suggestedParams(params)
                .approvalProgram(loccheck)
                .clearStateProgram(one)
                .args(ConversionUtils.convertArgs("str:hello"))
                .build();

        stx = creator.signTransaction(tx);
        r = v2Client.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(stx))
                .execute();

        waitForConfirmation(v2Client, r.body().txId, Duration.ofSeconds(10));
    }
}
