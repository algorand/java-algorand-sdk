package com.algorand.algosdk.abi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract {
    @JsonProperty("name")
    public String name;
    @JsonProperty("networks")
    public Map<String, AppID> networks;
    @JsonProperty("methods")
    public List<Method> methods = new ArrayList<>();

    @JsonCreator
    public Contract(
            @JsonProperty("name") String name,
            @JsonProperty("networks") Map<String, AppID> networks,
            @JsonProperty("methods") List<Method> methods
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.networks = networks;
        if (methods != null) this.methods = methods;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public Integer getAppIDbyHash(String genesisHash) {
        return this.networks.get(genesisHash).appID;
    }

    @JsonIgnore
    public Method getMethodByIndex(int index) {
        return this.methods.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        if (!contract.networks.keySet().equals(this.networks.keySet())) return false;
        for (String networkHash : this.networks.keySet()) {
            if (!contract.getAppIDbyHash(networkHash).equals(this.getAppIDbyHash(networkHash)))
                return false;
        }
        return Objects.equals(name, contract.name) && Objects.equals(methods, contract.methods);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static public class AppID {
        @JsonProperty("appID")
        public Integer appID;

        @JsonCreator
        public AppID(@JsonProperty("appID") Integer appID) {
            this.appID = Objects.requireNonNull(appID, "appID must not be null");
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) return false;
            AppID otherAppID = (AppID) o;
            return Objects.equals(appID, otherAppID.appID);
        }
    }
}
