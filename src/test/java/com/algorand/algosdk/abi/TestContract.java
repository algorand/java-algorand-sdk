package com.algorand.algosdk.abi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestContract {
    ObjectMapper objMapper;
    List<Method> methodList;

    @BeforeEach
    void setup() {
        objMapper = new ObjectMapper();
        methodList = Arrays.asList(
                new Method("add", null,
                        Arrays.asList(
                                new Method.Arg("a", "uint64", "..."),
                                new Method.Arg("b", "uint64", "...")
                        ), null),
                new Method("multiply", null,
                        Arrays.asList(
                                new Method.Arg("a", "uint64", "..."),
                                new Method.Arg("b", "uint64", "...")
                        ), null)
        );
    }

    @Test
    public void TestContractClassMethods() throws IOException {
        String testJSON = "{\"name\": \"Calculator\",\"appId\": 10, \"methods\": [{ \"name\": \"add\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] },{ \"name\": \"multiply\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] }]}";
        Contract readContract = objMapper.readValue(testJSON.getBytes(StandardCharsets.UTF_8), Contract.class);
        assertThat(readContract).isEqualTo(new Contract("Calculator", 10, methodList));
        assertThat(readContract.getAppId()).isEqualTo(10);
        assertThat(readContract.getName()).isEqualTo("Calculator");
        for (int i = 0; i < readContract.methods.size(); i++)
            assertThat(readContract.getMethodByIndex(i)).isEqualTo(methodList.get(i));
    }
}