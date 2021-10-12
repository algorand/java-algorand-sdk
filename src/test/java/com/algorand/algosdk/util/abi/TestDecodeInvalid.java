package com.algorand.algosdk.util.abi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDecodeInvalid {

    @Test
    public void TestBoolArray0() {
        byte[] input = new byte[]{(byte) 0xff};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("bool[9]").decode(input));
    }

    @Test
    public void TestBoolArray1() {
        byte[] input = new byte[]{(byte) 0xff, 0x00};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("bool[8]").decode(input));
    }

    @Test
    public void TestBoolArray2() {
        byte[] input = new byte[]{0x00, 0x0A, (byte) 0b10101010};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("bool[]").decode(input));
    }

    @Test
    public void TestBoolArray3() {
        byte[] input = new byte[]{0x00, 0x05, (byte) 0b10100000, 0x00};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("bool[]").decode(input));
    }

    @Test
    public void TestUintArray0() {
        byte[] input = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("uint64[10]").decode(input));
    }

    @Test
    public void TestUintArray1() {
        byte[] input = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("uint64[6]").decode(input));
    }

    @Test
    public void TestTuple0() {
        byte[] input = new byte[]{
                0x00, 0x04, (byte) 0b10100000, 0x00, 0x0A,
                0x00, 0x03, (byte) 'A', (byte) 'B', (byte) 'C',
                0x00, 0x03, (byte) 'D', (byte) 'E', (byte) 'F',
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(string,bool,bool,bool,bool,string)").decode(input)
        );
    }

    @Test
    public void TestBoolArrayTuple0() {
        byte[] encode0 = new byte[]{(byte) 0b11000000, (byte) 0b11000000, 0x00};
        byte[] encode1 = new byte[]{(byte) 0b11000000};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(bool[2],bool[2])").decode(encode0));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(bool[2],bool[2])").decode(encode1));
    }

    @Test
    public void TestBoolArrayTuple1() {
        byte[] encoded = new byte[]{
                (byte) 0b11000000,
                0x03,
                0x00, 0x02, (byte) 0b11000000
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(bool[2],bool[])").decode(encoded));

    }

    @Test
    public void TestBoolArrayTuple2() {
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x08,
                0x00, 0x02, (byte) 0b11000000, 0x00,
                0x00, 0x02, (byte) 0b11000000
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(bool[],bool[])").decode(encoded));
    }

    @Test
    public void TestBoolArrayTuple3() {
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Type.fromString("(bool[],bool[])").decode(encoded));
    }

    @Test
    public void TestEmptyTuple() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Type.fromString("()").decode(new byte[]{0x0F})
        );
    }
}
