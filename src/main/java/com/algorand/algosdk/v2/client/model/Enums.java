package com.algorand.algosdk.v2.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enums {

    /**
     * (apan) defines the what additional actions occur with the transaction.
     * Valid types:
     *   noop
     *   optin
     *   closeout
     *   clear
     *   update
     *   update
     *   delete
     */
    public enum OnCompletion {
        @JsonProperty("noop") NOOP("noop"),
        @JsonProperty("optin") OPTIN("optin"),
        @JsonProperty("closeout") CLOSEOUT("closeout"),
        @JsonProperty("clear") CLEAR("clear"),
        @JsonProperty("update") UPDATE("update"),
        @JsonProperty("delete") DELETE("delete");

        final String serializedName;
        OnCompletion(String name) {
            this.serializedName = name;
        }

        @Override
        public String toString() {
            return this.serializedName;
        }
    }

}
