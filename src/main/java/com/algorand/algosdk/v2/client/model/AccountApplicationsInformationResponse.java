package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountApplicationsInformationResponse contains a list of application resources
 * for an account.
 */
public class AccountApplicationsInformationResponse extends PathResponse {

    @JsonProperty("application-resources")
    public List<AccountApplicationResource> applicationResources = new ArrayList<AccountApplicationResource>();

    /**
     * Used for pagination, when making another request provide this token with the
     * next parameter. The next token is the next application ID to use as the
     * pagination cursor.
     */
    @JsonProperty("next-token")
    public String nextToken;

    /**
     * The round for which this information is relevant.
     */
    @JsonProperty("round")
    public Long round;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        AccountApplicationsInformationResponse other = (AccountApplicationsInformationResponse) o;
        if (!Objects.deepEquals(this.applicationResources, other.applicationResources)) return false;
        if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;

        return true;
    }
}
