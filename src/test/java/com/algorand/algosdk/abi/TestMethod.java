package com.algorand.algosdk.abi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMethod {

    public static class MethodTestCase {
        public String methodStr;
        public int txnCallCount;
        public List<Method.Arg> methodArgs;

        public MethodTestCase(String mStr, int txnCallCount_, List<String> methodArgs_) {
            this.methodStr = mStr;
            this.txnCallCount = txnCallCount_;
            this.methodArgs = new ArrayList<>();
            for (String methodArg : methodArgs_)
                this.methodArgs.add(new Method.Arg(null, methodArg, null));
        }
    }

    ObjectMapper objMapper;
    MethodTestCase[] testcases;

    @BeforeEach
    void setup() {
        objMapper = new ObjectMapper();
        testcases = new MethodTestCase[]{
                new MethodTestCase("someMethod(uint64,ufixed64x2,(bool,byte),address)void", 1,
                        Arrays.asList("uint64", "ufixed64x2", "(bool,byte)", "address")),
                new MethodTestCase("pseudoRandomGenerator()uint256", 1, new ArrayList<>()),
                new MethodTestCase("add(uint64,uint64)uint128", 1, Arrays.asList("uint64", "uint64")),
                new MethodTestCase("someEffectOnTheOtherSide___(uint64,(ufixed256x10,bool))void", 1,
                        Arrays.asList("uint64", "(ufixed256x10,bool)")),
                new MethodTestCase("someMethod(uint64,ufixed64x2,(bool,byte),address)void", 1,
                        Arrays.asList("uint64", "ufixed64x2", "(bool,byte)", "address")),
                new MethodTestCase("returnATuple(address)(byte[32],bool)", 1,
                        Collections.singletonList("address")),
                new MethodTestCase("txcalls(pay,pay,axfer,byte)bool", 4,
                        Arrays.asList("pay", "pay", "axfer", "byte")),
                new MethodTestCase("foreigns(account,pay,asset,application,bool)void", 2,
                        Arrays.asList("account", "pay", "asset", "application", "bool"))
        };
    }

    @Test
    public void TestMethodFromSignature() {
        for (MethodTestCase testcase : testcases) {
            Method fromStr = new Method(testcase.methodStr);
            assertThat(fromStr.getSignature()).isEqualTo(testcase.methodStr);
            assertThat(fromStr.getTxnCallCount()).isEqualTo(testcase.txnCallCount);
            assertThat(fromStr.args).isEqualTo(testcase.methodArgs);
        }
    }

    @Test
    public void TestMethodFromSignatureInvalid() {
        String[] failingTestcases = new String[]{
                "___nopeThis Not Right nAmE () void",
                "intentional(MessAroundWith(Parentheses(address)(uint8)"
        };
        for (String testcase : failingTestcases)
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
        for (MethodTestCase testcase : testcases) {
            byte[] jsonBytes = objMapper.writeValueAsBytes(new Method(testcase.methodStr));
            Method readMethod = objMapper.readValue(jsonBytes, Method.class);
            assertThat(readMethod).isEqualTo(new Method(testcase.methodStr));
        }
    }
}
