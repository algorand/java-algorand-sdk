package com.algorand.algosdk.abi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestMethod {

    @BeforeEach
    void setup() {

    }

    @Test
    public void TestMethodFromSignature() {
        String[] testcases = new String[]{
                "someMethod(uint64,ufixed64x2,(bool,byte),address)void",
                "pseudoRandomGenerator()uint256",
                "add(uint64,uint64)uint128",
                "someEffectOnTheOtherSide___(uint64,(ufixed256x10,bool))void",
                "returnATuple(address)(byte[32],bool)"
        };
        for (String testcase : testcases)
            assertThat(new Method(testcase).getSignature()).isEqualTo(testcase);
    }

    @Test
    public void TestMethodFromSignatureInvalid() {
        String[] testcases = new String[]{
                "___nopeThis Not Right nAmE () void",
                "intentional(MessAroundWith(Parentheses(address)(uint8)"
        };
        for (String testcase : testcases)
            Assertions.assertThrows(IllegalArgumentException.class, () -> new Method(testcase));
    }
}
