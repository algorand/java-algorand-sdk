package com.algorand.algosdk.abi;

import com.algorand.algosdk.util.Encoder;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
                assertThat(new TypeUint(bitSize).encode(bigRand)).isEqualTo(Encoder.encodeUintToBytes(bigRand, bitSize / Byte.SIZE));
            }
            // 2^[bitSize] - 1
            BigInteger upperLimit = BigInteger.ONE.shiftLeft(bitSize).add(BigInteger.ONE.negate());
            byte[] expected = new byte[bitSize / Byte.SIZE];
            for (int i = 0; i < bitSize / Byte.SIZE; i++)
                expected[i] = (byte) 0xff;
            assertThat(new TypeUint(bitSize).encode(upperLimit)).isEqualTo(expected);
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
                    assertThat(new TypeUfixed(bitSize, precision).encode(bigRand)).isEqualTo(Encoder.encodeUintToBytes(bigRand, bitSize / Byte.SIZE));
                }
                assertThat(new TypeUfixed(bitSize, precision).encode(upperLimit)).isEqualTo(expected);
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
            assertThat(new TypeAddress().encode(addrEncode)).isEqualTo(addrEncode);
        }
        assertThat(new TypeAddress().encode(upperEncoded)).isEqualTo(upperEncoded);
    }

    @Test
    public void TestBoolValid() {
        for (int i = 0; i < 2; i++)
            assertThat(new TypeBool().encode(i == 0)).isEqualTo(new byte[]{(i == 0) ? (byte) 0x80 : 0x00});
    }

    @Test
    public void TestByteValid() {
        for (int i = 0; i < (1 << 8); i++)
            assertThat(new TypeByte().encode((byte) i)).isEqualTo(new byte[]{(byte) i});
    }

    @Test
    public void TestStringValid() {
        for (int length = 1; length <= 400; length++) {
            for (int i = 0; i < 10; i++) {
                byte[] array = new byte[length];
                rand.nextBytes(array);
                String genStrings = new String(array, StandardCharsets.UTF_8);

                byte[] genBytes = genStrings.getBytes(StandardCharsets.UTF_8);
                ByteBuffer bf = ByteBuffer.allocate(2 + genBytes.length);
                bf.put(Encoder.encodeUintToBytes(BigInteger.valueOf(genBytes.length), 2));
                bf.put(genBytes);
                assertThat(new TypeString().encode(genStrings)).isEqualTo(bf.array());
            }
        }
    }

    @Test
    public void TestBoolArray0() {
        boolean[] inputs = new boolean[]{true, false, false, true, true};
        byte[] expected = new byte[]{(byte) 0b10011000};
        assertThat(new TypeArrayStatic(new TypeBool(), 5).encode(inputs)).isEqualTo(expected);

        Object[] objInputs = Type.unifyToArrayOfObjects(inputs);
        assertThat(new TypeArrayStatic(new TypeBool(), 5).encode(objInputs)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(objInputs);
        assertThat(new TypeArrayStatic(new TypeBool(), 5).encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArray1() {
        boolean[] inputs = new boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        byte[] expected = new byte[]{0b00011010, (byte) 0b10100000};
        assertThat(new TypeArrayStatic(new TypeBool(), 11).encode(inputs)).isEqualTo(expected);

        Object[] objInputs = Type.unifyToArrayOfObjects(inputs);
        assertThat(new TypeArrayStatic(new TypeBool(), 11).encode(objInputs)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(objInputs);
        assertThat(new TypeArrayStatic(new TypeBool(), 11).encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArray2() {
        boolean[] inputs = new boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        byte[] expected = new byte[]{0x00, 0x0B, 0b00011010, (byte) 0b10100000};
        assertThat(new TypeArrayDynamic(new TypeBool()).encode(inputs)).isEqualTo(expected);

        Object[] objInputs = Type.unifyToArrayOfObjects(inputs);
        assertThat(new TypeArrayDynamic(new TypeBool()).encode(objInputs)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(objInputs);
        assertThat(new TypeArrayDynamic(new TypeBool()).encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestUintArray() {
        long[] inputs = new long[]{1, 2, 3, 4, 5, 6, 7, 8};
        byte[] expected = new byte[]{
                0x00, 0x08,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
        };
        assertThat(Type.Of("uint64[]").encode(inputs)).isEqualTo(expected);

        int[] inputsInt = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        assertThat(Type.Of("uint64[]").encode(inputsInt)).isEqualTo(expected);

        short[] inputsShort = new short[]{1, 2, 3, 4, 5, 6, 7, 8};
        assertThat(Type.Of("uint64[]").encode(inputsShort)).isEqualTo(expected);

        byte[] inputsByte = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        assertThat(Type.Of("uint64[]").encode(inputsByte)).isEqualTo(expected);

        Integer[] inputIntegers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};
        assertThat(Type.Of("uint64[]").encode(inputIntegers)).isEqualTo(expected);

        Object[] objInputs = Type.unifyToArrayOfObjects(inputs);
        assertThat(Type.Of("uint64[]").encode(objInputs)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(objInputs);
        assertThat(Type.Of("uint64[]").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestDynamicTuple0() {
        Object[] elements = new Object[]{
                "ABC",
                true,
                false,
                true,
                false,
                "DEF"
        };
        byte[] expected = new byte[]{
                0x00, 0x05, (byte) 0b10100000, 0x00, 0x0A,
                0x00, 0x03, (byte) 'A', (byte) 'B', (byte) 'C',
                0x00, 0x03, (byte) 'D', (byte) 'E', (byte) 'F',
        };
        assertThat(Type.Of("(string,bool,bool,bool,bool,string)").encode(elements)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("(string,bool,bool,bool,bool,string)").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple0() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] expected = new byte[]{(byte) 0b11000000, (byte) 0b11000000};
        assertThat(Type.Of("(bool[2],bool[2])").encode(elements)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("(bool[2],bool[2])").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple1() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] expected = new byte[]{
                (byte) 0b11000000,
                0x00, 0x03,
                0x00, 0x02, (byte) 0b11000000};
        assertThat(Type.Of("(bool[2],bool[])").encode(elements)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("(bool[2],bool[])").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple2() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] expected = new byte[]{
                0x00, 0x04, 0x00, 0x07,
                0x00, 0x02, (byte) 0b11000000,
                0x00, 0x02, (byte) 0b11000000
        };
        assertThat(Type.Of("(bool[],bool[])").encode(elements)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("(bool[],bool[])").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestBoolArrayTuple3() {
        Object[] elements = new Object[]{
                new Object[0],
                new Object[0],
        };
        byte[] expected = new byte[]{
                0x00, 0x04, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00
        };
        assertThat(Type.Of("(bool[],bool[])").encode(elements)).isEqualTo(expected);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("(bool[],bool[])").encode(listInputs)).isEqualTo(expected);
    }

    @Test
    public void TestEmptyTuple() {
        Object[] elements = new Object[0];
        assertThat(Type.Of("()").encode(elements)).isEqualTo(new byte[0]);

        List<Object> listInputs = Arrays.asList(elements);
        assertThat(Type.Of("()").encode(listInputs)).isEqualTo(new byte[0]);
    }
}
