package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * A serializable raw signed bid class.
 */
@JsonPropertyOrder(alphabetic=true)
public class SignedBid {
    @JsonProperty("bid")
    public Bid bid = new Bid();
    @JsonProperty("sig")
    public Signature sig = new Signature();
    @JsonProperty("msig")
    public MultisigSignature mSig = new MultisigSignature();

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

    public SignedBid() {
    }

    @JsonCreator
    public SignedBid(
            @JsonProperty("bid") Bid bid,
            @JsonProperty("sig") byte[] sig,
            @JsonProperty("msig") MultisigSignature mSig
    )
    {
        if (bid != null) this.bid = bid;
        if (sig != null) this.sig = new Signature(sig);
        if (mSig != null) this.mSig = mSig;
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
