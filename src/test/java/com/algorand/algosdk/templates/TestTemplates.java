package com.algorand.algosdk.templates;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.transaction.Lease;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.*;

public class TestTemplates {
    @Test 
    public void testVarInt() {
        int a = 600000;
        byte[] buffer = Logic.putUVarint((int) a);
        Logic.VarintResult result = Logic.getUVarint(buffer, 0);
        assertThat(result.value).isEqualTo(a);
        assertThat(result.length).isEqualTo(buffer.length);
    }

    @Test
    public void testHTLC() throws Exception {
        String goldenAddress = "FBZIR3RWVT2BTGVOG25H3VAOLVD54RTCRNRLQCCJJO6SVSCT5IVDYKNCSU";
        String goldenProgram = "ASAE6AcBAMDPJCYDIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IBB2YRNPIfx8AiI9UKues2ALw//DcSQjoeR7sfmp2/VfIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITMQEiDjEQIxIQMQcyAxIQMQgkEhAxCSgSLQEpEhAxCSoSMQIlDRAREA==";
        String goldenLtxn = "gqRsc2lngqNhcmeRxAhwcmVpbWFnZaFsxJcBIAToBwEAwM8kJgMg5pqWHm8tX3rIZgeSZVK+mCNe0zNjyoiRi7nJOKkVtvkgEHZhE08h/HwCIj1Qq56zYAvD/8NxJCOh5Hux+anb9V8g/ryguxRKWk6ntDikaBrIDmyhBby2B/xWUyXJVpX2ohMxASIOMRAjEhAxBzIDEhAxCCQSEDEJKBItASkSEDEJKhIxAiUNEBEQo3R4boelY2xvc2XEIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5o2ZlZc0D6KJmdgGiZ2jEIH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpomx2ZKNzbmTEIChyiO42rPQZmq42un3UDl1H3kZii2K4CElLvSrIU+oqpHR5cGWjcGF5";


        // Create contract
        Address owner = new Address("726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM");
        Address receiver = new Address("42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE");
        String hashFn = "sha256";
        String hashImg = "EHZhE08h/HwCIj1Qq56zYAvD/8NxJCOh5Hux+anb9V8=";
        ContractTemplate contract = HTLC.MakeHTLC(
                owner,
                receiver,
                hashFn,
                hashImg,
                600000,
                1000);

        // Verify
        assertThat(contract.address.toString()).isEqualTo(goldenAddress);
        assertThat(Encoder.encodeToBase64(contract.program)).isEqualTo(goldenProgram);

        // Create transactions
        String preImageAsBase64 = "cHJlaW1hZ2U=";
        Digest gh = new Digest("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=");
        SignedTransaction stx = HTLC.GetHTLCTransaction(
                contract,
                preImageAsBase64,
                1,
                100,
                gh,
                0);

        // Verify
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx))).isEqualTo(goldenLtxn);
    }

    @Test
    public void testSplit() throws Exception {
        Address addr1 = new Address("WO3QIJ6T4DZHBX5PWJH26JLHFSRT7W7M2DJOULPXDTUS6TUX7ZRIO4KDFY");
        Address addr2 = new Address("W6UUUSEAOGLBHT7VFT4H2SDATKKSG6ZBUIJXTZMSLW36YS44FRP5NVAU7U");
        Address addr3 = new Address("XCIBIN7RT4ZXGBMVAMU3QS6L5EKB7XGROC5EPCNHHYXUIBAA5Q6C5Y7NEU");

        int minPay = 10000;
        int rat1 = 30;
        int rat2 = 100;
        ContractTemplate result = Split.MakeSplit(
                addr1,
                addr2,
                addr3,
                rat1,
                rat2,
                123456,
                minPay,
                5000000);
        String goldenProgram = "ASAIAcCWsQICAMDEB2QekE4mAyCztwQn0+DycN+vsk+vJWcsoz/b7NDS6i33HOkvTpf+YiC3qUpIgHGWE8/1LPh9SGCalSN7IaITeeWSXbfsS5wsXyC4kBQ38Z8zcwWVAym4S8vpFB/c0XC6R4mnPi9EBADsPDEQIhIxASMMEDIEJBJAABkxCSgSMQcyAxIQMQglEhAxAiEEDRAiQAAuMwAAMwEAEjEJMgMSEDMABykSEDMBByoSEDMACCEFCzMBCCEGCxIQMwAIIQcPEBA=";
        String goldenAddress = "HDY7A4VHBWQWQZJBEMASFOUZKBNGWBMJEMUXAGZ4SPIRQ6C24MJHUZKFGY";

        String encodedProgram = Encoder.encodeToBase64(result.program);
        assertThat(encodedProgram).isEqualTo(goldenProgram);
        assertThat(result.address.toString()).isEqualTo(goldenAddress);

        Digest genesisHash = new Digest(Encoder.decodeFromBase64("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk="));
        ContractTemplate decodedContract = new ContractTemplate(encodedProgram);

        assertThatThrownBy(() -> Split.GetSplitTransactions(
                decodedContract,
                130001,
                100000,
                101000,
                30,
                genesisHash))
            .isInstanceOf(Exception.class)
            .hasMessageStartingWith("The token split must be exactly ");

        assertThatThrownBy(() -> Split.GetSplitTransactions(
                decodedContract,
                130,
                10000,
                101000,
                30,
                genesisHash))
            .isInstanceOf(Exception.class)
            .hasMessageStartingWith("Receiver one must receive at least");

        byte[] transactions = Split.GetSplitTransactions(
                decodedContract,
                minPay * (rat1 + rat2),
                1,
                100,
                10000,
                genesisHash);

        String splitTransactionGolden = "gqRsc2lngaFsxM4BIAgBwJaxAgIAwMQHZB6QTiYDILO3BCfT4PJw36+yT68lZyyjP9vs0NLqLfcc6S9Ol/5iILepSkiAcZYTz/Us+H1IYJqVI3shohN55ZJdt+xLnCxfILiQFDfxnzNzBZUDKbhLy+kUH9zRcLpHiac+L0QEAOw8MRAiEjEBIwwQMgQkEkAAGTEJKBIxBzIDEhAxCCUSEDECIQQNECJAAC4zAAAzAQASMQkyAxIQMwAHKRIQMwEHKhIQMwAIIQULMwEIIQYLEhAzAAghBw8QEKN0eG6Jo2FtdM4ABJPgo2ZlZc4AId/gomZ2AaJnaMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjZ3JwxCBLA74bTV35FJNL1h0K9ZbRU24b4M1JRkD1YTogvvDXbqJsdmSjcmN2xCC3qUpIgHGWE8/1LPh9SGCalSN7IaITeeWSXbfsS5wsX6NzbmTEIDjx8HKnDaFoZSEjASK6mVBaawWJIylwGzyT0Rh4WuMSpHR5cGWjcGF5gqRsc2lngaFsxM4BIAgBwJaxAgIAwMQHZB6QTiYDILO3BCfT4PJw36+yT68lZyyjP9vs0NLqLfcc6S9Ol/5iILepSkiAcZYTz/Us+H1IYJqVI3shohN55ZJdt+xLnCxfILiQFDfxnzNzBZUDKbhLy+kUH9zRcLpHiac+L0QEAOw8MRAiEjEBIwwQMgQkEkAAGTEJKBIxBzIDEhAxCCUSEDECIQQNECJAAC4zAAAzAQASMQkyAxIQMwAHKRIQMwEHKhIQMwAIIQULMwEIIQYLEhAzAAghBw8QEKN0eG6Jo2FtdM4AD0JAo2ZlZc4AId/gomZ2AaJnaMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjZ3JwxCBLA74bTV35FJNL1h0K9ZbRU24b4M1JRkD1YTogvvDXbqJsdmSjcmN2xCC4kBQ38Z8zcwWVAym4S8vpFB/c0XC6R4mnPi9EBADsPKNzbmTEIDjx8HKnDaFoZSEjASK6mVBaawWJIylwGzyT0Rh4WuMSpHR5cGWjcGF5";
        assertThat(Encoder.encodeToBase64(transactions)).isEqualTo(splitTransactionGolden);
    }

    @Test
    public void testPeriodicPayment() throws Exception {
        String goldenAddress = "JMS3K4LSHPULANJIVQBTEDP5PZK6HHMDQS4OKHIMHUZZ6OILYO3FVQW7IY";
        String goldenProgram = "ASAHAegHZABfoMIevKOVASYCIAECAwQFBgcIAQIDBAUGBwgBAgMEBQYHCAECAwQFBgcIIJKvkYTkEzwJf2arzJOxERsSogG9nQzKPkpIoc4TzPTFMRAiEjEBIw4QMQIkGCUSEDEEIQQxAggSEDEGKBIQMQkyAxIxBykSEDEIIQUSEDEJKRIxBzIDEhAxAiEGDRAxCCUSEBEQ";
        String goldenWithdrawalTransaction = "gqRsc2lngaFsxJkBIAcB6AdkAF+gwh68o5UBJgIgAQIDBAUGBwgBAgMEBQYHCAECAwQFBgcIAQIDBAUGBwggkq+RhOQTPAl/ZqvMk7ERGxKiAb2dDMo+SkihzhPM9MUxECISMQEjDhAxAiQYJRIQMQQhBDECCBIQMQYoEhAxCTIDEjEHKRIQMQghBRIQMQkpEjEHMgMSEDECIQYNEDEIJRIQERCjdHhuiaNhbXTOAAehIKNmZWXNA+iiZnbNBLCiZ2jEIH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpomx2zQUPomx4xCABAgMEBQYHCAECAwQFBgcIAQIDBAUGBwgBAgMEBQYHCKNyY3bEIJKvkYTkEzwJf2arzJOxERsSogG9nQzKPkpIoc4TzPTFo3NuZMQgSyW1cXI76LA1KKwDMg39flXjnYOEuOUdDD0znzkLw7akdHlwZaNwYXk=";

        Address addr = new Address("SKXZDBHECM6AS73GVPGJHMIRDMJKEAN5TUGMUPSKJCQ44E6M6TC2H2UJ3I");
        Lease lease = new Lease("AQIDBAUGBwgBAgMEBQYHCAECAwQFBgcIAQIDBAUGBwg=");
        ContractTemplate result = PeriodicPayment.MakePeriodicPayment(
                addr,
                500000,
                95,
                100,
                1000,
                2445756,
                lease);

        assertThat(result.address.toString()).isEqualTo(goldenAddress);
        assertThat(Encoder.encodeToBase64(result.program)).isEqualTo(goldenProgram);

        Digest genesisHash = new Digest(Encoder.decodeFromBase64("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk="));

        SignedTransaction stx = PeriodicPayment.MakeWithdrawalTransaction(result, 1200, genesisHash, 0);
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx))).isEqualTo(goldenWithdrawalTransaction);
    }

    @Test
    public void testDynamicFee() throws Exception {
        String goldenAddress = "GCI4WWDIWUFATVPOQ372OZYG52EULPUZKI7Y34MXK3ZJKIBZXHD2H5C5TI";
        // algotmpl -d ${GOPATH}/src/github.com/algorand/go-algorand/tools/teal/templates/ dynamic-fee --amt 5000 --cls 42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE --to 726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM --fv 12345 --lv 12346 --lease "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk="
        String golden = "ASAFAgGIJ7lgumAmAyD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiEyDmmpYeby1feshmB5JlUr6YI17TM2PKiJGLuck4qRW2+SB/g7Flf/H8U7ktwYFIodZd/C1LH6PWdyhK3dIAEm2QaTIEIhIzABAjEhAzAAcxABIQMwAIMQESEDEWIxIQMRAjEhAxBygSEDEJKRIQMQgkEhAxAiUSEDEEIQQSEDEGKhIQ";

        // Initialize inputs
        Address addr1 = new Address("726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM");
        Address addr2 = new Address("42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE");
        byte[] pk1 = Encoder.decodeFromBase64("cv8E0Ln24FSkwDgGeuXKStOTGcze5u8yldpXxgrBxumFPYdMJymqcGoxdDeyuM8t6Kxixfq0PJCyJP71uhYT7w==");
        byte[] pk2 = Encoder.decodeFromBase64("2qjz96Vj9M6YOqtNlfJUOKac13EHCXyDty94ozCjuwwriI+jzFgStFx9E6kEk1l4+lFsW4Te2PY1KV8kNcccRg==");
        Account account1 = new Account(pk1);
        Account account2 = new Account(pk2);
        Lease lease = new Lease("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=");
        Digest gh = new Digest("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=");

        // Create contract.
        ContractTemplate program = DynamicFee.MakeDynamicFee(
                addr1,
                5000,
                12345,
                12346,
                addr2,
                lease);

        assertThat(Encoder.encodeToBase64(program.program)).isEqualTo(golden);
        assertThat(program.address.toString()).isEqualTo(goldenAddress);

        // Sign contract
        DynamicFee.SignedDynamicFee sdf = DynamicFee.SignDynamicFee(program, account1, gh);
        String encodedLsig = Encoder.encodeToBase64(Encoder.encodeToMsgPack(sdf.lsig));
        String encodedTxn = Encoder.encodeToBase64(Encoder.encodeToMsgPack(sdf.txn));

        String goldenTxn = "iqNhbXTNE4ilY2xvc2XEIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5o2ZlZc0D6KJmds0wOaJnaMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmibHbNMDqibHjEIH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpo3JjdsQg/ryguxRKWk6ntDikaBrIDmyhBby2B/xWUyXJVpX2ohOjc25kxCCFPYdMJymqcGoxdDeyuM8t6Kxixfq0PJCyJP71uhYT76R0eXBlo3BheQ==";
        String goldenLsig = "gqFsxLEBIAUCAYgnuWC6YCYDIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpMgQiEjMAECMSEDMABzEAEhAzAAgxARIQMRYjEhAxECMSEDEHKBIQMQkpEhAxCCQSEDECJRIQMQQhBBIQMQYqEhCjc2lnxEAhLNdfdDp9Wbi0YwsEQCpP7TVHbHG7y41F4MoESNW/vL1guS+5Wj4f5V9fmM63/VKTSMFidHOSwm5o+pbV5lYH";
        assertThat(encodedLsig).isEqualTo(goldenLsig);
        assertThat(encodedTxn).isEqualTo(goldenTxn);

        Transaction decodedTxn = Encoder.decodeFromMsgPack(encodedTxn, Transaction.class);
        LogicsigSignature decodedLsig = Encoder.decodeFromMsgPack(encodedLsig, LogicsigSignature.class);

        // Generate signed transactions (using data that would be passed to another person).
        byte[] stxns = DynamicFee.MakeReimbursementTransactions(
                decodedTxn,
                decodedLsig,
                account2,
                1234);

        String goldenStxns = "gqRsc2lngqFsxLEBIAUCAYgnuWC6YCYDIP68oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITIOaalh5vLV96yGYHkmVSvpgjXtMzY8qIkYu5yTipFbb5IH+DsWV/8fxTuS3BgUih1l38LUsfo9Z3KErd0gASbZBpMgQiEjMAECMSEDMABzEAEhAzAAgxARIQMRYjEhAxECMSEDEHKBIQMQkpEhAxCCQSEDECJRIQMQQhBBIQMQYqEhCjc2lnxEAhLNdfdDp9Wbi0YwsEQCpP7TVHbHG7y41F4MoESNW/vL1guS+5Wj4f5V9fmM63/VKTSMFidHOSwm5o+pbV5lYHo3R4boujYW10zROIpWNsb3NlxCDmmpYeby1feshmB5JlUr6YI17TM2PKiJGLuck4qRW2+aNmZWXOAAWq6qJmds0wOaJnaMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjZ3JwxCBRpaRVpA3ImXU4/ENcrzp+jsooLVHC7bF5kCGUK0KORaJsds0wOqJseMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjcmN2xCD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiE6NzbmTEIIU9h0wnKapwajF0N7K4zy3orGLF+rQ8kLIk/vW6FhPvpHR5cGWjcGF5gqNzaWfEQAilsGaC4M4zfYN5QpvREdHEC0DjI2ZWCXSIwwyUWHg2dzd5gKR2Cqu+iUmiCU1hOTTiOump3PILTgWeG0ZkUAajdHhuiqNhbXTOAAWq6qNmZWXOAATzvqJmds0wOaJnaMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjZ3JwxCBRpaRVpA3ImXU4/ENcrzp+jsooLVHC7bF5kCGUK0KORaJsds0wOqJseMQgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjcmN2xCCFPYdMJymqcGoxdDeyuM8t6Kxixfq0PJCyJP71uhYT76NzbmTEICuIj6PMWBK0XH0TqQSTWXj6UWxbhN7Y9jUpXyQ1xxxGpHR5cGWjcGF5";
        assertThat(Encoder.encodeToBase64(stxns)).isEqualTo(goldenStxns);
    }

    @Test
    public void testLimitOrder() throws Exception {
        Address owner = new Address("726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM");
        int assetId = 12345;
        int ratn = 30;
        int ratd = 100;
        int expirationRound = 123456;
        int minTrade = 10000;
        int maxFee = 5000000;
        ContractTemplate contract = LimitOrder.MakeLimitOrder(
                owner,
                assetId,
                ratn,
                ratd,
                expirationRound,
                minTrade,
                maxFee);

        String goldenAddress = "LXQWT2XLIVNFS54VTLR63UY5K6AMIEWI7YTVE6LB4RWZDBZKH22ZO3S36I";
        String goldenProgram = "ASAKAAHAlrECApBOBLlgZB7AxAcmASD+vKC7FEpaTqe0OKRoGsgObKEFvLYH/FZTJclWlfaiEzEWIhIxECMSEDEBJA4QMgQjEkAAVTIEJRIxCCEEDRAxCTIDEhAzARAhBRIQMwERIQYSEDMBFCgSEDMBEzIDEhAzARIhBx01AjUBMQghCB01BDUDNAE0Aw1AACQ0ATQDEjQCNAQPEEAAFgAxCSgSMQIhCQ0QMQcyAxIQMQgiEhAQ";
        String encodedContract = Encoder.encodeToBase64(contract.program);
        assertThat(encodedContract).isEqualTo(goldenProgram);
        assertThat(contract.address.toString()).isEqualTo(goldenAddress);

        // Test swap transaction
        byte[] pk1 = Encoder.decodeFromBase64("DTKVj7KMON3GSWBwMX9McQHtaDDi8SDEBi0bt4rOxlHNRahLa0zVG+25BDIaHB1dSoIHIsUQ8FFcdnCdKoG+Bg==");
        Account sender = new Account(pk1);
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk="));
        ContractTemplate serializedContract = new ContractTemplate(encodedContract);

        // Verify invalid exchange rate generates an error.
        assertThatThrownBy(() -> LimitOrder.MakeSwapAssetsTransaction(
                serializedContract,
                3000,
                10001, // The correct amount would be 10000
                sender,
                1234,
                2234,
                genesisHash,
                10))
            .isInstanceOf(Exception.class)
            .hasMessageStartingWith("The exchange ratio of assets to microalgos must be exactly");

        // Verify transactions with correct exchange rate.
        byte[] transactions = LimitOrder.MakeSwapAssetsTransaction(
                serializedContract,
                3000,
                10000,
                sender,
                1234,
                2234,
                genesisHash,
                10);

        String goldenTx1 =
                        "gqRsc2lngaFsxLcBIAoAAcCWsQICkE4EuWBkHsDEByYBIP68oLsUSlpOp7Q4pG" +
                        "gayA5soQW8tgf8VlMlyVaV9qITMRYiEjEQIxIQMQEkDhAyBCMSQABVMgQlEjEI" +
                        "IQQNEDEJMgMSEDMBECEFEhAzAREhBhIQMwEUKBIQMwETMgMSEDMBEiEHHTUCNQ" +
                        "ExCCEIHTUENQM0ATQDDUAAJDQBNAMSNAI0BA8QQAAWADEJKBIxAiEJDRAxBzID" +
                        "EhAxCCISEBCjdHhuiaNhbXTNJxCjZmVlzQisomZ2zQTSomdoxCB/g7Flf/H8U7" +
                        "ktwYFIodZd/C1LH6PWdyhK3dIAEm2QaaNncnDEIKz368WOGpdE/Ww0L8wUu5Ly" +
                        "2u2bpG3ZSMKCJvcvGApTomx2zQi6o3JjdsQgzUWoS2tM1RvtuQQyGhwdXUqCBy" +
                        "LFEPBRXHZwnSqBvgajc25kxCBd4Wnq60VaWXeVmuPt0x1XgMQSyP4nUnlh5G2R" +
                        "hyo+taR0eXBlo3BheQ==";
        String goldenTx2 =
                        "gqNzaWfEQKXv8Z6OUDNmiZ5phpoQJHmfKyBal4gBZLPYsByYnlXCAlXMBeVFG5" +
                        "CLP1k5L6BPyEG2/XIbjbyM0CGG55CxxAKjdHhuiqRhYW10zQu4pGFyY3bEIP68" +
                        "oLsUSlpOp7Q4pGgayA5soQW8tgf8VlMlyVaV9qITo2ZlZc0JJKJmds0E0qJnaM" +
                        "Qgf4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGmjZ3JwxCCs9+vFjhqX" +
                        "RP1sNC/MFLuS8trtm6Rt2UjCgib3LxgKU6Jsds0IuqNzbmTEIM1FqEtrTNUb7b" +
                        "kEMhocHV1KggcixRDwUVx2cJ0qgb4GpHR5cGWlYXhmZXKkeGFpZM0wOQ==";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(Encoder.decodeFromBase64(goldenTx1));
        bytes.write(Encoder.decodeFromBase64(goldenTx2));

        assertThat(Encoder.encodeToBase64(transactions)).isEqualTo(Encoder.encodeToBase64(bytes.toByteArray()));
    }
}
