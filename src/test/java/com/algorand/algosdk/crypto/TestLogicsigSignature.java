package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.util.Encoder;

public class TestLogicsigSignature {
    @Test(expected = NullPointerException.class)
    public void testLogicsigEmptyCreation() throws Exception {
        new LogicsigSignature(null, null);
        Assert.assertTrue(false);
    }

    @Test
    public void testLogicsigCreation() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();
        String programHash = "6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY";
        Address sender = new Address(programHash);

        LogicsigSignature lsig = new LogicsigSignature(program, null);
        Assert.assertEquals(program, lsig.logic);
        Assert.assertEquals(null, lsig.args);
        Assert.assertEquals(null, lsig.sig);
        Assert.assertEquals(null, lsig.msig);
        boolean verified = lsig.verify(sender);
        Assert.assertTrue(verified);
        Assert.assertEquals(sender, lsig.toAddress());

        byte[] arg1 = {1, 2, 3};
        byte[] arg2 = {4, 5, 6};
        args.add(arg1);
        args.add(arg2);

        lsig = new LogicsigSignature(program, args);
        Assert.assertEquals(program, lsig.logic);
        Assert.assertEquals(args, lsig.args);
        Assert.assertEquals(null, lsig.sig);
        Assert.assertEquals(null, lsig.msig);
        verified = lsig.verify(sender);
        Assert.assertTrue(verified);

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        Assert.assertTrue(lsig.equals(lsig1));

        // check serialization with null args
        lsig = new LogicsigSignature(program, null);
        outBytes = Encoder.encodeToMsgPack(lsig);
        lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        Assert.assertTrue(lsig.equals(lsig1));

        // check modified program fails on verification
        program[3] = 2;
        lsig = new LogicsigSignature(program, null);
        verified = lsig.verify(sender);
        Assert.assertFalse(verified);
        Assert.assertTrue(jsonSerializeDeserializeCheck(lsig));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogicsigInvalidProgramCreation() throws Exception {
        byte[] program = {
            0x02, 0x20, 0x01, 0x01, 0x22
        };
        new LogicsigSignature(program, null);
        Assert.assertTrue(false);
    }

    @Test
    public void testLogicsigSignature() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        LogicsigSignature lsig = new LogicsigSignature(program, null);
        Account account = new Account();
        lsig = account.signLogicsig(lsig);
        Assert.assertEquals(program, lsig.logic);
        Assert.assertEquals(null, lsig.args);
        Assert.assertNotEquals(null, lsig.sig);
        Assert.assertNotEquals(new Signature(), lsig.sig);
        Assert.assertEquals(null, lsig.msig);
        boolean verified = lsig.verify(account.getAddress());
        Assert.assertTrue(verified);

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        Assert.assertTrue(lsig.equals(lsig1));
        Assert.assertTrue(jsonSerializeDeserializeCheck(lsig));
    }

    @Test
    public void testLogicsigMultisigSignature() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        Address one = new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA");
        Address two = new Address("BFRTECKTOOE7A5LHCF3TTEOH2A7BW46IYT2SX5VP6ANKEXHZYJY77SJTVM");
        Address three = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        MultisigAddress ma = new MultisigAddress(
            1, 2, Arrays.asList(
                new Ed25519PublicKey(one.getBytes()),
                new Ed25519PublicKey(two.getBytes()),
                new Ed25519PublicKey(three.getBytes())
            )
        );

        String mn1 = "auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch";
        String mn2 = "since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left";
        String mn3 = "advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor";
        Account acc1 = new Account(mn1);
        Account acc2 = new Account(mn2);
        Account acc3 = new Account(mn3);
        Account account = new Account();

        LogicsigSignature lsig = new LogicsigSignature(program, null);
        lsig = acc1.signLogicsig(lsig, ma);
        Assert.assertEquals(program, lsig.logic);
        Assert.assertEquals(null, lsig.args);
        Assert.assertEquals(null, lsig.sig);
        Assert.assertNotEquals(null, lsig.msig);
        Assert.assertNotEquals(new MultisigSignature(), lsig.msig);
        boolean verified = lsig.verify(ma.toAddress());
        Assert.assertFalse(verified);

        boolean caught = false;
        try {
            account.appendToLogicsig(lsig);
            Assert.fail();
        } catch(IllegalArgumentException ex) {
            caught = true;
        }
        Assert.assertTrue(caught);

        lsig = acc2.appendToLogicsig(lsig);
        verified = lsig.verify(ma.toAddress());
        Assert.assertTrue(verified);

        // Add a single signature and ensure it fails
        LogicsigSignature lsig1 = new LogicsigSignature(program, null);
        lsig1 = account.signLogicsig(lsig1);
        lsig.sig = lsig1.sig;
        verified = lsig.verify(ma.toAddress());
        Assert.assertFalse(verified);
        verified = lsig.verify(account.getAddress());
        Assert.assertFalse(verified);

        // Remove and ensure it still works
        lsig.sig = null;
        verified = lsig.verify(ma.toAddress());
        Assert.assertTrue(verified);

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig2 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        Assert.assertTrue(lsig.equals(lsig2));
        verified = lsig2.verify(ma.toAddress());
        Assert.assertTrue(verified);
        Assert.assertTrue(jsonSerializeDeserializeCheck(lsig2));
    }

    private static boolean jsonSerializeDeserializeCheck(LogicsigSignature lsig) {
        String encoded, encoded2;
        try {
            encoded = Encoder.encodeToJson(lsig);
            ObjectMapper om = new ObjectMapper();
            LogicsigSignature decodedLogicSignature = om.readerFor(lsig.getClass()).readValue(encoded.getBytes());
            Assert.assertEquals(lsig, decodedLogicSignature);
            encoded2 = Encoder.encodeToJson(decodedLogicSignature);
            Assert.assertEquals(encoded, encoded2);
            LogicsigSignature decodedLogicSignature2 = om.readerFor(lsig.getClass()).readValue(encoded2.getBytes());
            Assert.assertEquals(decodedLogicSignature, decodedLogicSignature2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
