package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

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

        assertThat(addr.toString()).isEqualTo("UCE2U2JC4O4ZR6W763GUQCG57HQCDZEUJY4J5I6VYY4HQZUJDF7AKZO5GM");
        jsonSerializeDeserializeCheck(addr);
    }

    private static void jsonSerializeDeserializeCheck(MultisigAddress msig) {
        String encoded, encoded2;
        try {
            encoded = Encoder.encodeToJson(msig);
            ObjectMapper om = new ObjectMapper();
            MultisigAddress multisigAddress1 = om.readerFor(msig.getClass()).readValue(encoded.getBytes());
            assertThat(multisigAddress1).isEqualTo(msig);
            encoded2 = Encoder.encodeToJson(multisigAddress1);
            assertThat(encoded2).isEqualToIgnoringWhitespace(encoded);
            MultisigAddress multisigAddress2 = om.readerFor(msig.getClass()).readValue(encoded2.getBytes());
            Assert.assertEquals(multisigAddress1, multisigAddress2);
            assertThat(multisigAddress2).isEqualTo(multisigAddress1);
        } catch (Exception e) {
            fail("Should not throw an exception.", e);
        }
    }
}
