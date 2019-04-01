package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A serializable raw signed bid class.
 */
public class SignedBid {
    @JsonProperty("bid")
    public final Bid bid;
    @JsonProperty("sig")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public final Signature sig; // can be null
    @JsonProperty("msig")
    public final MultisigSignature mSig; // cannot be null due to the way we serialize in go-codec

    public SignedBid(Bid bid, Signature sig, MultisigSignature mSig) {
        this.bid = Objects.requireNonNull(bid, "tx must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
        this.sig = sig;
    }

    public SignedBid(Bid bid, Signature sig) {
        this(bid, sig, new MultisigSignature(0, 0, null));
    }

    public SignedBid(Bid bid, MultisigSignature mSig) {
        this(bid, null, mSig);
    }
}
