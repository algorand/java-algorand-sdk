package com.algorand.examples;

import java.security.PublicKey;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class VerifySignature {

    public static void main(String[] args) throws Exception {
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        Account acct = accts.get(0);

        TransactionParametersResponse sp = new TransactionParametersResponse();
        sp.minFee = 1000l;
        sp.fee = 0l;
        sp.lastRound = 1000l;
        sp.genesisId = "random-v1.0";

        Transaction ptxn = Transaction.PaymentTransactionBuilder().suggestedParams(sp)
                .sender(acct.getAddress()).receiver(acct.getAddress()).amount(100).build();

        SignedTransaction signedTxn = acct.signTransaction(ptxn);
        byte[] encodedSignedTxn = Encoder.encodeToMsgPack(signedTxn);
        verifySignature(encodedSignedTxn);
    }

    public static boolean verifySignature(byte[] rawSignedTxn) throws Exception {
        // example: OFFLINE_VERIFY_SIG
        // decode the signature
        SignedTransaction decodedSignedTransaction = Encoder.decodeFromMsgPack(rawSignedTxn,
                SignedTransaction.class);
        Transaction txn = decodedSignedTransaction.tx;

        // get the bytes that were signed
        byte[] signedBytes = txn.bytesToSign();
        // get the pubkey that signed them
        PublicKey pk = txn.sender.toVerifyKey();

        // set up the sig checker
        java.security.Signature sigChecker = java.security.Signature.getInstance("Ed25519");
        sigChecker.initVerify(pk);
        sigChecker.update(signedBytes);
        // verify the signature 
        boolean valid = sigChecker.verify(decodedSignedTransaction.sig.getBytes());
        System.out.printf("Valid? %b\n", valid);
        // example: OFFLINE_VERIFY_SIG
        return valid;
    }
}
