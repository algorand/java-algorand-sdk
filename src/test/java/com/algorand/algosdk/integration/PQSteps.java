package com.algorand.algosdk.integration;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.PQSignature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.signer.Falcon1024AlgorandSigner;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.falcon.Falcon1024;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Cucumber steps for the post-quantum (Falcon-1024) scenarios from
 * algorand-sdk-testing.
 * <p>
 * The SDK deliberately bundles no Falcon code -- its signers take a signing
 * callback -- so real Falcon-1024 key generation and signing come from the
 * falcon-det1024 library, plugged into the callback-based
 * {@link Falcon1024AlgorandSigner} exactly as a production external signer
 * would be. This mirrors the Python SDK's steps, which back the same callback
 * with the algorand-falcon package.
 */
public class PQSteps {
    /**
     * The falcon signature is ~1.2KB and the public key ~1.8KB, so a
     * falcon-signed transaction needs a larger flat fee than an ed25519 one.
     */
    private static final BigInteger FALCON_FEE = BigInteger.valueOf(3000);

    public Stepdefs base;

    Falcon1024AlgorandSigner signer;

    public PQSteps(Stepdefs base) {
        this.base = base;
    }

    private void makeSigner(byte[] seed) throws Exception {
        Falcon1024.Signer falcon = Falcon1024.Signer.generate(seed);
        signer = new Falcon1024AlgorandSigner(falcon.getPublicKey(), falcon::sign);
    }

    @Given("I get the default falcon1024 account")
    public void getDefaultFalconAccount() throws Exception {
        // The fixed account shared by the cross-SDK golden fixtures.
        // Address: AZM6UV2ONIVHH7BK2CSBUPJCXNPZH5LFA2YFBCZPHSYXUFJ4LLLFJOUT5Y
        byte[] seed = new byte[32];
        for (int i = 0; i < seed.length; i++) {
            seed[i] = (byte) i;
        }
        makeSigner(seed);
    }

    @Given("I generate and fund a falcon1024 key")
    public void genAndFundFalconKey() throws Exception {
        byte[] seed = new byte[32];
        new SecureRandom().nextBytes(seed);
        makeSigner(seed);

        // Fund the new account from the richest wallet account; a fixed
        // wallet index can land on an unfunded key.
        Address sender = base.richestWalletAccount();
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .receiver(signer.getAddress())
                .amount(100_000_000)
                .lookupParams(base.aclv2)
                .build();
        SignedTransaction stx = base.signWithAddress(tx, sender);
        Response<PostTransactionsResponse> rPost =
                base.aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute();
        if (!rPost.isSuccessful()) {
            throw new IllegalStateException("funding the falcon account failed: " + rPost.message());
        }
        Utils.waitForConfirmation(base.aclv2, rPost.body().txId, 1);
    }

    @Given("mnemonic for falcon1024 private key {string}")
    public void mnemonicForFalconKey(String mn) throws Exception {
        makeSigner(Mnemonic.toPQSeed(mn, PQSignature.falcon1024Scheme()));
    }

    @When("I create the falcon1024 payment transaction")
    public void createFalconTxn() {
        base.txn = Transaction.PaymentTransactionBuilder()
                .sender(signer.getAddress())
                .flatFee(base.fee)
                .firstValid(base.fv)
                .lastValid(base.lv)
                .note(base.note)
                .genesisID(base.gen)
                .genesisHash(base.gh)
                .amount(base.amt)
                .receiver(base.to)
                .closeRemainderTo(base.close)
                .build();
        base.pk = signer.getAddress();
    }

    @Given("I create the default falcon1024 transaction with parameters {int} {string}")
    public void defaultFalconTxn(int amt, String note) {
        base.getParams();
        base.note = note.equals("none") ? null : Encoder.decodeFromBase64(note);
        base.txnBuilder = Transaction.PaymentTransactionBuilder()
                .sender(signer.getAddress())
                .suggestedParams(base.params)
                .note(base.note)
                .amount(amt)
                .receiver(base.getAddress(1));
        base.txn = base.txnBuilder.build();
        base.txn.fee = FALCON_FEE;
        base.pk = signer.getAddress();
    }

    @Given("I add a fee to cover falcon1024 signatures")
    public void addFalconFee() {
        base.txn.fee = FALCON_FEE;
    }

    @When("I sign the falcon1024 transaction with the private key")
    public void signFalconTxn() throws Exception {
        base.stx = signer.signTransaction(base.txn);
    }
}
