package com.algorand.algosdk.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigInteger;

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

            byte[] actual = Encoder.encodeUint64(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        long[] invalidInputs = {
            -1,
            Long.MIN_VALUE,
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            long input = invalidInputs[i];
            assertThatCode(() -> Encoder.encodeUint64(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Value cannot be represented by a uint64");
        }
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

            byte[] actual = Encoder.encodeUint64(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        BigInteger[] invalidInputs = {
            BigInteger.valueOf(-1),
            Encoder.MAX_UINT64.add(BigInteger.valueOf(1))
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            BigInteger input = invalidInputs[i];
            assertThatCode(() -> Encoder.encodeUint64(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Value cannot be represented by a uint64");
        }
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
            new byte[] {},
            new byte[] {0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            byte[] input = invalidInputs[i];
            assertThatCode(() -> Encoder.decodeUint64(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Length of byte array is invalid");
        }
    }
}
