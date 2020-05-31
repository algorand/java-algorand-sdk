package com.algorand.algosdk.logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class StateSchema {
    @JsonProperty("nui")
    BigInteger numUint;

    @JsonProperty("nbs")
    BigInteger numByteSlice;

    public StateSchema(BigInteger numUint, BigInteger numByteSlice) {
        this.numUint = numUint;
        this.numByteSlice = numByteSlice;
    }

    public StateSchema(Long numUint, Long numByteSlice) {
        this(BigInteger.valueOf(numUint), BigInteger.valueOf(numByteSlice));
    }
}
