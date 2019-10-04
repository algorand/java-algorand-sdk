package com.algorand.algosdk.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
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
        tx = Account.transactionWithSuggestedFeePerByte(tx, BigInteger.valueOf(10));

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        byte[] golden = Encoder.decodeFromBase64("iKRhcGFyhKFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFmxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFtxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFyxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRjYWlkgqFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFpzQTSo2ZlZc0OzqJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/3o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaRhY2Zn");
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);

        Assert.assertArrayEquals(outBytes, golden);
        Assert.assertEquals(tx, o);
    }

    @Test
    public void testSerializationAssetFreeze() throws Exception {
        // TODO evan fail for now
        Assert.assertEquals(true, false);
    }

}
