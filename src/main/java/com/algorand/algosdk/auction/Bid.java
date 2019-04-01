package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.Address;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A raw serializable Bid class.
 */
public class Bid {
    @JsonProperty("bidder")
    public final Address bidderKey; // cannot be null
    @JsonProperty("auc")
    public final Address auctionKey; // cannot be null
    @JsonProperty("cur")
    public final long bidCurrency;
    @JsonProperty("price")
    public final long maxPrice;
    @JsonProperty("id")
    public final long bidID;
    @JsonProperty("aid")
    public final long auctionID;

    /**
     * Create a new bid
     * @param bidderKey
     * @param auctionKey
     * @param bidCurrency
     * @param maxPrice
     * @param bidID
     * @param auctionID
     */
    public Bid(Address bidderKey, Address auctionKey, long bidCurrency, long maxPrice, long bidID, long auctionID) {
        this.bidderKey = Objects.requireNonNull(bidderKey, "bidder key must not be null");
        this.auctionKey = Objects.requireNonNull(auctionKey, "auction key must not be null");
        this.bidCurrency = bidCurrency;
        this.maxPrice = maxPrice;
        this.bidID = bidID;
        this.auctionID = auctionID;
    }
}
