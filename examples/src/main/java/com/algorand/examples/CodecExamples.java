package com.algorand.examples;

import java.math.BigInteger;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class CodecExamples {

    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        Account acct = accts.get(0);

        // example: CODEC_ADDRESS
        String addrAsStr = "4H5UNRBJ2Q6JENAXQ6HNTGKLKINP4J4VTQBEPK5F3I6RDICMZBPGNH6KD4";
        // Instantiate a new Address object with string
        Address addr = new Address(addrAsStr);
        // Or with the bytes
        Address addrAgain = new Address(addr.getBytes());
        assert addrAgain.equals(addr);
        // example: CODEC_ADDRESS

        // example: CODEC_BASE64
        String encodedStr = "SGksIEknbSBkZWNvZGVkIGZyb20gYmFzZTY0";
        byte[] decodedBytes = Encoder.decodeFromBase64(encodedStr);
        String reEncodedStr = Encoder.encodeToBase64(decodedBytes);
        assert encodedStr.equals(reEncodedStr);
        // example: CODEC_BASE64

        // example: CODEC_UINT64
        BigInteger val = BigInteger.valueOf(1337);
        byte[] encodedVal = Encoder.encodeUint64(val);
        BigInteger decodedVal = Encoder.decodeUint64(encodedVal);
        assert val.equals(decodedVal);
        // example: CODEC_UINT64

        // example: CODEC_TRANSACTION_UNSIGNED
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // Wipe the `reserve` address through an AssetConfigTransaction
        Transaction ptxn = Transaction.PaymentTransactionBuilder().suggestedParams(sp)
                .sender(acct.getAddress()).receiver(acct.getAddress()).amount(100).build();

        byte[] encodedTxn = Encoder.encodeToMsgPack(ptxn);

        Transaction decodedTxn = Encoder.decodeFromMsgPack(encodedTxn, Transaction.class);
        assert decodedTxn.equals(ptxn);
        // example: CODEC_TRANSACTION_UNSIGNED

        // example: CODEC_TRANSACTION_SIGNED
        SignedTransaction signedTxn = acct.signTransaction(ptxn);
        byte[] encodedSignedTxn = Encoder.encodeToMsgPack(signedTxn);

        SignedTransaction decodedSignedTransaction = Encoder.decodeFromMsgPack(encodedSignedTxn, SignedTransaction.class);
        assert decodedSignedTransaction.equals(signedTxn);
        // example: CODEC_TRANSACTION_SIGNED
    }
}
