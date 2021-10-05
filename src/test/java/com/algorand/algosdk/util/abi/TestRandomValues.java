package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.abi.values.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class TestRandomValues {
    List<List<Value>> testValuePool;
    Random rand;

    public BigInteger genRandInt(int bitSize) {
        BigInteger bigRand = new BigInteger(bitSize, rand);
        while (bigRand.compareTo(BigInteger.ONE.shiftLeft(bitSize)) >= 0)
            bigRand = new BigInteger(bitSize, rand);
        return bigRand;
    }

    private void generateStaticArray() {
        for (int i = 0; i < testValuePool.get(0).size(); i += 400) {
            Value[] valueArr = new Value[20];
            for (int cnt = 0; cnt < 20; cnt++)
                valueArr[cnt] = testValuePool.get(0).get(i + cnt);
            testValuePool.get(6).add(new ValueArrayStatic(valueArr));
        }

        Value[] valueByteArr = new Value[20];
        for (int i = 0; i < 20; i++)
            valueByteArr[i] = testValuePool.get(2).get(i);
        testValuePool.get(6).add(new ValueArrayStatic(valueByteArr));

        Value[] boolArr = new Value[20];
        for (int i = 0; i < 20; i++) {
            int index = rand.nextBoolean() ? 0 : 1;
            boolArr[i] = testValuePool.get(3).get(index);
        }
        testValuePool.get(6).add(new ValueArrayStatic(boolArr));

        Value[] addressArr = new Value[20];
        for (int i = 0; i < 20; i++)
            addressArr[i] = testValuePool.get(4).get(i);
        testValuePool.get(6).add(new ValueArrayStatic(addressArr));

        Value[] stringAddr = new Value[20];
        for (int i = 0; i < 20; i++)
            stringAddr[i] = testValuePool.get(5).get(i);
        testValuePool.get(6).add(new ValueArrayStatic(stringAddr));
    }

    private void generateDynamicArray() {
        for (int i = 0; i < testValuePool.get(0).size(); i += 400) {
            Value[] valueArr = new Value[20];
            for (int cnt = 0; cnt < 20; cnt++)
                valueArr[cnt] = testValuePool.get(0).get(i + cnt);
            testValuePool.get(7).add(new ValueArrayDynamic(valueArr, valueArr[0].abiType));
        }

        Value[] valueByteArr = new Value[20];
        for (int i = 0; i < 20; i++)
            valueByteArr[i] = testValuePool.get(2).get(i);
        testValuePool.get(7).add(new ValueArrayDynamic(valueByteArr, valueByteArr[0].abiType));

        Value[] boolArr = new Value[20];
        for (int i = 0; i < 20; i++) {
            int index = rand.nextBoolean() ? 0 : 1;
            boolArr[i] = testValuePool.get(3).get(index);
        }
        testValuePool.get(7).add(new ValueArrayDynamic(boolArr, boolArr[0].abiType));

        Value[] addressArr = new Value[20];
        for (int i = 0; i < 20; i++)
            addressArr[i] = testValuePool.get(4).get(i);
        testValuePool.get(7).add(new ValueArrayDynamic(addressArr, addressArr[0].abiType));

        Value[] stringAddr = new Value[20];
        for (int i = 0; i < 20; i++)
            stringAddr[i] = testValuePool.get(5).get(i);
        testValuePool.get(7).add(new ValueArrayDynamic(stringAddr, stringAddr[0].abiType));
    }

    private void generateTuple(int index) {
        for (int i = 0; i < 100; i++) {
            int tupleLength = 1 + rand.nextInt(20);
            Value[] tupleValues = new Value[tupleLength];
            for (int j = 0; j < tupleLength; j++) {
                int valueTypeSlot = rand.nextInt(index + 1);
                int valueIndex = rand.nextInt(testValuePool.get(valueTypeSlot).size());
                tupleValues[j] = testValuePool.get(valueTypeSlot).get(valueIndex);
            }
            testValuePool.get(8).add(new ValueTuple(tupleValues));
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
                testValuePool.get(0).add(new ValueUint(i, this.genRandInt(i)));
            for (int j = 1; j <= 160; j++)
                testValuePool.get(1).add(new ValueUfixed(i, j, this.genRandInt(i)));
        }

        for (int i = 0; i < (1 << 8); i++)
            testValuePool.get(2).add(new ValueByte((byte) i));

        for (int i = 0; i < 2; i++)
            testValuePool.get(3).add(new ValueBool(i == 0));

        for (int i = 0; i < 500; i++) {
            BigInteger tempAddressInt = this.genRandInt(256);
            byte[] addressEncode = Encoder.encodeUintToBytes(tempAddressInt, 32);
            testValuePool.get(4).add(new ValueAddress(addressEncode));
        }

        for (int i = 1; i < 100; i++) {
            for (int j = 0; j < 5; j++) {
                byte[] array = new byte[i];
                rand.nextBytes(array);
                String genString = new String(array, StandardCharsets.UTF_8);
                testValuePool.get(5).add(new ValueString(genString));
            }
        }

        this.generateStaticArray();
        this.generateDynamicArray();
        this.generateTuple(7);
        this.generateTuple(8);
    }

    @Test
    public void TestRandomElemRoundTrip() {
        for (Value v : testValuePool.get(8)) {
            byte[] encoded = v.encode();
            Value decoded = Value.decode(encoded, v.abiType);
            assertThat(decoded).isEqualTo(v);
        }
    }
}
