package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.crypto.Signature;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestAccount {

    @Test
    public void testSignsTransactionE2E() throws Exception {
        final String REF_SIG_TXN = "82a3736967c4402f7d02826bc77dcd2a6e4d098ddcb619c4670c1dd98eba9a96f8d9a56e4fe8ff9868cee08ef1eae822bca9e99353244402717ad5850fd8136e0652f7295bd10da374786e87a3616d74cd04d2a366656501a26676ce0001a04fa26c76ce0001a437a3726376c4207d3f99e53d34ae49eb2f458761cf538408ffdaee35c70d8234166de7abe3e517a3736e64c4201bd63dc672b0bb29d42fcafa3422a4d385c0c8169bb01595babf8855cf596979a474797065a3706179";
        final String REF_TX_ID = "YGE4O2RBSMVPSPPXBK3SR45M453TRQA3L6U3GG7VYFLZL54Y4EZQ";
        final String FROM_ADDR = "DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA";
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        final String TO_ADDR = "PU7ZTZJ5GSXET2ZPIWDWDT2TQQEP7WXOGXDQ3ARUCZW6PK7D4ULSE6NYCE";

        // build unsigned transaction
        Transaction tx = new Transaction(
                new Address(FROM_ADDR),
                new Address(TO_ADDR),
                BigInteger.valueOf(1),
                BigInteger.valueOf(1234),
                BigInteger.valueOf(106575),
                BigInteger.valueOf(107575),
                "",
                new Digest()
        );
        byte[] seed = Mnemonic.toKey(FROM_SK);
        System.out.println(Encoder.encodeToJson(tx));
        Account account = new Account(seed);
        // make sure public key generated from mnemonic is correct
        Assert.assertEquals(FROM_ADDR, new Address(account.getClearTextPublicKey()).toString());
        // make sure address was also correctly computed
        Assert.assertEquals(FROM_ADDR, account.getAddress().toString());

        // sign the transaction
        SignedTransaction signedTx = account.signTransaction(tx);
        System.out.println(Encoder.encodeToJson(signedTx));

        byte[] signedTxBytes = Encoder.encodeToMsgPack(signedTx);
        String signedTxHex = Encoder.encodeToHexStr(signedTxBytes);
        Assert.assertEquals(REF_SIG_TXN, signedTxHex);

        // verify transaction ID
        String txID = signedTx.transactionID;
        Assert.assertEquals(REF_TX_ID, txID);
    }

    @Test
    public void testSignsTransactionZeroValE2E() throws Exception {
        final String REF_SIG_TXN = "82a3736967c440dfca36ff10b07b0b354f5593ef8ba9a2d10ff0256b1a8f3c17e7951c1e4d745e5c48d5d11ced0c2a3b91e622f5dbfdb72b5fbe4e2ea1b18b23bb34cf946dbe09a374786e86a3616d74cd04d2a366656501a26c76ce0001a437a3726376c4207d3f99e53d34ae49eb2f458761cf538408ffdaee35c70d8234166de7abe3e517a3736e64c4201bd63dc672b0bb29d42fcafa3422a4d385c0c8169bb01595babf8855cf596979a474797065a3706179";
        final String REF_TX_ID = "Y5RLONMU6BDW5WFVLWMYY52PMXCUNWG4LMT3JBA2OV4E6W6RRLXQ";
        final String FROM_ADDR = "DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA";
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        final String TO_ADDR = "PU7ZTZJ5GSXET2ZPIWDWDT2TQQEP7WXOGXDQ3ARUCZW6PK7D4ULSE6NYCE";

        // build unsigned transaction
        Transaction tx = new Transaction(
                new Address(FROM_ADDR),
                new Address(TO_ADDR),
                BigInteger.valueOf(1),
                BigInteger.valueOf(1234),
                BigInteger.valueOf(0),
                BigInteger.valueOf(107575),
                "",
                new Digest()
        );
        System.out.println(Encoder.encodeToJson(tx));
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        // make sure public key generated from mnemonic is correct
        Assert.assertEquals(FROM_ADDR, new Address(account.getClearTextPublicKey()).toString());
        // make sure address was also correctly computed
        Assert.assertEquals(FROM_ADDR, account.getAddress().toString());

        // sign the transaction
        SignedTransaction signedTx = account.signTransaction(tx);
        byte[] signedTxBytes = Encoder.encodeToMsgPack(signedTx);
        String signedTxHex = Encoder.encodeToHexStr(signedTxBytes);
        Assert.assertEquals(REF_SIG_TXN, signedTxHex);

        // verify transaction ID
        String txID = signedTx.transactionID;
        Assert.assertEquals(REF_TX_ID, txID);
    }

    @Test
    public void testKeygen() throws Exception {
        for (int i = 0; i < 100; i++) {
            Account account = new Account();
            Assert.assertTrue(account.getClearTextPublicKey().length > 0);
            Assert.assertNotNull(account.getAddress());
            Assert.assertArrayEquals(account.getClearTextPublicKey(), account.getAddress().getBytes());
        }
    }

    @Test
    public void testToMnemonic() throws Exception {
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        Assert.assertEquals(FROM_SK, account.toMnemonic());
    }

    private MultisigAddress makeTestMsigAddr() throws Exception {
        Address one = new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA");
        Address two = new Address("BFRTECKTOOE7A5LHCF3TTEOH2A7BW46IYT2SX5VP6ANKEXHZYJY77SJTVM");
        Address three = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        return new MultisigAddress(1, 2, Arrays.asList(
                new Ed25519PublicKey(one.getBytes()),
                new Ed25519PublicKey(two.getBytes()),
                new Ed25519PublicKey(three.getBytes())
        ));
    }

    @Test
    public void testSignMultisigTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();

        // build unsigned transaction
        Transaction tx = new Transaction(
                new Address(addr.toString()),
                BigInteger.valueOf(217000), // fee
                BigInteger.valueOf(972508), // first valid
                BigInteger.valueOf(973508), // last valid
                Encoder.decodeFromBase64("tFF5Ofz60nE="), // note
                BigInteger.valueOf(5000), // amount
                new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA"), // receiver
                "testnet-v31.0", // genesisID
                new Digest() // genesisHash
        );

        byte[] seed = Mnemonic.toKey("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
        Account account = new Account(seed);
        SignedTransaction stx = account.signMultisigTransaction(addr, tx);
        byte[] enc = Encoder.encodeToMsgPack(stx);

        // check the bytes convenience function is correct
        Assert.assertArrayEquals(enc, account.signMultisigTransactionBytes(addr, tx));

        // check main signature is correct
        byte[] golden = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWjcGF5");
        Assert.assertArrayEquals(golden, enc);
    }

    @Test
    public void testAppendMultisigTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] firstTxBytes = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWjcGF5");

        byte[] seed = Mnemonic.toKey("since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left");
        Account account = new Account(seed);
        byte[] appended = account.appendMultisigTransactionBytes(addr, firstTxBytes);
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIKicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQE4cdVDpoVoVVokXRGz6O9G3Ojljd+kd6d2AahXLPGDPtT/QA9DI1rB4w8cEDTy7gd5Padkn5EZC2pjzGh0McAeBonBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaN0aHICoXYBo3R4bomjYW10zROIo2ZlZc4AA0+oomZ2zgAO1tyjZ2VurXRlc3RuZXQtdjMxLjCibHbOAA7axKRub3RlxAi0UXk5/PrScaNyY3bEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4o3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaNwYXk=");
        Assert.assertArrayEquals(expected, appended);
    }

    @Test
    public void testSignMultisigKeyRegTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] encKeyRegTx = Encoder.decodeFromBase64("gaN0eG6Jo2ZlZc4AA8jAomZ2zgAO+dqibHbOAA79wqZzZWxrZXnEIDISKyvWPdxTMZYXpapTxLHCb+PcyvKNNiK1aXehQFyGo3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaZrZXlyZWemdm90ZWtkzScQp3ZvdGVrZXnEIHAb1/uRKwezCBH/KB2f7pVj5YAuICaJIxklj3f6kx6Ip3ZvdGVsc3TOAA9CQA==");
        SignedTransaction wrappedTx = Encoder.decodeFromMsgPack(encKeyRegTx, SignedTransaction.class);

        byte[] seed = Mnemonic.toKey("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
        Account account = new Account(seed);
        SignedTransaction stx = account.signMultisigTransaction(addr, wrappedTx.tx);
        byte[] enc = Encoder.encodeToMsgPack(stx);

        // check main signature is correct
        byte[] golden = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2ZlZc4AA8jAomZ2zgAO+dqibHbOAA79wqZzZWxrZXnEIDISKyvWPdxTMZYXpapTxLHCb+PcyvKNNiK1aXehQFyGo3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaZrZXlyZWemdm90ZWtkzScQp3ZvdGVrZXnEIHAb1/uRKwezCBH/KB2f7pVj5YAuICaJIxklj3f6kx6Ip3ZvdGVsc3TOAA9CQA==");
        Assert.assertArrayEquals(golden, enc);
    }

    @Test
    public void testAppendMultisigKeyRegTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] firstTxBytes = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2ZlZc4AA8jAomZ2zgAO+dqibHbOAA79wqZzZWxrZXnEIDISKyvWPdxTMZYXpapTxLHCb+PcyvKNNiK1aXehQFyGo3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaZrZXlyZWemdm90ZWtkzScQp3ZvdGVrZXnEIHAb1/uRKwezCBH/KB2f7pVj5YAuICaJIxklj3f6kx6Ip3ZvdGVsc3TOAA9CQA==");

        byte[] seed = Mnemonic.toKey("advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor");
        Account account = new Account(seed);
        byte[] appended = account.appendMultisigTransactionBytes(addr, firstTxBytes);
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        Assert.assertArrayEquals(expected, appended);
    }

    @Test
    public void testMergeMultisigTransaction() throws Exception {
        byte[] firstAndThird = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        byte[] secondAndThird = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgaJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXiConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxEC/jqaH0Dvo3Fa0ZVXsQAP8M5UL9+JxzWipDnA1wmApqllyuZHkZNwG0eSY+LDKMBoB2WaYcJNWypJi4l1f6aIPgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4KicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQL+OpofQO+jcVrRlVexAA/wzlQv34nHNaKkOcDXCYCmqWXK5keRk3AbR5Jj4sMowGgHZZphwk1bKkmLiXV/pog+ConBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaFzxECshVlZrJ6hvMpK/7OkkmZuuOyCVjknT3/UpTftPlxKXn3mYyi2o7trYebPRdpHGhLqlWGxzZhKQyJT9iEckJwDo3RocgKhdgGjdHhuiaNmZWXOAAPIwKJmds4ADvnaomx2zgAO/cKmc2Vsa2V5xCAyEisr1j3cUzGWF6WqU8Sxwm/j3MryjTYitWl3oUBchqNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWma2V5cmVnpnZvdGVrZM0nEKd2b3Rla2V5xCBwG9f7kSsHswgR/ygdn+6VY+WALiAmiSMZJY93+pMeiKd2b3RlbHN0zgAPQkA=");
        byte[] a = Account.mergeMultisigTransactionBytes(firstAndThird, secondAndThird);
        byte[] b = Account.mergeMultisigTransactionBytes(secondAndThird, firstAndThird);
        Assert.assertArrayEquals(a, b);
        Assert.assertArrayEquals(expected, a);
    }

    @Test
    public void testSignBytes() throws Exception {
        byte[] b = new byte[15];
        new Random().nextBytes(b);
        Account account = new Account();
        Signature signature = account.signBytes(b);
        Assert.assertTrue(account.getAddress().verifyBytes(b, signature));
        int firstByte = (int) b[0];
        firstByte = (firstByte+1) % 256;
        b[0] = (byte) firstByte;
        Assert.assertFalse(account.getAddress().verifyBytes(b, signature));
    }

    @Test
    public void testVerifyBytes() throws Exception {
        byte[] message = Encoder.decodeFromBase64("rTs7+dUj");
        Signature signature = new Signature(Encoder.decodeFromBase64("COEBmoD+ysVECoyVOAsvMAjFxvKeQVkYld+RSHMnEiHsypqrfj2EdYqhrm4t7dK3ZOeSQh3aXiZK/zqQDTPBBw=="));
        Address address = new Address("DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA");
        Assert.assertTrue(address.verifyBytes(message, signature));
        int firstByte = (int) message[0];
        firstByte = (firstByte+1) % 256;
        message[0] = (byte) firstByte;
        Assert.assertFalse(address.verifyBytes(message, signature));
    }

    @Test
    public void testLogicsigTransaction() throws Exception {
        Address from = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        Address to = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");
        String mn = "advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor";
        Account account = new Account(mn);

        BigInteger fee = BigInteger.valueOf(1000);
        BigInteger amount = BigInteger.valueOf(2000);
        String genesisID = "devnet-v1.0";
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("sC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0E"));
        BigInteger firstRound = BigInteger.valueOf(2063137);
        byte[] note = Encoder.decodeFromBase64("8xMCTuLQ810=");

        Transaction tx = new Transaction(
            from, fee, firstRound, firstRound.add(BigInteger.valueOf(1000)),
            note, genesisID, genesisHash, amount, to, null
        );

        /*
        {
        "lsig": {
            "arg": [
            "MTIz",
            "NDU2"
            ],
            "l": "// version 1\nintcblock 1\nintc_0\n",
            "sig": "ToddojkrSVyrnSj/LdtY5izLD1MuL+iitkHjFo12fVnXjfnW7Z/olM43jvx+X4mEg/gc1FEAiH8jwRZcE+klDQ=="
        },
        "txn": {
            "amt": 2000,
            "fee": 1000,
            "fv": 2063137,
            "gen": "devnet-v1.0",
            "gh": "sC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0E=",
            "lv": 2064137,
            "note": "8xMCTuLQ810=",
            "rcv": "PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI",
            "snd": "47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU",
            "type": "pay"
            }
        }
        */
        String goldenTx = "gqRsc2lng6NhcmeSxAMxMjPEAzQ1NqFsxAUBIAEBIqNzaWfEQE6HXaI5K0lcq50o/y3bWOYsyw9TLi/oorZB4xaNdn1Z14351u2f6JTON478fl+JhIP4HNRRAIh/I8EWXBPpJQ2jdHhuiqNhbXTNB9CjZmVlzQPoomZ2zgAfeyGjZ2Vuq2Rldm5ldC12MS4womdoxCCwLc/t7ZJ1uookrS1uIJ0r211Klt7pd4IYp2g3OaWPQaJsds4AH38JpG5vdGXECPMTAk7i0PNdo3JjdsQge2ziT+tbrMCxZOKcIixX9fY9w4fUOQSCWEEcX+EPfAKjc25kxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaR0eXBlo3BheQ==";

        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        ArrayList<byte[]> args = new ArrayList<byte[]>();
        byte[] arg1 = {49, 50, 51};
        byte[] arg2 = {52, 53, 54};
        args.add(arg1);
        args.add(arg2);

        LogicsigSignature lsig = new LogicsigSignature(program, args);
        account.signLogicsig(lsig);
        SignedTransaction stx = Account.signLogicsigTransaction(lsig, tx);

        Assert.assertTrue(Arrays.equals(Encoder.encodeToMsgPack(stx), Encoder.decodeFromBase64(goldenTx)));
    }
}
