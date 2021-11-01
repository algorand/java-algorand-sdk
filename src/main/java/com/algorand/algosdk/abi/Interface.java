package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Interface {
    @JsonProperty("name")
    public String name;
    @JsonProperty("methods")
    public List<Method> methods;

    @JsonCreator
    public Interface(
            @JsonProperty("name") String name,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.methods = Objects.requireNonNull(methods, "methods must not be null");
    }

    // default values for serializer to ignore
    public Interface() {
    }
}
