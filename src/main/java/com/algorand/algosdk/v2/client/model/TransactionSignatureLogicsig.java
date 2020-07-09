package com.algorand.algosdk.v2.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * (lsig) Programatic transaction signature.
 * Definition:
 * data/transactions/logicsig.go
 */
public class TransactionSignatureLogicsig extends PathResponse {

    /**
     * (arg) Logic arguments, base64 encoded.
     */
    @JsonProperty("args")
    public void args(List<String> base64Encoded) {
         this.args = new ArrayList<byte[]>();
         for (String val : base64Encoded) {
             this.args.add(Encoder.decodeFromBase64(val));
         }
     }
     @JsonProperty("args")
     public List<String> args() {
         ArrayList<String> ret = new ArrayList<String>();
         for (byte[] val : this.args) {
             ret.add(Encoder.encodeToBase64(val));
         }
         return ret; 
     }
    public List<byte[]> args;

    /**
     * (l) Program signed by a signature or multi signature, or hashed to be the
     * address of ana ccount. Base64 encoded TEAL program.
     */
    @JsonProperty("logic")
    public void logic(String base64Encoded) {
        this.logic = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("logic")
    public String logic() {
        return Encoder.encodeToBase64(this.logic);
    }
    public byte[] logic;

    @JsonProperty("multisig-signature")
    public TransactionSignatureMultisig multisigSignature;

    /**
     * (sig) ed25519 signature.
     */
    @JsonProperty("signature")
    public void signature(String base64Encoded) {
        this.signature = Encoder.decodeFromBase64(base64Encoded);
    }
    @JsonProperty("signature")
    public String signature() {
        return Encoder.encodeToBase64(this.signature);
    }
    public byte[] signature;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;

        TransactionSignatureLogicsig other = (TransactionSignatureLogicsig) o;
        if (!Objects.deepEquals(this.args, other.args)) return false;
        if (!Objects.deepEquals(this.logic, other.logic)) return false;
        if (!Objects.deepEquals(this.multisigSignature, other.multisigSignature)) return false;
        if (!Objects.deepEquals(this.signature, other.signature)) return false;

        return true;
    }
}
