package com.algorand.algosdk.auction;

import com.algorand.algosdk.crypto.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;

/**
 * A raw serializable Bid class.
 */
@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Bid {
    @JsonProperty("bidder")
    public Address bidderKey = new Address(); // cannot be null
    @JsonProperty("auc")
    public Address auctionKey = new Address(); // cannot be null
    @JsonProperty("cur")
    public BigInteger bidCurrency = BigInteger.valueOf(0);
    @JsonProperty("price")
    public BigInteger maxPrice = BigInteger.valueOf(0);
    @JsonProperty("id")
    public BigInteger bidID = BigInteger.valueOf(0);
    @JsonProperty("aid")
    public BigInteger auctionID = BigInteger.valueOf(0);

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
        if (bidderKey != null) this.bidderKey = bidderKey;
        if (auctionKey != null) this.auctionKey = auctionKey;
        if (bidCurrency != null) this.bidCurrency = bidCurrency;
        if (maxPrice != null) this.maxPrice = maxPrice;
        if (bidID != null) this.bidID = bidID;
        if (auctionID != null) this.auctionID = auctionID;
    }

    @JsonCreator
    public Bid(
            @JsonProperty("bidder") byte[] bidderKey,
            @JsonProperty("auc") byte[] auctionKey,
            @JsonProperty("cur") BigInteger bidCurrency,
            @JsonProperty("price") BigInteger maxPrice,
            @JsonProperty("id") BigInteger bidID,
            @JsonProperty("aid") BigInteger auctionID) {
        if (bidderKey != null) this.bidderKey = new Address(bidderKey);
        if (auctionKey != null) this.auctionKey = new Address(auctionKey);
        if (bidCurrency != null) this.bidCurrency = bidCurrency;
        if (maxPrice != null) this.maxPrice = maxPrice;
        if (bidID != null) this.bidID = bidID;
        if (auctionID != null) this.auctionID = auctionID;
    }

    public Bid() {
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
