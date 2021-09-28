package com.algorand.algosdk.util.abi;

import com.algorand.algosdk.util.abi.types.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

            int rand_precision = rand.nextInt(1024);
            while (rand_precision >= 1 && rand_precision <= 160)
                rand_precision = rand.nextInt(1024);
            final int final_rand_precision = rand_precision;

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

    }

    @Test
    public void TestUfixedFromStringValid() {
        for (int i = 8; i <= 512; i+= 8) {
            for (int j = 1; j <= 160; j++) {
                String encoded = "ufixed" + i + "x" + j;
                UfixedT ufixedT = new UfixedT(i, j);
                assertThat((UfixedT) Type.fromString(encoded)).isEqualTo(ufixedT);
            }
        }
    }
}
