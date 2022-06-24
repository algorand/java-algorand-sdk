package com.algorand.algosdk.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Encoder {
    private static final char BASE32_PAD_CHAR = '=';

    private final static ObjectMapper jsonMapper;
    private final static ObjectMapper msgpMapper;
    private final static ObjectMapper basicMapper = new ObjectMapper();

    /**
     * The length of an encoded uint64, in bytes.
     */
    public static final int UINT64_LENGTH = 8;

    /**
     * The maximum value that a uint64 can contain.
     */
    public static final BigInteger MAX_UINT64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);

    static {
        // It is important to sort fields alphabetically to match the Algorand canonical encoding

        // MessagePack
        msgpMapper = new ObjectMapper(new MessagePackFactory());
        msgpMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        msgpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        // There's some odd bug in Jackson < 2.8.? where null values are not excluded. See:
        // https://github.com/FasterXML/jackson-databind/issues/1351. So we will
        // also annotate all fields manually
        msgpMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        // JSON
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }
    /**
     * Convenience method for serializing arbitrary objects.
     * @return serialized object
     * @throws JsonProcessingException if serialization failed
     */
    public static byte[] encodeToMsgPack(Object o) throws JsonProcessingException {
        return msgpMapper.writeValueAsBytes(o);
    }

    /**
     * Convenience method for deserializing arbitrary objects encoded with canonical msg-pack
     * @param input base64 encoded byte array representing canonical msg-pack encoding
     * @param tClass class of type of object to deserialize as
     * @param <T> object type
     * @return deserialized object
     * @throws IOException if decoding failed
     */
    public static <T> T decodeFromMsgPack(String input, Class<T> tClass) throws IOException {
        return decodeFromMsgPack(decodeFromBase64(input), tClass);
    }

    /**
     * Convenience method for deserializing arbitrary objects encoded with canonical msg-pack
     * @param input byte array representing canonical msg-pack encoding
     * @param tClass class of type of object to deserialize as
     * @param <T> object type
     * @return deserialized object
     * @throws IOException if decoding failed
     */
    public static <T> T decodeFromMsgPack(byte[] input, Class<T> tClass) throws IOException {
        return msgpMapper.readValue(input, tClass);
    }

    /**
     * Encode an object as json.
     * @param o object to encode
     * @return json string
     * @throws JsonProcessingException error
     */
    public static String encodeToJson(Object o) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(o);
    }

    /**
     * Encode an object as json.
     * @param input json string to decode
     * @param tClass class to decode the json string into
     * @return object specified by tClass
     * @throws JsonProcessingException error
     */
    public static <T> T decodeFromJson(String input, Class<T> tClass) throws IOException {
        return basicMapper.readerFor(tClass).readValue(input);
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
     * Convenience method for decoding bytes from hex.
     * @param hexStr hex string to decode
     * @return byte array
     * @throws DecoderException
     */
    public static byte[] decodeFromHexStr(String hexStr) throws DecoderException {
        return Hex.decodeHex(hexStr);
    }

    /**
     * Convenience method for writing bytes as base32
     * @param bytes input
     * @return base32 string with stripped whitespace
     */
    public static String encodeToBase32StripPad(byte[] bytes) {
        Base32 codec = new Base32((byte)BASE32_PAD_CHAR);
        String paddedStr = codec.encodeToString(bytes);
        return StringUtils.stripEnd(paddedStr, String.valueOf(BASE32_PAD_CHAR));
    }

    /**
     * Convenience method for reading base32 back into bytes
     * @param base32 input string with optional padding.
     * @return bytes for base32 data
     */
    public static byte[] decodeFromBase32StripPad(String base32) {
        Base32 codec = new Base32((byte)BASE32_PAD_CHAR);
        return codec.decode(base32);
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

    /**
     * Decode from base64 string.
     * @param str input
     * @return decoded bytes
     */
    public static byte[] decodeFromBase64(String str) {
        Base64 codec = new Base64();
        return codec.decode(str);
    }

    /**
     * Encode an non-negative integer as a big-endian uint64 value.
     * @param value The value to encode.
     * @throws IllegalArgumentException if value is negative.
     * @return A byte array containing the big-endian encoding of the value. Its length will be Encoder.UINT64_LENGTH.
     */
    public static byte[] encodeUint64(BigInteger value) {
        return Encoder.encodeUintToBytes(value, UINT64_LENGTH);
    }

    /**
     * Encode an non-negative integer as a big-endian uint64 value.
     * @param value The value to encode.
     * @throws IllegalArgumentException if value is negative.
     * @return A byte array containing the big-endian encoding of the value. Its length will be Encoder.UINT64_LENGTH.
     */
    public static byte[] encodeUint64(long value) {
        return Encoder.encodeUintToBytes(BigInteger.valueOf(value), UINT64_LENGTH);
    }

    /**
     * Decode an encoded big-endian uint64 value.
     * @param encoded The encoded uint64 value. Its length must be Encoder.UINT64_LENGTH.
     * @throws IllegalArgumentException if encoded is the wrong length.
     * @return The decoded value.
     */
    public static BigInteger decodeUint64(byte[] encoded) {
        if (encoded.length > Encoder.UINT64_LENGTH)
            throw new IllegalArgumentException("Length of byte array is invalid");

        return new BigInteger(1, encoded);
    }

    /**
     * Encode an non-negative integer as a big-endian general uint value.
     * @param value The value to encode.
     * @param byteNum The size of output bytes.
     * @throws IllegalArgumentException if value cannot be represented by the byte array of length byteNum.
     * @return A byte array containing the big-endian encoding of the value. Its length will be byteNum.
     */
    public static byte[] encodeUintToBytes(BigInteger value, int byteNum) {
        if (value.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalArgumentException("Encode int to byte: input BigInteger < 0");
        if (value.compareTo(BigInteger.ONE.shiftLeft(byteNum * 8)) >= 0)
            throw new IllegalArgumentException("Encode int to byte: integer size exceeds the given byte number");

        byte[] buffer = new byte[byteNum];
        byte[] encoded = value.toByteArray();
        // in case number >= 2^(byteNum * 8 - 1), the 2's complement representation will extend 1 byte header of 0's
        if (encoded.length == byteNum + 1)
            encoded = Arrays.copyOfRange(encoded, 1, encoded.length);

        System.arraycopy(encoded, 0, buffer, buffer.length - encoded.length, encoded.length);
        return buffer;
    }

    /**
     * Decode an encoded big-endian uint value.
     * @param encoded The encoded bytes for the value.
     * @return A byte array containing the big-endian encoding of the value. Its length will be Encoder.UINT64_LENGTH.
     */
    public static BigInteger decodeBytesToUint(byte[] encoded) {
        return new BigInteger(1, encoded);
    }

    public static String encodeToURL(Object value) throws Exception {
        if (value instanceof byte[]) {
            return URLEncoder.encode(new String((byte[])value, StandardCharsets.ISO_8859_1.name()), StandardCharsets.ISO_8859_1.name());
        }
        return value.toString();
    }
}
