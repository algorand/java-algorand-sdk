package com.algorand.algosdk.templates;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.Base64ParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.Lease;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DynamicFee {
    private static String referenceProgram = "ASAFAgEFBgcmAyD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiEyDmmpYeby1feshmB5JlUr6YI17TM2PKiJGLuck4qRW2+QEGMgQiEjMAECMSEDMABzEAEhAzAAgxARIQMRYjEhAxECMSEDEHKBIQMQkpEhAxCCQSEDECJRIQMQQhBBIQMQYqEhA=";
    private static int [] referenceOffsets = {5, 6, 7, 11, 44, 76};

    /**
     * DynamicFee contract allows you to create a transaction without
     * specifying the fee. The fee will be determined at the moment of
     * transfer.
     *
     * @param receiver (str): address to receive the assets
     * @param amount (int): amount of assets to transfer
     * @param firstValid (int): first valid round for the transaction
     * @param lastValid (int, optional): last valid round for the transaction (default to first_valid + 1000)
     * @param closeRemainderAddress (str, optional): if you would like to close the account after the transfer, specify the address that would recieve the remainder
     */
    public static ContractTemplate MakeDynamicFee(final String receiver, final Integer amount, final Integer firstValid, final Integer lastValid, final String closeRemainderAddress) throws NoSuchAlgorithmException {
        byte[] program = Encoder.decodeFromBase64(referenceProgram);
        byte[] lease = Lease.makeRandomLease();

        ParameterValue[] values = {
                new IntParameterValue(amount),
                new IntParameterValue(firstValid),
                new IntParameterValue(lastValid),
                new AddressParameterValue(receiver),
                new AddressParameterValue(closeRemainderAddress),
                new Base64ParameterValue(lease)
        };

        return ContractTemplate.inject(program, referenceOffsets, values);
    }

    /**
     * Create and sign the secondary dynamic fee transaction, update
     * transaction fields, and sign as the fee payer; return both
     * transactions.
     *
     * @param txn main transaction from payer
     * @param contract signed logic received from payer
     * @param privateKey the secret key of the account that pays the fee in base64
     * @param feePerByte fee per byte, for both transactions
     * @param firstValid first valid round for the transaction
     * @param lastValid last valid round for the transaction
     */
    public static ArrayList<Transaction> MakeTransactions(final Transaction txn, final byte[] contract, final String privateKey, final int feePerByte, final int firstValid, final int lastValid) throws NoSuchAlgorithmException {
        txn.firstValid = BigInteger.valueOf(firstValid);
        txn.lastValid = BigInteger.valueOf(lastValid);
        Account.setFeeByFeePerByte(txn, BigInteger.valueOf(feePerByte));

        return null;
    }
}
