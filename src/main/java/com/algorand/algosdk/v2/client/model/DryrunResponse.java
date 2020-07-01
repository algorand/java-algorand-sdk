package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.algorand.algosdk.v2.client.model.DryrunTxnResult;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DryrunResponse extends PathResponse {

    @JsonProperty("error")
    public String error;

    @JsonProperty("txns")
    public List<DryrunTxnResult> txns = new ArrayList<DryrunTxnResult>();

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunResponse other = (DryrunResponse) o;
        if (!Objects.deepEquals(this.error, other.error)) return false;
        if (!Objects.deepEquals(this.txns, other.txns)) return false;

        return true;
    }
}
