package com.algorand.algosdk.templates;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.templates.ContractTemplate.DataType;
import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;

import com.algorand.algosdk.util.Encoder;

public class HTLC {

	private static String referenceProgram = "ASAEBQEABiYDIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITAQYg5pqWHm8tX3rIZgeSZVK+mCNe0zNjyoiRi7nJOKkVtvkxASIOMRAjEhAxBzIDEhAxCCQSEDEJKBItASkSEDEJKhIxAiUNEBEQ";
	private static int [] referenceOffsets = {3, 6, 10, 42, 44, 101};
	/**
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
			String owner, 
			String receiver, 
			String hashFunction, 
			String hashImage, 
			int expiryRound, 
			int maxFee) throws NoSuchAlgorithmException {

		int hashInject = 0;
		String programToUse;
		if (hashFunction.equals("sha256")) {
			hashInject = 1;
		} else if (hashFunction.equals("keccak256")) {
			hashInject = 2;
		} else {
			throw new RuntimeException("invalid hash function supplied");
		}

		ParameterValue[] values = {
				new ParameterValue(DataType.INT, maxFee),
				new ParameterValue(DataType.INT, expiryRound),
				new ParameterValue(DataType.ADDRESS, receiver),
				new ParameterValue(DataType.BASE64, hashImage),
				new ParameterValue(DataType.ADDRESS, owner),
				new ParameterValue(DataType.INT, hashInject)
		};
		return ContractTemplate.inject(referenceProgram, referenceOffsets, values);
	}
}
