package com.algorand.algosdk.abi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class TestTypes {
    Random rand;
    List<ABIType> type_testpool;
    List<ABIType> tuple_testpool;

    private TypeTuple generateRandomTupleType() {
        int tupleLen = rand.nextInt(20);
        List<ABIType> tupleElems = new ArrayList<>();
        for (int i = 0; i < tupleLen; i++) {
            int baseOrTuple = rand.nextInt(5);
            if (baseOrTuple == 1 && tuple_testpool.size() > 0)
                tupleElems.add(tuple_testpool.get(rand.nextInt(tuple_testpool.size())));
            else
                tupleElems.add(type_testpool.get(rand.nextInt(type_testpool.size())));
        }
        return new TypeTuple(tupleElems);
    }

    @BeforeEach
    void setup() {
        rand = new Random();
        type_testpool = new ArrayList<>(Arrays.asList(
                new TypeBool(),
                new TypeAddress(),
                new TypeString(),
                new TypeByte()
        ));
        for (int i = 8; i <= 512; i += 8)
            type_testpool.add(new TypeUint(i));
        for (int i = 8; i <= 512; i += 8) {
            for (int j = 1; j <= 160; j++)
                type_testpool.add(new TypeUfixed(i, j));
        }
        int currentLength = type_testpool.size();
        for (int i = 0; i < currentLength; i++) {
            type_testpool.add(new TypeArrayDynamic(type_testpool.get(i)));
            type_testpool.add(new TypeArrayStatic(type_testpool.get(i), 10));
            type_testpool.add(new TypeArrayStatic(type_testpool.get(i), 20));
        }
        tuple_testpool = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TypeTuple tempTuple = generateRandomTupleType();
            tuple_testpool.add(tempTuple);
        }
    }

    @Test
    public void TestUintValid() {
        for (int i = 8; i <= 512; i += 8) {
            TypeUint uintT = new TypeUint(i);
            assertThat(uintT.toString()).isEqualTo("uint" + i);
        }
    }

    @Test
    public void TestUintInvalid() {
        for (int i = 0; i < 1000; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(1000);
            final int finalSize_rand = size_rand;
            Assertions.assertThrows(IllegalArgumentException.class, () -> new TypeUint(finalSize_rand));
        }
    }

    @Test
    public void TestUfixedValid() {
        for (int i = 8; i <= 512; i += 8) {
            for (int j = 1; j <= 160; j++) {
                TypeUfixed ufixedT = new TypeUfixed(i, j);
                assertThat(ufixedT.toString()).isEqualTo("ufixed" + i + "x" + j);
            }
        }
    }

    @Test
    public void TestUfixedInvalid() {
        for (int i = 0; i < 1000; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(65536);
            final int final_rand_size = size_rand;

            int precision_rand = rand.nextInt(1024);
            while (precision_rand >= 1 && precision_rand <= 160)
                precision_rand = rand.nextInt(1024);
            final int final_rand_precision = precision_rand;

            Assertions.assertThrows(IllegalArgumentException.class, () -> new TypeUfixed(final_rand_size, final_rand_precision));
        }
    }

    @Test
    public void TestSimpleTypesValid() {
        assertThat(new TypeByte().toString()).isEqualTo("byte");
        assertThat(new TypeString().toString()).isEqualTo("string");
        assertThat(new TypeAddress().toString()).isEqualTo("address");
        assertThat(new TypeBool().toString()).isEqualTo("bool");
    }

    @Test
    public void TestTypeToStringValid() {
        assertThat(new TypeArrayDynamic(new TypeUint(32)).toString()).isEqualTo("uint32[]");
        assertThat(new TypeArrayDynamic(new TypeArrayDynamic(new TypeByte())).toString()).isEqualTo("byte[][]");
        assertThat(new TypeArrayStatic(new TypeUfixed(128, 10), 100).toString()).isEqualTo("ufixed128x10[100]");
        assertThat(new TypeArrayStatic(new TypeArrayStatic(new TypeBool(), 128), 256).toString()).isEqualTo("bool[128][256]");
        assertThat(
                new TypeTuple(
                        Arrays.asList(
                                new TypeUint(32),
                                new TypeTuple(Arrays.asList(
                                        new TypeAddress(),
                                        new TypeByte(),
                                        new TypeArrayStatic(new TypeBool(), 10),
                                        new TypeArrayDynamic(new TypeUfixed(256, 10))
                                ))
                        )
                ).toString()
        ).isEqualTo("(uint32,(address,byte,bool[10],ufixed256x10[]))");
        assertThat(new TypeTuple(new ArrayList<>()).toString()).isEqualTo("()");
    }

    @Test
    public void TestUintFromStringValid() {
        for (int i = 8; i <= 512; i += 8) {
            String encoded = "uint" + i;
            TypeUint uintT = new TypeUint(i);
            assertThat((TypeUint) ABIType.valueOf(encoded)).isEqualTo(uintT);
        }
    }

    @Test
    public void TestUintFromStringInvalid() {
        for (int i = 0; i < 100; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(65536);
            String encoded = "uint" + size_rand;
            Assertions.assertThrows(IllegalArgumentException.class, () -> ABIType.valueOf(encoded));
        }
    }

    @Test
    public void TestUfixedFromStringValid() {
        for (int i = 8; i <= 512; i += 8) {
            for (int j = 1; j <= 160; j++) {
                String encoded = "ufixed" + i + "x" + j;
                TypeUfixed ufixedT = new TypeUfixed(i, j);
                assertThat((TypeUfixed) ABIType.valueOf(encoded)).isEqualTo(ufixedT);
            }
        }
    }

    @Test
    public void TestUfixedFromStringInvalid() {
        for (int i = 0; i < 1000; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(65536);

            int precision_rand = rand.nextInt(1024);
            while (precision_rand >= 1 && precision_rand <= 160)
                precision_rand = rand.nextInt(1024);

            String encoded = "ufixed" + size_rand + "x" + precision_rand;
            Assertions.assertThrows(IllegalArgumentException.class, () -> ABIType.valueOf(encoded));
        }
    }

    @Test
    public void TestSimpleTypeFromStringValid() {
        assertThat(ABIType.valueOf("address")).isEqualTo(new TypeAddress());
        assertThat(ABIType.valueOf("byte")).isEqualTo(new TypeByte());
        assertThat(ABIType.valueOf("bool")).isEqualTo(new TypeBool());
        assertThat(ABIType.valueOf("string")).isEqualTo(new TypeString());
    }

    @Test
    public void TestTypeFromStringValid() {
        assertThat(ABIType.valueOf("uint256[]")).isEqualTo(new TypeArrayDynamic(new TypeUint(256)));
        assertThat(ABIType.valueOf("ufixed256x64[]")).isEqualTo(new TypeArrayDynamic(new TypeUfixed(256, 64)));
        assertThat(ABIType.valueOf("byte[][][][]")).isEqualTo(
                new TypeArrayDynamic(new TypeArrayDynamic(new TypeArrayDynamic(new TypeArrayDynamic(new TypeByte())))));
        assertThat(ABIType.valueOf("address[100]")).isEqualTo(new TypeArrayStatic(new TypeAddress(), 100));
        assertThat(ABIType.valueOf("uint64[][100]")).isEqualTo(new TypeArrayStatic(new TypeArrayDynamic(new TypeUint(64)), 100));
        assertThat(ABIType.valueOf("()")).isEqualTo(new TypeTuple(new ArrayList<>()));
        assertThat(ABIType.valueOf("(uint32,(address,byte,bool[10],ufixed256x10[]),byte[])")).isEqualTo(
                new TypeTuple(
                        Arrays.asList(
                                new TypeUint(32),
                                new TypeTuple(Arrays.asList(
                                        new TypeAddress(),
                                        new TypeByte(),
                                        new TypeArrayStatic(new TypeBool(), 10),
                                        new TypeArrayDynamic(new TypeUfixed(256, 10))
                                )),
                                new TypeArrayDynamic(new TypeByte())
                        )
                )
        );
        assertThat(ABIType.valueOf("(uint32,(address,byte,bool[10],(ufixed256x10[])))")).isEqualTo(
                new TypeTuple(
                        Arrays.asList(
                                new TypeUint(32),
                                new TypeTuple(Arrays.asList(
                                        new TypeAddress(),
                                        new TypeByte(),
                                        new TypeArrayStatic(new TypeBool(), 10),
                                        new TypeTuple(Collections.singletonList(
                                                new TypeArrayDynamic(new TypeUfixed(256, 10))
                                        ))
                                ))
                        )
                )
        );
        assertThat(ABIType.valueOf("((uint32),(address,(byte,bool[10],ufixed256x10[])))")).isEqualTo(
                new TypeTuple(
                        Arrays.asList(
                                new TypeTuple(Collections.singletonList(new TypeUint(32))),
                                new TypeTuple(Arrays.asList(
                                        new TypeAddress(),
                                        new TypeTuple(Arrays.asList(
                                                new TypeByte(),
                                                new TypeArrayStatic(new TypeBool(), 10),
                                                new TypeArrayDynamic(new TypeUfixed(256, 10))
                                        ))
                                ))
                        )
                )
        );
    }

    @Test
    public void TestTypeFromStringInvalid() {
        String[] testcases = new String[]{
                // uint
                "uint123x345",
                "uint 128",
                "uint8 ",
                "uint!8",
                "uint[32]",
                "uint-893",
                "uint#120\\",
                // ufixed
                "ufixed000000000016x0000010",
                "ufixed123x345",
                "ufixed 128 x 100",
                "ufixed64x10 ",
                "ufixed!8x2 ",
                "ufixed[32]x16",
                "ufixed-64x+100",
                "ufixed16x+12",
                // dynamic array
                "uint256 []",
                "byte[] ",
                "[][][]",
                "stuff[]",
                // static array
                "ufixed32x10[0]",
                "byte[10 ]",
                "uint64[0x21]",
                // tuple
                "(ufixed128x10))",
                "(,uint128,byte[])",
                "(address,ufixed64x5,)",
                "(byte[16],somethingwrong)",
                "(                )",
                "((uint32)",
                "(byte,,byte)",
                "((byte),,(byte))",
                // some random stuffs
                "",
        };
        for (String testcase : testcases)
            Assertions.assertThrows(IllegalArgumentException.class, () -> ABIType.valueOf(testcase));
    }

    @Test
    public void TestTupleRoundTrip() {
        for (ABIType t : tuple_testpool) {
            String encoded = t.toString();
            ABIType decoded = ABIType.valueOf(encoded);
            assertThat((TypeTuple) decoded).isEqualTo(t);
        }
    }

    @Test
    public void TestSelfEquiv() {
        for (ABIType t : type_testpool)
            assertThat(t).isEqualTo(t);
        for (ABIType t : tuple_testpool)
            assertThat(t).isEqualTo(t);
        for (int i = 0; i < 1000; i++) {
            int index0 = rand.nextInt(type_testpool.size());
            int index1 = rand.nextInt(type_testpool.size());
            while (type_testpool.get(index0).toString().equals(type_testpool.get(index1).toString()))
                index1 = rand.nextInt(type_testpool.size());
            assertThat(type_testpool.get(index0)).isNotEqualTo(type_testpool.get(index1));
        }
        for (int i = 0; i < 1000; i++) {
            int index0 = rand.nextInt(tuple_testpool.size());
            int index1 = rand.nextInt(tuple_testpool.size());
            while (tuple_testpool.get(index0).toString().equals(tuple_testpool.get(index1).toString()))
                index1 = rand.nextInt(tuple_testpool.size());
            assertThat(tuple_testpool.get(index0)).isNotEqualTo(tuple_testpool.get(index1));
        }
    }

    @Test
    public void TestIsDynamic() {
        for (ABIType t : type_testpool) {
            String encoded = t.toString();
            boolean inferFromString = encoded.contains("[]") || encoded.contains("string");
            assertThat(inferFromString).isEqualTo(t.isDynamic());
        }
        for (ABIType t : tuple_testpool) {
            String encoded = t.toString();
            boolean inferFromString = encoded.contains("[]") || encoded.contains("string");
            assertThat(inferFromString).isEqualTo(t.isDynamic());
        }
    }

    @Test
    public void TestByteLen() {
        assertThat(new TypeAddress().byteLen()).isEqualTo(32);
        assertThat(new TypeByte().byteLen()).isEqualTo(1);
        for (ABIType t : type_testpool) {
            if (t.isDynamic())
                Assertions.assertThrows(IllegalArgumentException.class, t::byteLen);
        }
        for (ABIType t : tuple_testpool) {
            if (t.isDynamic())
                Assertions.assertThrows(IllegalArgumentException.class, t::byteLen);
            else {
                int size = 0;
                ABIType[] ctList = ((TypeTuple) t).childTypes.toArray(new ABIType[0]);
                for (int i = 0; i < ctList.length; i++) {
                    if (ctList[i] instanceof TypeBool) {
                        int boolNum = ABIType.findBoolLR(ctList, i, 1) + 1;
                        size += boolNum / 8;
                        size += (boolNum % 8 != 0) ? 1 : 0;
                    } else size += ctList[i].byteLen();
                }
                assertThat(size).isEqualTo(t.byteLen());
            }
        }
    }
}
