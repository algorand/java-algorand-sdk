package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.*;

/**
 * DryrunSource is TEAL source text that gets uploaded, compiled, and inserted into
 * transactions or application state.
 */
public class DryrunSource extends PathResponse {

    @JsonProperty("app-index")
    public java.math.BigInteger appIndex;

    /**
     * FieldName is what kind of sources this is. If lsig then it goes into the
     * transactions[this.TxnIndex].LogicSig. If approv or clearp it goes into the
     * Approval Program or Clear State Program of application[this.AppIndex].
     */
    @JsonProperty("field-name")
    public String fieldName;

    @JsonProperty("source")
    public String source;

    @JsonProperty("txn-index")
    public Long txnIndex;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        DryrunSource other = (DryrunSource) o;
        if (!Objects.deepEquals(this.appIndex, other.appIndex)) return false;
        if (!Objects.deepEquals(this.fieldName, other.fieldName)) return false;
        if (!Objects.deepEquals(this.source, other.source)) return false;
        if (!Objects.deepEquals(this.txnIndex, other.txnIndex)) return false;

        return true;
    }
}
