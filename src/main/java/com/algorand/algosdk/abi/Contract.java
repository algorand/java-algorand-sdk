package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract {
    @JsonProperty("name")
    public String name;
    @JsonProperty("desc")
    public String description;
    @JsonProperty("networks")
    public Map<String, NetworkInfo> networks = new HashMap<>();
    @JsonProperty("methods")
    public List<Method> methods = new ArrayList<>();

    @JsonCreator
    public Contract(
            @JsonProperty("name") String name,
            @JsonProperty("desc") String description,
            @JsonProperty("networks") Map<String, NetworkInfo> networks,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = description;
        if (networks != null) this.networks = networks;
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
    public NetworkInfo getNetworkInfo(String genesisHash) {
        return this.networks.get(genesisHash);
    }

    @JsonIgnore
    public Method getMethodByIndex(int index) {
        return this.methods.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(name, contract.name) && Objects.equals(methods, contract.methods) &&
                Objects.equals(description, contract.description) && Objects.equals(networks, contract.networks);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static public class NetworkInfo {
        @JsonProperty("appID")
        public Long appID;

        @JsonCreator
        public NetworkInfo(@JsonProperty("appID") Long appID) {
            this.appID = Objects.requireNonNull(appID, "appID must not be null");
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) return false;
            NetworkInfo other = (NetworkInfo) o;
            return Objects.equals(appID, other.appID);
        }
    }
}
