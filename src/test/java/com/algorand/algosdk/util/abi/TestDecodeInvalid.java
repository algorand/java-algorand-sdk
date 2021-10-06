package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.abi.types.*;
import com.algorand.algosdk.util.abi.values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestDecodeInvalid {

    @Test
    public void TestBoolArray0() {
        byte[] input = new byte[]{(byte) 0xff};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueArrayStatic(new TypeBool(), 9, input));
    }

    @Test
    public void TestBoolArray1() {
        byte[] input = new byte[]{(byte) 0xff, 0x00};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueArrayStatic(new TypeBool(), 8, input));
    }

    @Test
    public void TestBoolArray2() {
        byte[] input = new byte[]{0x00, 0x0A, (byte) 0b10101010};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueArrayDynamic(new TypeBool(), input));
    }

    @Test
    public void TestBoolArray3() {
        byte[] input = new byte[]{0x00, 0x05, (byte) 0b10100000, 0x00};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueArrayDynamic(new TypeBool(), input));
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
                () -> new ValueArrayStatic(new TypeUint(64), 10, input));
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
                () -> new ValueArrayStatic(new TypeUint(64), 6, input));
    }

    @Test
    public void TestTuple0() {
        byte[] input = new byte[]{
                0x00, 0x04, (byte) 0b10100000, 0x00, 0x0A,
                0x00, 0x03, (byte) 'A', (byte) 'B', (byte) 'C',
                0x00, 0x03, (byte) 'D', (byte) 'E', (byte) 'F',
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(new ArrayList<>(
                        Arrays.asList(
                                new TypeString(),
                                new TypeBool(),
                                new TypeBool(),
                                new TypeBool(),
                                new TypeBool(),
                                new TypeString()
                        )
                )), input)
        );
    }

    @Test
    public void TestBoolArrayTuple0() {
        byte[] encode0 = new byte[]{(byte) 0b11000000, (byte) 0b11000000, 0x00};
        byte[] encode1 = new byte[]{(byte) 0b11000000};
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(new ArrayList<>(
                        Arrays.asList(
                                new TypeArrayStatic(new TypeBool(), 2),
                                new TypeArrayStatic(new TypeBool(), 2)
                        )
                )), encode0));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(new ArrayList<>(
                        Arrays.asList(
                                new TypeArrayStatic(new TypeBool(), 2),
                                new TypeArrayStatic(new TypeBool(), 2)
                        )
                )), encode1));
    }

    @Test
    public void TestBoolArrayTuple1() {
        byte[] encoded = new byte[]{
                (byte) 0b11000000,
                0x03,
                0x00, 0x02, (byte) 0b11000000
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(
                new ArrayList<>(
                        Arrays.asList(
                                new TypeArrayStatic(new TypeBool(), 2),
                                new TypeArrayDynamic(new TypeBool())
                        )
                )), encoded));
    }

    @Test
    public void TestBoolArrayTuple2() {
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x08,
                0x00, 0x02, (byte) 0b11000000, 0x00,
                0x00, 0x02, (byte) 0b11000000
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(
                new ArrayList<>(
                        Arrays.asList(
                                new TypeArrayDynamic(new TypeBool()),
                                new TypeArrayDynamic(new TypeBool())
                        )
                )), encoded));
    }

    @Test
    public void TestBoolArrayTuple3() {
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00
        };
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(
                new ArrayList<>(
                        Arrays.asList(
                                new TypeArrayDynamic(new TypeBool()),
                                new TypeArrayDynamic(new TypeBool())
                        )
                )
        ), encoded));
    }

    @Test
    public void TestEmptyTuple() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ValueTuple(new TypeTuple(new ArrayList<>()), new byte[]{0x0F})
        );
    }
}
