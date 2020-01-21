package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestMultisigAddress {
    @Test
    public void testToString() throws Exception {
        Address one = new Address("XMHLMNAVJIMAW2RHJXLXKKK4G3J3U6VONNO3BTAQYVDC3MHTGDP3J5OCRU");
        Address two = new Address("HTNOX33OCQI2JCOLZ2IRM3BC2WZ6JUILSLEORBPFI6W7GU5Q4ZW6LINHLA");
        Address three = new Address("E6JSNTY4PVCY3IRZ6XEDHEO6VIHCQ5KGXCIQKFQCMB2N6HXRY4IB43VSHI");

        MultisigAddress addr = new MultisigAddress(1, 2, Arrays.asList(
                new Ed25519PublicKey(one.getBytes()),
                new Ed25519PublicKey(two.getBytes()),
                new Ed25519PublicKey(three.getBytes())
        ));

        Assert.assertEquals("UCE2U2JC4O4ZR6W763GUQCG57HQCDZEUJY4J5I6VYY4HQZUJDF7AKZO5GM", addr.toString());

        Assert.assertTrue(jsonSerializeDeserializeCheck(addr));
    }

    private static boolean jsonSerializeDeserializeCheck(MultisigAddress msig) {
        String encoded, encoded2;
        try {
            encoded = Encoder.encodeToJson(msig);
            ObjectMapper om = new ObjectMapper();
            MultisigAddress multisigAddress1 = om.readerFor(msig.getClass()).readValue(encoded.getBytes());
            Assert.assertEquals(msig, multisigAddress1);
            encoded2 = Encoder.encodeToJson(multisigAddress1);
            Assert.assertEquals(encoded.trim(), encoded2.trim());
            MultisigAddress multisigAddress2 = om.readerFor(msig.getClass()).readValue(encoded2.getBytes());
            Assert.assertEquals(multisigAddress1, multisigAddress2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
