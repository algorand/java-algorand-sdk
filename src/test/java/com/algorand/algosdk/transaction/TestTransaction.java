package com.algorand.algosdk.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.util.Encoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigInteger;

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
        tx.setFeeWithSuggestedFeePerByte(BigInteger.valueOf(10));

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        byte[] golden = Encoder.decodeFromBase64("iKRhcGFyhKFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFmxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFtxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFyxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRjYWlkgqFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFpzQTSo2ZlZc0OzqJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/3o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaRhY2Zn");
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);

        Assert.assertArrayEquals(outBytes, golden);
        Assert.assertEquals(tx, o);
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

        Transaction tx = Transaction.acceptAssetTransaction(
                recipient,
                BigInteger.valueOf(10),
                firstValidRound,
                lastValidRound,
                null,
                "",
                new Digest(gh),
                creator,
                assetIndex);

        tx.setFeeWithSuggestedFeePerByte(tx.fee);
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

        Transaction tx = Transaction.assetTransferTransaction(
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

        tx.setFeeWithSuggestedFeePerByte(tx.fee);
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
