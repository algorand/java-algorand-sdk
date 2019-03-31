package com.algorand.algosdk.auction;

import java.util.Objects;

/**
 * A raw serializable Bid class.
 */
public class Bid {
    private static final int BID_ADDR_LEN = 32;

    public final byte[] bidderKey = new byte[BID_ADDR_LEN];
    public final byte[] auctionKey = new byte[BID_ADDR_LEN];
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
    public Bid(byte[] bidderKey, byte[] auctionKey, long bidCurrency, long maxPrice, long bidID, long auctionID) {
        Objects.requireNonNull(bidderKey, "bidder key must not be null");
        Objects.requireNonNull(auctionKey, "auction key must not be null");
        if (bidderKey.length != BID_ADDR_LEN) {
            throw new IllegalArgumentException("bidder key wrong length");
        }
        if (auctionKey.length != BID_ADDR_LEN) {
            throw new IllegalArgumentException("auction key wrong length");
        }
        System.arraycopy(bidderKey, 0, this.bidderKey, 0, BID_ADDR_LEN);
        System.arraycopy(auctionKey, 0, this.auctionKey, 0, BID_ADDR_LEN);
        this.bidCurrency = bidCurrency;
        this.maxPrice = maxPrice;
        this.bidID = bidID;
        this.auctionID = auctionID;
    }
}
