package com.algorand.algosdk.templates;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;
import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import static com.algorand.algosdk.account.Account.setFeeByFeePerByte;
import static com.algorand.algosdk.templates.ContractTemplate.*;

public class LimitOrder {
    protected static String referenceProgram = "ASAKAAEFAgYEBwgJCiYBIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMRYiEjEQIxIQMQEkDhAyBCMSQABVMgQlEjEIIQQNEDEJMgMSEDMBECEFEhAzAREhBhIQMwEUKBIQMwETMgMSEDMBEiEHHTUCNQExCCEIHTUENQM0ATQDDUAAJDQBNAMSNAI0BA8QQAAWADEJKBIxAiEJDRAxBzIDEhAxCCISEBA=";

    /**
     * MakeLimitOrder allows a user to exchange some number of assets for some number of algos.
     * Fund the contract with some number of Algos to limit the maximum number of
     * Algos you're willing to trade for some other asset.
     *
     * Works on two cases:
     * - trading Algos for some other asset
     * - closing out Algos back to the originator after a timeout
     *
     * trade case, a 2 transaction group:
     * - gtxn[0] (this txn) Algos from Me to Other
     * - gtxn[1] asset from Other to Me
     *
     * We want to get at least some amount of the other asset per our Algos
     *   gtxn[1].AssetAmount / gtxn[0].Amount >= N / D
     *   ===
     *   gtxn[1].AssetAmount * D >= gtxn[0].Amount * N
     *
     * close-out case:
     * - txn alone, close out value after timeout
     *
     * @param owner an address that can receive the asset after the expiry round
     * @param assetId asset to be transfered
     * @param ratn the numerator of the exchange rate
     * @param ratd the denominator of the exchange rate
     * @param expirationRound the round on which the assets can be transferred back to owner
     * @param minTrade the minimum amount (of Algos) to be traded away
     * @param maxFee the maximum fee that can be paid to the network by the account
     */
    public static ContractTemplate MakeLimitOrder(final Address owner, final Integer assetId, final Integer ratn, final Integer ratd, final Integer expirationRound, final Integer minTrade, final Integer maxFee) throws NoSuchAlgorithmException {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(assetId);
        Objects.requireNonNull(ratn);
        Objects.requireNonNull(ratd);
        Objects.requireNonNull(expirationRound);
        Objects.requireNonNull(minTrade);
        Objects.requireNonNull(maxFee);

        List<ParameterValue> values = ImmutableList.of(
                new IntParameterValue(5, maxFee),
                new IntParameterValue(7, minTrade),
                new IntParameterValue(9, assetId),
                new IntParameterValue(10, ratd),
                new IntParameterValue(11, ratn),
                new IntParameterValue(12, expirationRound),
                new AddressParameterValue(16, owner)
        );

        byte[] program = Encoder.decodeFromBase64(referenceProgram);
        return inject(program, values);
    }

    /**
     * Creates a group transaction array which transfer funds according to the contract's ratio
     *
     * @param contract previously created LimitOrder contract
     * @param assetAmount amount of assets to be sent
     * @param sender account to sign the payment transaction
     * @param feePerByte fee per byte used for the transactions
     * @param algoAmount number of algos to transfer
     * @param firstValid first round on which these txns will be valid
     * @param lastValid last round on which these txns will be valid
     * @param genesisHash Genesis hash for the network where the transaction will be submitted.
     * @return
     * @throws IOException
     */
    public static byte[] MakeSwapAssetsTransaction(ContractTemplate contract, Integer assetAmount, Account sender, Integer feePerByte, Integer algoAmount, Integer firstValid, Integer lastValid, Digest genesisHash) throws IOException, NoSuchAlgorithmException {
        Logic.ProgramData data = readAndVerifyContract(contract.program, 10, 1);

        Address owner = new Address(data.byteBlock.get(0));
        int assetId = data.intBlock.get(6);
        int ratd = data.intBlock.get(7);
        int ratn = data.intBlock.get(8);

        // Verify the exchange rate ratio
        if (assetAmount * ratd < algoAmount * ratn) {
            throw new IllegalArgumentException("The provided amounts to not meet the contract exchange rate of assetAmount = algoAmount * " + ratn + " / " + ratd);
        }

        Transaction tx1 = new Transaction(
                owner,
                Account.MIN_TX_FEE_UALGOS,
                BigInteger.valueOf(firstValid),
                BigInteger.valueOf(lastValid),
                null,
                "",
                genesisHash,
                BigInteger.valueOf(assetAmount),
                sender.getAddress(),
                null);
        setFeeByFeePerByte(tx1, BigInteger.valueOf(feePerByte));

        Transaction tx2 = Transaction.createAssetTransferTransaction(
                sender.getAddress(),
                owner,
                null,
                BigInteger.valueOf(assetAmount),
                Account.MIN_TX_FEE_UALGOS,
                BigInteger.valueOf(firstValid),
                BigInteger.valueOf(lastValid),
                null,
                "",
                genesisHash,
                BigInteger.valueOf(assetId));
        setFeeByFeePerByte(tx2, BigInteger.valueOf(feePerByte));

        TxGroup.assignGroupID(null, tx1, tx2);
        LogicsigSignature lsig = new LogicsigSignature(contract.program);
        SignedTransaction stx1 = new SignedTransaction(tx1, lsig, tx1.txID());
        SignedTransaction stx2 = sender.signTransaction(tx2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Encoder.encodeToMsgPack(stx1));
        baos.write(Encoder.encodeToMsgPack(stx2));
        return baos.toByteArray();
    }
}
