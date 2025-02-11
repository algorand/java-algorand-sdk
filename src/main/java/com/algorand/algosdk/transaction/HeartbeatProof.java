package com.algorand.algosdk.transaction;

import java.io.Serializable;
import java.util.Objects;

import com.algorand.algosdk.crypto.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HeartbeatProof implements Serializable {
    @JsonProperty("s")
    public Signature sig = new Signature();

    @JsonProperty("p")
    public Ed25519PublicKey pk = new Ed25519PublicKey();

    @JsonProperty("p2")
    public Ed25519PublicKey pk2 = new Ed25519PublicKey();

    @JsonProperty("p1s")
    public Signature pk1Sig = new Signature();

    @JsonProperty("p2s")
    public Signature pk2Sig = new Signature();

    public HeartbeatProof() {}

    public HeartbeatProof(
        Signature sig,
        Ed25519PublicKey pk,
        Ed25519PublicKey pk2,
        Signature pk1Sig,
        Signature pk2Sig
    ) {
        this.sig = Objects.requireNonNull(sig, "sig must not be null");
        this.pk = Objects.requireNonNull(pk, "pk must not be null");
        this.pk2 = Objects.requireNonNull(pk2, "pk2 must not be null");
        this.pk1Sig = Objects.requireNonNull(pk1Sig, "pk1Sig must not be null");
        this.pk2Sig = Objects.requireNonNull(pk2Sig, "pk2Sig must not be null");
    }

    @JsonCreator
    public HeartbeatProof(
        @JsonProperty("s") byte[] sig,
        @JsonProperty("p") byte[] pk,
        @JsonProperty("p2") byte[] pk2,
        @JsonProperty("p1s") byte[] pk1Sig,
        @JsonProperty("p2s") byte[] pk2Sig
    ) {
        this.sig = new Signature(sig);
        this.pk = new Ed25519PublicKey(pk);
        this.pk2 = new Ed25519PublicKey(pk2);
        this.pk1Sig = new Signature(pk1Sig);
        this.pk2Sig = new Signature(pk2Sig);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeartbeatProof that = (HeartbeatProof) o;
        return Objects.equals(sig, that.sig) &&
                Objects.equals(pk, that.pk) &&
                Objects.equals(pk2, that.pk2) &&
                Objects.equals(pk1Sig, that.pk1Sig) &&
                Objects.equals(pk2Sig, that.pk2Sig);
    }

}