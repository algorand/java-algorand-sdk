package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.types.TypeBool;
import com.algorand.algosdk.util.abi.values.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class TestEncode {
    Random rand;

    @BeforeEach
    void setup() {
        rand = new Random();
    }

    @Test
    public void TestUintValid() {
        for (int bitSize = 8; bitSize <= 512; bitSize += 8) {
            for (int i = 0; i < 100; i++) {
                BigInteger bigRand = new BigInteger(bitSize, rand);
                while (bigRand.compareTo(BigInteger.ONE.shiftLeft(bitSize)) >= 0)
                    bigRand = new BigInteger(bitSize, rand);
                ValueUint uint = new ValueUint(bitSize, bigRand);
                assertThat(uint.encode()).isEqualTo(Encoder.encodeUintToBytes(bigRand, bitSize / Byte.SIZE));
            }
            // 2^[bitSize] - 1
            BigInteger upperLimit = BigInteger.ONE.shiftLeft(bitSize).add(BigInteger.ONE.negate());
            byte[] expected = new byte[bitSize / Byte.SIZE];
            for (int i = 0; i < bitSize / Byte.SIZE; i++)
                expected[i] = (byte) 0xff;
            assertThat(new ValueUint(bitSize, upperLimit).encode()).isEqualTo(expected);
        }
    }

    @Test
    public void TestUfixedValid() {
        for (int bitSize = 8; bitSize <= 512; bitSize += 8) {
            BigInteger upperLimit = BigInteger.ONE.shiftLeft(bitSize).add(BigInteger.ONE.negate());
            byte[] expected = new byte[bitSize / Byte.SIZE];
            for (int i = 0; i < bitSize / Byte.SIZE; i++)
                expected[i] = (byte) 0xff;
            for (int precision = 1; precision <= 160; precision++) {
                for (int i = 0; i <= 10; i++) {
                    BigInteger bigRand = new BigInteger(bitSize, rand);
                    while (bigRand.compareTo(BigInteger.ONE.shiftLeft(bitSize)) >= 0)
                        bigRand = new BigInteger(bitSize, rand);
                    ValueUfixed ufixed = new ValueUfixed(bitSize, precision, bigRand);
                    assertThat(ufixed.encode()).isEqualTo(Encoder.encodeUintToBytes(bigRand, bitSize / Byte.SIZE));
                }
                assertThat(new ValueUfixed(bitSize, precision, upperLimit).encode()).isEqualTo(expected);
            }
        }
    }

    @Test
    public void TestAddressValid() {
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(256).add(BigInteger.ONE.negate());
        byte[] upperEncoded = Encoder.encodeUintToBytes(upperLimit, 32);
        for (int i = 0; i < 1000; i++) {
            BigInteger bigRand = new BigInteger(256, rand);
            while (bigRand.compareTo(BigInteger.ONE.shiftLeft(256)) >= 0)
                bigRand = new BigInteger(256, rand);
            byte[] addrEncode = Encoder.encodeUintToBytes(bigRand, 32);
            assertThat(new ValueAddress(addrEncode).encode()).isEqualTo(addrEncode);
        }
        assertThat(new ValueAddress(upperEncoded).encode()).isEqualTo(upperEncoded);
    }

    @Test
    public void TestBoolValid() {
        for (int i = 0; i < 2; i++) {
            ValueBool boolV = new ValueBool(i == 0);
            assertThat(boolV.encode()).isEqualTo(new byte[]{(i == 0) ? (byte) 0x80 : 0x00});
        }
    }

    @Test
    public void TestByteValid() {
        for (int i = 0; i < (1 << 8); i++) {
            ValueByte byteV = new ValueByte((byte) i);
            assertThat(byteV.encode()).isEqualTo(new byte[]{(byte) i});
        }
    }

    @Test
    public void TestStringValid() {
        for (int length = 1; length <= 400; length++) {
            for (int i = 0; i < 10; i++) {
                byte[] array = new byte[length];
                new Random().nextBytes(array);
                String genStrings = new String(array, StandardCharsets.UTF_8);
                ValueString valueString = new ValueString(genStrings);

                byte[] genBytes = genStrings.getBytes(StandardCharsets.UTF_8);
                ByteBuffer bf = ByteBuffer.allocate(2 + genBytes.length);
                bf.put(Encoder.encodeUintToBytes(BigInteger.valueOf(genBytes.length), 2));
                bf.put(genBytes);
                assertThat(valueString.encode()).isEqualTo(bf.array());
            }
        }
    }

    @Test
    public void TestBoolArray0() {
        boolean[] inputs = new boolean[]{true, false, false, true, true};
        Value[] arrayElems = new ValueBool[5];
        for (int i = 0; i < 5; i++)
            arrayElems[i] = new ValueBool(inputs[i]);
        ValueArrayStatic boolArray = new ValueArrayStatic(arrayElems);
        byte[] expected = new byte[]{(byte) 0b10011000};
        assertThat(boolArray.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArray1() {
        boolean[] inputs = new boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        Value[] arrayElems = new ValueBool[11];
        for (int i = 0; i < 11; i++)
            arrayElems[i] = new ValueBool(inputs[i]);
        ValueArrayStatic boolArray = new ValueArrayStatic(arrayElems);
        byte[] expected = new byte[]{0b00011010, (byte) 0b10100000};
        assertThat(boolArray.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArray2() {
        boolean[] inputs = new boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        Value[] arrayElems = new ValueBool[11];
        for (int i = 0; i < 11; i++)
            arrayElems[i] = new ValueBool(inputs[i]);
        ValueArrayDynamic boolArray = new ValueArrayDynamic(arrayElems, new TypeBool());
        byte[] expected = new byte[]{0x00, 0x0B, 0b00011010, (byte) 0b10100000};
        assertThat(boolArray.encode()).isEqualTo(expected);
    }

    @Test
    public void TestDynamicTuple0() {
        Value[] elements = new Value[]{
                new ValueString("ABC"),
                new ValueBool(true),
                new ValueBool(false),
                new ValueBool(true),
                new ValueBool(false),
                new ValueString("DEF")
        };
        byte[] expected = new byte[]{
                0x00, 0x05, (byte) 0b10100000, 0x00, 0x0A,
                0x00, 0x03, (byte) 'A', (byte) 'B', (byte) 'C',
                0x00, 0x03, (byte) 'D', (byte) 'E', (byte) 'F',
        };
        Value tuple = new ValueTuple(elements);
        assertThat(tuple.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple0() {
        Value[] elements = new Value[]{
                new ValueArrayStatic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                }),
                new ValueArrayStatic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                })
        };
        Value tuple = new ValueTuple(elements);
        byte[] expected = new byte[]{(byte) 0b11000000, (byte) 0b11000000};
        assertThat(tuple.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple1() {
        Value[] elements = new Value[]{
                new ValueArrayStatic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                }),
                new ValueArrayDynamic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                }, new TypeBool())
        };
        Value tuple = new ValueTuple(elements);
        byte[] expected = new byte[]{
                (byte) 0b11000000,
                0x00, 0x03,
                0x00, 0x02, (byte) 0b11000000};
        assertThat(tuple.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple2() {
        Value[] elements = new Value[]{
                new ValueArrayDynamic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                }, new TypeBool()),
                new ValueArrayDynamic(new Value[]{
                        new ValueBool(true),
                        new ValueBool(true)
                }, new TypeBool())
        };
        Value tuple = new ValueTuple(elements);
        byte[] expected = new byte[]{
                0x00, 0x04, 0x00, 0x07,
                0x00, 0x02, (byte) 0b11000000,
                0x00, 0x02, (byte) 0b11000000
        };
        assertThat(tuple.encode()).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple3() {
        Value[] elements = new Value[]{
                new ValueArrayDynamic(new Value[]{}, new TypeBool()),
                new ValueArrayDynamic(new Value[]{}, new TypeBool())
        };
        Value tuple = new ValueTuple(elements);
        byte[] expected = new byte[]{
                0x00, 0x04, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00
        };
        assertThat(tuple.encode()).isEqualTo(expected);
    }

    @Test
    public void TestEmptyTuple() {
        Value tuple = new ValueTuple(new Value[0]);
        assertThat(tuple.encode()).isEqualTo(new byte[0]);
    }
}
