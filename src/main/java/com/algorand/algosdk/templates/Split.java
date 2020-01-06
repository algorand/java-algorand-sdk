package com.algorand.algosdk.templates;

import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.templates.ContractTemplate.ParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.IntParameterValue;
import com.algorand.algosdk.templates.ContractTemplate.AddressParameterValue;
import com.algorand.algosdk.util.Encoder;

public class Split {
	private static String referenceProgram = "ASAIAQUCAAYHCAkmAyDYHIR7TIW5eM/WAZcXdEDqv7BD+baMN6i2/A5JatGbNCDKsaoZHPQ3Zg8zZB/BZ1oDgt77LGo5np3rbto3/gloTyB40AS2H3I72YCbDk4hKpm7J7NnFy2Xrt39TJG0ORFg+zEQIhIxASMMEDIEJBJAABkxCSgSMQcyAxIQMQglEhAxAiEEDRAiQAAuMwAAMwEAEjEJMgMSEDMABykSEDMBByoSEDMACCEFCzMBCCEGCxIQMwAIIQcPEBA=";
	private static int [] referenceOffsets = {4, 7, 8, 9, 10, 14, 47, 80};

	/**
	 * 
	 * @param owner the address to refund funds to on timeout
	 * @param receiver1 the first recipient in the split account
	 * @param receiver2 the second recipient in the split account
	 * @param ratn fraction of money to be paid to the first recipient
	 * @param ratd fraction of money to be paid to the second recipient
	 * @param expiryRound the round at which the account expires
	 * @param minPay minimum amount to be paid out of the account
	 * @param maxFee half of the maximum fee used by each split forwarding group transaction
	 * @return ContractTemplate with the address and the program with the given parameter values
	 * @throws NoSuchAlgorithmException
	 */
	public static ContractTemplate MakeSplit(
			String owner, 
			String receiver1, 
			String receiver2,
			int ratn,
			int ratd,
			int expiryRound, 			
			int minPay,
			int maxFee) throws NoSuchAlgorithmException {

		ParameterValue[] values = {
				new IntParameterValue(maxFee),
				new IntParameterValue(expiryRound),
				new IntParameterValue(ratn),
				new IntParameterValue(ratd),
				new IntParameterValue(minPay),
				new AddressParameterValue(owner),
				new AddressParameterValue(receiver1),
				new AddressParameterValue(receiver2)
		};
		return ContractTemplate.inject(Encoder.decodeFromBase64(referenceProgram), referenceOffsets, values);
	}
}
