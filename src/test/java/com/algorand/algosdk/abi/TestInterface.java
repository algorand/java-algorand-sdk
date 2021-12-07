package com.algorand.algosdk.abi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestInterface {
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
    public void TestInterfaceClassMethods() throws IOException {
        String testJSON = "{\"name\": \"Calculator\",\"methods\": [{ \"name\": \"add\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] },{ \"name\": \"multiply\", \"args\": [ { \"name\": \"a\", \"type\": \"uint64\", \"desc\": \"...\" },{ \"name\": \"b\", \"type\": \"uint64\", \"desc\": \"...\" } ] }]}";
        Interface readInterface = objMapper.readValue(testJSON.getBytes(StandardCharsets.UTF_8), Interface.class);
        assertThat(readInterface).isEqualTo(new Interface(
                "Calculator",
                methodList
        ));
        assertThat(readInterface.getName()).isEqualTo("Calculator");
        for (int i = 0; i < readInterface.methods.size(); i++)
            assertThat(readInterface.getMethodByIndex(i)).isEqualTo(methodList.get(i));
    }
}
