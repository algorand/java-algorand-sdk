package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Interface {
    @JsonProperty("name")
    public String name;
    @JsonProperty("desc")
    public String description;
    @JsonProperty("methods")
    public List<Method> methods = new ArrayList<>();

    @JsonCreator
    public Interface(
            @JsonProperty("name") String name,
            @JsonProperty("desc") String description,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = description;
        if (methods != null) this.methods = methods;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public String getDescription() {
        return this.description;
    }

    @JsonIgnore
    public Method getMethodByIndex(int index) {
        return this.methods.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Interface that = (Interface) o;
        return Objects.equals(name, that.name) && Objects.equals(methods, that.methods);
    }
}
