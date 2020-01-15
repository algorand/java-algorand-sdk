package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.util.Digester;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * TxGroup exports computeGroupID and assignGroupID functions
 */
@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TxGroup implements Serializable{
    private static final byte[] TG_PREFIX = ("TG").getBytes(StandardCharsets.UTF_8);

    @JsonProperty("txlist")
    private Digest[] txGroupHashes;

    /**
     * Compute group ID for a group of unsigned transactions
     * @param txns array of transactions
     * @return Digest
     */
    public static Digest computeGroupID(Transaction ...txns) throws IOException, IllegalArgumentException {
        if (txns == null || txns.length == 0) {
            throw new IllegalArgumentException("empty transaction list");
        }
        Digest[] txIDs = new Digest[txns.length];
        for (int i = 0; i < txns.length; i++) {
            txIDs[i] = txns[i].rawTxID();
        }

        TxGroup txgroup = new TxGroup(txIDs);
        try {
            byte[] gid = Digester.digest(txgroup.bytesToSign());
            return new Digest(gid);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("tx computation failed", e);
        }
    }

    /**
     * Assigns group id to a given array of unsigned transactions
     * @param txns array of transactions
     * @param address optional sender address specifying which transaction return
     * @return array of grouped transactions, optionally filtered with the address parameter.
     */
    public static Transaction[] assignGroupID(Address address, Transaction ...txns) throws IOException {
        return assignGroupID(txns, address);
    }

    /**
     * Assigns group id to a given array of unsigned transactions
     * @param txns array of transactions
     * @param address optional sender address specifying which transaction return
     * @return array of grouped transactions, optionally filtered with the address parameter.
     *
     * @Deprecated use assignGroupID(address, Transaction ...txns)
     */
    @Deprecated // Jan 8, 2020
    public static Transaction[] assignGroupID(
        Transaction[] txns, Address address
    ) throws IOException {
        Digest gid = TxGroup.computeGroupID(txns);
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        for (Transaction tx : txns) {
            if (address == null || address.toString() == "" || address == tx.sender) {
                tx.assignGroupID(gid);
                result.add(tx);
            }
        }
        return result.toArray(new Transaction[result.size()]);
    }

    @JsonCreator
    private TxGroup(@JsonProperty("txlist") Digest[] txGroupHashes) {
        this.txGroupHashes = txGroupHashes;
    }

    /**
     * Return encoded representation of the transaction with a prefix
     * suitable for signing
     */
    private byte[] bytesToSign() throws IOException {
        try {
            byte[] encodedTx = Encoder.encodeToMsgPack(this);
            byte[] prefixEncodedTx = Arrays.copyOf(TG_PREFIX, TG_PREFIX.length + encodedTx.length);
            System.arraycopy(encodedTx, 0, prefixEncodedTx, TG_PREFIX.length, encodedTx.length);
            return prefixEncodedTx;
        } catch (IOException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }
}
