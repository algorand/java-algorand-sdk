package com.algorand.algosdk.transaction;


import java.util.Objects;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 * TODO msgpack codec tags
 */
public class Transaction {
    private static final int TX_ADDR_LEN = 32;

    public final Type type;
    public final Header header;

    public Transaction(Type type, Header header) {
        // we could do this with annotations but let's avoid pulling in dependencies
        Objects.requireNonNull(type, "txtype must not be null");
        Objects.requireNonNull(header, "header must not be null");
        this.type = type;
        this.header = header;
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
        public final byte[] sender = new byte[TX_ADDR_LEN];
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
        public Header(byte[] sender, long fee, long firstValid, long lastValid, byte[] note, String genesisID) {
            Objects.requireNonNull(sender, "sender must not be null");
            Objects.requireNonNull(genesisID, "genesis ID must not be null");

            if (sender.length != TX_ADDR_LEN) {
                throw new IllegalArgumentException("sender address wrong length");
            }
            System.arraycopy(sender, 0, this.sender, 0, TX_ADDR_LEN);
            this.fee = fee;
            this.firstValid = firstValid;
            this.lastValid = lastValid;
            this.note = note;
            this.genesisID = genesisID;
        }
    }

    static public class KeyregTxnFields {
        private static final int VOTE_PK_LEN = 32;
        private static final int VRF_PK_LEN = 32;

        // VotePK is the participation public key used in key registration transactions
        public final byte[] votePK = new byte[VOTE_PK_LEN];
        // selectionPK is the VRF public key used in key registration transactions
        public final byte[] selectionPK= new byte[VRF_PK_LEN];

        /**
         *
         * @param votePK
         * @param vrfPK
         */
        public KeyregTxnFields(byte[] votePK, byte[] vrfPK) {
            Objects.requireNonNull(votePK, "participation key must not be null");
            Objects.requireNonNull(vrfPK, "selection key must not be null");

            if (votePK.length != VOTE_PK_LEN) {
                throw new IllegalArgumentException("participation key wrong length");
            }
            if (vrfPK.length != VRF_PK_LEN) {
                throw new IllegalArgumentException("vrf key wrong length");
            }
            System.arraycopy(votePK, 0, this.votePK, 0, VOTE_PK_LEN);
            System.arraycopy(vrfPK, 0, this.selectionPK, 0, VRF_PK_LEN);
        }
    }

    static public class PaymentTxnFields {
        public final byte[] receiver = new byte[TX_ADDR_LEN];
        public final long amount;
        public final byte[] closeRemainderTo; // can be null, optional

        /**
         *
         * @param receiver
         * @param amount
         * @param closeRemainderTo
         */
        public PaymentTxnFields(byte[] receiver, long amount, byte[] closeRemainderTo) {
            Objects.requireNonNull(receiver, "receiver must not be null");
            if (receiver.length != TX_ADDR_LEN) {
                throw new IllegalArgumentException("receiver address wrong length");
            }
            System.arraycopy(receiver, 0, this.receiver, 0, TX_ADDR_LEN);
            if (closeRemainderTo == null) {
                this.closeRemainderTo = null;
            } else {
                if (closeRemainderTo.length != TX_ADDR_LEN) {
                    throw new IllegalArgumentException("close remainder address wrong length");
                }
                this.closeRemainderTo = new byte[TX_ADDR_LEN];
                System.arraycopy(closeRemainderTo, 0, this.closeRemainderTo, 0, TX_ADDR_LEN);
            }
            this.amount = amount;
        }
    }
}
