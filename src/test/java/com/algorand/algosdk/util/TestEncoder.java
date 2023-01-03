package com.algorand.algosdk.util;

import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.model.Account;
import com.algorand.algosdk.v2.client.model.Enums;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.*;

public class TestEncoder {
    @ParameterizedTest
    @ValueSource(strings = {
            "3DV5VXT3QFRPVRTZTWP7PSGJN37BXJNXQWAFFW34OJWIZ2UOINQQ",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HURQ",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HU======",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HU"
    })
    public void Base32EncodeDecode(String input) {
        byte[] decoded = Encoder.decodeFromBase32StripPad(input);
        String reEncoded = Encoder.encodeToBase32StripPad(decoded);
        assertThat(reEncoded).isEqualTo(StringUtils.stripEnd(input, "="));
    }

    @Test
    public void testEncodeUint64Long() {
        long[] inputs = {
            0,
            1,
            500,
            Long.MAX_VALUE - 1,
            Long.MAX_VALUE,
        };

        byte[][] expectedItems = {
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 1},
            new byte[] {0, 0, 0, 0, 0, 0, 1, (byte) 0xf4},
            new byte[] {(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe},
            new byte[] {(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff},
        };

        for (int i = 0; i < inputs.length; i++) {
            long input = inputs[i];
            byte[] expected = expectedItems[i];

            byte[] actual = Encoder.encodeUintToBytes(BigInteger.valueOf(input), 8);
            assertThat(actual).isEqualTo(expected);
        }
        
        long[] invalidInputs = {
            -1,
            Long.MIN_VALUE,
        };

        for (long input : invalidInputs)
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> Encoder.encodeUintToBytes(BigInteger.valueOf(input), 8));
    }
    
    @Test
    public void testEncodeUint64BigInt() {
        BigInteger[] inputs = {
            BigInteger.valueOf(0),
            BigInteger.valueOf(1),
            BigInteger.valueOf(500),
            Encoder.MAX_UINT64.subtract(BigInteger.valueOf(1)),
            Encoder.MAX_UINT64,
        };

        byte[][] expectedItems = {
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 1},
            new byte[] {0, 0, 0, 0, 0, 0, 1, (byte) 0xf4},
            new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe},
            new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff},
        };

        for (int i = 0; i < inputs.length; i++) {
            BigInteger input = inputs[i];
            byte[] expected = expectedItems[i];

            byte[] actual = Encoder.encodeUintToBytes(input, 8);
            assertThat(actual).isEqualTo(expected);
        }
        
        BigInteger[] invalidInputs = {
            BigInteger.valueOf(-1),
            Encoder.MAX_UINT64.add(BigInteger.valueOf(1))
        };

        for (BigInteger input : invalidInputs)
            Assertions.assertThrows(IllegalArgumentException.class, () -> Encoder.encodeUintToBytes(input, 8));
    }

    @Test
    public void testDecodeUint64() {
        byte[][] inputs = {
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 1},
            new byte[] {0, 0, 0, 0, 0, 0, 1, (byte) 0xf4},
            new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe},
            new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff},
        };

        BigInteger[] expectedItems = {
            BigInteger.valueOf(0),
            BigInteger.valueOf(1),
            BigInteger.valueOf(500),
            Encoder.MAX_UINT64.subtract(BigInteger.valueOf(1)),
            Encoder.MAX_UINT64,
        };

        for (int i = 0; i < inputs.length; i++) {
            byte[] input = inputs[i];
            BigInteger expected = expectedItems[i];

            BigInteger actual = Encoder.decodeUint64(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        byte[][] invalidInputs = {
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        for (byte[] input : invalidInputs)
            assertThatCode(() -> Encoder.decodeUint64(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Length of byte array is invalid");
    }

    @ParameterizedTest
    @CsvSource({
            "pay, pay",
            "blah, ",
            "blah, ''"
    })
    public void testTransactionEnum(String type, String expected) throws IOException {
        String txnString = "{\"type\": \"" + type + "\"}";
        Transaction txn = Encoder.decodeFromJson(txnString, Transaction.class);
        if (expected != null) {
            Transaction.Type enumValue = Transaction.Type.forValue(expected);
            assertThat(enumValue).isEqualTo(txn.type);
        } else {
            assertThat(Transaction.Type.Default).isEqualTo(txn.type);
        }

        TestUtil.serializeDeserializeCheck(txn);
    }

    @ParameterizedTest
    @CsvSource({
            "sig, sig",
            "blah, ",
            "blah, ''"
    })
    public void testHttpModelEnum(String type, String expected) throws IOException {
        String acctString = "{\"sig-type\": \"" + type + "\"}";
        Account acct = Encoder.decodeFromJson(acctString, Account.class);
        if (expected != null) {
            Enums.SigType enumValue = Enums.SigType.forValue(expected);
            assertThat(enumValue).isEqualTo(acct.sigType);
        } else {
            assertThat(Enums.SigType.UNKNOWN).isEqualTo(acct.sigType);
        }

        TestUtil.serializeDeserializeCheck(acct);
    }
}
