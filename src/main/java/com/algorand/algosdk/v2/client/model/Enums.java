package com.algorand.algosdk.v2.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enums {

    /**
     * Combine with the address parameter to define what type of address to search for.
     */
    public enum AddressRole {
        @JsonProperty("sender") SENDER("sender"),
        @JsonProperty("receiver") RECEIVER("receiver"),
        @JsonProperty("freeze-target") FREEZETARGET("freeze-target");

        final String serializedName;
        AddressRole(String name) {
            this.serializedName = name;
        }

        @Override
        public String toString() {
            return this.serializedName;
        }
    }

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

    /**
     * SigType filters just results using the specified type of signature:
     *   sig - Standard
     *   msig - MultiSig
     *   lsig - LogicSig
     */
    public enum SigType {
        @JsonProperty("sig") SIG("sig"),
        @JsonProperty("msig") MSIG("msig"),
        @JsonProperty("lsig") LSIG("lsig");

        final String serializedName;
        SigType(String name) {
            this.serializedName = name;
        }

        @Override
        public String toString() {
            return this.serializedName;
        }
    }

    /**
     * (type) Indicates what type of transaction this is. Different types have
     * different fields.
     * Valid types, and where their fields are stored:
     *   (pay) payment-transaction
     *   (keyreg) keyreg-transaction
     *   (acfg) asset-config-transaction
     *   (axfer) asset-transfer-transaction
     *   (afrz) asset-freeze-transaction
     *   (appl) application-transaction
     */
    public enum TxType {
        @JsonProperty("pay") PAY("pay"),
        @JsonProperty("keyreg") KEYREG("keyreg"),
        @JsonProperty("acfg") ACFG("acfg"),
        @JsonProperty("axfer") AXFER("axfer"),
        @JsonProperty("afrz") AFRZ("afrz");

        final String serializedName;
        TxType(String name) {
            this.serializedName = name;
        }

        @Override
        public String toString() {
            return this.serializedName;
        }
    }

}
