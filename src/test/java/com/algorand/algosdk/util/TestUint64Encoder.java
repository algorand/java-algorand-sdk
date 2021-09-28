package com.algorand.algosdk.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class TestUint64Encoder {

    @Test
    public void testEncodeLong() {
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

            byte[] actual = Uint64Encoder.encode(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        long[] invalidInputs = {
            -1,
            Long.MIN_VALUE,
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            long input = invalidInputs[i];
            assertThatCode(() -> Uint64Encoder.encode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Value cannot be represented by a uint64");
        }
    }
    
    @Test
    public void testEncodeBigInt() {
        BigInteger[] inputs = {
            BigInteger.valueOf(0),
            BigInteger.valueOf(1),
            BigInteger.valueOf(500),
            Uint64Encoder.MAX_UINT64.subtract(BigInteger.valueOf(1)),
            Uint64Encoder.MAX_UINT64,
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

            byte[] actual = Uint64Encoder.encode(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        BigInteger[] invalidInputs = {
            BigInteger.valueOf(-1),
            Uint64Encoder.MAX_UINT64.add(BigInteger.valueOf(1))
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            BigInteger input = invalidInputs[i];
            assertThatCode(() -> Uint64Encoder.encode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Value cannot be represented by a uint64");
        }
    }

    @Test
    public void testDecode() {
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
            Uint64Encoder.MAX_UINT64.subtract(BigInteger.valueOf(1)),
            Uint64Encoder.MAX_UINT64,
        };

        for (int i = 0; i < inputs.length; i++) {
            byte[] input = inputs[i];
            BigInteger expected = expectedItems[i];

            BigInteger actual = Uint64Encoder.decode(input);
            assertThat(actual).isEqualTo(expected);
        }
        
        byte[][] invalidInputs = {
            new byte[] {},
            new byte[] {0, 0, 0, 0, 0, 0, 0},
            new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            byte[] input = invalidInputs[i];
            assertThatCode(() -> Uint64Encoder.decode(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Length of byte array is invalid");
        }
    }
}
