package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 * TODO msgpack codec tags
 */
public class Transaction {
    @JsonProperty("type")
    public final Type type;
    @JsonUnwrapped
    public final Header header;
    @JsonUnwrapped
    public final PaymentTxnFields paymentTxnFields; // optional
    @JsonUnwrapped
    public final KeyregTxnFields keyregTxnFields; // optional
    @JsonProperty("amt")
    public final long amount; // not optional, even for keyreg, because of the way we serialize transactions in golang.

    public Transaction(Type type, Header header, long amount, PaymentTxnFields paymentTxnFields, KeyregTxnFields keyregTxnFields) {
        // we could do this with annotations but let's avoid pulling in dependencies
        this.type = Objects.requireNonNull(type, "txtype must not be null");
        this.header = Objects.requireNonNull(header, "header must not be null");
        this.keyregTxnFields = keyregTxnFields;
        this.paymentTxnFields = paymentTxnFields;
        this.amount = amount;
    }

    public Transaction(Address fromAddr, Address toAddr, long fee, long amount, long firstRound,
                       long lastRound, byte[] note, Address closeRemainderTo, String genesisID) {
        this.type = Type.Payment;
        this.header = new Header(fromAddr, fee, firstRound, lastRound, note, genesisID);
        this.paymentTxnFields = new PaymentTxnFields(toAddr, closeRemainderTo);
        this.amount = amount;
        this.keyregTxnFields = null;
    }

    public Transaction(Address fromAddr, Address toAddr, long fee, long amount, long firstRound,
                       long lastRound) {
        this(fromAddr, toAddr, fee, amount, firstRound, lastRound, null, null, "");
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

    /**
     * Fields common to all transactions.
     */
    static public class Header {
        @JsonProperty("snd")
        public final Address sender; // not null (should never serialize header without sender)
        @JsonProperty("fee")
        public final long fee;
        @JsonProperty("fv")
        public final long firstValid;
        @JsonProperty("lv")
        public final long lastValid;
        @JsonProperty("note")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public final byte[] note; // can be null (optional)
        @JsonProperty("gen")
        public final String genesisID;

        public Header(Address sender, long fee, long firstValid, long lastValid, byte[] note, String genesisID) {
            this.sender = Objects.requireNonNull(sender, "sender must not be null");
            this.genesisID = Objects.requireNonNull(genesisID, "genesis ID must not be null");
            this.fee = fee;
            this.firstValid = firstValid;
            this.lastValid = lastValid;
            this.note = note;
        }
    }

    /**
     * Fields specified for key reg transactions.
     */
    static public class KeyregTxnFields {
        // VotePK is the participation public key used in key registration transactions
        @JsonProperty("votekey")
        public final ParticipationPublicKey votePK;
        // selectionPK is the VRF public key used in key registration transactions
        @JsonProperty("selkey")
        public final VRFPublicKey selectionPK;

        /**
         * Create new key registration transaction metadata.
         * @param votePK a participation public key
         * @param vrfPK a selection public key
         */
        public KeyregTxnFields(ParticipationPublicKey votePK, VRFPublicKey vrfPK) {
            this.votePK = Objects.requireNonNull(votePK, "participation key must not be null");
            this.selectionPK = Objects.requireNonNull(vrfPK, "selection key must not be null");
        }
    }

    /**
     * Fields for payment transactions.
     */
    static public class PaymentTxnFields {
        @JsonProperty("rcv")
        public final Address receiver;
        @JsonProperty("close")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public final Address closeRemainderTo; // can be null, optional

        public PaymentTxnFields(Address receiver, Address closeRemainderTo) {
            this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
            this.closeRemainderTo = closeRemainderTo;
        }

        public PaymentTxnFields(Address receiver) {
            // close out to the zero address
            this(receiver, null);
        }
    }
}
