package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Contract {
    @JsonProperty("name")
    public String name;
    @JsonProperty("appId")
    public BigInteger appId;
    @JsonProperty("methods")
    public List<Method> methods;

    @JsonCreator
    public Contract(
            @JsonProperty("name") String name,
            @JsonProperty("appId") BigInteger appId,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.appId = Objects.requireNonNull(appId, "name must not be null");
        this.methods = Objects.requireNonNull(methods, "methods must not be null");
    }

    // default values for serializer to ignore
    public Contract() {
    }
}
