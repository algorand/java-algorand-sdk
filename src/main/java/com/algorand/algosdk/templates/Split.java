package com.algorand.algosdk.templates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;
import com.google.common.collect.ImmutableList;

import static com.algorand.algosdk.templates.ContractTemplate.readAndVerifyContract;

public class Split {
    private static String referenceProgram = "ASAIAQUCAAYHCAkmAyDYHIR7TIW5eM/WAZcXdEDqv7BD+baMN6i2/A5JatGbNCDKsaoZHPQ3Zg8zZB/BZ1oDgt77LGo5np3rbto3/gloTyB40AS2H3I72YCbDk4hKpm7J7NnFy2Xrt39TJG0ORFg+zEQIhIxASMMEDIEJBJAABkxCSgSMQcyAxIQMQglEhAxAiEEDRAiQAAuMwAAMwEAEjEJMgMSEDMABykSEDMBByoSEDMACCEFCzMBCCEGCxIQMwAIIQcPEBA=";

    /**
     * Split allows locking algos in an account which allows transfering to two
     * predefined addresses in a specified ratio such that for the given ratn and
     * ratd parameters we have:
     *
     *     first_recipient_amount * rat_2 == second_recipient_amount * rat_1
     *
     * Split also has an expiry round, after which the owner can transfer back
     * the funds.
     *
     * @param owner the address to refund funds to on timeout
     * @param receiver1 the first recipient in the split account
     * @param receiver2 the second recipient in the split account
     * @param rat1 how much receiver1 receives (proportionally)
     * @param rat2 how much receiver2 receives (proportionally)
     * @param expiryRound the round at which the account expires
     * @param minPay minimum amount to be paid out of the account to receiver1
     * @param maxFee half of the maximum fee used by each split forwarding group transaction
     * @return ContractTemplate with the address and the program with the given parameter values
     * @throws NoSuchAlgorithmException
     */
    public static ContractTemplate MakeSplit(
            Address owner,
            Address receiver1,
            Address receiver2,
            int rat1,
            int rat2,
            int expiryRound,
            int minPay,
            int maxFee) throws NoSuchAlgorithmException {

        List<ParameterValue> values = ImmutableList.of(
                new IntParameterValue(4, maxFee),
                new IntParameterValue(7, expiryRound),
                new IntParameterValue(8, rat2),
                new IntParameterValue(9, rat1),
                new IntParameterValue(10, minPay),
                new AddressParameterValue(14, owner),
                new AddressParameterValue(47, receiver1),
                new AddressParameterValue(80, receiver2)
        );

        return ContractTemplate.inject(Encoder.decodeFromBase64(referenceProgram), values);
    }

    /**
     * Generate group transactions to transfer funds according to the contract's ratio.
     *
     * @param contract the contract created with Split.MakeSplit
     * @param amount amount to be transferred from the contract to the receivers according to the contract ratio.
     * @param firstValid first round where the transactions are valid
     * @param genesisHash genesis hash
     * @param feePerByte fee per byte multiplier
     * @return
     */
    public static byte[] GetSplitTransactions(
            ContractTemplate contract,
            int amount,
            int firstValid,
            int lastValid,
            int feePerByte,
            Digest genesisHash) throws NoSuchAlgorithmException, IOException {
        Logic.ProgramData data = readAndVerifyContract(contract.program, 8, 3);

        int maxFee = data.intBlock.get(1);
        int rat1 = data.intBlock.get(6);
        int rat2 = data.intBlock.get(5);
        int minTrade = data.intBlock.get(7);

        Double fraction = Double.valueOf(rat1) / Double.valueOf(rat1+rat2);
        int receiverOneAmount = Long.valueOf(Math.round(fraction * amount)).intValue();
        int receiverTwoAmount = Long.valueOf(Math.round((1.0 - fraction) * amount)).intValue();

        // With proper rounding, this should hopefully never happen.
        if (amount - receiverOneAmount - receiverTwoAmount != 0) {
            throw new RuntimeException("Unable to exactly split " + amount + " using the contract ratio of " + rat1 + " / " + rat2);
        }

        if (receiverOneAmount < minTrade) {
            throw new RuntimeException("Receiver one must receive at least " + minTrade);
        }

        BigInteger rcv1 = BigInteger.valueOf(receiverOneAmount).multiply(BigInteger.valueOf(rat2));
        BigInteger rcv2 = BigInteger.valueOf(receiverTwoAmount).multiply(BigInteger.valueOf(rat1));
        if (rcv1.equals(rcv2) == false) {
            throw new RuntimeException("The token split must be exactly " + rat1 + " / " + rat2 + ", received " + receiverOneAmount + " / " + receiverTwoAmount);
        }

        Address receiver1 = new Address(data.byteBlock.get(1));
        Address receiver2 = new Address(data.byteBlock.get(2));

        Transaction tx1 = new Transaction(
                contract.address,
                receiver1,
                null,
                BigInteger.valueOf(receiverOneAmount),
                BigInteger.valueOf(firstValid),
                BigInteger.valueOf(lastValid),
                null,
                genesisHash);
        Account.setFeeByFeePerByte(tx1, BigInteger.valueOf(feePerByte));

        Transaction tx2 = new Transaction(
                contract.address,
                receiver2,
                null,
                BigInteger.valueOf(receiverTwoAmount),
                BigInteger.valueOf(firstValid),
                BigInteger.valueOf(lastValid),
                null,
                genesisHash);
        Account.setFeeByFeePerByte(tx2, BigInteger.valueOf(feePerByte));

        if (tx1.fee.longValue() > maxFee || tx2.fee.longValue() > maxFee) {
            long fee = Math.max(tx1.fee.longValue(), tx2.fee.longValue());
            throw new RuntimeException("Transaction fee is greater than maxFee: " + fee + " > " + maxFee);
        }

        LogicsigSignature lsig = new LogicsigSignature(contract.program);
        TxGroup.assignGroupID(tx1, tx2);
        SignedTransaction stx1 = new SignedTransaction(tx1, lsig, tx1.txID());
        SignedTransaction stx2 = new SignedTransaction(tx2, lsig, tx2.txID());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Encoder.encodeToMsgPack(stx1));
        baos.write(Encoder.encodeToMsgPack(stx2));
        return baos.toByteArray();
    }
}
