package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.abi.types.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class TestTypes {
    Random rand;

    @BeforeEach
    void setup() {
        rand = new Random();
    }

    @Test
    public void TestUintValid() {
        for (int i = 8; i <= 512; i += 8) {
            UintT uintT = new UintT(i);
            assertThat(uintT.string()).isEqualTo("uint" + i);
        }
    }

    @Test
    public void TestUintInvalid() {
        for (int i = 0; i < 1000; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(1000);
            final int finalSize_rand = size_rand;
            Assertions.assertThrows(IllegalArgumentException.class, () -> new UintT(finalSize_rand));
        }
    }

    @Test
    public void TestUfixedValid() {
        for (int i = 8; i <= 512; i += 8) {
            for (int j = 1; j <= 160; j++) {
                UfixedT ufixedT = new UfixedT(i, j);
                assertThat(ufixedT.string()).isEqualTo("ufixed" + i + "x" + j);
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

            Assertions.assertThrows(IllegalArgumentException.class, () -> new UfixedT(final_rand_size, final_rand_precision));
        }
    }

    @Test
    public void TestSimpleTypesValid() {
        assertThat(new ByteT().string()).isEqualTo("byte");
        assertThat(new StringT().string()).isEqualTo("string");
        assertThat(new AddressT().string()).isEqualTo("address");
        assertThat(new BoolT().string()).isEqualTo("bool");
    }

    @Test
    public void TestTypeToStringValid() throws IllegalAccessException {
        assertThat(new ArrayDynamicT(new UintT(32)).string()).isEqualTo("uint32[]");
        assertThat(new ArrayDynamicT(new ArrayDynamicT(new ByteT())).string()).isEqualTo("byte[][]");
        assertThat(new ArrayStaticT(new UfixedT(128, 10), 100).string()).isEqualTo("ufixed128x10[100]");
        assertThat(new ArrayStaticT(new ArrayStaticT(new BoolT(), 128), 256).string()).isEqualTo("bool[128][256]");
        assertThat(
                new TupleT(
                        Arrays.asList(
                                new UintT(32),
                                new TupleT(Arrays.asList(
                                        new AddressT(),
                                        new ByteT(),
                                        new ArrayStaticT(new BoolT(), 10),
                                        new ArrayDynamicT(new UfixedT(256, 10))
                                ))
                        )
                ).string()
        ).isEqualTo("(uint32,(address,byte,bool[10],ufixed256x10[]))");
        assertThat(new TupleT(new ArrayList<>()).string()).isEqualTo("()");
    }

    @Test
    public void TestUintFromStringValid() {
        for (int i = 8; i <= 512; i += 8) {
            String encoded = "uint" + i;
            UintT uintT = new UintT(i);
            assertThat((UintT) Type.fromString(encoded)).isEqualTo(uintT);
        }
    }

    @Test
    public void TestUintFromStringInvalid() {
        for (int i = 0; i < 100; i++) {
            int size_rand = rand.nextInt(65536);
            while (size_rand % 8 == 0 && size_rand <= 512 && size_rand >= 8)
                size_rand = rand.nextInt(65536);
            String encoded = "uint" + size_rand;
            Assertions.assertThrows(IllegalArgumentException.class, () -> Type.fromString(encoded));
        }
    }

    @Test
    public void TestUfixedFromStringValid() {
        for (int i = 8; i <= 512; i += 8) {
            for (int j = 1; j <= 160; j++) {
                String encoded = "ufixed" + i + "x" + j;
                UfixedT ufixedT = new UfixedT(i, j);
                assertThat((UfixedT) Type.fromString(encoded)).isEqualTo(ufixedT);
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
            Assertions.assertThrows(IllegalArgumentException.class, () -> Type.fromString(encoded));
        }
    }

    @Test
    public void TestSimpleTypeFromStringValid() {
        assertThat(Type.fromString("address")).isEqualTo(new AddressT());
        assertThat(Type.fromString("byte")).isEqualTo(new ByteT());
        assertThat(Type.fromString("bool")).isEqualTo(new BoolT());
        assertThat(Type.fromString("string")).isEqualTo(new StringT());
    }

    @Test
    public void TestTypeFromStringValid() {
        assertThat(Type.fromString("uint256[]")).isEqualTo(new ArrayDynamicT(new UintT(256)));
        assertThat(Type.fromString("ufixed256x64[]")).isEqualTo(new ArrayDynamicT(new UfixedT(256, 64)));
        assertThat(Type.fromString("byte[][][][]")).isEqualTo(
                new ArrayDynamicT(new ArrayDynamicT(new ArrayDynamicT(new ArrayDynamicT(new ByteT())))));
        assertThat(Type.fromString("address[100]")).isEqualTo(new ArrayStaticT(new AddressT(), 100));
        assertThat(Type.fromString("uint64[][100]")).isEqualTo(new ArrayStaticT(new ArrayDynamicT(new UintT(64)), 100));
        assertThat(Type.fromString("()")).isEqualTo(new TupleT(new ArrayList<>()));
        assertThat(Type.fromString("(uint32,(address,byte,bool[10],ufixed256x10[]),byte[])")).isEqualTo(
                new TupleT(
                        Arrays.asList(
                                new UintT(32),
                                new TupleT(Arrays.asList(
                                        new AddressT(),
                                        new ByteT(),
                                        new ArrayStaticT(new BoolT(), 10),
                                        new ArrayDynamicT(new UfixedT(256, 10))
                                )),
                                new ArrayDynamicT(new ByteT())
                        )
                )
        );
        assertThat(Type.fromString("(uint32,(address,byte,bool[10],(ufixed256x10[])))")).isEqualTo(
                new TupleT(
                        Arrays.asList(
                                new UintT(32),
                                new TupleT(Arrays.asList(
                                        new AddressT(),
                                        new ByteT(),
                                        new ArrayStaticT(new BoolT(), 10),
                                        new TupleT(Collections.singletonList(
                                                new ArrayDynamicT(new UfixedT(256, 10))
                                        ))
                                ))
                        )
                )
        );
        assertThat(Type.fromString("((uint32),(address,(byte,bool[10],ufixed256x10[])))")).isEqualTo(
                new TupleT(
                        Arrays.asList(
                                new TupleT(Collections.singletonList(new UintT(32))),
                                new TupleT(Arrays.asList(
                                        new AddressT(),
                                        new TupleT(Arrays.asList(
                                                new ByteT(),
                                                new ArrayStaticT(new BoolT(), 10),
                                                new ArrayDynamicT(new UfixedT(256, 10))
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
        };
        for (String testcase : testcases)
            Assertions.assertThrows(IllegalArgumentException.class, () -> Type.fromString(testcase));
    }
}
