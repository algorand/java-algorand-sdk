package com.algorand.algosdk.templates;


import com.algorand.algosdk.transaction.SignedTransaction;
import org.junit.Test;

import com.algorand.algosdk.util.Encoder;

import static org.assertj.core.api.Assertions.*;


public class TestTemplates {

	@Test 
	public void testVarInt() {
		int a = 600000;
		byte[] buffer = ContractTemplate.putUVarint((int) a);
		int [] result = ContractTemplate.getUVarint(buffer, 0);
		assertThat(result[0]).isEqualTo(a);
		assertThat(result[1]).isEqualTo(buffer.length);
	}

	@Test
	public void testHTLC() throws Exception {
		String owner = "726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM";
		String receiver = "42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE";
		String hashFn = "sha256";
		String hashImg = "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=";

		ContractTemplate result = HTLC.MakeHTLC(owner, receiver, hashFn, hashImg, 600000, 1000);

		String goldenAddress = "KNBD7ATNUVQ4NTLOI72EEUWBVMBNKMPHWVBCETERV2W7T2YO6CVMLJRBM4";
		String goldenProgram = "ASAE6AcBAMDPJCYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQMQgkEhAxCSgSLQEpEhAxCSoSMQIlDRAREA==";

		assertThat(Encoder.encodeToBase64(result.program)).isEqualTo(goldenProgram);
		assertThat(result.address.toString()).isEqualTo(goldenAddress);
	}

	@Test
	public void testSplit() throws Exception {
		String addr1 = "WO3QIJ6T4DZHBX5PWJH26JLHFSRT7W7M2DJOULPXDTUS6TUX7ZRIO4KDFY";
		String addr2 = "W6UUUSEAOGLBHT7VFT4H2SDATKKSG6ZBUIJXTZMSLW36YS44FRP5NVAU7U";
		String addr3 = "XCIBIN7RT4ZXGBMVAMU3QS6L5EKB7XGROC5EPCNHHYXUIBAA5Q6C5Y7NEU";

		ContractTemplate result = Split.MakeSplit(addr1, addr2, addr3, 30, 100, 123456, 10000, 5000000);
		String goldenProgram = "ASAIAcCWsQICAMDEBx5kkE4mAyCztwQn0+DycN+vsk+vJWcsoz/b7NDS6i33HOkvTpf+YiC3qUpIgHGWE8/1LPh9SGCalSN7IaITeeWSXbfsS5wsXyC4kBQ38Z8zcwWVAym4S8vpFB/c0XC6R4mnPi9EBADsPDEQIhIxASMMEDIEJBJAABkxCSgSMQcyAxIQMQglEhAxAiEEDRAiQAAuMwAAMwEAEjEJMgMSEDMABykSEDMBByoSEDMACCEFCzMBCCEGCxIQMwAIIQcPEBA=";
		String goldenAddress = "KPYGWKTV7CKMPMTLQRNGMEQRSYTYDHUOFNV4UDSBDLC44CLIJPQWRTCPBU";

		assertThat(Encoder.encodeToBase64(result.program)).isEqualTo(goldenProgram);
		assertThat(result.address.toString()).isEqualTo(goldenAddress);
	}

	@Test
	public void testPeriodicPayment() throws Exception {
		String goldenAddress = "IJPPJDULMZNQXBIGCLCWVP5P4VD6U564BHC7C5CMZB2FVXKGZSIVPJFPT4";
		String goldenProgram = "ASAHAQoLAOcHkE7AxAcmAiB/g7Flf/H8U7ktwYFIodZd/C1LH6PWdyhK3dIAEm2QaSD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiEzEQIhIxASMOEDECJBglEhAxBCEEMQIIEhAxBigSEDEJMgMSMQcpEhAxCCEFEhAxCSkSMQcyAxIQMQIhBg0QMQglEhAREA==";
		String goldenWithdrawlTransaction = "gqRsc2lngaFsxJcBIAcBCgsA5weQTsDEByYCIH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMRAiEjEBIw4QMQIkGCUSEDEEIQQxAggSEDEGKBIQMQkyAxIxBykSEDEIIQUSEDEJKRIxBzIDEhAxAiEGDRAxCCUSEBEQo3R4bomjYW10zScQo2ZlZQqiZnbNBLqiZ2jEIH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpomx2zQihomx4xCB/g7Flf/H8U7ktwYFIodZd/C1LH6PWdyhK3dIAEm2QaaNyY3bEIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITo3NuZMQgQl70jotmWwuFBhLFar+v5Ufqd9wJxfF0TMh0Wt1GzJGkdHlwZaNwYXk=";

		int fee = 10;
		String addr = "726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM";
		byte[] lease = Encoder.decodeFromBase64("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=");
		ContractTemplate result = PeriodicPayment.MakePeriodicPayment(addr, 10000, 999, 11, fee, 123456, lease);


		assertThat(Encoder.encodeToBase64(result.program)).isEqualTo(goldenProgram);
		assertThat(result.address.toString()).isEqualTo(goldenAddress);

		String genesisHash = "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=";

		SignedTransaction stx = PeriodicPayment.MakeWithdrawlTransaction(result.program, 1210, genesisHash);
		assertThat(Encoder.encodeToMsgPack(stx)).isEqualTo(Encoder.decodeFromBase64(goldenWithdrawlTransaction));
	}
}
