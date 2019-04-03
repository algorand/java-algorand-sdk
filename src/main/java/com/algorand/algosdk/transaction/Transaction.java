package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;
import com.fasterxml.jackson.annotation.*;

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
    public final Address sender; // not null (should never serialize tx without sender)
    @JsonProperty("fee")
    public final long fee;
    @JsonProperty("fv")
    public final long firstValid;
    @JsonProperty("lv")
    public final long lastValid;
    @JsonProperty("note")
    public final byte[] note; // can be null (optional)
    @JsonProperty("gen")
    public final String genesisID;

    /* payment fields */
    @JsonProperty("amt")
    public final long amount;
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
    public Transaction(Address fromAddr, Address toAddr, long fee, long amount, long firstRound,
                       long lastRound) {
        this(fromAddr, fee, firstRound, lastRound, null, amount, toAddr);
    }

    public Transaction(Address sender, long fee, long firstValid, long lastValid, byte[] note,
                       long amount, Address receiver) {
        this(sender, fee, firstValid, lastValid, note, null, amount, receiver, null);
    }

    public Transaction(Address sender, long fee, long firstValid, long lastValid, byte[] note, String genesisID,
                       long amount, Address receiver, Address closeRemainderTo) {
        this(Type.Payment, sender, fee, firstValid, lastValid, note, genesisID, amount, receiver, closeRemainderTo, null, null);
        Objects.requireNonNull(receiver, "receiver must not be null");
    }

    /**
     * Create a key registration transaction
     * @param sender source address
     * @param fee transaction fee
     * @param firstValid first valid round
     * @param lastValid last valid round
     * @param note optional notes field (can be null)
     * @param votePK the new participation key to register
     * @param vrfPK the sortition key to register
     */
    public Transaction(Address sender, long fee, long firstValid, long lastValid, byte[] note,
                       ParticipationPublicKey votePK, VRFPublicKey vrfPK) {
        this(Type.KeyRegistration, sender, fee, firstValid, lastValid, note, null, 0, null, null, votePK, vrfPK);
        Objects.requireNonNull(votePK, "participation key must not be null");
        Objects.requireNonNull(vrfPK, "selection key must not be null");
    }

    private Transaction(Type type,
                       Address sender, long fee, long firstValid, long lastValid, byte[] note, String genesisID,
                       long amount, Address receiver, Address closeRemainderTo, ParticipationPublicKey votePK, VRFPublicKey vrfPK) {
        this.type = Objects.requireNonNull(type, "txtype must not be null");
        // header fields
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.genesisID = genesisID;
        this.fee = fee;
        this.firstValid = firstValid;
        this.lastValid = lastValid;
        this.note = note;
        // payment fields
        this.amount = amount;
        this.receiver = receiver;
        this.closeRemainderTo = closeRemainderTo;
        // keyreg fields
        this.votePK = votePK;
        this.selectionPK = vrfPK;
    }

    /**
     * TxType represents a transaction type.
     */
    public enum Type {
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



}
