package com.algorand.algosdk.abi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class TestMethod {

    ObjectMapper objMapper;
    String[] testcases;

    @BeforeEach
    void setup() {
        objMapper = new ObjectMapper();
        testcases = new String[]{
                "someMethod(uint64,ufixed64x2,(bool,byte),address)void",
                "pseudoRandomGenerator()uint256",
                "add(uint64,uint64)uint128",
                "someEffectOnTheOtherSide___(uint64,(ufixed256x10,bool))void",
                "returnATuple(address)(byte[32],bool)",
                "methodWithTxType(axfer,uint32,pay,bool)void"
        };
    }

    @Test
    public void TestMethodFromSignature() {
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

    @Test
    public void TestMethodGetSelector() {
        Map<String, byte[]> methodSelectorMap = new HashMap<String, byte[]>() {{
            put("add(uint32,uint32)uint32", new byte[]{0x3e, 0x1e, 0x52, (byte) 0xbd});
            put("add(uint64,uint64)uint128", new byte[]{(byte) 0x8a, (byte) 0xa3, (byte) 0xb6, 0x1f});
        }};
        for (Map.Entry<String, byte[]> entry : methodSelectorMap.entrySet())
            assertThat(new Method(entry.getKey()).getSelector()).isEqualTo(entry.getValue());
    }

    @Test
    public void TestMethodJSONRoundTrip() throws IOException {
        for (String testcase : testcases) {
            byte[] jsonBytes = objMapper.writeValueAsBytes(new Method(testcase));
            Method readMethod = objMapper.readValue(jsonBytes, Method.class);
            assertThat(readMethod).isEqualTo(new Method(testcase));
        }
    }
}
