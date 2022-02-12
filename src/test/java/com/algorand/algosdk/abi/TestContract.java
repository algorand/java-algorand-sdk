package com.algorand.algosdk.abi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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
                        ), new Method.Returns("void", null)),
                new Method("multiply", null,
                        Arrays.asList(
                                new Method.Arg("a", "uint64", "..."),
                                new Method.Arg("b", "uint64", "...")
                        ), new Method.Returns("void", null))
        );
    }

    @Test
    public void TestContractClassMethods() throws IOException {
        String testJSON = "{\"name\": \"Calculator\", \"desc\":\"example description\", \"networks\": {\"wGHE2Pwdvd7S12BL5FaOP20EGYesN73ktiC1qzkkit8=\": {\"appID\": 10}}, \"methods\": [{ \"name\": \"add\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] },{ \"name\": \"multiply\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] }]}";
        Contract readContract = objMapper.readValue(testJSON.getBytes(StandardCharsets.UTF_8), Contract.class);
        assertThat(readContract).isEqualTo(new Contract("Calculator", "example description", Collections.singletonMap("wGHE2Pwdvd7S12BL5FaOP20EGYesN73ktiC1qzkkit8=", new Contract.NetworkInfo(10L)), methodList));
        assertThat(readContract.getNetworkInfo("wGHE2Pwdvd7S12BL5FaOP20EGYesN73ktiC1qzkkit8=")).isEqualTo(new Contract.NetworkInfo(10L));
        assertThat(readContract.getName()).isEqualTo("Calculator");
        for (int i = 0; i < readContract.methods.size(); i++)
            assertThat(readContract.getMethodByIndex(i)).isEqualTo(methodList.get(i));
    }
}
