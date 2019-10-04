package com.algorand.algosdk.transaction;


import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 */
@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Transaction implements Serializable {
    @JsonProperty("type")
    public Type type = Type.Default;

    // Instead of embedding POJOs and using JsonUnwrapped, we explicitly export inner fields. This circumvents our encoders'
    // inability to sort child fields.
    /* header fields */
    @JsonProperty("snd")
    public Address sender = new Address();
    @JsonProperty("fee")
    public BigInteger fee = BigInteger.valueOf(0);
    @JsonProperty("fv")
    public BigInteger firstValid = BigInteger.valueOf(0);
    @JsonProperty("lv")
    public BigInteger lastValid = BigInteger.valueOf(0);
    @JsonProperty("note")
    public byte[] note;
    @JsonProperty("gen")
    public String genesisID = "";
    @JsonProperty("gh")
    public Digest genesisHash = new Digest();

    /* payment fields */
    @JsonProperty("amt")
    public BigInteger amount = BigInteger.valueOf(0);
    @JsonProperty("rcv")
    public Address receiver = new Address();
    @JsonProperty("close")
    public Address closeRemainderTo = new Address(); // can be null, optional

    /* keyreg fields */
    // VotePK is the participation public key used in key registration transactions
    @JsonProperty("votekey")
    public ParticipationPublicKey votePK = new ParticipationPublicKey();
    // selectionPK is the VRF public key used in key registration transactions
    @JsonProperty("selkey")
    public VRFPublicKey selectionPK = new VRFPublicKey();
    // voteFirst is the first round this keyreg tx is valid for
    @JsonProperty("votefst")
    public BigInteger voteFirst = BigInteger.valueOf(0);
    // voteLast is the last round this keyreg tx is valid for
    @JsonProperty("votelst")
    public BigInteger voteLast = BigInteger.valueOf(0);
    // voteKeyDilution
    @JsonProperty("votekd")
    public BigInteger voteKeyDilution = BigInteger.valueOf(0);
    
    /* asset creation and config fields */
    @JsonProperty("apar")
    public AssetParams assetParams = new AssetParams();
    @JsonProperty("caid")
    public AssetID assetID = new AssetID();

    /* asset freeze fields */
    @JsonProperty("faid")
    public AssetID assetFreezeID = new AssetID();
    @JsonProperty("fadd")
    public Address freezeTarget = new Address();
    @JsonProperty("afrz")
    public boolean freezeState = false;

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

    /**
     * Create a payment transaction. Make sure to sign with a suggested fee.
     * @param fromAddr source address
     * @param toAddr destination address
     * @param amount amount to send
     * @param firstRound first valid round
     * @param lastRound last valid round
     * @param genesisID genesis id
     * @param genesisHash genesis hash
     */
    public Transaction(Address fromAddr, Address toAddr, long amount, long firstRound, long lastRound,
                       String genesisID, Digest genesisHash) {
        this(fromAddr, BigInteger.valueOf(0), BigInteger.valueOf(firstRound), BigInteger.valueOf(lastRound), null, BigInteger.valueOf(amount), toAddr, genesisID, genesisHash);
    }

    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       BigInteger amount, Address receiver, String genesisID, Digest genesisHash) {
        this(sender, fee, firstValid, lastValid, note, genesisID, genesisHash, amount, receiver, new Address());
    }

    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note, String genesisID, Digest genesisHash,
                       BigInteger amount, Address receiver, Address closeRemainderTo) {
        this(Type.Payment, sender, fee, firstValid, lastValid, note, genesisID, genesisHash, amount, receiver, closeRemainderTo,
                new ParticipationPublicKey(), new VRFPublicKey(), BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(0), new AssetID(), new AssetParams(), new Address(), new AssetID(), false);
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
     * @param voteFirst key reg valid first round
     * @param voteLast key reg valid last round
     * @param voteKeyDilution key reg dilution
     */
    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       String genesisID, Digest genesisHash,
                       ParticipationPublicKey votePK, VRFPublicKey vrfPK,
                       BigInteger voteFirst, BigInteger voteLast, BigInteger voteKeyDilution) {
        // populate with default values which will be ignored...
        this(Type.KeyRegistration, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), votePK, vrfPK, voteFirst, voteLast, 
                voteKeyDilution, new AssetID(), new AssetParams(), new Address(), new AssetID(), false);
    }

    /**
     * Create an asset creation transaction. Note can be null. manager, reserve, freeze, and clawback can be zeroed.
     * @param sender source address
     * @param fee transaction fee
     * @param firstValid first valid round
     * @param lastValid last valid round
     * @param note optional note field (can be null)
     * @param genesisID
     * @param genesisHash
     * @param assetTotal total asset issuance
     * @param defaultFrozen whether accounts have this asset frozen by default
     * @param assetUnitName name of unit of the asset
     * @param assetName name of the asset
     * @param manager account which can reconfigure the asset
     * @param reserve account whose asset holdings count as non-minted
     * @param freeze account which can freeze or unfreeze holder accounts
     * @param clawback account which can issue clawbacks against holder accounts
     */
    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       String genesisID, Digest genesisHash, BigInteger assetTotal, boolean defaultFrozen,
                       String assetUnitName, String assetName, Address manager, Address reserve, Address freeze, Address clawback) {
        // populate ignored values with default or null values
        this(Type.AssetConfig, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), null, null, BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(0), new AssetID(), 
                new AssetParams(assetTotal, defaultFrozen, assetUnitName, assetName, manager, reserve, freeze, clawback), new Address(), new AssetID(), false);
    }

    /**
     * Create an asset configuration transaction. Note can be null. manager, reserve, freeze, and clawback can be zeroed.
     * @param sender source address
     * @param fee transaction fee
     * @param firstValid first valid round
     * @param lastValid last valid round
     * @param note optional note field (can be null)
     * @param genesisID
     * @param genesisHash
     * @param creator asset creator
     * @param index asset index
     * @param assetUnitName name of unit of the asset
     * @param assetName name of the asset
     * @param manager account which can reconfigure the asset
     * @param reserve account whose asset holdings count as non-minted
     * @param freeze account which can freeze or unfreeze holder accounts
     * @param clawback account which can issue clawbacks against holder accounts
     */
    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       String genesisID, Digest genesisHash, Address creator, BigInteger index,
                    Address manager, Address reserve, Address freeze, Address clawback) {
        // populate ignored values with default or null values
        this(Type.AssetConfig, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), null, null, BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(0), new AssetID(creator, index), 
                new AssetParams(BigInteger.valueOf(0), false, "", "", manager, reserve, freeze, clawback), new Address(), new AssetID(), false);
    }

    public Transaction(Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                       String genesisID, Digest genesisHash, AssetID assetID, AssetParams assetParams) {
        // populate ignored values with default or null values
        this(Type.AssetConfig, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), null, null, BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(0), assetID, assetParams, new Address(), new AssetID(), false);
    }

    /**
     * Create an asset freeze/un-freeze transaction.
     * @param sender source address (must be the freeze manager for said asset)
     * @param fee transaction fee
     * @param firstValid first round transaction is valid
     * @param lastValid last round transaction is valid
     * @param note optional note field (can be null)
     * @param genesisID
     * @param genesisHash
     * @param assetFreezeID asset creator and index
     * @param freezeTarget account to be un-frozen or frozen
     * @param freezeState new frozen state for target account (true==frozen==cannot transact)
     */
    public Transaction (Address sender, BigInteger fee, BigInteger firstValid, BigInteger lastValid, byte[] note,
                        String genesisID, Digest genesisHash, AssetID assetFreezeID, Address freezeTarget, boolean freezeState) {
        // populate ignored values with default or null values
        this(type.AssetFreeze, sender, fee, firstValid, lastValid, note, genesisID, genesisHash,
                BigInteger.valueOf(0), new Address(), new Address(), null, null, BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(0), null, null,
                freezeTarget, assetFreezeID, freezeState);
    }

    // workaround for nested JsonValue classes
    @JsonCreator
    private Transaction(@JsonProperty("type") Type type,
                        @JsonProperty("snd") byte[] sender,
                        @JsonProperty("fee") BigInteger fee,
                        @JsonProperty("fv") BigInteger firstValid,
                        @JsonProperty("lv") BigInteger lastValid,
                        @JsonProperty("note") byte[] note,
                        @JsonProperty("gen") String genesisID,
                        @JsonProperty("gh") byte[] genesisHash,
                        @JsonProperty("amt") BigInteger amount,
                        @JsonProperty("rcv") byte[] receiver,
                        @JsonProperty("close") byte[] closeRemainderTo,
                        @JsonProperty("votekey") byte[] votePK,
                        @JsonProperty("selkey") byte[] vrfPK,
                        @JsonProperty("votefst") BigInteger voteFirst,
                        @JsonProperty("votelst") BigInteger voteLast,
                        @JsonProperty("votekd") BigInteger voteKeyDilution,
                        @JsonProperty("caid") AssetID assetID,
                        @JsonProperty("apar") AssetParams assetParams,
                        @JsonProperty("fadd") Address freezeTarget,
                        @JsonProperty("faid") AssetID assetFreezeID,
                        @JsonProperty("afrz") boolean freezeState) {
        this(type, new Address(sender), fee, firstValid, lastValid, note, genesisID, new Digest(genesisHash), amount,
                new Address(receiver), new Address(closeRemainderTo), new ParticipationPublicKey(votePK), new VRFPublicKey(vrfPK),
                voteFirst, voteLast, voteKeyDilution, assetID, assetParams, freezeTarget, assetFreezeID, freezeState);
    }

    private Transaction(Type type,
                        Address sender,
                        BigInteger fee,
                        BigInteger firstValid,
                        BigInteger lastValid,
                        byte[] note,
                        String genesisID,
                        Digest genesisHash,
                        BigInteger amount,
                        Address receiver,
                        Address closeRemainderTo,
                        ParticipationPublicKey votePK,
                        VRFPublicKey vrfPK,
                        BigInteger voteFirst,
                        BigInteger voteLast,
                        BigInteger voteKeyDilution,
                        AssetID assetID,
                        AssetParams assetParams,
                        Address freezeTarget,
                        AssetID assetFreezeID,
                        boolean freezeState) {
        if (type != null) this.type = type;
        if (sender != null) this.sender = sender;
        if (fee != null) this.fee = fee;
        if (firstValid != null) this.firstValid = firstValid;
        if (lastValid != null) this.lastValid = lastValid;
        if (note != null) this.note = note;
        if (genesisID != null) this.genesisID = genesisID;
        if (genesisHash != null) this.genesisHash = genesisHash;
        if (amount != null) this.amount = amount;
        if (receiver != null) this.receiver = receiver;
        if (closeRemainderTo != null) this.closeRemainderTo = closeRemainderTo;
        if (votePK != null) this.votePK = votePK;
        if (vrfPK != null) this.selectionPK = vrfPK;
        if (voteFirst != null) this.voteFirst = voteFirst;
        if (voteLast != null) this.voteLast = voteLast;
        if (voteKeyDilution != null) this.voteKeyDilution = voteKeyDilution;
        if (assetID != null) this.assetID = assetID;
        if (assetParams != null) this.assetParams = assetParams;
        if (freezeTarget != null) this.freezeTarget = freezeTarget;
        if (assetFreezeID != null) this.assetFreezeID = assetFreezeID;
        this.freezeState = freezeState;
    }

    public Transaction() {}

    /**
     * TxType represents a transaction type.
     */
    public enum Type {
        Default(""),
        Payment("pay"),
        KeyRegistration("keyreg"),
        AssetConfig("acfg"),
        AssetFreeze("afrz");

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
                selectionPK.equals(that.selectionPK) &&
                voteFirst.equals(that.voteFirst) &&
                voteLast.equals(that.voteLast) &&
                voteKeyDilution.equals(that.voteKeyDilution) &&
                assetParams.equals(that.assetParams) &&
                assetID.equals(that.assetID) &&
                freezeTarget.equals(that.freezeTarget) &&
                assetFreezeID.equals(that.assetFreezeID) &&
                freezeState.equals(that.freezeState);
    }

    @JsonPropertyOrder(alphabetic=true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class AssetParams implements Serializable {
        // total asset issuance
        @JsonProperty("t")
        public BigInteger assetTotal = BigInteger.valueOf(0);
        // whether each account has their asset slot frozen for this asset by default
        @JsonProperty("df")
        public boolean assetDefaultFrozen = false;
        // a hint to the unit name of the asset
        @JsonProperty("un")
        public String assetUnitName = "";
        // the name of the asset
        @JsonProperty("an")
        public String assetName = "";
        // the address which has the ability to reconfigure the asset
        @JsonProperty("m")
        public Address assetManager = new Address();
        // the asset reserve: assets owned by this address do not count against circulation
        @JsonProperty("r")
        public Address assetReserve = new Address();
        // the address which has the ability to freeze/unfreeze accounts holding this asset
        @JsonProperty("f")
        public Address assetFreeze = new Address();
        // the address which has the ability to issue clawbacks against asset-holding accounts
        @JsonProperty("c")
        public Address assetClawback = new Address();
        
        public AssetParams(BigInteger assetTotal, boolean defaultFrozen, String assetUnitName, String assetName, Address manager, Address reserve, Address freeze, Address clawback) {
            if(assetTotal != null) this.assetTotal = assetTotal;
            this.assetDefaultFrozen = defaultFrozen;
            if(assetUnitName != null) this.assetUnitName = assetUnitName;
            if(assetName != null) this.assetName = assetName;
            if(manager != null) this.assetManager = manager;
            if(reserve != null) this.assetReserve = reserve;
            if(freeze != null) this.assetFreeze = freeze;
            if(clawback != null) this.assetClawback = clawback;
        }

        public AssetParams() {

        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssetParams that = (AssetParams) o;
            return assetTotal.equals(that.assetTotal) &&
                (assetDefaultFrozen == that.assetDefaultFrozen) &&
                assetName.equals(that.assetName) &&
                assetUnitName.equals(that.assetUnitName) &&
                assetManager.equals(that.assetManager) &&
                assetReserve.equals(that.assetReserve) &&
                assetFreeze.equals(that.assetFreeze) &&
                assetClawback.equals(that.assetClawback);
        }

        @JsonCreator
        private AssetParams(@JsonProperty("t") BigInteger assetTotal,
            @JsonProperty("df") boolean assetDefaultFrozen,
            @JsonProperty("un") String assetUnitName,
            @JsonProperty("an") String assetName,
            @JsonProperty("m") byte[] assetManager,
            @JsonProperty("r") byte[] assetReserve,
            @JsonProperty("f") byte[] assetFreeze,
            @JsonProperty("c") byte[] assetClawback) {
            this(assetTotal, assetDefaultFrozen, assetUnitName, assetName, new Address(assetManager), new Address(assetReserve), new Address(assetFreeze), new Address(assetClawback));
        }
    }

    @JsonPropertyOrder(alphabetic=true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class AssetID implements Serializable {
        // asset creator
        @JsonProperty("c")
        public Address creator = new Address();
        // asset index
        @JsonProperty("i")
        public BigInteger index = BigInteger.valueOf(0);

        public AssetID(Address creator, BigInteger index) {
            if(creator != null) this.creator = creator;
            if(index != null) this.index = index;
        }

        public AssetID() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssetID that = (AssetID) o;
            return creator.equals(that.creator) &&
                index.equals(that.index);
        }

        @JsonCreator
        private AssetID(@JsonProperty("c") byte[] creator,
            @JsonProperty("i") BigInteger index) {
                this(new Address(creator), index);
        }
    }

}
