package com.algorand.algosdk.templates;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.util.Encoder;

public class HTLC {

	private static String sha256ReferenceProgram = "ASAECAEACSYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQMQgkEhAxCSgSLQEpEhAxCSoSMQIlDRAREA==";
	private static String keccak256ReferenceProgram = "ASAECAEACSYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQMQgkEhAxCSgSLQIpEhAxCSoSMQIlDRAREA==";
	private static int [] referenceOffsets = { /*fee*/ 3 /*expiryRound*/, 6 /*receiver*/, 10 /*hashImage*/, 43 /*owner*/, 76};

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
			BigInteger expiryRound, 
			BigInteger maxFee) throws NoSuchAlgorithmException {

		String programToUse;
		if (hashFunction.equals("sha256")) {
			programToUse = sha256ReferenceProgram;
		} else if (hashFunction.equals("keccak256")) {
			programToUse = keccak256ReferenceProgram;
		} else {
			throw new RuntimeException("invalid hash function supplied");
		}
		
		byte [][] values = {
				Encoder.decodeFromBase64(Encoder.encodeToBase64(maxFee.toByteArray())), 
				expiryRound.toByteArray(), 
				receiver.getBytes(), 
				hashImage.getBytes(), 
				owner.getBytes()};
		
		return ContractTemplate.inject(programToUse, referenceOffsets, values);
	}


	//orig = base64.b64decode(orig)

	//output = base64.b64encode(inject(orig, [4, 7, 8, 9, 10, 14, 20], [100,200,300,400,500, "W6UUUSEAOGLBHT7VFT4H2SDATKKSG6ZBUIJXTZMSLW36YS44FRP5NVAU7U", "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk="], [Types.INT, Types.INT, Types.INT, Types.INT, Types.INT, Types.ADDRESS, Types.BASE64]))

	
}
