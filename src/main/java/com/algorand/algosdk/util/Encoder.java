package com.algorand.algosdk.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class Encoder {
    private static final char BASE32_PAD_CHAR = '=';
    /**
     * Convenience method for serializing arbitrary objects.
     * @return serialized object
     * @throws JsonProcessingException if serialization failed
     */
    public static byte[] encodeToMsgPack(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        // It is important to sort fields alphabetically to match the Algorand canonical encoding
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        // There's some odd bug in Jackson < 2.8.? where null values are not excluded. See:
        // https://github.com/FasterXML/jackson-databind/issues/1351. So we will
        // also annotate all fields manually
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        return objectMapper.writeValueAsBytes(o);
    }

    /**
     * Encode an object as json.
     * @param o object to encode
     * @return json string
     * @throws JsonProcessingException error
     */
    public static String encodeToJson(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(ObjectMapper.class.getPackage().getSpecificationVersion());
        // It is important to sort fields alphabetically to match the Algorand canonical encoding
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        return objectMapper.writeValueAsString(o);
    }

    /**
     * Convenience method for writing bytes as hex.
     * @param bytes input to encodeToMsgPack as hex string
     * @return encoded hex string
     */
    public static String encodeToHexStr(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    /**
     * Convenience method for writing bytes as base32
     * @param bytes input
     * @return base32 string with stripped whitespace
     */
    public static String encodeToBase32StripPad(byte[] bytes) {
        Base32 codec = new Base32((byte)BASE32_PAD_CHAR);
        String paddedStr =  codec.encodeToString(bytes);
        // strip padding
        int i = 0;
        for (; i < paddedStr.length(); i++) {
            if (paddedStr.charAt(i) == BASE32_PAD_CHAR) {
                break;
            }
        }
        return paddedStr.substring(0, i);
    }

    /**
     * Encode to base64 string. Does not strip padding.
     * @param bytes input
     * @return base64 string with appropriate padding
     */
    public static String encodeToBase64(byte[] bytes) {
        Base64 codec = new Base64();
        return codec.encodeToString(bytes);
    }
}
