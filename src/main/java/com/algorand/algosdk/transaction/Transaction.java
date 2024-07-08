package com.algorand.algosdk.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.*;
import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.util.Digester;
import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * A raw serializable transaction class, used to generate transactions to broadcast to the network.
 * This is distinct from algod.model.Transaction, which is only returned for GET requests to algod.
 */
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Transaction implements Serializable {
    private static final byte[] TX_SIGN_PREFIX = ("TX").getBytes(StandardCharsets.UTF_8);
    @JsonProperty("type")
    public Type type = Type.Default;

    // Instead of embedding POJOs and using JsonUnwrapped, we explicitly export inner fields.
    // This circumvents our encoders' inability to sort child fields.
    /* header fields ***********************************************************/
    @JsonProperty("snd")
    public Address sender = new Address();
    @JsonProperty("fee")
    public BigInteger fee = BigInteger.ZERO;
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
    @JsonProperty("grp")
    public Digest group = new Digest();
    @JsonProperty("lx")
    public byte[] lease;
    @JsonProperty("rekey")
    public Address rekeyTo = new Address();

    /* payment fields  *********************************************************/
    @JsonProperty("amt")
    public BigInteger amount = BigInteger.valueOf(0);
    @JsonProperty("rcv")
    public Address receiver = new Address();
    @JsonProperty("close")
    public Address closeRemainderTo = new Address(); // can be null, optional

    /* keyreg fields ***********************************************************/
    // VotePK is the participation public key used in key registration transactions
    @JsonProperty("votekey")
    public ParticipationPublicKey votePK = new ParticipationPublicKey();

    // selectionPK is the VRF public key used in key registration transactions
    @JsonProperty("selkey")
    public VRFPublicKey selectionPK = new VRFPublicKey();

    // stateProofKey
    @JsonProperty("sprfkey")
    public MerkleVerifier stateProofKey = new MerkleVerifier();

    // nonparticiation mark
    @JsonProperty("nonpart")
    public boolean nonpart = false;

    // voteFirst is the first round this keyreg tx is valid for
    @JsonProperty("votefst")
    public BigInteger voteFirst = BigInteger.valueOf(0);

    // voteLast is the last round this keyreg tx is valid for
    @JsonProperty("votelst")
    public BigInteger voteLast = BigInteger.valueOf(0);
    // voteKeyDilution
    @JsonProperty("votekd")
    public BigInteger voteKeyDilution = BigInteger.valueOf(0);

    /* asset creation and configuration fields *********************************/
    @JsonProperty("apar")
    public AssetParams assetParams = new AssetParams();
    @JsonProperty("caid")
    public BigInteger assetIndex = BigInteger.valueOf(0);

    /* asset transfer fields ***************************************************/
    @JsonProperty("xaid")
    public BigInteger xferAsset = BigInteger.valueOf(0);

    // The amount of asset to transfer. A zero amount transferred to self
    // allocates that asset in the account's Assets map.
    @JsonProperty("aamt")
    public BigInteger assetAmount = BigInteger.valueOf(0);

    // The sender of the transfer.  If this is not a zero value, the real
    // transaction sender must be the Clawback address from the AssetParams. If
    // this is the zero value, the asset is sent from the transaction's Sender.
    @JsonProperty("asnd")
    public Address assetSender = new Address();

    // The receiver of the transfer.
    @JsonProperty("arcv")
    public Address assetReceiver = new Address();

    // Indicates that the asset should be removed from the account's Assets map,
    // and specifies where the remaining asset holdings should be transferred.
    // It's always valid to transfer remaining asset holdings to the AssetID
    // account.
    @JsonProperty("aclose")
    public Address assetCloseTo = new Address();

    /* asset freeze fields */
    @JsonProperty("fadd")
    public Address freezeTarget = new Address();

    @JsonProperty("faid")
    public BigInteger assetFreezeID = BigInteger.valueOf(0);

    @JsonProperty("afrz")
    public boolean freezeState = false;

    /* application fields */
    @JsonProperty("apaa")
    public List<byte[]> applicationArgs = new ArrayList<>();

    @JsonProperty("apan")
    public OnCompletion onCompletion = OnCompletion.NoOpOC;

    @JsonProperty("apap")
    public TEALProgram approvalProgram = null;

    @JsonProperty("apat")
    public List<Address> accounts = new ArrayList<>();

    @JsonProperty("apfa")
    public List<Long> foreignApps = new ArrayList<>();

    @JsonProperty("apas")
    public List<Long> foreignAssets = new ArrayList<>();

    @JsonProperty("apbx")
    public List<BoxReference> boxReferences = new ArrayList<>();

    @JsonProperty("apgs")
    public StateSchema globalStateSchema = new StateSchema();

    @JsonProperty("apid")
    public Long applicationId = 0L;

    @JsonProperty("apls")
    public StateSchema localStateSchema = new StateSchema();

    @JsonProperty("apsu")
    public TEALProgram clearStateProgram = null;

    @JsonProperty("apep")
    public Long extraPages = 0L;

    /* state proof fields */
    @JsonProperty("sptype")
    public Integer stateProofType = null;

    @JsonProperty("sp")
    public Map<String,Object> stateProof = null;

    @JsonProperty("spmsg")
    public Map<String,Object> stateProofMessage = null;

    /**
     * Helper for Jackson conversion.
     */
    private static List<Address> convertToAddressList(List<byte[]> addressBytes) {
        if (addressBytes == null) return null;
        List<Address> result = new ArrayList<>();
        for (byte[] addr : addressBytes) {
            result.add(new Address(addr));
        }
        return result;
    }

    // workaround for nested JsonValue classes
    @JsonCreator
    private Transaction(@JsonProperty("type") Type type,
                        //header fields
                        @JsonProperty("snd") byte[] sender,
                        @JsonProperty("fee") BigInteger fee,
                        @JsonProperty("fv") BigInteger firstValid,
                        @JsonProperty("lv") BigInteger lastValid,
                        @JsonProperty("note") byte[] note,
                        @JsonProperty("gen") String genesisID,
                        @JsonProperty("gh") byte[] genesisHash,
                        @JsonProperty("lx") byte[] lease,
                        @JsonProperty("rekey") byte[] rekeyTo,
                        @JsonProperty("grp") byte[] group,
                        // payment fields
                        @JsonProperty("amt") BigInteger amount,
                        @JsonProperty("rcv") byte[] receiver,
                        @JsonProperty("close") byte[] closeRemainderTo,
                        // keyreg fields
                        @JsonProperty("votekey") byte[] votePK,
                        @JsonProperty("selkey") byte[] vrfPK,
                        @JsonProperty("sprfkey") byte[] stateProofKey,
                        @JsonProperty("votefst") BigInteger voteFirst,
                        @JsonProperty("votelst") BigInteger voteLast,
                        @JsonProperty("votekd") BigInteger voteKeyDilution,
                        @JsonProperty("nonpart") boolean nonpart,
                        // asset creation and configuration
                        @JsonProperty("apar") AssetParams assetParams,
                        @JsonProperty("caid") BigInteger assetIndex,
                        // Asset xfer transaction fields
                        @JsonProperty("xaid") BigInteger xferAsset,
                        @JsonProperty("aamt") BigInteger assetAmount,
                        @JsonProperty("asnd") byte[] assetSender,
                        @JsonProperty("arcv") byte[] assetReceiver,
                        @JsonProperty("aclose") byte[] assetCloseTo,
                        // asset freeze fields
                        @JsonProperty("fadd") byte[] freezeTarget,
                        @JsonProperty("faid") BigInteger assetFreezeID,
                        @JsonProperty("afrz") boolean freezeState,
                        // application fields
                        @JsonProperty("apaa") List<byte[]> applicationArgs,
                        @JsonProperty("apan") OnCompletion onCompletion,
                        @JsonProperty("apap") byte[] approvalProgram,
                        @JsonProperty("apat") List<byte[]> accounts,
                        @JsonProperty("apfa") List<Long> foreignApps,
                        @JsonProperty("apas") List<Long> foreignAssets,
                        @JsonProperty("apbx") List<BoxReference> boxReferences,
                        @JsonProperty("apgs") StateSchema globalStateSchema,
                        @JsonProperty("apid") Long applicationId,
                        @JsonProperty("apls") StateSchema localStateSchema,
                        @JsonProperty("apsu") byte[] clearStateProgram,
                        @JsonProperty("apep") Long extraPages
    ) throws IOException {
        this(
                type,
                //header fields
                new Address(sender),
                fee,
                firstValid,
                lastValid,
                note,
                genesisID,
                new Digest(genesisHash),
                lease,
                new Address(rekeyTo),
                new Digest(group),
                // payment fields
                amount,
                new Address(receiver),
                new Address(closeRemainderTo),
                // keyreg fields
                new ParticipationPublicKey(votePK),
                new VRFPublicKey(vrfPK),
                new MerkleVerifier(stateProofKey),
                voteFirst,
                voteLast,
                voteKeyDilution,
                nonpart,
                // asset creation and configuration
                assetParams,
                assetIndex,
                // asset transfer fields
                xferAsset,
                assetAmount,
                new Address(assetSender),
                new Address(assetReceiver),
                new Address(assetCloseTo),
                new Address(freezeTarget),
                assetFreezeID,
                freezeState,
                // application fields
                applicationArgs,
                onCompletion,
                approvalProgram == null ? null : new TEALProgram(approvalProgram),
                convertToAddressList(accounts),
                foreignApps,
                foreignAssets,
                boxReferences,
                globalStateSchema,
                applicationId,
                localStateSchema,
                clearStateProgram == null ? null : new TEALProgram(clearStateProgram),
                extraPages
        );
    }

    /**
     * Constructor which takes all the fields of Transaction including nonpart and state proof.
     * For details about which fields to use with different transaction types, refer to the developer documentation:
     * https://developer.algorand.org/docs/reference/transactions/#asset-transfer-transaction
     */
    private Transaction(
            Type type,
            //header fields
            Address sender,
            BigInteger fee,
            BigInteger firstValid,
            BigInteger lastValid,
            byte[] note,
            String genesisID,
            Digest genesisHash,
            byte[] lease,
            Address rekeyTo,
            Digest group,
            // payment fields
            BigInteger amount,
            Address receiver,
            Address closeRemainderTo,
            // keyreg fields
            ParticipationPublicKey votePK,
            VRFPublicKey vrfPK,
            MerkleVerifier stateProofKey,
            BigInteger voteFirst,
            BigInteger voteLast,
            // voteKeyDilution
            BigInteger voteKeyDilution,
            boolean nonpart,
            // asset creation and configuration
            AssetParams assetParams,
            BigInteger assetIndex,
            // asset transfer fields
            BigInteger xferAsset,
            BigInteger assetAmount,
            Address assetSender,
            Address assetReceiver,
            Address assetCloseTo,
            Address freezeTarget,
            BigInteger assetFreezeID,
            boolean freezeState,
            // application fields
            List<byte[]> applicationArgs,
            OnCompletion onCompletion,
            TEALProgram approvalProgram,
            List<Address> accounts,
            List<Long> foreignApps,
            List<Long> foreignAssets,
            List<BoxReference> boxReferences,
            StateSchema globalStateSchema,
            Long applicationId,
            StateSchema localStateSchema,
            TEALProgram clearStateProgram,
            Long extraPages
    ) {
        if (type != null) this.type = type;
        if (sender != null) this.sender = sender;
        this.fee = (fee == null ? BigInteger.ZERO : fee);
        if (firstValid != null) this.firstValid = firstValid;
        if (lastValid != null) this.lastValid = lastValid;
        setNote(note);
        if (genesisID != null) this.genesisID = genesisID;
        if (genesisHash != null) this.genesisHash = genesisHash;
        if (lease != null) setLease(new Lease(lease));
        if (rekeyTo != null) this.rekeyTo = rekeyTo;
        if (group != null) this.group = group;
        if (amount != null) this.amount = amount;
        if (receiver != null) this.receiver = receiver;
        if (closeRemainderTo != null) this.closeRemainderTo = closeRemainderTo;
        if (votePK != null) this.votePK = votePK;
        if (vrfPK != null) this.selectionPK = vrfPK;
        if (stateProofKey != null) this.stateProofKey = stateProofKey;
        if (voteFirst != null) this.voteFirst = voteFirst;
        if (voteLast != null) this.voteLast = voteLast;
        if (voteKeyDilution != null) this.voteKeyDilution = voteKeyDilution;
        this.nonpart = nonpart;
        if (assetParams != null) this.assetParams = assetParams;
        if (assetIndex != null) this.assetIndex = assetIndex;
        if (xferAsset != null) this.xferAsset = xferAsset;
        if (assetAmount != null) this.assetAmount = assetAmount;
        if (assetSender != null) this.assetSender = assetSender;
        if (assetReceiver != null) this.assetReceiver = assetReceiver;
        if (assetCloseTo != null) this.assetCloseTo = assetCloseTo;
        if (freezeTarget != null) this.freezeTarget = freezeTarget;
        if (assetFreezeID != null) this.assetFreezeID = assetFreezeID;
        this.freezeState = freezeState;
        if (applicationArgs != null) this.applicationArgs = applicationArgs;
        if (onCompletion != null) this.onCompletion = onCompletion;
        if (approvalProgram != null) this.approvalProgram = approvalProgram;
        if (accounts != null) this.accounts = accounts;
        if (foreignApps != null) this.foreignApps = foreignApps;
        if (foreignAssets != null) this.foreignAssets = foreignAssets;
        if (boxReferences != null) this.boxReferences = boxReferences;
        if (globalStateSchema != null) this.globalStateSchema = globalStateSchema;
        if (applicationId != null) this.applicationId = applicationId;
        if (localStateSchema != null) this.localStateSchema = localStateSchema;
        if (clearStateProgram != null) this.clearStateProgram = clearStateProgram;
        if (extraPages != null) this.extraPages = extraPages;
    }

    // Used by Jackson to determine "default" values.
    public Transaction() {
    }

    private void setNote(byte[] note) {
        if (note != null && note.length != 0) {
            this.note = note;
        }
    }

    /**
     * Lease enforces mutual exclusion of transactions.  If this field
     * is nonzero, then once the transaction is confirmed, it acquires
     * the lease identified by the (Sender, Lease) pair of the
     * transaction until the LastValid round passes.  While this
     * transaction possesses the lease, no other transaction
     * specifying this lease can be confirmed.
     * The Size is fixed at 32 bytes.
     *
     * @param lease Lease object
     **/
    public void setLease(Lease lease) {
        if (lease != null) {
            this.lease = lease.getBytes();
        }
    }

    /**
     * TxType represents a transaction type.
     */
    public enum Type {
        Default(""),
        Payment("pay"),
        KeyRegistration("keyreg"),
        AssetConfig("acfg"),
        AssetTransfer("axfer"),
        AssetFreeze("afrz"),
        ApplicationCall("appl"),
        StateProof("stpf");

        private static Map<String, Type> namesMap = new HashMap<String, Type>(6);

        private final String value;

        Type(String value) {
            this.value = value;
        }

        /**
         * Return the enumeration for the given string value. Required for JSON serialization.
         *
         * @param value string representation
         * @return enumeration type
         */
        @JsonCreator
        public static Type forValue(String value) {
            for (Type t : values()) {
                if (t.value.equalsIgnoreCase(value)) {
                    return t;
                }
            }
            return null;
        }

        /**
         * Return the string value for this enumeration. Required for JSON serialization.
         *
         * @return string value
         */
        @JsonValue
        public String toValue() {
            return value;
        }
    }

    public enum OnCompletion {
        NoOpOC(0, "noop"),
        OptInOC(1, "optin"),
        CloseOutOC(2, "closeout"),
        ClearStateOC(3, "clearstate"),
        UpdateApplicationOC(4, "update"),
        DeleteApplicationOC(5, "delete");

        private final int serializedValue;
        private final String serializedName;

        OnCompletion(int serializeValue, String serializedName) {
            this.serializedValue = serializeValue;
            this.serializedName = serializedName;
        }

        public static OnCompletion String(String name) {
            for (OnCompletion oc : values()) {
                if (oc.serializedName.equalsIgnoreCase(name)) {
                    return oc;
                }
            }
            return null;
        }

        @JsonCreator
        public static OnCompletion forValue(int value) {
            for (OnCompletion oc : values()) {
                if (oc.serializedValue == value) {
                    return oc;
                }
            }
            return null;
        }

        @JsonValue
        public int toValue() {
            return serializedValue;
        }

        @Override
        public String toString() {
            return serializedName;
        }
    }

    /**
     * Return encoded representation of the transaction
     */
    public byte[] bytes() throws IOException {
        try {
            return Encoder.encodeToMsgPack(this);
        } catch (IOException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }

    /**
     * Return encoded representation of the transaction with a prefix
     * suitable for signing
     */
    public byte[] bytesToSign() throws IOException {
        try {
            byte[] encodedTx = Encoder.encodeToMsgPack(this);
            byte[] prefixEncodedTx = new byte[encodedTx.length + TX_SIGN_PREFIX.length];
            System.arraycopy(TX_SIGN_PREFIX, 0, prefixEncodedTx, 0, TX_SIGN_PREFIX.length);
            System.arraycopy(encodedTx, 0, prefixEncodedTx, TX_SIGN_PREFIX.length, encodedTx.length);
            return prefixEncodedTx;
        } catch (IOException e) {
            throw new RuntimeException("serialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Return transaction ID as Digest
     */
    public Digest rawTxID() throws IOException {
        try {
            return new Digest(Digester.digest(this.bytesToSign()));
        } catch (IOException e) {
            throw new RuntimeException("tx computation failed", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("tx computation failed", e);
        }
    }

    /**
     * Return transaction ID as string
     */
    public String txID() throws IOException, NoSuchAlgorithmException {
        return Encoder.encodeToBase32StripPad(this.rawTxID().getBytes());
    }

    public void assignGroupID(Digest gid) {
        this.group = gid;
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
                Arrays.equals(lease, that.lease) &&
                group.equals(that.group) &&
                amount.equals(that.amount) &&
                receiver.equals(that.receiver) &&
                closeRemainderTo.equals(that.closeRemainderTo) &&
                votePK.equals(that.votePK) &&
                selectionPK.equals(that.selectionPK) &&
                stateProofKey.equals(that.stateProofKey) &&
                voteFirst.equals(that.voteFirst) &&
                voteLast.equals(that.voteLast) &&
                voteKeyDilution.equals(that.voteKeyDilution) &&
                nonpart == that.nonpart &&
                assetParams.equals(that.assetParams) &&
                assetIndex.equals(that.assetIndex) &&
                xferAsset.equals(that.xferAsset) &&
                assetAmount.equals(that.assetAmount) &&
                assetSender.equals(that.assetSender) &&
                assetReceiver.equals(that.assetReceiver) &&
                assetCloseTo.equals(that.assetCloseTo) &&
                freezeTarget.equals(that.freezeTarget) &&
                assetFreezeID.equals(that.assetFreezeID) &&
                freezeState == that.freezeState &&
                rekeyTo.equals(that.rekeyTo) &&
                extraPages.equals(that.extraPages) &&
                boxReferences.equals(that.boxReferences);
    }

    /**
     * Create a {@link PaymentTransactionBuilder}.
     */
    public static PaymentTransactionBuilder<?> PaymentTransactionBuilder() {
        return PaymentTransactionBuilder.Builder();
    }

    /**
     * Create a {@link KeyRegistrationTransactionBuilder}.
     */
    public static KeyRegistrationTransactionBuilder<?> KeyRegistrationTransactionBuilder() {
        return KeyRegistrationTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetCreateTransactionBuilder}.
     */
    public static AssetCreateTransactionBuilder<?> AssetCreateTransactionBuilder() {
        return AssetCreateTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetConfigureTransactionBuilder}.
     */
    public static AssetConfigureTransactionBuilder<?> AssetConfigureTransactionBuilder() {
        return AssetConfigureTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetDestroyTransactionBuilder}.
     */
    public static AssetDestroyTransactionBuilder<?> AssetDestroyTransactionBuilder() {
        return AssetDestroyTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetAcceptTransactionBuilder}.
     */
    public static AssetAcceptTransactionBuilder<?> AssetAcceptTransactionBuilder() {
        return AssetAcceptTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetTransferTransactionBuilder}.
     */
    public static AssetTransferTransactionBuilder<?> AssetTransferTransactionBuilder() {
        return AssetTransferTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetClawbackTransactionBuilder}.
     */
    public static AssetClawbackTransactionBuilder<?> AssetClawbackTransactionBuilder() {
        return AssetClawbackTransactionBuilder.Builder();
    }

    /**
     * Create a {@link AssetFreezeTransactionBuilder}.
     */
    public static AssetFreezeTransactionBuilder<?> AssetFreezeTransactionBuilder() {
        return AssetFreezeTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationCreateTransactionBuilder}.
     */
    public static ApplicationCreateTransactionBuilder<?> ApplicationCreateTransactionBuilder() {
        return ApplicationCreateTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationUpdateTransactionBuilder}.
     */
    public static ApplicationUpdateTransactionBuilder<?> ApplicationUpdateTransactionBuilder() {
        return ApplicationUpdateTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationDeleteTransactionBuilder}.
     */
    public static ApplicationDeleteTransactionBuilder<?> ApplicationDeleteTransactionBuilder() {
        return ApplicationDeleteTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationOptInTransactionBuilder}.
     */
    public static ApplicationOptInTransactionBuilder<?> ApplicationOptInTransactionBuilder() {
        return ApplicationOptInTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationCloseTransactionBuilder}.
     */
    public static ApplicationCloseTransactionBuilder<?> ApplicationCloseTransactionBuilder() {
        return ApplicationCloseTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationCallTransactionBuilder}.
     */
    public static ApplicationCallTransactionBuilder<?> ApplicationCallTransactionBuilder() {
        return ApplicationCallTransactionBuilder.Builder();
    }

    /**
     * Create a {@link ApplicationClearTransactionBuilder}.
     */
    public static ApplicationClearTransactionBuilder<?> ApplicationClearTransactionBuilder() {
        return ApplicationClearTransactionBuilder.Builder();
    }

    @JsonPropertyOrder(alphabetic = true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class BoxReference {
        // the index in the foreign apps array of the app this box belongs to
        @JsonProperty("i")
        private final int appIndex;

        // the name of the box unique to the app it belongs to
        @JsonProperty("n")
        private final byte[] name;

        public BoxReference(
                @JsonProperty("i") int appIndex,
                @JsonProperty("n") byte[] name) {
            this.appIndex = appIndex;
            this.name = name == null ? new byte[]{} : Arrays.copyOf(name, name.length);
        }

        // Foreign apps start from index 1.  Index 0 is the called App ID.
        // Must apply offset to yield the foreign app index expected by algod.
        private static final int FOREIGN_APPS_INDEX_OFFSET = 1;
        private static final long NEW_APP_ID = 0L;

        public static BoxReference fromAppBoxReference(AppBoxReference abr, List<Long> foreignApps, Long currentApp) {
            if (abr.getAppId() == NEW_APP_ID)
                return new BoxReference(0, abr.getName());

            if (foreignApps == null || !foreignApps.contains(abr.getAppId()))
                // If the app references itself in foreign apps, then prefer foreign app index.
                // Otherwise, fallback to comparing against the invoked app (`currentApp`).
                if (Long.valueOf(abr.getAppId()).equals(currentApp))
                    return new BoxReference(0, abr.getName());
                else
                    throw new RuntimeException(
                            String.format("Box app ID (%d) is not present in the foreign apps array: %d %s", abr.getAppId(), currentApp, foreignApps));
            else
                return new BoxReference(foreignApps.indexOf(abr.getAppId()) + FOREIGN_APPS_INDEX_OFFSET, abr.getName());
        }

        public byte[] getName() {
            return Arrays.copyOf(name, name.length);
        }

        public int getAppIndex() {
            return appIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxReference that = (BoxReference) o;
            return appIndex == that.appIndex && Arrays.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(appIndex);
            result = 31 * result + Arrays.hashCode(name);
            return result;
        }

        @Override
        public String toString() {
            return "BoxReference{" +
                    "appIndex=" + appIndex +
                    ", name=" + Arrays.toString(name) +
                    '}';
        }
    }
}
