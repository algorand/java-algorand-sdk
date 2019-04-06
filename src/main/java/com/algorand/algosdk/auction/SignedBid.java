package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A serializable raw signed bid class.
 */
public class SignedBid {
    @JsonProperty("bid")
    public final Bid bid;
    @JsonProperty("sig")
    public final Signature sig;
    @JsonProperty("msig")
    public final MultisigSignature mSig;

    public SignedBid(Bid bid, Signature sig, MultisigSignature mSig) {
        this.bid = Objects.requireNonNull(bid, "tx must not be null");
        this.mSig = Objects.requireNonNull(mSig, "mSig must not be null");
        this.sig = Objects.requireNonNull(sig, "sig must not be null");
    }

    public SignedBid(Bid bid, Signature sig) {
        this(bid, sig, new MultisigSignature());
    }

    public SignedBid(Bid bid, MultisigSignature mSig) {
        this(bid, new Signature(), mSig);
    }

    // default constructor for encoding analysis
    public SignedBid() {
        this.bid = new Bid();
        this.sig = new Signature();
        this.mSig = new MultisigSignature();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedBid signedBid = (SignedBid) o;
        return Objects.equals(bid, signedBid.bid) &&
                Objects.equals(sig, signedBid.sig) &&
                Objects.equals(mSig, signedBid.mSig);
    }

}
