package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

public class ApplicationLogsResponse extends PathResponse {

    /**
     * (appidx) application index.
     */
    @JsonProperty("application-id")
    public Long applicationId;

    /**
     * Round at which the results were computed.
     */
    @JsonProperty("current-round")
    public Long currentRound;

    @JsonProperty("log-data")
    public List<ApplicationLogData> logData = new ArrayList<ApplicationLogData>();

    /**
     * Used for pagination, when making another request provide this token with the
     * next parameter.
     */
    @JsonProperty("next-token")
    public String nextToken;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        ApplicationLogsResponse other = (ApplicationLogsResponse) o;
        if (!Objects.deepEquals(this.applicationId, other.applicationId)) return false;
        if (!Objects.deepEquals(this.currentRound, other.currentRound)) return false;
        if (!Objects.deepEquals(this.logData, other.logData)) return false;
        if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;

        return true;
    }
}
