package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The logged messages from an app call along with the app ID and outer transaction
 * ID. Logs appear in the same order that they were emitted.
 */
public class AppCallLogs extends PathResponse {

    /**
     * The application from which the logs were generated
     */
    @JsonProperty("application-index")
    public Long applicationIndex;

    /**
     * An array of logs
     */
    @JsonProperty("logs")
    public List<byte[]> logs = new ArrayList<byte[]>();
    @JsonIgnore
    public void logs(List<String> base64Encoded) {
        this.logs = new ArrayList<byte[]>();
        for (String val : base64Encoded) {
            this.logs.add(Encoder.decodeFromBase64(val));
        }
    }
    @JsonIgnore
    public List<String> logs() {
        ArrayList<String> ret = new ArrayList<String>();
        for (byte[] val : this.logs) {
            ret.add(Encoder.encodeToBase64(val));
        }
        return ret; 
    }

    /**
     * The transaction ID of the outer app call that lead to these logs
     */
    @JsonProperty("txId")
    public String txId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AppCallLogs other = (AppCallLogs) o;
        if (!Objects.deepEquals(this.applicationIndex, other.applicationIndex)) return false;
        if (!Objects.deepEquals(this.logs, other.logs)) return false;
        if (!Objects.deepEquals(this.txId, other.txId)) return false;

        return true;
    }
}
