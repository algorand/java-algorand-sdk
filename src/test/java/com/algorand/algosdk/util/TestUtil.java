package com.algorand.algosdk.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestUtil {
    public static void serializeDeserializeCheck(Object object) {
        jsonSerializeTests(object);
        messagePackSerializeTests(object);
    }

    private static void jsonSerializeTests(Object object) {
        String encoded, encoded2;
        Object decoded, decoded2;
        try {
            encoded = Encoder.encodeToJson(object);
            decoded = Encoder.decodeFromJson(encoded, object.getClass());
            assertThat(decoded).isEqualTo(object);

            encoded2 = Encoder.encodeToJson(decoded);
            assertThat(encoded2).isEqualTo(encoded);

            decoded2 = Encoder.decodeFromJson(encoded2, decoded.getClass());
            assertThat(decoded2).isEqualTo(decoded);
        } catch (Exception e) {
            fail("Should not have thrown an exception.", e);
        }
    }

    private static void messagePackSerializeTests(Object object) {
        String encoded, encoded2;
        Object decoded, decoded2;
        try {
            encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(object));
            decoded = Encoder.decodeFromMsgPack(encoded, object.getClass());
            assertThat(decoded).isEqualTo(object);

            encoded2 = Encoder.encodeToBase64(Encoder.encodeToMsgPack(decoded));
            assertThat(encoded2).isEqualTo(encoded);

            decoded2 = Encoder.decodeFromMsgPack(encoded2, decoded.getClass());
            assertThat(decoded2).isEqualTo(decoded);
        } catch (Exception e) {
            fail("Should not have thrown an exception.", e);
        }
    }
}
