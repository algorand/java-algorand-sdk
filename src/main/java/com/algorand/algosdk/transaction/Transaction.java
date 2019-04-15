package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 */
@JsonPropertyOrder(alphabetic=true)
public class Transaction {
    @JsonProperty("type")
    public final Type type;

    // Instead of embedding POJOs and using JsonUnwrapped, we explicitly export inner fields. This circumvents our encoders'
    // inability to sort child fields.
    /* header fields */
    @JsonProperty("snd")
    public final Address sender;
    @JsonProperty("fee")
    public final BigInteger fee;
    @JsonProperty("fv")
    public final BigInteger firstValid;
    @JsonProperty("lv")
    public final BigInteger lastValid;
    @JsonProperty("note")
    public final byte[] note;
    @JsonProperty("gen")
    public final String genesisID;
    @JsonProperty("gh")
    public final Digest genesisHash;

    /* payment fields */
    @JsonProperty("amt")
    public final BigInteger amount;
    @JsonProperty("rcv")
    public final Address receiver;
    @JsonProperty("close")
    public final Address closeRemainderTo; // can be null, optional

    /* keyreg fields */
    // VotePK is the participation public key used in key registration transactions
    @JsonProperty("votekey")
    public final ParticipationPublicKey votePK;
    // selectionPK is the VRF public key used in key registration transactions
    @JsonProperty("selkey")
    public final VRFPublicKey selectionPK;

    /**
     * Create a payment transaction
     * @param fromAddr source address
     * @param toAddr destination address
     * @param fee transaction fee
     * @param amount payment amount
     * @param firstRound first valid round
     * @param lastRound last valid round
     */
    public Transaction(Address fromAddr, Address toAddr, BigInteger fee, BigInteger amount, BigInteger firstRound,
                       BigInteger lastRound) {
        this(fromAddr, fee, firstRound, lastRound, null, amount, toAddr, "", new Digest());
    }

    public Transaction(Address fromAddr, Address toAddr, BigInteger fee, BigInteger amount, BigInteger firstRound,
                       BigInteger lastRound,
                       String genesisID, Digest genesisHash) {
        this(fromAddr, fee, firstRound, lastRound, null, amount, toAddr, genesisID, genesisHash);
    }

    // Helper with long types
    public Transaction(Address fromAddr, Address toAddr, long fee, long amount, long firstRound, long lastRound,
                       String genesisID, Digest genesisHash) {
        this(fromAddr, BigInteger.valueOf(fee), BigInteger.valueOf(firstRound), BigInteger.valueOf(lastRound), null, BigInteger.valueOf(amount), toAddr, genesisID, genesisHash);
    }

    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       BigInteger amount, Address receiver, String genesisID, Digest genesisHash) {
        this(sender, fee, firstValid, lastValid, note, genesisID, genesisHash, amount, receiver, new Address());
    }

    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note, String genesisID, Digest genesisHash,
                       BigInteger amount, Address receiver, Address closeRemainderTo) {
        this(Type.Payment, sender, fee, firstValid, lastValid, note, genesisID, genesisHash, amount, receiver, closeRemainderTo,
                new ParticipationPublicKey(), new VRFPublicKey());
    }

    /**
     * Create a key registration transaction. No field can be null except the note field.
     * @param sender source address
     * @param fee transaction fee
     * @param firstValid first valid round
     * @param lastValid last valid round
     * @param note optional notes field (can be null)
     * @param votePK the new participation key to register
     * @param vrfPK the sortition key to register
     */
    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       String genesisID, Digest genesisHash,
                       ParticipationPublicKey votePK, VRFPublicKey vrfPK) {
        // populate with default values which will be ignored...
        this(Type.KeyRegistration, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), votePK, vrfPK);
    }

    private Transaction(Type type,
                       Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note, String genesisID, Digest genesisHash,
                       BigInteger amount, Address receiver, Address closeRemainderTo, ParticipationPublicKey votePK, VRFPublicKey vrfPK) {
        this.type = Objects.requireNonNull(type, "txtype must not be null");
        // header fields
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.genesisID = Objects.requireNonNull(genesisID, "genesisID must not be null");
        this.fee = Objects.requireNonNull(fee, "fee must not be null");
        this.firstValid = Objects.requireNonNull(firstValid, "firstValid must not be null");
        this.lastValid = Objects.requireNonNull(lastValid, "lastValid must not be null");
        this.genesisHash = Objects.requireNonNull(genesisHash, "genesisHash must not be null");
        // payment fields
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
        this.closeRemainderTo = Objects.requireNonNull(closeRemainderTo, "closeRemainderTo must not be null");
        this.note = note; // can be null, since it matches golang's default value
        // keyreg fields
        this.votePK = Objects.requireNonNull(votePK, "votePK must not be null");
        this.selectionPK = Objects.requireNonNull(vrfPK, "selectionPK must not be null");
    }

    // default values for serializer to ignore
    public Transaction() {
        this.type = Type.Default;
        this.sender = new Address();
        this.genesisID = "";
        this.genesisHash = new Digest();
        this.fee = BigInteger.valueOf(0);
        this.firstValid = BigInteger.valueOf(0);
        this.lastValid = BigInteger.valueOf(0);
        this.note = null;
        this.amount = BigInteger.valueOf(0);
        this.receiver = new Address();
        this.closeRemainderTo = new Address();
        this.votePK = new ParticipationPublicKey();
        this.selectionPK = new VRFPublicKey();
    }

    /**
     * TxType represents a transaction type.
     */
    public enum Type {
        Default(""),
        Payment("pay"),
        KeyRegistration("keyreg");

        private final String value;
        private Type(String value) {
            this.value = value;
        }

        /**
         * Get underlying string value
         * @return String the string repr of this txtype
         */
        @JsonValue
        public String getValue() {
            return this.value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return type == that.type &&
                sender.equals(that.sender) &&
                fee.equals(that.fee) &&
                firstValid.equals(that.firstValid) &&
                lastValid.equals(that.lastValid) &&
                Arrays.equals(note, that.note) &&
                genesisID.equals(that.genesisID) &&
                genesisHash.equals(that.genesisHash) &&
                amount.equals(that.amount) &&
                receiver.equals(that.receiver) &&
                closeRemainderTo.equals(that.closeRemainderTo) &&
                votePK.equals(that.votePK) &&
                selectionPK.equals(that.selectionPK);
    }

}
