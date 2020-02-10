package com.algorand.algosdk.templates;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.BytesParameterValue;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.google.common.collect.ImmutableList;

import javax.annotation.Signed;

import static com.algorand.algosdk.templates.ContractTemplate.readAndVerifyContract;

public class HTLC {

	private static String referenceProgram = "ASAEBQEABiYDIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITAQYg5pqWHm8tX3rIZgeSZVK+mCNe0zNjyoiRi7nJOKkVtvkxASIOMRAjEhAxBzIDEhAxCCQSEDEJKBItASkSEDEJKhIxAiUNEBEQ";
	/**
	 * Hash Time Locked Contract allows a user to recieve the Algo prior to a
	 * deadline (in terms of a round) by proving knowledge of a special value
	 * or to forfeit the ability to claim, returning it to the payer.
	 * This contract is usually used to perform cross-chained atomic swaps.
	 *
	 * More formally, algos can be transfered under only two circumstances:
	 *     1. To receiver if hash_function(arg_0) = hash_value
	 *     2. To owner if txn.FirstValid > expiry_round
	 * 
	 * @param owner an address that can receive the asset after the expiry round
	 * @param receiver address to receive Algos
	 * @param hashFunction the hash function to be used (must be either sha256 or keccak256)
	 * @param hashImage the hash image in base64
	 * @param expiryRound the round on which the assets can be transferred back to owner
	 * @param maxFee the maximum fee that can be paid to the network by the account
	 * @return ContractTemplate with the address and the program with the given parameter values
	 * @throws NoSuchAlgorithmException
	 */
	public static ContractTemplate MakeHTLC(
			Address owner,
			Address receiver,
			String hashFunction, 
			String hashImage, 
			int expiryRound, 
			int maxFee) throws NoSuchAlgorithmException {

		int hashInject;
		if (hashFunction.equals("sha256")) {
			hashInject = 1;
		} else if (hashFunction.equals("keccak256")) {
			hashInject = 2;
		} else {
			throw new RuntimeException("invalid hash function supplied");
		}

		List<ParameterValue> values = ImmutableList.of(
				new IntParameterValue(3, maxFee),
				new IntParameterValue(6, expiryRound),
				new AddressParameterValue(10, receiver),
				new BytesParameterValue(42, hashImage),
				new AddressParameterValue(45, owner),
				new IntParameterValue(102, hashInject)
		);
		return ContractTemplate.inject(Encoder.decodeFromBase64(referenceProgram), values);
	}

	/**
	 *
	 * @param contract the contract created with HTLC.MakeHTLC
	 * @param preImage Base64 encoded pre-image string.
	 * @param firstValid first round where the transactions are valid
	 * @param lastValid last round where the transactions are valid
	 * @param feePerByte fee per byte multiplier
	 * @param genesisHash genesis hash
	 * @return
	 */
	public static SignedTransaction GetHTLCTransaction(
			ContractTemplate contract,
			String preImage,
			int firstValid,
			int lastValid,
			Digest genesisHash,
			int feePerByte) throws NoSuchAlgorithmException, IOException {

		Logic.ProgramData data = readAndVerifyContract(contract.program, 4, 3);
		int maxFee = data.intBlock.get(0);
		// read the hash algorithm
		Address receiver = new Address(data.byteBlock.get(0));
		int hashFunction = Integer.valueOf(contract.program[136]);
		byte[] hashImage = data.byteBlock.get(1);

		// TODO: Verify hash function.

		Transaction txn = new Transaction(
				contract.address,
				BigInteger.valueOf(0),
				BigInteger.valueOf(firstValid),
				BigInteger.valueOf(lastValid),
				null,
				"",
				genesisHash,
				BigInteger.ZERO,
				null,
				receiver
		);
		Account.setFeeByFeePerByte(txn, feePerByte);

		if (txn.fee.intValue() > maxFee) {
			throw new RuntimeException("Transaction fee is too high: " + txn.fee.intValue() + " > " + maxFee);
		}

		List<byte[]> args = ImmutableList.of(Encoder.decodeFromBase64(preImage));
		LogicsigSignature lsig = new LogicsigSignature(contract.program, args);
		return new SignedTransaction(txn, lsig);
		//return Encoder.encodeToMsgPack(stx);
	}
}
