package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the global information associated with an application.
 */
public class ApplicationLogData extends PathResponse {

    /**
     * (lg) Logs for the application being executed by the transaction.
     */
    @JsonProperty("logs")
    public void logs(List<String> base64Encoded) {
         this.logs = new ArrayList<byte[]>();
         for (String val : base64Encoded) {
             this.logs.add(Encoder.decodeFromBase64(val));
         }
     }
     @JsonProperty("logs")
     public List<String> logs() {
         ArrayList<String> ret = new ArrayList<String>();
         for (byte[] val : this.logs) {
             ret.add(Encoder.encodeToBase64(val));
         }
         return ret; 
     }
    public List<byte[]> logs = new ArrayList<byte[]>();

    /**
     * Transaction ID
     */
    @JsonProperty("txid")
    public String txid;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationLogData other = (ApplicationLogData) o;
        if (!Objects.deepEquals(this.logs, other.logs)) return false;
        if (!Objects.deepEquals(this.txid, other.txid)) return false;

        return true;
    }
}
