package com.algorand.algosdk.templates;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.BytesParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.transaction.Lease;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PeriodicPayment {
    private static String referenceProgram = "ASAHAQoLAAwNDiYCAQYg/ryguxRKWk6ntDikaBrIDmyhBby2B/xWUyXJVpX2ohMxECISMQEjDhAxAiQYJRIQMQQhBDECCBIQMQYoEhAxCTIDEjEHKRIQMQghBRIQMQkpEjEHMgMSEDECIQYNEDEIJRIQERA=";

    /**
     * PeriodicPayment contract enables creating an account which allows the
     * withdrawal of a fixed amount of assets every fixed number of rounds to a
     * specific Algrorand Address. In addition, the contract allows to add
     * timeout, after which the address can withdraw the rest of the assets.
     *
     * @param receiver address to receive the assets.
     * @param amount amount of assets to transfer at every cycle.
     * @param withdrawingWindow the number of blocks in which the user can withdraw the asset once the period start (must be < 1000).
     * @param period how often the address can withdraw assets (in rounds).
     * @param fee maximum fee per transaction.
     * @param timeout a round in which the receiver can withdraw the rest of the funds after.
     *
     * @return PeriodicPayment contract.
     */
    public static ContractTemplate MakePeriodicPayment(final String receiver, final int amount, final int withdrawingWindow, final int period, final int fee, final int timeout) throws NoSuchAlgorithmException {
        return MakePeriodicPayment(receiver, amount, withdrawingWindow, period, fee, timeout, null);
    }

    /**
     * Need a way to specify the lease for testing.
     */
    protected static ContractTemplate MakePeriodicPayment(final String receiver, final int amount, final int withdrawingWindow, final int period, final int fee, final int timeout, final Lease lease) throws NoSuchAlgorithmException {
        if (withdrawingWindow < 0 || withdrawingWindow > 1000) {
            throw new IllegalArgumentException("The withdrawingWindow must be a positive number less than 1000");
        }

        List<ParameterValue> values = ImmutableList.of(
                new IntParameterValue(4, fee),
                new IntParameterValue(5, period),
                new IntParameterValue(7, withdrawingWindow),
                new IntParameterValue(8, amount),
                new IntParameterValue(9, timeout),
                new BytesParameterValue(12, lease == null ? new Lease() : lease),
                new AddressParameterValue(15, receiver)
        );

        return ContractTemplate.inject(Encoder.decodeFromBase64(referenceProgram), values);
    }

    /**
     * Return the withdrawal transaction to be sent to the network.
     *
     * @param contract contract containing information, this should be provided by the payer.
     * @param firstValid first round the transaction should be valid.
     * @param genesisHash genesis hash in base64.
     * @return Signed withdrawal transaction.
     */
    public static SignedTransaction MakeWithdrawalTransaction(final ContractTemplate contract, final int firstValid, final Digest genesisHash) throws IOException, NoSuchAlgorithmException {
        Logic.ProgramData data = Logic.readProgram(contract.program, null);
        if (data.intBlock.size() != 7 || data.byteBlock.size() != 2) {
            throw new IllegalArgumentException("Unexpected contract data. Expected 7 int constants and 2 byte constants, found " + data.intBlock.size() + " and " + data.byteBlock.size());
        }

        int fee = data.intBlock.get(1);
        int period = data.intBlock.get(2);
        int withdrawingWindow = data.intBlock.get(4);
        int amount = data.intBlock.get(5);
        Lease lease = new Lease(data.byteBlock.get(0));
        Address receiver = new Address(data.byteBlock.get(1));

        if (firstValid % period != 0) {
            throw new IllegalArgumentException("invalid contract: firstValid must be divisible by the period");
        }

        LogicsigSignature lsig = new LogicsigSignature(contract.program);
        Address address = lsig.toAddress();
        Transaction tx = new Transaction(
                address,
                BigInteger.valueOf(fee),
                BigInteger.valueOf(firstValid),
                BigInteger.valueOf(firstValid + withdrawingWindow),
                null,
                BigInteger.valueOf(amount),
                receiver,
                "",
                genesisHash);
        tx.setLease(lease);

        Account.setFeeByFeePerByte(tx, BigInteger.valueOf(fee));

        if (!lsig.verify(tx.sender)) {
            throw new IllegalArgumentException("Failed to verify transaction.");
        }

        return Account.signLogicsigTransaction(lsig, tx);
    }
}
