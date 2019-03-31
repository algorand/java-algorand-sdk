package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.Address;

import java.util.Objects;

/**
 * A raw serializable Bid class.
 */
public class Bid {
    public final Address bidderKey;
    public final Address auctionKey;
    public final long bidCurrency;
    public final long maxPrice;
    public final long bidID;
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
