package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.Address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A raw serializable Bid class.
 */
@JsonPropertyOrder(alphabetic=true)
public class Bid {
    @JsonProperty("bidder")
    public final Address bidderKey; // cannot be null
    @JsonProperty("auc")
    public final Address auctionKey; // cannot be null
    @JsonProperty("cur")
    public final BigInteger bidCurrency;
    @JsonProperty("price")
    public final BigInteger maxPrice;
    @JsonProperty("id")
    public final BigInteger bidID;
    @JsonProperty("aid")
    public final BigInteger auctionID;

    /**
     * Create a new bid
     * @param bidderKey
     * @param auctionKey
     * @param bidCurrency
     * @param maxPrice
     * @param bidID
     * @param auctionID
     */
    public Bid(Address bidderKey, Address auctionKey, BigInteger bidCurrency, BigInteger maxPrice, BigInteger bidID, BigInteger auctionID) {
        this.bidderKey = Objects.requireNonNull(bidderKey, "bidder key must not be null");
        this.auctionKey = Objects.requireNonNull(auctionKey, "auction key must not be null");
        this.bidCurrency = Objects.requireNonNull(bidCurrency, "bidCurrency must not be null");
        this.maxPrice = Objects.requireNonNull(maxPrice, "maxPrice must not be null");
        this.bidID = Objects.requireNonNull(bidID, "bidID must not be null");
        this.auctionID = Objects.requireNonNull(auctionID, "auctionID must not be null");
    }

    public Bid() {
        this.bidderKey = new Address();
        this.auctionKey = new Address();
        this.bidCurrency = BigInteger.valueOf(0);
        this.maxPrice = BigInteger.valueOf(0);
        this.bidID = BigInteger.valueOf(0);
        this.auctionID = BigInteger.valueOf(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return bidderKey.equals(bid.bidderKey) &&
                auctionKey.equals(bid.auctionKey) &&
                bidCurrency.equals(bid.bidCurrency) &&
                maxPrice.equals(bid.maxPrice) &&
                bidID.equals(bid.bidID) &&
                auctionID.equals(bid.auctionID);
    }

}
