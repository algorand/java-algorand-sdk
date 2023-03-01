package com.algorand.examples;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class AccountExamples {

        public static void main(String[] args) throws Exception {
                AlgodClient algodClient = ExampleUtils.getAlgodClient();
                List<Account> accts = ExampleUtils.getSandboxAccounts();

                // Grab some accounts from the sandbox kmd
                Account acct1 = accts.get(0);
                Account acct2 = accts.get(1);
                Account acct3 = accts.get(2);

                MultisigAddress msig = createMsig(acct1, acct2, acct3);

                // Pay the multisig address so it can issue transactions for the demo
                Response<TransactionParametersResponse> spResponse = algodClient.TransactionParams().execute();
                TransactionParametersResponse sp = spResponse.body();
                Transaction ptxn = Transaction.PaymentTransactionBuilder()
                                .sender(acct1.getAddress()).amount(1000000).receiver(msig.toAddress())
                                .suggestedParams(sp).build();
                SignedTransaction stxn = acct1.signTransaction(ptxn);
                ExampleUtils.sendPrint(algodClient, stxn, "seed msig");

                // example: MULTISIG_SIGN
                // Construct transaction with sender as address of msig
                Transaction msigPayTxn = Transaction.PaymentTransactionBuilder()
                                .sender(msig.toAddress())
                                .amount(1000)
                                .receiver(acct1.getAddress())
                                .suggestedParams(sp)
                                .build();

                // For each subsig, sign or append to the existing partially signed transaction
                SignedTransaction signedMsigPayTxn = acct1.signMultisigTransaction(msig, msigPayTxn);
                signedMsigPayTxn = acct2.appendMultisigTransaction(msig, signedMsigPayTxn);
                Response<PostTransactionsResponse> msigSubResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedMsigPayTxn)).execute();
                // example: MULTISIG_SIGN
                ExampleUtils.printTxnResults(algodClient, msigSubResponse.body(), "msig pay");

                rekeyAcct(algodClient, acct1, acct2);

        }

        public static MultisigAddress createMsig(Account addr1, Account addr2, Account addr3)
                        throws NoSuchAlgorithmException {
                // example: MULTISIG_CREATE
                int version = 1; // no other versions at the time of writing
                int threshold = 2; // we're making a 2/3 msig

                // Populate a list of Ed25519 pubkeys
                List<Ed25519PublicKey> accts = new ArrayList<>();
                accts.add(addr1.getEd25519PublicKey());
                accts.add(addr2.getEd25519PublicKey());
                accts.add(addr3.getEd25519PublicKey());
                // create the MultisigAddress object
                MultisigAddress msig = new MultisigAddress(version, threshold, accts);
                System.out.printf("msig address: %s\n", msig.toAddress().toString());
                // example: MULTISIG_CREATE
                return msig;
        }

        public static void rekeyAcct(AlgodClient algodClient, Account acct1, Account acct2) throws Exception {
                TransactionParametersResponse sp = algodClient.TransactionParams().execute().body();

                // example: ACCOUNT_REKEY

                // Any kind of transaction can contain a rekey, here we use a Payment
                // transaction
                Transaction rekeyTxn = Transaction.PaymentTransactionBuilder().sender(acct1.getAddress())
                                .receiver(acct1.getAddress()).suggestedParams(sp).rekey(acct2.getAddress()).build();
                SignedTransaction signedRekeyTxn = acct1.signTransaction(rekeyTxn);
                Response<PostTransactionsResponse> resp = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedRekeyTxn)).execute();
                ExampleUtils.printTxnResults(algodClient, resp.body(), "rekey");

                // Create a transaction to rekey it back
                Transaction rekeyBack = Transaction.PaymentTransactionBuilder().sender(acct1.getAddress())
                                .receiver(acct1.getAddress()).suggestedParams(sp).rekey(acct1.getAddress()).build();

                // note we sign with acct2's key
                SignedTransaction signedRekeyBack = acct2.signTransaction(rekeyBack);
                Response<PostTransactionsResponse> rekeyBackResponse = algodClient.RawTransaction()
                                .rawtxn(Encoder.encodeToMsgPack(signedRekeyBack)).execute();
                ExampleUtils.printTxnResults(algodClient, rekeyBackResponse.body(), "rekey back");
                // example: ACCOUNT_REKEY
        }

}