package com.algorand.algosdk.templates;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.BytesParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.transaction.Lease;
import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DynamicFee {
    protected static String referenceProgram = "ASAFAgEFBgcmAyD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiEyDmmpYeby1feshmB5JlUr6YI17TM2PKiJGLuck4qRW2+QEGMgQiEjMAECMSEDMABzEAEhAzAAgxARIQMRYjEhAxECMSEDEHKBIQMQkpEhAxCCQSEDECJRIQMQQhBBIQMQYqEhA=";
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
    public static ContractTemplate MakeDynamicFee(final Address receiver, final Integer amount, final Integer firstValid, final Integer lastValid, final Address closeRemainderAddress) throws NoSuchAlgorithmException {
        return MakeDynamicFee(receiver, amount, firstValid, lastValid, closeRemainderAddress, null);
    }

    protected static ContractTemplate MakeDynamicFee(final Address receiver, final Integer amount, final Integer firstValid, final Integer lastValid, final Address closeRemainderAddress, final Lease lease) throws NoSuchAlgorithmException {
        Objects.requireNonNull(receiver);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(firstValid);

        byte[] program = Encoder.decodeFromBase64(referenceProgram);

        ParameterValue[] values = {
                new IntParameterValue(amount),
                new IntParameterValue(firstValid),
                new IntParameterValue(lastValid == null ? firstValid + 1000 : lastValid),
                new AddressParameterValue(receiver),
                new AddressParameterValue(closeRemainderAddress == null ? new Address() : closeRemainderAddress),
                new BytesParameterValue(lease == null ? new Lease() : lease)
        };

        return ContractTemplate.inject(program, referenceOffsets, values);
    }

    /**
     * Container class for the signed dynamic fee data returned by SignDynamicFee.
     */
    public static class SignedDynamicFee {
        public final Transaction txn;
        public final LogicsigSignature lsig;

        private SignedDynamicFee(final Transaction txn, final LogicsigSignature lsig) {
            this.txn = txn;
            this.lsig = lsig;
        }
    }

    /**
     * Return the main transaction and signed logic needed to complete the
     * transfer. These should be sent to the fee payer, who can use
     * get_transactions() to update fields and create the auxiliary
     * transaction.
     *
     * The transaction and logicsig should be sent to the other party as base64 encoded objects:
     * SignedDynamicFee sdf = DynamicFee.SignDynamicFee(...);
     * String encodedLsig = Encoder.encodeToBase64(Encoder.encodeToMsgPack(sdf.lsig));
     * String encodedTxn = Encoder.encodeToBase64(Encoder.encodeToMsgPack(sdf.txn));
     *
     * @param contract DynamicFee contract created with MakeDynamicFee.
     * @param senderAccount sender account to sign the transaction.
     * @param genesisHash Genesis hash for the network where the transaction will be submitted.
     * @return
     */
    public static SignedDynamicFee SignDynamicFee(final ContractTemplate contract, final Account senderAccount, final Digest genesisHash) throws IOException {
        Logic.ProgramData data = Logic.readProgram(contract.program, null);
        Address receiverAddress = new Address(data.byteBlock.get(0));
        Address closeToAddress = new Address(data.byteBlock.get(1));
        Lease lease = new Lease(data.byteBlock.get(2));

        BigInteger amount = BigInteger.valueOf(data.intBlock.get(2));
        BigInteger firstValid = BigInteger.valueOf(data.intBlock.get(3));
        BigInteger lastValid = BigInteger.valueOf(data.intBlock.get(4));

        Transaction txn = new Transaction(
                senderAccount.getAddress(),
                BigInteger.valueOf(0),
                firstValid,
                lastValid,
                null,
                "",
                genesisHash,
                amount,
                receiverAddress,
                closeToAddress);
        txn.setLease(lease);

        LogicsigSignature lsig = senderAccount.signLogicsig(new LogicsigSignature(contract.program));

        return new SignedDynamicFee(txn, lsig);
    }

    /**
     * Create and sign the secondary dynamic fee transaction, update
     * transaction fields, and sign as the fee payer; return both
     * transactions ready to be sent.
     *
     * Create the Transaction and LogicsigSignature objects from base64 encoded objects:
     * Encoder.decodeFromMsgPack(encodedTxn, Transaction.class),
     * Encoder.decodeFromMsgPack(encodedLsig, LogicsigSignature.class),
     *
     * @param txn main transaction from payer
     * @param lsig signed logic received from payer
     * @param account an account initialized with a signing key.
     * @param feePerByte fee per byte, for both transactions
     */
    public static byte[] MakeReimbursementTransactions(final Transaction txn, final LogicsigSignature lsig, final Account account, final int feePerByte) throws NoSuchAlgorithmException, IOException {
        // Due to an issue with how transactions are initialized, the fee at
        // this point is inconsistent between SDKs. Adjust the fee here to
        // allow the same goldens to be used across all SDKs.
        txn.fee = Account.MIN_TX_FEE_UALGOS;
        Account.setFeeByFeePerByte(txn, BigInteger.valueOf(feePerByte));

        // Reimbursement transaction
        Transaction txn2 = new Transaction(
                account.getAddress(),
                BigInteger.valueOf(feePerByte),
                txn.firstValid,
                txn.lastValid,
                null,
                txn.genesisID,
                txn.genesisHash,
                txn.fee,
                txn.sender,
                null);
        txn2.setLease(new Lease(txn.lease));
        Account.setFeeByFeePerByte(txn2, BigInteger.valueOf(feePerByte));

        TxGroup.assignGroupID(null, txn, txn2);
        SignedTransaction stx1 = new SignedTransaction(txn, lsig, txn.txID());
        SignedTransaction stx2 = account.signTransaction(txn2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Encoder.encodeToMsgPack(stx1));
        baos.write(Encoder.encodeToMsgPack(stx2));
        return baos.toByteArray();
    }
}
