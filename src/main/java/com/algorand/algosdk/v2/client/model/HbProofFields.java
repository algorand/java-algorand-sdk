package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * (hbprf) HbProof is a signature using HeartbeatAddress's partkey, thereby showing
 * it is online.
 */
public class HbProofFields extends PathResponse {

    /**
     * (p) Public key of the heartbeat message.
     */
    @JsonProperty("hb-pk")
    public void hbPk(String base64Encoded) {
        this.hbPk = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbPk() {
        return Encoder.encodeToBase64(this.hbPk);
    }
    public byte[] hbPk;

    /**
     * (p1s) Signature of OneTimeSignatureSubkeyOffsetID(PK, Batch, Offset) under the
     * key PK2.
     */
    @JsonProperty("hb-pk1sig")
    public void hbPk1sig(String base64Encoded) {
        this.hbPk1sig = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbPk1sig() {
        return Encoder.encodeToBase64(this.hbPk1sig);
    }
    public byte[] hbPk1sig;

    /**
     * (p2) Key for new-style two-level ephemeral signature.
     */
    @JsonProperty("hb-pk2")
    public void hbPk2(String base64Encoded) {
        this.hbPk2 = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbPk2() {
        return Encoder.encodeToBase64(this.hbPk2);
    }
    public byte[] hbPk2;

    /**
     * (p2s) Signature of OneTimeSignatureSubkeyBatchID(PK2, Batch) under the master
     * key (OneTimeSignatureVerifier).
     */
    @JsonProperty("hb-pk2sig")
    public void hbPk2sig(String base64Encoded) {
        this.hbPk2sig = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbPk2sig() {
        return Encoder.encodeToBase64(this.hbPk2sig);
    }
    public byte[] hbPk2sig;

    /**
     * (s) Signature of the heartbeat message.
     */
    @JsonProperty("hb-sig")
    public void hbSig(String base64Encoded) {
        this.hbSig = Encoder.decodeFromBase64(base64Encoded);
    }
    public String hbSig() {
        return Encoder.encodeToBase64(this.hbSig);
    }
    public byte[] hbSig;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        HbProofFields other = (HbProofFields) o;
        if (!Objects.deepEquals(this.hbPk, other.hbPk)) return false;
        if (!Objects.deepEquals(this.hbPk1sig, other.hbPk1sig)) return false;
        if (!Objects.deepEquals(this.hbPk2, other.hbPk2)) return false;
        if (!Objects.deepEquals(this.hbPk2sig, other.hbPk2sig)) return false;
        if (!Objects.deepEquals(this.hbSig, other.hbSig)) return false;

        return true;
    }
}
