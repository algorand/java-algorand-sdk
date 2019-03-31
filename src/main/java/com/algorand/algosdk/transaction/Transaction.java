package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;

import java.util.Objects;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 * TODO msgpack codec tags
 */
public class Transaction {
    public final Type type;
    public final Header header;

    public Transaction(Type type, Header header) {
        // we could do this with annotations but let's avoid pulling in dependencies
        this.type = Objects.requireNonNull(type, "txtype must not be null");
        this.header = Objects.requireNonNull(header, "header must not be null");
    }

    /**
     * TxType represents a transaction type.
     * When serialized, TODO
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
        public String getValue() {
            return this.value;
        }
    }

    static public class Header {
        public final Address sender;
        public final long fee;
        public final long firstValid;
        public final long lastValid;
        public final byte[] note; // can be null (optional)
        public final String genesisID;

        /**
         *
         * @param sender
         * @param fee
         * @param firstValid
         * @param lastValid
         * @param note
         * @param genesisID
         */
        public Header(Address sender, long fee, long firstValid, long lastValid, byte[] note, String genesisID) {
            this.sender = Objects.requireNonNull(sender, "sender must not be null");
            this.genesisID = Objects.requireNonNull(genesisID, "genesis ID must not be null");
            this.fee = fee;
            this.firstValid = firstValid;
            this.lastValid = lastValid;
            this.note = note;
        }
    }

    static public class KeyregTxnFields {
        // VotePK is the participation public key used in key registration transactions
        public final ParticipationPublicKey votePK;
        // selectionPK is the VRF public key used in key registration transactions
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
     * TODO
     */
    static public class PaymentTxnFields {
        public final Address receiver;
        public final long amount;
        public final Address closeRemainderTo; // can be null, optional

        /**
         *
         * @param receiver
         * @param amount
         * @param closeRemainderTo
         */
        public PaymentTxnFields(Address receiver, long amount, Address closeRemainderTo) {
            this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
            this.closeRemainderTo = Objects.requireNonNull(closeRemainderTo, "close remainder address must not be null");
            this.amount = amount;
        }

        /**
         *
         * @param receiver
         * @param amount
         */
        public PaymentTxnFields(Address receiver, long amount) {
            // close out to the zero address
            this(receiver, amount, new Address(new byte[Address.LEN_BYTES]));
        }
    }
}
