package com.algorand.algosdk.abi;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class TestRandomValues {
    List<List<RawValueWithABIType>> testValuePool;
    Random rand;

    private static class RawValueWithABIType {
        String abiT;
        Object rawValue;

        RawValueWithABIType(String abiT, Object rawValue) {
            this.abiT = abiT;
            this.rawValue = rawValue;
        }
    }

    public BigInteger genRandInt(int bitSize) {
        BigInteger bigRand = new BigInteger(bitSize, rand);
        while (bigRand.compareTo(BigInteger.ONE.shiftLeft(bitSize)) >= 0)
            bigRand = new BigInteger(bitSize, rand);
        return bigRand;
    }

    private void generateStaticArray() {
        for (int i = 0; i < testValuePool.get(0).size(); i += 400) {
            Object[] valueArr = new Object[20];
            for (int cnt = 0; cnt < 20; cnt++)
                valueArr[cnt] = testValuePool.get(0).get(i + cnt).rawValue;
            ABIType elemT = ABIType.Of(testValuePool.get(0).get(i).abiT);
            testValuePool.get(6).add(
                    new RawValueWithABIType(new TypeArrayStatic(elemT, 20).toString(), valueArr));
        }

        Object[] valueByteArr = new Object[20];
        for (int i = 0; i < 20; i++)
            valueByteArr[i] = testValuePool.get(2).get(i).rawValue;
        testValuePool.get(6).add(new RawValueWithABIType("byte[20]", valueByteArr));

        Object[] boolArr = new Object[20];
        for (int i = 0; i < 20; i++) {
            int index = rand.nextBoolean() ? 0 : 1;
            boolArr[i] = testValuePool.get(3).get(index).rawValue;
        }
        testValuePool.get(6).add(new RawValueWithABIType("bool[20]", boolArr));

        Object[] addressArr = new Object[20];
        for (int i = 0; i < 20; i++)
            addressArr[i] = testValuePool.get(4).get(i).rawValue;
        testValuePool.get(6).add(new RawValueWithABIType("address[20]", addressArr));

        Object[] stringArr = new Object[20];
        for (int i = 0; i < 20; i++)
            stringArr[i] = testValuePool.get(5).get(i).rawValue;
        testValuePool.get(6).add(new RawValueWithABIType("string[20]", stringArr));
    }

    private void generateDynamicArray() {
        for (int i = 0; i < testValuePool.get(0).size(); i += 400) {
            Object[] valueArr = new Object[20];
            for (int cnt = 0; cnt < 20; cnt++)
                valueArr[cnt] = testValuePool.get(0).get(i + cnt).rawValue;
            ABIType elemT = ABIType.Of(testValuePool.get(0).get(i).abiT);
            testValuePool.get(7).add(new RawValueWithABIType(new TypeArrayDynamic(elemT).toString(), valueArr));
        }

        Object[] valueByteArr = new Object[20];
        for (int i = 0; i < 20; i++)
            valueByteArr[i] = testValuePool.get(2).get(i).rawValue;
        testValuePool.get(7).add(new RawValueWithABIType("byte[]", valueByteArr));

        Object[] boolArr = new Object[20];
        for (int i = 0; i < 20; i++) {
            int index = rand.nextBoolean() ? 0 : 1;
            boolArr[i] = testValuePool.get(3).get(index).rawValue;
        }
        testValuePool.get(7).add(new RawValueWithABIType("bool[]", boolArr));

        Object[] addressArr = new Object[20];
        for (int i = 0; i < 20; i++)
            addressArr[i] = testValuePool.get(4).get(i).rawValue;
        testValuePool.get(7).add(new RawValueWithABIType("address[]", addressArr));

        Object[] stringAddr = new Object[20];
        for (int i = 0; i < 20; i++)
            stringAddr[i] = testValuePool.get(5).get(i).rawValue;
        testValuePool.get(7).add(new RawValueWithABIType("string[]", stringAddr));
    }

    private void generateTuple(int index) {
        for (int i = 0; i < 100; i++) {
            int tupleLength = 1 + rand.nextInt(20);
            Object[] tupleValues = new Object[tupleLength];
            ABIType[] tupleTypes = new ABIType[tupleLength];
            for (int j = 0; j < tupleLength; j++) {
                int valueTypeSlot = rand.nextInt(index + 1);
                int valueIndex = rand.nextInt(testValuePool.get(valueTypeSlot).size());
                tupleValues[j] = testValuePool.get(valueTypeSlot).get(valueIndex).rawValue;
                tupleTypes[j] = ABIType.Of(testValuePool.get(valueTypeSlot).get(valueIndex).abiT);
            }
            String abiT = new TypeTuple(Arrays.asList(tupleTypes)).toString();
            testValuePool.get(8).add(new RawValueWithABIType(abiT, tupleValues));
        }
    }

    @BeforeEach
    public void setup() {
        rand = new Random();
        testValuePool = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            testValuePool.add(new ArrayList<>());

        for (int i = 8; i <= 512; i += 8) {
            for (int j = 0; j < 200; j++)
                testValuePool.get(0).add(new RawValueWithABIType(new TypeUint(i).toString(), this.genRandInt(i)));
            for (int j = 1; j <= 160; j++)
                testValuePool.get(1).add(new RawValueWithABIType(new TypeUfixed(i, j).toString(), this.genRandInt(i)));
        }

        for (int i = 0; i < (1 << 8); i++)
            testValuePool.get(2).add(new RawValueWithABIType(new TypeByte().toString(), (byte) i));

        for (int i = 0; i < 2; i++)
            testValuePool.get(3).add(new RawValueWithABIType(new TypeBool().toString(), (i == 0)));

        for (int i = 0; i < 500; i++) {
            BigInteger tempAddressInt = this.genRandInt(256);
            byte[] addressEncode = Encoder.encodeUintToBytes(tempAddressInt, 32);
            testValuePool.get(4).add(new RawValueWithABIType(new TypeAddress().toString(), new Address(addressEncode)));
        }

        for (int i = 1; i < 100; i++) {
            for (int j = 0; j < 5; j++) {
                byte[] array = new byte[i];
                rand.nextBytes(array);
                String genString = new String(array, StandardCharsets.UTF_8);
                testValuePool.get(5).add(new RawValueWithABIType(new TypeString().toString(), genString));
            }
        }

        this.generateStaticArray();
        this.generateDynamicArray();
        this.generateTuple(7);
        this.generateTuple(8);
    }

    @Test
    public void TestRandomElemRoundTrip() {
        for (RawValueWithABIType v : testValuePool.get(8)) {
            byte[] encoded = ABIType.Of(v.abiT).encode(v.rawValue);
            assertThat(ABIType.Of(v.abiT).decode(encoded)).isEqualTo(v.rawValue);
        }
    }
}
