package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Validation signature associated with some data. Only one of the signatures
 * should be provided.
 */
public class TransactionSignature extends PathResponse {

    /**
     * (lsig) Programatic transaction signature.
     * Definition:
     * data/transactions/logicsig.go
     */
    @JsonProperty("logicsig")
    public TransactionSignatureLogicsig logicsig;

    /**
     * (msig) structure holding multiple subsignatures.
     * Definition:
     * crypto/multisig.go : MultisigSig
     */
    @JsonProperty("multisig")
    public TransactionSignatureMultisig multisig;

    /**
     * (sig) Standard ed25519 signature.
     */
    @JsonProperty("sig")
    public void sig(String base64Encoded) {
        this.sig = Encoder.decodeFromBase64(base64Encoded);
    }
    public String sig() {
        return Encoder.encodeToBase64(this.sig);
    }
    public byte[] sig;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionSignature other = (TransactionSignature) o;
        if (!Objects.deepEquals(this.logicsig, other.logicsig)) return false;
        if (!Objects.deepEquals(this.multisig, other.multisig)) return false;
        if (!Objects.deepEquals(this.sig, other.sig)) return false;

        return true;
    }
}
