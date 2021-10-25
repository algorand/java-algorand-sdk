package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * A serializable representation of a state proof key.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Verifier implements Serializable {

    @JsonProperty("r")
    private byte[] root;
    @JsonProperty("vr")
    private boolean hasValidRoot;
    /**
     * Create a new state proof key.
     * @param root a length 64 byte array.
     * @param hasValidRoot a boolean.
     */
    @JsonCreator
    public Verifier(@JsonProperty("r") byte[] root, @JsonProperty("vr") boolean hasValidRoot) {
        this.root= root;
        this.hasValidRoot= hasValidRoot;
    }
    // default values for serializer to ignore
    public Verifier() {
        root = null;
        hasValidRoot = false;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Verifier) {
            if(((Verifier) obj).hasValidRoot == this.hasValidRoot && ((Verifier) obj).root==this.root){
                return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }
}
