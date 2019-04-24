package com.algorand.algosdk.crypto;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Random;

public class TestAddress {

    @Test
    public void testEncodeDecodeStr() throws Exception {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            byte[] randKey = new byte[32];
            r.nextBytes(randKey);
            Address addr = new Address(randKey);
            String addrStr = addr.encodeAsString();
            Address reencAddr = new Address(addrStr);
            Assert.assertEquals(addr, reencAddr);
        }
    }

    @Test
    public void testGoldenValues() {
        final String golden = "7777777777777777777777777777777777777777777777777774MSJUVU";
        byte[] bytes = new byte[32];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)0xff; // careful with signedness
        }
        Assert.assertEquals(golden, new Address(bytes).toString());
    }

    @Test
    public void testAddressSerializable() throws Exception {
        Address a = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(a);
        out.flush();
        byte[] outBytes = bos.toByteArray();
        bos.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(outBytes);
        ObjectInput in = null;
        in = new ObjectInputStream(bis);
        Address o = (Address)in.readObject();
        in.close();
        Assert.assertEquals(o, a);
        Assert.assertEquals("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ", o.encodeAsString());
    }
}
