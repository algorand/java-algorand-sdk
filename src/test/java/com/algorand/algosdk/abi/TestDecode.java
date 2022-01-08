package com.algorand.algosdk.abi;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDecode {
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
                byte[] encodedUint = new TypeUint(bitSize).encode(bigRand);
                assertThat(new TypeUint(bitSize).decode(encodedUint)).isEqualTo(bigRand);
            }
            // 2^[bitSize] - 1
            BigInteger upperLimit = BigInteger.ONE.shiftLeft(bitSize).add(BigInteger.ONE.negate());
            byte[] expected = new byte[bitSize / Byte.SIZE];
            for (int i = 0; i < bitSize / Byte.SIZE; i++)
                expected[i] = (byte) 0xff;
            assertThat(new TypeUint(bitSize).decode(expected)).isEqualTo(upperLimit);
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
                    byte[] encodedUfixed = new TypeUfixed(bitSize, precision).encode(bigRand);
                    assertThat(new TypeUfixed(bitSize, precision).decode(encodedUfixed)).isEqualTo(bigRand);
                }
                assertThat(new TypeUfixed(bitSize, precision).decode(expected))
                        .isEqualTo(upperLimit);
            }
        }
    }

    @Test
    public void TestAddressValid() {
        BigInteger upperLimit = BigInteger.ONE.shiftLeft(Address.LEN_BYTES * 8).add(BigInteger.ONE.negate());
        byte[] upperEncoded = Encoder.encodeUintToBytes(upperLimit, Address.LEN_BYTES);
        for (int i = 0; i < 1000; i++) {
            BigInteger bigRand = new BigInteger(Address.LEN_BYTES * 8, rand);
            while (bigRand.compareTo(BigInteger.ONE.shiftLeft(Address.LEN_BYTES * 8)) >= 0)
                bigRand = new BigInteger(Address.LEN_BYTES * 8, rand);
            byte[] addrEncode = Encoder.encodeUintToBytes(bigRand, Address.LEN_BYTES);
            assertThat(new TypeAddress().decode(addrEncode)).isEqualTo(new Address(addrEncode));
        }
        assertThat(new TypeAddress().decode(upperEncoded)).isEqualTo(new Address(upperEncoded));
    }

    @Test
    public void TestBoolValid() {
        for (int i = 0; i < 2; i++)
            assertThat(new TypeBool().decode(new byte[]{(i == 0) ? (byte) 0x80 : 0x00})).isEqualTo(i == 0);
    }

    @Test
    public void TestByteValid() {
        for (int i = 0; i < (1 << 8); i++)
            assertThat(new TypeByte().decode(new byte[]{(byte) i})).isEqualTo((byte) i);
    }

    @Test
    public void TestStringValid() {
        for (int length = 1; length <= 400; length++) {
            for (int i = 0; i < 10; i++) {
                byte[] array = new byte[length];
                new Random().nextBytes(array);
                String genStrings = new String(array, StandardCharsets.UTF_8);

                byte[] genBytes = genStrings.getBytes(StandardCharsets.UTF_8);
                ByteBuffer bf = ByteBuffer.allocate(ABIType.ABI_DYNAMIC_HEAD_BYTE_LEN + genBytes.length);
                bf.put(Encoder.encodeUintToBytes(BigInteger.valueOf(genBytes.length), ABIType.ABI_DYNAMIC_HEAD_BYTE_LEN));
                bf.put(genBytes);
                assertThat(new TypeString().decode(bf.array())).isEqualTo(genStrings);
            }
        }
    }

    @Test
    public void TestBoolArray0() {
        Boolean[] inputs = new Boolean[]{true, false, false, true, true};
        assertThat(ABIType.valueOf("bool[5]").decode(new byte[]{(byte) 0b10011000})).isEqualTo(inputs);
    }

    @Test
    public void TestBoolArray1() {
        Boolean[] inputs = new Boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        assertThat(ABIType.valueOf("bool[11]").decode(new byte[]{0b00011010, (byte) 0b10100000})).isEqualTo(inputs);
    }

    @Test
    public void TestBoolArray2() {
        Boolean[] inputs = new Boolean[]{false, false, false, true, true, false, true, false, true, false, true};
        assertThat(ABIType.valueOf("bool[]").decode(new byte[]{0x00, 0x0B, 0b00011010, (byte) 0b10100000})).isEqualTo(inputs);
    }

    @Test
    public void TestUintArray() {
        BigInteger[] inputs = new BigInteger[8];
        for (int i = 1; i <= 8; i++)
            inputs[i - 1] = BigInteger.valueOf(i);
        assertThat(ABIType.valueOf("uint64[8]").decode(new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
        })).isEqualTo(inputs);
    }

    @Test
    public void TestTuple0() {
        Object[] tuple = new Object[]{
                "ABC",
                true,
                false,
                true,
                false,
                "DEF"
        };
        assertThat(ABIType.valueOf("(string,bool,bool,bool,bool,string)").decode(new byte[]{
                0x00, 0x05, (byte) 0b10100000, 0x00, 0x0A,
                0x00, 0x03, (byte) 'A', (byte) 'B', (byte) 'C',
                0x00, 0x03, (byte) 'D', (byte) 'E', (byte) 'F',
        })).isEqualTo(tuple);
    }

    @Test
    public void TestBoolArrayTuple0() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] expected = new byte[]{(byte) 0b11000000, (byte) 0b11000000};
        assertThat(ABIType.valueOf("(bool[2],bool[2])").decode(expected)).isEqualTo(elements);
    }

    @Test
    public void TestBoolArrayTuple1() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] encoded = new byte[]{
                (byte) 0b11000000,
                0x00, 0x03,
                0x00, 0x02, (byte) 0b11000000
        };
        assertThat(ABIType.valueOf("(bool[2],bool[])").decode(encoded)).isEqualTo(elements);
    }

    @Test
    public void TestBoolArrayTuple2() {
        Object[] elements = new Object[]{
                new Object[]{true, true},
                new Object[]{true, true},
        };
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x07,
                0x00, 0x02, (byte) 0b11000000,
                0x00, 0x02, (byte) 0b11000000
        };
        assertThat(ABIType.valueOf("(bool[],bool[])").decode(encoded)).isEqualTo(elements);
    }

    @Test
    public void TestBoolArrayTuple3() {
        Object[] elements = new Object[]{
                new Object[0],
                new Object[0],
        };
        byte[] encoded = new byte[]{
                0x00, 0x04, 0x00, 0x06,
                0x00, 0x00, 0x00, 0x00
        };
        assertThat(ABIType.valueOf("(bool[],bool[])").decode(encoded)).isEqualTo(elements);
    }

    @Test
    public void TestEmptyTuple() {
        assertThat(ABIType.valueOf("()").decode(new byte[0])).isEqualTo(new Object[0]);
    }
}
