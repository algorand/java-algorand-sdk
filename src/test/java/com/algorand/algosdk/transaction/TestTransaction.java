package com.algorand.algosdk.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.util.Encoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;


public class TestTransaction {

    @Test
    public void testSerialization() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Address to = new Address("CQW2QBBUW5AGFDXMURQBRJN2AM3OHHQWXXI4PEJXRCVTEJ3E5VBTNRTEAE");
        Transaction tx = new Transaction(from, to, 100, 301, 1300, "", new Digest());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(tx);
        out.flush();
        byte[] outBytes = bos.toByteArray();
        bos.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(outBytes);
        ObjectInput in = null;
        in = new ObjectInputStream(bis);
        Transaction o = (Transaction) in.readObject();
        in.close();
        Assert.assertEquals(tx, o);
        Assert.assertEquals(tx.sender, o.sender);
        Assert.assertEquals(tx.receiver, o.receiver);
        Assert.assertEquals(tx.amount, o.amount);
        Assert.assertEquals(tx.lastValid, o.lastValid);
        Assert.assertEquals(tx.genesisHash, o.genesisHash);
    }

    @Test
    public void testSerializationMsgpack() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Address to = new Address("CQW2QBBUW5AGFDXMURQBRJN2AM3OHHQWXXI4PEJXRCVTEJ3E5VBTNRTEAE");
        Transaction tx = new Transaction(from, to, 100, 301, 1300, "", new Digest());

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);

        Assert.assertEquals(tx, o);
        Assert.assertEquals(tx.sender, o.sender);
        Assert.assertEquals(tx.receiver, o.receiver);
        Assert.assertEquals(tx.amount, o.amount);
        Assert.assertEquals(tx.lastValid, o.lastValid);
        Assert.assertEquals(tx.genesisHash, o.genesisHash);
    }

    @Test
    public void testSerializationAssetConfig() throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address creator = addr;
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;
        Address clawback = addr;

        Transaction tx = new Transaction(sender, BigInteger.valueOf(10), BigInteger.valueOf(322575), BigInteger.valueOf(323575), null, "", new Digest(gh), creator, BigInteger.valueOf(1234), manager, reserve, freeze, clawback);
        Account.transactionWithSuggestedFeePerByte(tx, BigInteger.valueOf(10));

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        byte[] golden = Encoder.decodeFromBase64("iKRhcGFyhKFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFmxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFtxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFyxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRjYWlkgqFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFpzQTSo2ZlZc0OzqJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/3o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaRhY2Zn");
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);

        Assert.assertArrayEquals(outBytes, golden);
        Assert.assertEquals(tx, o);
    }

    @Test
    public void testSerializationAssetFreeze() throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address creator = addr;
        Address target = addr;
        BigInteger index = BigInteger.valueOf(1);
        Transaction.AssetID assetFreezeID = new Transaction.AssetID(creator, index);
        boolean freezeState = true;
        Transaction tx = new Transaction(sender, BigInteger.valueOf(10), BigInteger.valueOf(322575), BigInteger.valueOf(323575), null,
                "", new Digest(gh), assetFreezeID, target, freezeState);
        Account.transactionWithSuggestedFeePerByte(tx, BigInteger.valueOf(10));
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        byte[] golden = Encoder.decodeFromBase64("iaRhZnJ6w6RmYWRkxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRmYWlkgqFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFpAaNmZWXNCqCiZnbOAATsD6JnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOAATv96NzbmTEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pHR5cGWkYWZyeg==");

        Assert.assertArrayEquals(outBytes, golden);
        Assert.assertEquals(tx, o);
        return;
    }

    @Test
    public void testTransactionGroup() throws Exception {
        Address from = new Address("UPYAFLHSIPMJOHVXU2MPLQ46GXJKSDCEMZ6RLCQ7GWB5PRDKJUWKKXECXI");
        Address to = new Address("UPYAFLHSIPMJOHVXU2MPLQ46GXJKSDCEMZ6RLCQ7GWB5PRDKJUWKKXECXI");
        BigInteger fee = BigInteger.valueOf(1000);
        BigInteger amount = BigInteger.valueOf(2000);
        String genesisID = "devnet-v1.0";
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("sC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0E"));
        BigInteger firstRound1 = BigInteger.valueOf(710399);
        byte[] note1 = Encoder.decodeFromBase64("wRKw5cJ0CMo=");

        Transaction tx1 = new Transaction(
            from, fee, firstRound1, firstRound1.add(BigInteger.valueOf(1000)),
            note1, genesisID, genesisHash, amount, to, null
        );

        BigInteger firstRound2 = BigInteger.valueOf(710515);
        byte[] note2 = Encoder.decodeFromBase64("dBlHI6BdrIg=");

        Transaction tx2 = new Transaction(
            from, fee, firstRound2, firstRound2.add(BigInteger.valueOf(1000)),
            note2, genesisID, genesisHash, amount, to, null
        );

        // check serialization/deserialization without group field
        Assert.assertEquals(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx1), Transaction.class), tx1);
        Assert.assertEquals(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx2), Transaction.class), tx2);

        String goldenTx1 = "gaN0eG6Ko2FtdM0H0KNmZWXNA+iiZnbOAArW/6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bomx2zgAK2uekbm90ZcQIwRKw5cJ0CMqjcmN2xCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKNzbmTEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0spHR5cGWjcGF5";
        String goldenTx2 = "gaN0eG6Ko2FtdM0H0KNmZWXNA+iiZnbOAArXc6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bomx2zgAK21ukbm90ZcQIdBlHI6BdrIijcmN2xCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKNzbmTEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0spHR5cGWjcGF5";

        // goal clerk send dumps unsigned transaction as signed with empty signature in order to save tx type
        SignedTransaction stx1 = new SignedTransaction(tx1, new Signature(), new MultisigSignature(), tx1.txID());
        SignedTransaction stx2 = new SignedTransaction(tx2, new Signature(), new MultisigSignature(), tx2.txID());

        Assert.assertTrue(Arrays.equals(Encoder.encodeToMsgPack(stx1), Encoder.decodeFromBase64(goldenTx1)));
        Assert.assertTrue(Arrays.equals(Encoder.encodeToMsgPack(stx2), Encoder.decodeFromBase64(goldenTx2)));

        Digest gid = TxGroup.computeGroupID(new Transaction[]{tx1, tx2});
        tx1.assignGroupID(gid);
        tx2.assignGroupID(gid);

        // check serialization/deserialization with group field set
        Assert.assertEquals(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx1), Transaction.class), tx1);
        Assert.assertEquals(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx2), Transaction.class), tx2);

        // goal clerk group sets Group to every transaction and concatenate them in output file
        // simulating that behavior here
        String goldenTxg = "gaN0eG6Lo2FtdM0H0KNmZWXNA+iiZnbOAArW/6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bo2dycMQgLiQ9OBup9H/bZLSfQUH2S6iHUM6FQ3PLuv9FNKyt09SibHbOAAra56Rub3RlxAjBErDlwnQIyqNyY3bEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0so3NuZMQgo/ACrPJD2Jcet6aY9cOeNdKpDERmfRWKHzWD18RqTSykdHlwZaNwYXmBo3R4boujYW10zQfQo2ZlZc0D6KJmds4ACtdzo2dlbqtkZXZuZXQtdjEuMKJnaMQgsC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0GjZ3JwxCAuJD04G6n0f9tktJ9BQfZLqIdQzoVDc8u6/0U0rK3T1KJsds4ACttbpG5vdGXECHQZRyOgXayIo3JjdsQgo/ACrPJD2Jcet6aY9cOeNdKpDERmfRWKHzWD18RqTSyjc25kxCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKR0eXBlo3BheQ==";
        stx1 = new SignedTransaction(tx1, new Signature(), new MultisigSignature(), tx1.txID());
        stx2 = new SignedTransaction(tx2, new Signature(), new MultisigSignature(), tx2.txID());
        byte[] stx1Enc = Encoder.encodeToMsgPack(stx1);
        byte[] stx2Enc = Encoder.encodeToMsgPack(stx2);
        byte[] concat = Arrays.copyOf(stx1Enc, stx1Enc.length + stx2Enc.length);
        System.arraycopy(stx2Enc, 0, concat, stx1Enc.length, stx2Enc.length);

        Assert.assertTrue(Arrays.equals(concat, Encoder.decodeFromBase64(goldenTxg)));

        // check assignGroupID
        Transaction[] result = TxGroup.assignGroupID(new Transaction[]{tx1, tx2}, null);
        Assert.assertEquals(result.length, 2);

        result = TxGroup.assignGroupID(new Transaction[]{tx1, tx2}, from);
        Assert.assertEquals(result.length, 2);

        result = TxGroup.assignGroupID(new Transaction[]{tx1, tx2}, to);
        Assert.assertEquals(result.length, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransactionGroupEmpty() throws IOException {
        TxGroup.computeGroupID(new Transaction[]{});
        Assert.fail("no expected exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransactionGroupNull() throws IOException {
        TxGroup.computeGroupID(null);
        Assert.fail("no expected exception");
    }
    
    @Test
    public void testMakeAssetAcceptanceTxn() throws Exception {

        final String FROM_SK = "awful drop leaf tennis indoor begin mandate discover uncle seven only coil atom any hospital uncover make any climb actor armed measure need above hundred";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address recipient = addr;
        Address creator = addr;

        BigInteger assetIndex = BigInteger.valueOf(1);
        BigInteger firstValidRound = BigInteger.valueOf(322575);
        BigInteger lastValidRound = BigInteger.valueOf(323575);

        Transaction tx = Transaction.createAcceptAssetTransaction(
                recipient,
                BigInteger.valueOf(10),
                firstValidRound,
                lastValidRound,
                null,
                "",
                new Digest(gh),
                creator,
                assetIndex);

        Account.transactionWithSuggestedFeePerByte(tx, tx.fee);
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        Assert.assertEquals(o,  tx);

        /*  Example from: go-algorand-sdk/transaction/transaction_test.go
         *  {
         *     "sig:b64": "0u7LDECdsm6RA0vqlX3w09zsDTNih1eYrwuIbISB64HMi4opQXdEZnWwJfbu31CWfkyTR+cGJ1aR7T7e4oHKDw==",
         *     "txn": {
         *       "arcv:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *       "fee": 2670,
         *       "fv": 322575,
         *       "gh:b64": "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=",
         *       "lv": 323575,
         *       "snd:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *       "type": "axfer",
         *       "xaid": {
         *         "c:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *         "i": 1
         *       }
         *     }
         *   }
         */
        SignedTransaction stx = account.signTransaction(tx);
        byte[] signedOutBytes = Encoder.encodeToMsgPack(stx);
        byte[] golden = Encoder.decodeFromBase64("gqNzaWfEQNLuywxAnbJukQNL6pV98NPc7A0zYodXmK8LiGyEgeuBzIuKKUF3RGZ1sCX27t9Qln5Mk0fnBidWke0+3uKByg+jdHhuiKRhcmN2xCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aNmZWXNCm6iZnbOAATsD6JnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOAATv96NzbmTEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pHR5cGWlYXhmZXKkeGFpZIKhY8QgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2haQE=");

        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(signedOutBytes, SignedTransaction.class);
        Assert.assertEquals(stx, stxDecoded);
        Assert.assertArrayEquals(signedOutBytes, golden);
    }


    @Test
    public void testMakeAssetTransferTxn() throws Exception {

        final String FROM_SK = "awful drop leaf tennis indoor begin mandate discover uncle seven only coil atom any hospital uncover make any climb actor armed measure need above hundred";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address recipient = addr;
        Address creator = addr;
        Address closeAssetsTo = addr;

        BigInteger assetIndex = BigInteger.valueOf(1);
        BigInteger firstValidRound = BigInteger.valueOf(322575);
        BigInteger lastValidRound = BigInteger.valueOf(323576);
        BigInteger amountToSend = BigInteger.valueOf(1);

        Transaction tx = Transaction.createTransferAssetTransaction(
                sender,
                recipient,
                closeAssetsTo,
                amountToSend,
                BigInteger.valueOf(10),
                firstValidRound,
                lastValidRound,
                null,
                "",
                new Digest(gh),
                creator,
                assetIndex);

        Account.transactionWithSuggestedFeePerByte(tx, tx.fee);
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        Assert.assertEquals(o,  tx);

        /*
         * Golden from: go-algorand-sdk/transaction/transaction_test.go
         *{
         *  "sig:b64": "aST0K284qefidTn3GY8ahm9i/7pMK7kH3k+DBD9jK3AT12vX316ESoCdJLQLZJo7hiEQECT4lU6LBmJGr/DWCA==",
         *  "txn": {
         *    "aamt": 1,
         *    "aclose:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *    "arcv:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *    "fee": 3140,
         *    "fv": 322575,
         *    "gh:b64": "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=",
         *    "lv": 323576,
         *    "snd:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *    "type": "axfer",
         *    "xaid": {
         *      "c:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
         *      "i": 1
         *    }
         *  }
         *}
         *
         * goal command:
         * goal asset send  -a 1
         * --from     BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4
         * --to       BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4
         * --creator  BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4
         * --close-to BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4
         * --assetid 1 -s
         */

        SignedTransaction stx = account.signTransaction(tx);
        byte[] signedOutBytes = Encoder.encodeToMsgPack(stx);
        byte[] golden = Encoder.decodeFromBase64("gqNzaWfEQGkk9CtvOKnn4nU59xmPGoZvYv+6TCu5B95PgwQ/YytwE9dr199ehEqAnSS0C2SaO4YhEBAk+JVOiwZiRq/w1gijdHhuiqRhYW10AaZhY2xvc2XEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pGFyY3bEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9o2ZlZc0MRKJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/4o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaVheGZlcqR4YWlkgqFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFpAQ==");
        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(signedOutBytes, SignedTransaction.class);

        Assert.assertEquals(stx, stxDecoded);
        Assert.assertArrayEquals(signedOutBytes, golden);
    }
}
