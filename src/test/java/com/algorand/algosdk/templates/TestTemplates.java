package com.algorand.algosdk.templates;


import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;


public class TestTemplates {

	@Test 
	public void testVarInt() {

		int a = 600000;
		byte[] buffer = ContractTemplate.putVarint((int) a);
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

		ContractTemplate result = HTLC.MakeHTLC(owner, receiver, hashFn, hashImg, 600000, 1000);

		String goldenAddress = "KNBD7ATNUVQ4NTLOI72EEUWBVMBNK" +
				"MPHWVBCETERV2W7T2YO6CVMLJRBM4";
		String goldenProgram = "ASAE6AcBAMDPJCYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTip" +
				"Fbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsU" +
				"SlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQ" +
				"MQgkEhAxCSgSLQEpEhAxCSoSMQIlDRAREA==";

		Assert.assertEquals(goldenProgram, result.program);
		Assert.assertEquals(goldenAddress,  result.address);
	}

	@Test
	public void testSplit() throws Exception {
		String addr1 = "WO3QIJ6T4DZHBX5PWJH26JLHFSRT7W7M2DJOULPXDTUS6TUX7ZRIO4KDFY";
		String addr2 = "W6UUUSEAOGLBHT7VFT4H2SDATKKSG6ZBUIJXTZMSLW36YS44FRP5NVAU7U";
		String addr3 = "XCIBIN7RT4ZXGBMVAMU3QS6L5EKB7XGROC5EPCNHHYXUIBAA5Q6C5Y7NEU";

		ContractTemplate result = Split.MakeSplit(addr1, addr2, addr3, 30, 100, 123456, 10000, 5000000);
		String goldenProgram = "ASAIAcCWsQICAMDEBx5kkE4mAyCztwQn0+DycN+vsk+vJWcsoz/b7NDS" +
				"6i33HOkvTpf+YiC3qUpIgHGWE8/1LPh9SGCalSN7IaITeeWSXbfsS5ws" +
				"XyC4kBQ38Z8zcwWVAym4S8vpFB/c0XC6R4mnPi9EBADsPDEQIhIxASMM" +
				"EDIEJBJAABkxCSgSMQcyAxIQMQglEhAxAiEEDRAiQAAuMwAAMwEAEjEJ" +
				"MgMSEDMABykSEDMBByoSEDMACCEFCzMBCCEGCxIQMwAIIQcPEBA=";
		String goldenAddress = "KPYGWKTV7CKMPMTLQRNGMEQRSYTYD" +
				"HUOFNV4UDSBDLC44CLIJPQWRTCPBU";
		Assert.assertEquals(goldenProgram, result.program);        
		Assert.assertEquals(goldenAddress,  result.address);
	}
}
