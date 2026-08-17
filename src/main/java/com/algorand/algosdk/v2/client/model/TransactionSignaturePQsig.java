package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * structure holding a post-quantum signature.
 * Definition:
 * data/transactions/pqsig.go : PQSig
 */
public class TransactionSignaturePQsig extends PathResponse {

    /**
     * (pk)
     */
    @JsonProperty("public-key")
    public void publicKey(String base64Encoded) {
        this.publicKey = Encoder.decodeFromBase64(base64Encoded);
    }
    public String publicKey() {
        return Encoder.encodeToBase64(this.publicKey);
    }
    public byte[] publicKey;

    /**
     * (slt) a single byte, added to ensure the hashed address is not an Ed25519 curve
     * point
     */
    @JsonProperty("salt")
    public Long salt;

    /**
     * (sch) identifies the internal signature scheme.
     */
    @JsonProperty("scheme")
    public String scheme;

    /**
     * (sig)
     */
    @JsonProperty("signature")
    public void signature(String base64Encoded) {
        this.signature = Encoder.decodeFromBase64(base64Encoded);
    }
    public String signature() {
        return Encoder.encodeToBase64(this.signature);
    }
    public byte[] signature;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionSignaturePQsig other = (TransactionSignaturePQsig) o;
        if (!Objects.deepEquals(this.publicKey, other.publicKey)) return false;
        if (!Objects.deepEquals(this.salt, other.salt)) return false;
        if (!Objects.deepEquals(this.scheme, other.scheme)) return false;
        if (!Objects.deepEquals(this.signature, other.signature)) return false;

        return true;
    }
}
