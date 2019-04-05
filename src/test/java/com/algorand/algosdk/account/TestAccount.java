package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.security.Security;

public class TestAccount {

    @BeforeClass
    public static void setup() {
        // add bouncy castle provider for crypto
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

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
                BigInteger.valueOf(107575)
        );
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
                BigInteger.valueOf(107575)
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
}
