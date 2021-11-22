package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.TypeTuple;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Signed;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AtomicTransactionComposer {
    public enum Status {
        BUILDING,
        BUILT,
        SIGNED,
        SUBMITTED,
        COMMITTED
    }

    public static final int MAX_GROUP_SIZE = 16;

    private static final byte[] ABI_RET_HASH = new byte[]{0x15, 0x1f, 0x7c, 0x75};

    private Status status;
    private final Map<Integer, Method> methodMap;
    private final List<TransactionWithSigner> transactionList;
    private List<SignedTransaction> signedTxns;

    public AtomicTransactionComposer() {
        this.status = Status.BUILDING;
        this.methodMap = new HashMap<>();
        this.transactionList = new ArrayList<>();
        this.signedTxns = new ArrayList<>();
    }

    /**
     * Get the status of this composer's transaction group.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Get the number of transactions currently in this atomic group.
     */
    public int getCount() {
        return this.transactionList.size();
    }

    /**
     * Create a new composer with the same underlying transactions. The new composer's status will be
     * BUILDING, so additional transactions may be added to it.
     */
    public AtomicTransactionComposer cloneAtomicTxnComposer() throws IOException {
        AtomicTransactionComposer cloned = new AtomicTransactionComposer();
        cloned.signedTxns = new ArrayList<>();
        for (Map.Entry<Integer, Method> entry : this.methodMap.entrySet())
            cloned.methodMap.put(entry.getKey(), new Method(entry.getValue()));
        ObjectMapper om = new ObjectMapper();
        for (TransactionWithSigner txWithSigner : this.transactionList) {
            byte[] txSerialized = om.writeValueAsBytes(txWithSigner.txn);
            Transaction tx = om.readValue(txSerialized, Transaction.class);
            tx.group = new Digest();
            // We might need the same signer, so I wonder if the signer should be shallow copied...
            TransactionWithSigner newTxWithSigner = new TransactionWithSigner(tx, txWithSigner.signer);
            cloned.transactionList.add(newTxWithSigner);
        }
        return cloned;
    }

    /**
     * Add a transaction to this atomic group.
     * <p>
     * An error will be thrown if the composer's status is not BUILDING, or if adding this transaction
     * causes the current group to exceed MAX_GROUP_SIZE.
     */
    public void addTransaction(TransactionWithSigner txnAndSigner) {
        if (!txnAndSigner.txn.group.equals(new Digest()))
            throw new IllegalArgumentException("Atomic Transaction Composer group field must be zero");
        if (!this.status.equals(Status.BUILDING))
            throw new IllegalArgumentException("Atomic Transaction Composer only add transaction in BUILDING stage");
        if (this.transactionList.size() == MAX_GROUP_SIZE)
            throw new IllegalArgumentException("Atomic Transaction Composer cannot exceed MAX_GROUP_SIZE == 16 transactions");
        this.transactionList.add(txnAndSigner);
    }

    /**
     * Add a smart contract method call to this atomic group.
     * <p>
     * An error will be thrown if the composer's status is not BUILDING, if adding this transaction
     * causes the current group to exceed MAX_GROUP_SIZE, or if the provided arguments are invalid
     * for the given method.
     */
    public void addMethodCall(MethodCalParams methodCall) {
        if (!this.status.equals(Status.BUILDING))
            throw new IllegalArgumentException("Atomic Transaction Composer must be in BUILDING stage");
        if (this.transactionList.size() + methodCall.method.getTxnCallCount() > MAX_GROUP_SIZE)
            throw new IllegalArgumentException("Atomic Transaction Composer cannot exceed MAX_GROUP_SIZE = 16 transactions");

        List<byte[]> encodedABIArgs = new ArrayList<>();
        encodedABIArgs.add(methodCall.method.getSelector());

        List<ABIValue> abiValues = new ArrayList<>();
        for (MethodArgument arg : methodCall.methodArgs) {
            if (arg instanceof ABIValue) {
                abiValues.add((ABIValue) arg);
            } else if (arg instanceof TransactionWithSigner) {
                this.addTransaction((TransactionWithSigner) arg);
            } else
                throw new IllegalArgumentException("MethodArgument should only be ABI value or transaction with signer");
        }

        if (abiValues.size() > 14) {
            List<ABIType> abiTypes = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            for (int i = 14; i < abiValues.size(); i++) {
                abiTypes.add(abiValues.get(i).abiType);
                values.add(abiValues.get(i).value);
            }

            TypeTuple tupleT = new TypeTuple(abiTypes);
            abiValues = abiValues.subList(0, 14);
            abiValues.add(new ABIValue(tupleT, values));
        }

        for (ABIValue v : abiValues)
            encodedABIArgs.add(v.abiType.encode(v.value));

        ApplicationCallTransactionBuilder<?> txBuilder = ApplicationCallTransactionBuilder.Builder();

        txBuilder
                .suggestedParams(methodCall.suggestedParams)
                .firstValid(methodCall.fv)
                .lastValid(methodCall.lv)
                .fee(methodCall.fee)
                .sender(methodCall.sender)
                .flatFee(methodCall.flatFee)
                .applicationId(methodCall.appID)
                .args(encodedABIArgs)
                .note(methodCall.note)
                .lease(methodCall.lease);

        if (methodCall.rekeyTo != null)
            txBuilder.rekey(methodCall.rekeyTo);

        Transaction tx = txBuilder.build();

        tx.onCompletion = methodCall.onCompletion;

        this.transactionList.add(new TransactionWithSigner(tx, methodCall.signer));
        this.methodMap.put(this.transactionList.size() - 1, methodCall.method);
    }

    /**
     * Finalize the transaction group and returned the finalized transactions.
     * <p>
     * The composer's status will be at least BUILT after executing this method.
     */
    public List<TransactionWithSigner> buildGroup() throws IOException {
        if (this.status.compareTo(Status.BUILT) >= 0)
            return this.transactionList;

        if (this.transactionList.size() == 0)
            throw new IllegalArgumentException("should not build transaction group with 0 transaction in composer");

        List<Transaction> groupTxns = new ArrayList<>();
        for (TransactionWithSigner t : this.transactionList) groupTxns.add(t.txn);
        Digest groupID = TxGroup.computeGroupID(groupTxns.toArray(new Transaction[0]));
        for (TransactionWithSigner transactionWithSigner : this.transactionList)
            transactionWithSigner.txn.assignGroupID(groupID);

        this.status = Status.BUILT;
        return this.transactionList;
    }

    /**
     * Obtain signatures for each transaction in this group. If signatures have already been obtained,
     * this method will return cached versions of the signatures.
     * <p>
     * The composer's status will be at least SIGNED after executing this method.
     * <p>
     * An error will be thrown if signing any of the transactions fails.
     *
     * @return an array of signed transactions.
     */
    public List<SignedTransaction> gatherSignatures() throws IOException, NoSuchAlgorithmException {
        if (this.status.compareTo(Status.SIGNED) >= 0)
            return this.signedTxns;

        List<TransactionWithSigner> txnAndSignerList = this.buildGroup();

        // TODO how to setup a map from transactionSigner to indices
        Map<TxnSigner, List<Integer>> transSignerToIndices = new HashMap<>();
        List<SignedTransaction> tempSignedTxns = new ArrayList<>();

        for (TransactionWithSigner tws : txnAndSignerList) {
            SignedTransaction stxn = tws.signer.signTxn(tws.txn);
            this.signedTxns.add(stxn);
        }

        this.status = Status.SIGNED;
        return this.signedTxns;
    }

    protected List<String> getTxIDs() {
        List<String> txids = new ArrayList<>();
        for (SignedTransaction stxn : this.signedTxns) txids.add(stxn.transactionID);
        return txids;
    }

    /**
     * Send the transaction group to the network, but don't wait for it to be committed to a block. An
     * error will be thrown if submission fails.
     * <p>
     * The composer's status must be SUBMITTED or lower before calling this method. If submission is
     * successful, this composer's status will update to SUBMITTED.
     * <p>
     * Note: a group can only be submitted again if it fails.
     *
     * @return If the submission is successful, resolves to a list of TxIDs of the submitted transactions.
     */
    public List<String> submit(AlgodClient client) throws Exception {
        if (this.status.compareTo(Status.SUBMITTED) > 0)
            throw new IllegalArgumentException("Atomic Transaction Composer cannot submit committed transaction");

        this.gatherSignatures();

        List<byte[]> encodings = new ArrayList<>();
        int lengthEncoded = 0;
        for (SignedTransaction stx : this.signedTxns) {
            byte[] temp = Encoder.encodeToMsgPack(stx);
            encodings.add(temp);
            lengthEncoded += temp.length;
        }
        ByteBuffer bf = ByteBuffer.allocate(lengthEncoded);
        for (byte[] subEncode : encodings)
            bf.put(subEncode);
        byte[] encoded = bf.array();
        Response<PostTransactionsResponse> rPost = client.RawTransaction().rawtxn(encoded).execute();

        if (!rPost.isSuccessful())
            throw new Exception("transaction should be submitted successfully");

        this.status = Status.SUBMITTED;
        return this.getTxIDs();
    }

    /**
     * Send the transaction group to the network and wait until it's committed to a block. An error
     * will be thrown if submission or execution fails.
     * <p>
     * The composer's status must be SUBMITTED or lower before calling this method, since execution is
     * only allowed once. If submission is successful, this composer's status will update to SUBMITTED.
     * If the execution is also successful, this composer's status will update to COMMITTED.
     * <p>
     * Note: a group can only be submitted again if it fails.
     *
     * @return If the execution is successful, resolves to an object containing the confirmed round for
     * this transaction, the txIDs of the submitted transactions, and an array of results containing
     * one element for each method call transaction in this group. If a method has no return value
     * (void), then the method results array will contain null for that method's return value.
     */
    public ExecuteResult execute(AlgodClient client, int waitRounds) throws Exception {
        if (this.status.compareTo(Status.SUBMITTED) > 0)
            throw new IllegalArgumentException("status shows this is already committed");
        if (waitRounds < 0)
            throw new IllegalArgumentException("wait round for execute should be non-negative");
        this.submit(client);
        PendingTransactionResponse txInfo =
                Utils.waitForConfirmation(client, this.signedTxns.get(0).transactionID, waitRounds);
        List<ReturnValue> retList = new ArrayList<>();

        for (int i = 0; i < this.transactionList.size(); i++) {
            if (!this.methodMap.containsKey(i))
                continue;
            if (this.methodMap.get(i).returns.type.equals("void")) {
                retList.add(new ReturnValue(
                        this.transactionList.get(i).txn.txID(),
                        new byte[]{},
                        null,
                        null
                ));
                continue;
            }
            Response<PendingTransactionResponse> resp =
                    client.PendingTransactionInformation(this.transactionList.get(i).txn.txID()).execute();
            if (!resp.isSuccessful()) {
                retList.add(new ReturnValue(
                        null,
                        null,
                        null,
                        resp.message()
                ));
                continue;
            }

            // question why it is not replying anything in log?
            PendingTransactionResponse respBody = resp.body();
            List<byte[]> logs = respBody.logs;
            byte[] retLine = null;
            for (int logIndex = logs.size() - 1; logIndex >= 0; logIndex--) {
                byte[] line = logs.get(logIndex);
                if (!checkLogRet(line))
                    continue;
                retLine = line;
                break;
            }
            if (retLine == null) {
                retList.add(new ReturnValue(
                        null,
                        null,
                        null,
                        "expected to find logged return value, got none"
                ));
                continue;
            }

            byte[] abiEncoded = Arrays.copyOfRange(retLine, 4, retLine.length);
            ABIValue decoded = null;
            String parseError = null;
            try {
                ABIType ABIType = com.algorand.algosdk.abi.ABIType.valueOf(this.methodMap.get(i).returns.type);
                Object decodedVar = ABIType.decode(abiEncoded);
                decoded = new ABIValue(ABIType, decodedVar);
            } catch (Exception e) {
                parseError = e.getMessage();
            }
            retList.add(new ReturnValue(
                    this.transactionList.get(i).txn.txID(),
                    abiEncoded,
                    decoded,
                    parseError
            ));
        }

        this.status = Status.COMMITTED;

        return new ExecuteResult(txInfo.confirmedRound, this.getTxIDs(), retList);
    }

    private static boolean checkLogRet(byte[] logLine) {
        if (logLine.length < 4)
            return false;
        for (int i = 0; i < 4; i++) {
            if (logLine[i] != ABI_RET_HASH[i])
                return false;
        }
        return true;
    }

    public static class ExecuteResult {
        public Long confirmedRound;
        public List<String> txIDs;
        public List<ReturnValue> methodResults;

        public ExecuteResult(Long confirmedRound, List<String> txIDs, List<ReturnValue> methodResults) {
            this.confirmedRound = confirmedRound;
            this.txIDs = txIDs;
            this.methodResults = methodResults;
        }
    }

    public static class ReturnValue {
        public String txID;
        public byte[] rawValue;
        public ABIValue value;
        public String parseError;

        public ReturnValue(String txID, byte[] rawValue, ABIValue value, String parseError) {
            this.txID = txID;
            this.rawValue = rawValue;
            this.value = value;
            this.parseError = parseError;
        }
    }

    public interface TxnSigner {
        SignedTransaction signTxn(Transaction txn) throws NoSuchAlgorithmException, IOException;

        SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign)
                throws NoSuchAlgorithmException, IOException;
    }

    public interface MethodArgument {
    }

    public static class ABIValue implements MethodArgument {
        public ABIType abiType;
        public Object value;

        public ABIValue(ABIType abiType, Object value) {
            abiType.encode(value);
            this.abiType = abiType;
            this.value = value;
        }
    }

    public static class TransactionWithSigner implements MethodArgument {
        public Transaction txn;
        public TxnSigner signer;

        public TransactionWithSigner(Transaction txn, TxnSigner signer) {
            this.txn = txn;
            this.signer = signer;
        }
    }
}
