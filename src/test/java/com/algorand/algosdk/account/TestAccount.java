package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.google.common.primitives.Bytes;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAccount {
    @Test
    public void testSignsTransactionE2E() throws Exception {
        final String REF_SIG_TXN = "82a3736967c4403f5a5cbc5cb038b0d29a53c0adf8a643822da0e41681bcab050e406fd40af20aa56a2f8c0e05d3bee8d4e8489ef13438151911b31b5ed5b660cac6bae4080507a374786e87a3616d74cd04d2a3666565cd03e8a26676ce0001a04fa26c76ce0001a437a3726376c4207d3f99e53d34ae49eb2f458761cf538408ffdaee35c70d8234166de7abe3e517a3736e64c4201bd63dc672b0bb29d42fcafa3422a4d385c0c8169bb01595babf8855cf596979a474797065a3706179";
        final String REF_TX_ID = "BXSNCHKYEXB4AQXFRROUJGZ4ZWD7WL2F5D27YUPFR7ONDK5TMN5Q";
        final String FROM_ADDR = "DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA";
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        final String TO_ADDR = "PU7ZTZJ5GSXET2ZPIWDWDT2TQQEP7WXOGXDQ3ARUCZW6PK7D4ULSE6NYCE";

        // build unsigned transaction
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(FROM_ADDR)
                .receiver(TO_ADDR)
                .flatFee(Account.MIN_TX_FEE_UALGOS)
                .amount(1234)
                .firstValid(106575)
                .lastValid(107575)
                .genesisHash(new Digest())
                .build();

        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        // make sure public key generated from mnemonic is correct
        assertThat(new Address(account.getClearTextPublicKey()).toString()).isEqualTo(FROM_ADDR);
        // make sure address was also correctly computed
        assertThat(account.getAddress().toString()).isEqualTo(FROM_ADDR);

        // sign the transaction
        SignedTransaction signedTx = account.signTransaction(tx);

        byte[] signedTxBytes = Encoder.encodeToMsgPack(signedTx);
        String signedTxHex = Encoder.encodeToHexStr(signedTxBytes);
        assertThat(signedTxHex).isEqualTo(REF_SIG_TXN);

        // verify transaction ID
        String txID = signedTx.transactionID;
        assertThat(txID).isEqualTo(REF_TX_ID);
    }

    @Test
    public void testSignsTransactionZeroValE2E() throws Exception {
        final String REF_SIG_TXN = "82a3736967c440fc12c24dc9d7c48ff0bfb3464c3f4d429088ffe98353a844ba833fd32aaef577e78b49e2674f9998fa5ddfc49db52d8e0c258cafdb5d55ab73edd6678d4b230ea374786e86a3616d74cd04d2a3666565cd03e8a26c76ce0001a437a3726376c4207d3f99e53d34ae49eb2f458761cf538408ffdaee35c70d8234166de7abe3e517a3736e64c4201bd63dc672b0bb29d42fcafa3422a4d385c0c8169bb01595babf8855cf596979a474797065a3706179";
        final String REF_TX_ID = "LH7ZXC6OO2LMDSDUIGA42WTILX7TX2K6HE4JVHGAR2UFYU6JZQOA";
        final String FROM_ADDR = "DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA";
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        final String TO_ADDR = "PU7ZTZJ5GSXET2ZPIWDWDT2TQQEP7WXOGXDQ3ARUCZW6PK7D4ULSE6NYCE";

        // build unsigned transaction
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(FROM_ADDR)
                .receiver(TO_ADDR)
                .flatFee(Account.MIN_TX_FEE_UALGOS)
                .amount(1234)
                .firstValid(0)
                .lastValid(107575)
                .genesisHash(new Digest())
                .build();
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        // make sure public key generated from mnemonic is correct
        assertThat(new Address(account.getClearTextPublicKey()).toString()).isEqualTo(FROM_ADDR);
        // make sure address was also correctly computed
        assertThat(account.getAddress().toString()).isEqualTo(FROM_ADDR);

        // sign the transaction
        SignedTransaction signedTx = account.signTransaction(tx);
        byte[] signedTxBytes = Encoder.encodeToMsgPack(signedTx);
        String signedTxHex = Encoder.encodeToHexStr(signedTxBytes);
        assertThat(signedTxHex).isEqualTo(REF_SIG_TXN);

        // verify transaction ID
        String txID = signedTx.transactionID;
        assertThat(txID).isEqualTo(REF_TX_ID);
    }

    @Test
    public void testGenerateAccount() throws Exception {
        for (int i = 0; i < 100; i++) {
            Account account = new Account();
            assertThat(account.getClearTextPublicKey()).isNotEmpty();
            assertThat(account.getAddress()).isNotNull();
            assertThat(account.getClearTextPublicKey()).isEqualTo(account.getAddress().getBytes());

            byte[] testSignMessage = ("some test message").getBytes(StandardCharsets.UTF_8);
            Signature sig = account.signBytes(testSignMessage);
            assertThat(account.getAddress().verifyBytes(testSignMessage, sig)).isTrue();

            Account anotherAccount = new Account();
            assertThat(account.equals(anotherAccount)).isFalse();
        }
    }

    @Test
    public void testAccountFromPrivateKey() throws Exception {
        final String FROM_SK = "olympic cricket tower model share zone grid twist sponsor avoid eight apology patient party success claim famous rapid donor pledge bomb mystery security ability often";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account exampleAccount = new Account(seed);
        KeyPairGenerator gen = KeyPairGenerator.getInstance("Ed25519");
        gen.initialize(256, new FixedSecureRandom(seed));
        KeyPair kp = gen.generateKeyPair();

        Account ac0 = new Account(kp);
        assertThat(exampleAccount).isEqualTo(ac0);

        Account ac1 = new Account(kp.getPrivate());
        assertThat(exampleAccount).isEqualTo(ac1);
    }

    @Test
    public void testToMnemonic() throws Exception {
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        assertThat(account.toMnemonic()).isEqualTo(FROM_SK);
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
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(addr.toString())
                .flatFee(217000)
                .firstValid(972508)
                .lastValid(973508)
                .noteB64("tFF5Ofz60nE=")
                .amount(5000)
                .receiver("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA")
                .genesisID("testnet-v31.0")
                .genesisHash(new Digest())
                .build();

        // generate an account for address DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA
        byte[] seed = Mnemonic.toKey("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
        Account account = new Account(seed);
        SignedTransaction stx = account.signMultisigTransaction(addr, tx);
        // if we are signing transaction from one of the multi-sig address, no auth address should be assigned
        assertThat(stx.authAddr).isEqualTo(new Address());

        byte[] enc = Encoder.encodeToMsgPack(stx);

        // check the bytes convenience function is correct
        assertThat(account.signMultisigTransactionBytes(addr, tx)).isEqualTo(enc);

        // check main signature is correct
        byte[] golden = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWjcGF5");
        assertThat(enc).isEqualTo(golden);

        // if there is only 1 party signed in multi-sig, verification should be false (less than threshold number)
        assertThat(stx.mSig.verify(tx.bytesToSign())).isFalse();

        // decode and verify
        SignedTransaction stxFromBytes = Encoder.decodeFromMsgPack(golden, SignedTransaction.class);
        assertThat(stxFromBytes.authAddr).isEqualTo(new Address());
        assertThat(stxFromBytes.tx).isEqualTo(tx);
        assertThat(stxFromBytes.mSig.verify(tx.bytesToSign())).isFalse();
    }

    @Test
    public void testSignMultisigTransactionWithAuthAddr() throws Exception {
        MultisigAddress ma = makeTestMsigAddr();

        // build unsigned transaction
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU")
                .flatFee(217000)
                .firstValid(972508)
                .lastValid(973508)
                .noteB64("tFF5Ofz60nE=")
                .amount(5000)
                .receiver("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA")
                .genesisID("testnet-v31.0")
                .genesisHash(new Digest())
                .build();

        // generate an account for address DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA
        byte[] seed = Mnemonic.toKey("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
        Account signerAccount = new Account(seed);
        SignedTransaction stx = signerAccount.signMultisigTransaction(ma, tx);

        // send from 47YP... to DN7M..., rekeyed to DN7M's multi-sig address
        // auth addr should be DN7M's multi-sig address
        assertThat(stx.authAddr).isEqualTo(ma.toAddress());

        byte[] enc = Encoder.encodeToMsgPack(stx);

        // check the bytes convenience function is correct
        assertThat(signerAccount.signMultisigTransactionBytes(ma, tx)).isEqualTo(enc);

        // check main signature is correct
        byte[] golden = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAPtPyJUVhOEpyylkr18r6Bw4Bsc48LtiMcICnneKI96Kjye6IAAtsLGoHylMiywx4gs1CyXZqcu0/fIpHR6+3AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaRzZ25yxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEIOfw+E0GgR358xyNh4sRVfRnHVGhhcIAkIZn9ElYcGihpHR5cGWjcGF5");
        assertThat(enc).isEqualTo(golden);

        // since there is only 1 party signed in multi-sig, verification should be false (less than threshold number)
        assertThat(stx.mSig.verify(tx.bytesToSign())).isFalse();

        // decode and verify
        SignedTransaction stxFromBytes = Encoder.decodeFromMsgPack(golden, SignedTransaction.class);
        assertThat(stxFromBytes.authAddr).isEqualTo(ma.toAddress());
        assertThat(stxFromBytes.tx).isEqualTo(tx);
        assertThat(stxFromBytes.mSig.verify(tx.bytesToSign())).isFalse();
    }

    @Test
    public void testAppendMultisigTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] firstTxBytes = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWjcGF5");

        byte[] seed = Mnemonic.toKey("since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left");
        Account account = new Account(seed);
        byte[] appended = account.appendMultisigTransactionBytes(addr, firstTxBytes);
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAdvZ3y9GsInBPutdwKc7Jy+an13CcjSV1lcvRAYQKYOxXwfgT5B/mK14R57ueYJTYyoDO8zBY6kQmBalWkm95AIKicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQE4cdVDpoVoVVokXRGz6O9G3Ojljd+kd6d2AahXLPGDPtT/QA9DI1rB4w8cEDTy7gd5Padkn5EZC2pjzGh0McAeBonBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaN0aHICoXYBo3R4bomjYW10zROIo2ZlZc4AA0+oomZ2zgAO1tyjZ2VurXRlc3RuZXQtdjMxLjCibHbOAA7axKRub3RlxAi0UXk5/PrScaNyY3bEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4o3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaNwYXk=");
        assertThat(appended).isEqualTo(expected);

        // decode and verify
        SignedTransaction stxFromBytes = Encoder.decodeFromMsgPack(expected, SignedTransaction.class);
        assertThat(stxFromBytes.authAddr).isEqualTo(new Address());
        // now it has 2 party signed, reach threshold and should be verified as true
        byte[] bytesToSign = stxFromBytes.tx.bytesToSign();
        boolean verified = stxFromBytes.mSig.verify(bytesToSign);
        assertThat(verified).isTrue();
        // change fee component in bytes, signature verification cannot match
        int feeIndex = Bytes.indexOf(bytesToSign, ("fee").getBytes(StandardCharsets.UTF_8));
        bytesToSign[feeIndex + 4] = 1;
        assertThat(stxFromBytes.mSig.verify(bytesToSign)).isFalse();
    }

    @Test
    public void testAppendMultisigTransactionWithAuthAddr() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] firstTxBytes = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAPtPyJUVhOEpyylkr18r6Bw4Bsc48LtiMcICnneKI96Kjye6IAAtsLGoHylMiywx4gs1CyXZqcu0/fIpHR6+3AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaRzZ25yxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aN0eG6Jo2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womx2zgAO2sSkbm90ZcQItFF5Ofz60nGjcmN2xCAbfsCwS+pht5aQl+bL9AfhCKcFNR0LyYq+sSIJqKuBeKNzbmTEIOfw+E0GgR358xyNh4sRVfRnHVGhhcIAkIZn9ElYcGihpHR5cGWjcGF5");

        byte[] seed = Mnemonic.toKey("since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left");
        Account account = new Account(seed);
        byte[] appended = account.appendMultisigTransactionBytes(addr, firstTxBytes);
        byte[] expected = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAPtPyJUVhOEpyylkr18r6Bw4Bsc48LtiMcICnneKI96Kjye6IAAtsLGoHylMiywx4gs1CyXZqcu0/fIpHR6+3AIKicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQJyHbmtNvoe6ok5Ac3OH4umOWy5dJJM8WXIx4C8jBGqhDRZ+hDIWFA6ybwRbup85VBxGWMzEflEuDe6T9uxm+g+BonBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaN0aHICoXYBpHNnbnLEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfpo3R4bomjYW10zROIo2ZlZc4AA0+oomZ2zgAO1tyjZ2VurXRlc3RuZXQtdjMxLjCibHbOAA7axKRub3RlxAi0UXk5/PrScaNyY3bEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4o3NuZMQg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGkdHlwZaNwYXk=");
        assertThat(appended).isEqualTo(expected);

        // decode and verify
        SignedTransaction stxFromBytes = Encoder.decodeFromMsgPack(expected, SignedTransaction.class);
        assertThat(stxFromBytes.authAddr).isEqualTo(addr.toAddress());
        // now it has 2 party signed, reach threshold and should be verified as true
        byte[] bytesToSign = stxFromBytes.tx.bytesToSign();
        boolean verified = stxFromBytes.mSig.verify(bytesToSign);
        assertThat(verified).isTrue();
        // change fee component in bytes, signature verification cannot match
        int feeIndex = Bytes.indexOf(bytesToSign, ("fee").getBytes(StandardCharsets.UTF_8));
        bytesToSign[feeIndex + 4] = 1;
        assertThat(stxFromBytes.mSig.verify(bytesToSign)).isFalse();
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
        assertThat(enc).isEqualTo(golden);
    }

    @Test
    public void testAppendMultisigKeyRegTransaction() throws Exception {
        MultisigAddress addr = makeTestMsigAddr();
        byte[] firstTxBytes = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Jo2ZlZc4AA8jAomZ2zgAO+dqibHbOAA79wqZzZWxrZXnEIDISKyvWPdxTMZYXpapTxLHCb+PcyvKNNiK1aXehQFyGo3NuZMQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mkdHlwZaZrZXlyZWemdm90ZWtkzScQp3ZvdGVrZXnEIHAb1/uRKwezCBH/KB2f7pVj5YAuICaJIxklj3f6kx6Ip3ZvdGVsc3TOAA9CQA==");

        byte[] seed = Mnemonic.toKey("advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor");
        Account account = new Account(seed);
        byte[] appended = account.appendMultisigTransactionBytes(addr, firstTxBytes);
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        assertThat(appended).isEqualTo(expected);
    }

    @Test
    public void testMergeMultisigTransaction() throws Exception {
        byte[] firstAndThird = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4GicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        byte[] secondAndThird = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgaJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXiConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxEC/jqaH0Dvo3Fa0ZVXsQAP8M5UL9+JxzWipDnA1wmApqllyuZHkZNwG0eSY+LDKMBoB2WaYcJNWypJi4l1f6aIPgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RArIVZWayeobzKSv+zpJJmbrjsglY5J09/1KU37T5cSl595mMotqO7a2Hmz0XaRxoS6pVhsc2YSkMiU/YhHJCcA6N0aHICoXYBo3R4bomjZmVlzgADyMCiZnbOAA752qJsds4ADv3CpnNlbGtlecQgMhIrK9Y93FMxlhelqlPEscJv49zK8o02IrVpd6FAXIajc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlpmtleXJlZ6Z2b3Rla2TNJxCndm90ZWtlecQgcBvX+5ErB7MIEf8oHZ/ulWPlgC4gJokjGSWPd/qTHoindm90ZWxzdM4AD0JA");
        byte[] expected = Encoder.decodeFromBase64("gqRtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAujReoxR7FeTUTqgOn+rS20XOF3ENA+JrSgZ5yvrDPg3NQAzQzUXddB0PVvPRn490oVSQaHEIY05EDJXVBFPJD4KicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQL+OpofQO+jcVrRlVexAA/wzlQv34nHNaKkOcDXCYCmqWXK5keRk3AbR5Jj4sMowGgHZZphwk1bKkmLiXV/pog+ConBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaFzxECshVlZrJ6hvMpK/7OkkmZuuOyCVjknT3/UpTftPlxKXn3mYyi2o7trYebPRdpHGhLqlWGxzZhKQyJT9iEckJwDo3RocgKhdgGjdHhuiaNmZWXOAAPIwKJmds4ADvnaomx2zgAO/cKmc2Vsa2V5xCAyEisr1j3cUzGWF6WqU8Sxwm/j3MryjTYitWl3oUBchqNzbmTEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfppHR5cGWma2V5cmVnpnZvdGVrZM0nEKd2b3Rla2V5xCBwG9f7kSsHswgR/ygdn+6VY+WALiAmiSMZJY93+pMeiKd2b3RlbHN0zgAPQkA=");
        byte[] a = Account.mergeMultisigTransactionBytes(firstAndThird, secondAndThird);
        byte[] b = Account.mergeMultisigTransactionBytes(secondAndThird, firstAndThird);
        assertThat(a).isEqualTo(b);
        assertThat(a).isEqualTo(expected);

        SignedTransaction stx = Encoder.decodeFromMsgPack(a, SignedTransaction.class);
        assertThat(stx.authAddr).isEqualTo(new Address());
    }

    @Test
    public void testMergeMultisigTransactionWithAuthAddr() throws Exception {
        byte[] firstAndThird = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAPtPyJUVhOEpyylkr18r6Bw4Bsc48LtiMcICnneKI96Kjye6IAAtsLGoHylMiywx4gs1CyXZqcu0/fIpHR6+3AIGicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RAtg9YnPSv4sLt8brF1oZxGCK69m+8hHKEuM5a2LEsuj/wBSBE8oL/N4L8tPLZUxjS7FqeXQAoeUFpz8Z/c5mgBKN0aHICoXYBpHNnbnLEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfpo3R4bomjYW10zROIo2ZlZc4AA0+oomZ2zgAO1tyjZ2VurXRlc3RuZXQtdjMxLjCibHbOAA7axKRub3RlxAi0UXk5/PrScaNyY3bEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4o3NuZMQg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGkdHlwZaNwYXk=");
        byte[] secondAndThird = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgaJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXiConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxECch25rTb6HuqJOQHNzh+LpjlsuXSSTPFlyMeAvIwRqoQ0WfoQyFhQOsm8EW7qfOVQcRljMxH5RLg3uk/bsZvoPgqJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGhc8RAtg9YnPSv4sLt8brF1oZxGCK69m+8hHKEuM5a2LEsuj/wBSBE8oL/N4L8tPLZUxjS7FqeXQAoeUFpz8Z/c5mgBKN0aHICoXYBpHNnbnLEII2StImQAXOgTfpDWaNmamr86ixCoF3Zwfc+66VHgDfpo3R4bomjYW10zROIo2ZlZc4AA0+oomZ2zgAO1tyjZ2VurXRlc3RuZXQtdjMxLjCibHbOAA7axKRub3RlxAi0UXk5/PrScaNyY3bEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4o3NuZMQg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGkdHlwZaNwYXk=");
        byte[] expected = Encoder.decodeFromBase64("g6Rtc2lng6ZzdWJzaWeTgqJwa8QgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXihc8RAPtPyJUVhOEpyylkr18r6Bw4Bsc48LtiMcICnneKI96Kjye6IAAtsLGoHylMiywx4gs1CyXZqcu0/fIpHR6+3AIKicGvEIAljMglTc4nwdWcRdzmRx9A+G3PIxPUr9q/wGqJc+cJxoXPEQJyHbmtNvoe6ok5Ac3OH4umOWy5dJJM8WXIx4C8jBGqhDRZ+hDIWFA6ybwRbup85VBxGWMzEflEuDe6T9uxm+g+ConBrxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaFzxEC2D1ic9K/iwu3xusXWhnEYIrr2b7yEcoS4zlrYsSy6P/AFIETygv83gvy08tlTGNLsWp5dACh5QWnPxn9zmaAEo3RocgKhdgGkc2ducsQgjZK0iZABc6BN+kNZo2ZqavzqLEKgXdnB9z7rpUeAN+mjdHhuiaNhbXTNE4ijZmVlzgADT6iiZnbOAA7W3KNnZW6tdGVzdG5ldC12MzEuMKJsds4ADtrEpG5vdGXECLRReTn8+tJxo3JjdsQgG37AsEvqYbeWkJfmy/QH4QinBTUdC8mKvrEiCairgXijc25kxCDn8PhNBoEd+fMcjYeLEVX0Zx1RoYXCAJCGZ/RJWHBooaR0eXBlo3BheQ==");
        byte[] a = Account.mergeMultisigTransactionBytes(firstAndThird, secondAndThird);
        byte[] b = Account.mergeMultisigTransactionBytes(secondAndThird, firstAndThird);
        assertThat(a).isEqualTo(b);
        assertThat(a).isEqualTo(expected);

        SignedTransaction stx = Encoder.decodeFromMsgPack(a, SignedTransaction.class);
        assertThat(stx.authAddr).isEqualTo(makeTestMsigAddr().toAddress());
    }

    @Test
    public void testSignBytes() throws Exception {
        byte[] b = new byte[15];
        new Random().nextBytes(b);
        Account account = new Account();
        Signature signature = account.signBytes(b);
        assertThat(account.getAddress().verifyBytes(b, signature)).isTrue();
        int firstByte = b[0];
        firstByte = (firstByte+1) % 256;
        b[0] = (byte) firstByte;
        assertThat(account.getAddress().verifyBytes(b, signature)).isFalse();
    }

    @Test
    public void testVerifyBytes() throws Exception {
        byte[] message = Encoder.decodeFromBase64("rTs7+dUj");
        Signature signature = new Signature(Encoder.decodeFromBase64("COEBmoD+ysVECoyVOAsvMAjFxvKeQVkYld+RSHMnEiHsypqrfj2EdYqhrm4t7dK3ZOeSQh3aXiZK/zqQDTPBBw=="));
        Address address = new Address("DPLD3RTSWC5STVBPZL5DIIVE2OC4BSAWTOYBLFN2X6EFLT2ZNF4SMX64UA");
        assertThat(address.verifyBytes(message, signature)).isTrue();
        int firstByte = message[0];
        firstByte = (firstByte+1) % 256;
        message[0] = (byte) firstByte;
        assertThat(address.verifyBytes(message, signature)).isFalse();
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

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .flatFee(fee)
                .firstValid(firstRound)
                .lastValid(firstRound.longValue() + 1000)
                .note(note)
                .genesisID(genesisID)
                .genesisHash(genesisHash)
                .amount(amount)
                .receiver(to)
                .build();

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

        ArrayList<byte[]> args = new ArrayList<>();
        byte[] arg1 = {49, 50, 51};
        byte[] arg2 = {52, 53, 54};
        args.add(arg1);
        args.add(arg2);

        LogicsigSignature lsig = new LogicsigSignature(program, args);
        account.signLogicsig(lsig);
        SignedTransaction stx = Account.signLogicsigTransaction(lsig, tx);

        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx))).isEqualTo(goldenTx);
    }

    @Test
    public void testTealSign() throws Exception {
        byte[] data = Encoder.decodeFromBase64("Ux8jntyBJQarjKGF8A==");
        byte[] seed = Encoder.decodeFromBase64("5Pf7eGMA52qfMT4R4/vYCt7con/7U3yejkdXkrcb26Q=");
        byte[] prog = Encoder.decodeFromBase64("ASABASI=");
        Address addr = new Address("6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY");
        Account acc = new Account(seed);
        Signature sig1 = acc.tealSign(data, addr);
        Signature sig2 = acc.tealSignFromProgram(data, prog);

        assertThat(sig1).isEqualTo(sig2);

        byte[] prefix = ("ProgData").getBytes(StandardCharsets.UTF_8);
        byte[] rawAddr = addr.getBytes();
        byte[] message = new byte[prefix.length + rawAddr.length + data.length];
        ByteBuffer buf = ByteBuffer.wrap(message);
        buf.put(prefix).put(rawAddr).put(data);

        PublicKey pk = acc.getAddress().toVerifyKey();

        boolean verified;
        try {
            java.security.Signature sig = java.security.Signature.getInstance("EdDSA");
            sig.initVerify(pk);
            sig.update(buf.array());
            verified = sig.verify(sig1.getBytes());
        } catch (Exception ex) {
            verified = false;
        }
        assertThat(verified).isTrue();
    }

    @Test
    void testToSeed() throws Exception {
        final String FROM_SK = "actress tongue harbor tray suspect odor load topple vocal avoid ignore apple lunch unknown tissue museum once switch captain place lemon sail outdoor absent creek";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);
        assertThat(account.toSeed()).isEqualTo(seed);
    }

    public static class TestLogicSigTransaction {
        byte[] program;
        ArrayList<byte[]> args;
        String otherAddrStr, programHash;
        byte[] note;
        MultisigAddress ma;
        KeyPair maKp0, maKp1, maKp2;

        private KeyPair mnemonicToKeyPair(String mnemonic) throws GeneralSecurityException {
            byte[] seed = Mnemonic.toKey(mnemonic);
            KeyPairGenerator gen = KeyPairGenerator.getInstance("Ed25519");
            gen.initialize(256, new FixedSecureRandom(seed));
            return gen.generateKeyPair();
        }

        @BeforeEach
        void setUp() throws GeneralSecurityException {
            program = new byte[]{0x01, 0x20, 0x01, 0x01, 0x22};
            args = new ArrayList<>();
            byte[] arg0 = {0x01};
            byte[] arg1 = {0x02, 0x03};
            args.add(arg0);
            args.add(arg1);
            note = new byte[]{(byte) 180, 81, 121, 57, (byte) 252, (byte) 250, (byte) 210, 113};
            otherAddrStr = "WTDCE2FEYM2VB5MKNXKLRSRDTSPR2EFTIGVH4GRW4PHGD6747GFJTBGT2A";
            programHash = "6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY";


            Address one = new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA");
            Address two = new Address("BFRTECKTOOE7A5LHCF3TTEOH2A7BW46IYT2SX5VP6ANKEXHZYJY77SJTVM");
            Address three = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
            ma = new MultisigAddress(1, 2, Arrays.asList(
                    new Ed25519PublicKey(one.getBytes()),
                    new Ed25519PublicKey(two.getBytes()),
                    new Ed25519PublicKey(three.getBytes())
            ));
            maKp0 = mnemonicToKeyPair("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
            maKp1 = mnemonicToKeyPair("since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left");
            maKp2 = mnemonicToKeyPair("advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor");
        }

        private void testSign(LogicsigSignature lsig, Address sender, String expectedTxid, Address expectedAuthAddr, byte[] expectedBytes)
                throws GeneralSecurityException, IOException {
            Address to = new Address(otherAddrStr);

            BigInteger firstRound = BigInteger.valueOf(972508);

            Transaction tx = Transaction.PaymentTransactionBuilder()
                    .sender(sender)
                    .flatFee(BigInteger.valueOf(217000))
                    .firstValid(firstRound)
                    .lastValid(firstRound.longValue() + 1000)
                    .note(note)
                    .genesisID("testnet-v31.0")
                    .genesisHash(new Digest())
                    .amount(BigInteger.valueOf(5000))
                    .receiver(to)
                    .build();

            SignedTransaction stx = Account.signLogicsigTransaction(lsig, tx);
            byte[] stxBytes = Encoder.encodeToMsgPack(stx);
            assertThat(stxBytes).isEqualTo(expectedBytes);
            assertThat(stx.transactionID).isEqualTo(expectedTxid);
            assertThat(stx.authAddr).isEqualTo(expectedAuthAddr);
            assertThat(stx.lSig).isEqualTo(lsig);
            assertThat(stx.mSig).isEqualTo(new MultisigSignature());
            assertThat(stx.sig).isEqualTo(new Signature());
            assertThat(stx.lSig).isEqualTo(lsig);
        }

        @Test
        public void testNoSigContractAddr() throws GeneralSecurityException, IOException {
            LogicsigSignature lsigNoSig = new LogicsigSignature(program, args);
            Address programAddr = new Address(programHash);
            String expectedTxID = "IL5UCKXGWBA2MQ4YYFQKYC3BFCWO2KHZSNZWVDIXOOZS3AWVIQDA";
            Address expectedAuthAddr = new Address();
            String expectedByteEncoded = "QKSGY43JM6BKGYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFI3UPBXITI3BNV2M2E4IUNTGKZOOAABU7KFCMZ3M4AAO23OKGZ3FN2WXIZLTORXGK5BNOYZTCLRQUJWHNTQAB3NMJJDON52GLRAIWRIXSOP47LJHDI3SMN3MIIFUYYRGRJGDGVIPLCTN2S4MUI44T4ORBM2BVJ7BUNXDZZQ7X7HZRKRXG3TEYQQPM5RNVR23DGL5NQWJMGAGQBIHJEGXSUIS77T7W5QLE44K7HD7DLNEOR4XAZNDOBQXS";
            byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
            testSign(lsigNoSig, programAddr, expectedTxID, expectedAuthAddr, expectedBytes);
        }

        @Test
        public void testNoSigNotContractAddr() throws GeneralSecurityException, IOException {
            LogicsigSignature lsigNoSig = new LogicsigSignature(program, args);
            Address programAddr = new Address(programHash);
            String expectedTxID = "U4X24Q45MCZ6JSL343QNR3RC6AJO2NUDQXFCTNONIW3SU2AYQJOA";
            String expectedByteEncoded = "QOSGY43JM6BKGYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFJDTM5XHFRBA6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GW2G5DYN2E2GYLNOTGRHCFDMZSWLTQAANH2RITGO3HAADWW3SRWOZLOVV2GK43UNZSXILLWGMYS4MFCNR3M4AAO3LCKI3TPORS4ICFUKF4TT7H22JY2G4TDO3CCBNGGEJUKJQZVKD2YU3OUXDFCHHE7DUILGQNKPYNDNY6OMH57Z6MKUNZW4ZGEEC2MMITIUTBTKUHVRJW5JOGKEOOJ6HIQWNA2U7Q2G3R44YP37T4YVJDUPFYGLI3QMF4Q";
            byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
            testSign(lsigNoSig, new Address(otherAddrStr), expectedTxID, programAddr, expectedBytes);
        }

        @Test
        public void testSingleSigContractAddr() throws GeneralSecurityException, IOException {
            Account acc = new Account("olympic cricket tower model share zone grid twist sponsor avoid eight apology patient party success claim famous rapid donor pledge bomb mystery security ability often");
            LogicsigSignature lsig = new LogicsigSignature(program, args);
            acc.signLogicsig(lsig);
            String expectedTxID = "XPFTYDOV5RCL7K5EGTC32PSTRKFO3ITZIL64CRTYIJEPHXOTCCXA";
            String expectedByteEncoded = "QKSGY43JM6B2GYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFI3TNFT4IQB6AU6TSTP3CK6GK6M75IYYU64OUJIYWVJMRK7GZV5HMUW5RMAYPYQQKLNZERRISFXFMF2M2DYZVS4WYRNEFGIZSEI54R6OJ7AS5THAFI3UPBXITI3BNV2M2E4IUNTGKZOOAABU7KFCMZ3M4AAO23OKGZ3FN2WXIZLTORXGK5BNOYZTCLRQUJWHNTQAB3NMJJDON52GLRAIWRIXSOP47LJHDI3SMN3MIIFUYYRGRJGDGVIPLCTN2S4MUI44T4ORBM2BVJ7BUNXDZZQ7X7HZRKRXG3TEYQQF4Z2PDQFO53BXOGEY6YOHN725ESQZPE7CZEP2BBIWEY7DQVZ6UQVEOR4XAZNDOBQXS";
            byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
            testSign(lsig, acc.getAddress(), expectedTxID, new Address(), expectedBytes);
        }

        @Test
        public void testSingleSigNotContractAddr() throws GeneralSecurityException, IOException {
            Account acc = new Account("olympic cricket tower model share zone grid twist sponsor avoid eight apology patient party success claim famous rapid donor pledge bomb mystery security ability often");
            LogicsigSignature lsig = new LogicsigSignature(program, args);
            acc.signLogicsig(lsig);
            Transaction tx = Transaction.PaymentTransactionBuilder()
                    .sender(new Address(otherAddrStr))
                    .flatFee(BigInteger.valueOf(217000))
                    .firstValid(BigInteger.valueOf(972508))
                    .lastValid(BigInteger.valueOf(973508))
                    .note(note)
                    .genesisID("testnet-v31.0")
                    .genesisHash(new Digest())
                    .amount(BigInteger.valueOf(5000))
                    .receiver(new Address(otherAddrStr))
                    .build();
            Assertions.assertThrows(IllegalArgumentException.class, () -> Account.signLogicsigTransaction(lsig, tx));
        }

        @Test
        public void testMultiSigContractAddr() throws GeneralSecurityException, IOException {
            LogicsigSignature lsig = new LogicsigSignature(program, args);
            Account acc0 = new Account(maKp0.getPrivate());
            acc0.signLogicsig(lsig, ma);
            Account acc1 = new Account(maKp1.getPrivate());
            acc1.appendToLogicsig(lsig);
            String expectedTxID = "2I2QT3MGXNMB5IOTNWEQZWUJCHLJ5QFBYI264X5NWMUDKZXPZ5RA";
            String expectedByteEncoded = "QKSGY43JM6B2GYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFJDNONUWPA5GON2WE43JM6JYFITQNPCCAG36YCYEX2TBW6LJBF7GZP2APYIIU4CTKHILZGFL5MJCBGUKXALYUFZ4IQCJCO4ALUM6P4WBBAHWGN7BQVFHZ3VO4EG5XUJWLBF7SO3V6MDDCWI4UIYM5XXSHULUDNJJ3MH7543VJVSG6S2WD7ELXQWXWTTDLS6QFAVCOBV4IIAJMMZASU3TRHYHKZYRO44ZDR6QHYNXHSGE6UV7NL7QDKRFZ6OCOGQXHRCAMS6FLW7NSGREDVBKWZQPPYKKXGMZUUVTWFYVRTX4H5H6PSZCIEKK3KJ5L2CAKAQKC6TGTAYDEJHINI4LNI3MKVF6EBIP7U7OVCZQICMBUJYGXRBA47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ2G5DIOIBKC5QBUN2HQ3UJUNQW25GNCOEKGZTFMXHAAA2PVCRGM5WOAAHNNXFDM5SW5LLUMVZXI3TFOQWXMMZRFYYKE3DWZYAA5WWEURXG65DFYQELIULZHH6PVUTRUNZGG5WEEC2MMITIUTBTKUHVRJW5JOGKEOOJ6HIQWNA2U7Q2G3R44YP37T4YVI3TNZSMIIENSK2ITEABOOQE36SDLGRWM2TK7TVCYQVALXM4D5Z65OSUPABX5GSHI6LQMWRXAYLZ";
            byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
            testSign(lsig, ma.toAddress(), expectedTxID, new Address(), expectedBytes);
        }

        @Test
        public void testMultiSigNotContractAddr() throws GeneralSecurityException, IOException {
            LogicsigSignature lsig = new LogicsigSignature(program, args);
            Account acc0 = new Account(maKp0.getPrivate());
            acc0.signLogicsig(lsig, ma);
            Account acc1 = new Account(maKp1.getPrivate());
            acc1.appendToLogicsig(lsig);
            String expectedTxID = "U4X24Q45MCZ6JSL343QNR3RC6AJO2NUDQXFCTNONIW3SU2AYQJOA";
            String expectedByteEncoded = "QOSGY43JM6B2GYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFJDNONUWPA5GON2WE43JM6JYFITQNPCCAG36YCYEX2TBW6LJBF7GZP2APYIIU4CTKHILZGFL5MJCBGUKXALYUFZ4IQCJCO4ALUM6P4WBBAHWGN7BQVFHZ3VO4EG5XUJWLBF7SO3V6MDDCWI4UIYM5XXSHULUDNJJ3MH7543VJVSG6S2WD7ELXQWXWTTDLS6QFAVCOBV4IIAJMMZASU3TRHYHKZYRO44ZDR6QHYNXHSGE6UV7NL7QDKRFZ6OCOGQXHRCAMS6FLW7NSGREDVBKWZQPPYKKXGMZUUVTWFYVRTX4H5H6PSZCIEKK3KJ5L2CAKAQKC6TGTAYDEJHINI4LNI3MKVF6EBIP7U7OVCZQICMBUJYGXRBA47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ2G5DIOIBKC5QBURZWO3TSYQQI3EVURGIAC45AJX5EGWNDMZVGV7HKFRBKAXOZYH3T525FI6ADP2NDOR4G5CNDMFWXJTITRCRWMZLFZYAAGT5IUJTHNTQAB3LNZI3HMVXK25DFON2G4ZLUFV3DGMJOGCRGY5WOAAHNVRFENZXXIZOEBC2FC6JZ7T5NE4NDOJRXNRBAWTDCE2FEYM2VB5MKNXKLRSRDTSPR2EFTIGVH4GRW4PHGD6747GFKG43OMTCCBNGGEJUKJQZVKD2YU3OUXDFCHHE7DUILGQNKPYNDNY6OMH57Z6MKUR2HS4DFUNYGC6I";
            byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
            testSign(lsig, new Address(otherAddrStr), expectedTxID, ma.toAddress(), expectedBytes);
        }
    }
}
