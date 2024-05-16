package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AccountAssetsInformationResponse contains a list of assets held by an account.
 */
public class AccountAssetsInformationResponse extends PathResponse {

    @JsonProperty("asset-holdings")
    public List<AccountAssetHolding> assetHoldings = new ArrayList<AccountAssetHolding>();

    /**
     * Used for pagination, when making another request provide this token with the
     * next parameter.
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

        AccountAssetsInformationResponse other = (AccountAssetsInformationResponse) o;
        if (!Objects.deepEquals(this.assetHoldings, other.assetHoldings)) return false;
        if (!Objects.deepEquals(this.nextToken, other.nextToken)) return false;
        if (!Objects.deepEquals(this.round, other.round)) return false;

        return true;
    }
}
