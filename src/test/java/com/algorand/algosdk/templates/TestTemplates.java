package com.algorand.algosdk.templates;


import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;


public class TestTemplates {

	@Test 
	public void testVarInt() {
		
		int a = 48329284;
		byte[] buffer = ContractTemplate.putVarint((long) a);
		int [] result = ContractTemplate.getVarint(buffer, 0);
		Assert.assertEquals(a, result[0]);
		Assert.assertEquals(buffer.length, result[1]);

	}
    @Test
    public void testHTLC() throws Exception {
    	   	
    	// Inputs
    	String owner = "726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM";
    	String receiver = "42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE";
    	String hashFn = "sha256";
    	String hashImg = "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=";
    	BigInteger expiryRound = BigInteger.valueOf(600000);
    	BigInteger maxFee = BigInteger.valueOf(1000);
    	String result = HTLC.MakeHTLC(owner, receiver, hashFn, hashImg, expiryRound, maxFee);

    	
 
    	String goldenProgram = "ASAE6AcBAMDPJCYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQMQgkEhAxCSgSLQEpEhAxCSoSMQIlDRAREA==";
    	Assert.assertEquals(goldenProgram, result);
//    	goldenAddress := "KNBD7ATNUVQ4NTLOI72EEUWBVMBNKMPHWVBCETERV2W7T2YO6CVMLJRBM4"
  //  	require.Equal(t, goldenAddress, c.GetAddress())

    	
    	

    	
    	
    }

}
