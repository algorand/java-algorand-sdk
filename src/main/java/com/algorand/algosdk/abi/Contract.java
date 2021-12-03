package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract {
    @JsonProperty("name")
    public String name;
    @JsonProperty("appId")
    public Integer appId;
    @JsonProperty("methods")
    public List<Method> methods = new ArrayList<>();

    @JsonCreator
    public Contract(
            @JsonProperty("name") String name,
            @JsonProperty("appId") Integer appId,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.appId = Objects.requireNonNull(appId, "name must not be null");
        if (methods != null) this.methods = methods;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public Integer getAppId() {
        return this.appId;
    }

    @JsonIgnore
    public Method getMethodByIndex(int index) {
        return this.methods.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(name, contract.name) && Objects.equals(appId, contract.appId) && Objects.equals(methods, contract.methods);
    }
}
