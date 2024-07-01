package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.algod.SimulateTransaction;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
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
    public AtomicTransactionComposer cloneComposer() throws IOException {
        AtomicTransactionComposer cloned = new AtomicTransactionComposer();
        cloned.methodMap.putAll(this.methodMap);
        ObjectMapper om = new ObjectMapper();
        for (TransactionWithSigner txWithSigner : this.transactionList) {
            byte[] txSerialized = om.writeValueAsBytes(txWithSigner.txn);
            Transaction tx = om.readValue(txSerialized, Transaction.class);
            tx.group = new Digest();
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
     * <p>
     * For help creating a MethodCallParams object, see {@link com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder}
     */
    public void addMethodCall(MethodCallParams methodCall) {
        if (!this.status.equals(Status.BUILDING))
            throw new IllegalArgumentException("Atomic Transaction Composer must be in BUILDING stage");

        List<TransactionWithSigner> txns = methodCall.createTransactions();

        if (this.transactionList.size() + txns.size() > MAX_GROUP_SIZE)
            throw new IllegalArgumentException(
                    "Atomic Transaction Composer cannot exceed MAX_GROUP_SIZE = " + MAX_GROUP_SIZE + " transactions"
            );

        this.transactionList.addAll(txns);
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
        else if (this.transactionList.size() > 1) {
            List<Transaction> groupTxns = new ArrayList<>();
            for (TransactionWithSigner t : this.transactionList) groupTxns.add(t.txn);
            Digest groupID = TxGroup.computeGroupID(groupTxns.toArray(new Transaction[0]));
            for (TransactionWithSigner tws : this.transactionList) tws.txn.group = groupID;
        }

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
    public List<SignedTransaction> gatherSignatures() throws Exception {
        if (this.status.compareTo(Status.SIGNED) >= 0)
            return this.signedTxns;

        List<TransactionWithSigner> txnAndSignerList = this.buildGroup();

        HashMap<TxnSigner, List<Integer>> txnSignerToIndices = new HashMap<>();
        List<SignedTransaction> tempSignedTxns = new ArrayList<>();
        for (int i = 0; i < txnAndSignerList.size(); i++) tempSignedTxns.add(null);

        for (int i = 0; i < txnAndSignerList.size(); i++) {
            TransactionWithSigner tws = txnAndSignerList.get(i);
            if (!txnSignerToIndices.containsKey(tws.signer))
                txnSignerToIndices.put(tws.signer, new ArrayList<Integer>());
            txnSignerToIndices.get(tws.signer).add(i);
        }

        Transaction[] txnGroup = new Transaction[txnAndSignerList.size()];
        for (int i = 0; i < txnAndSignerList.size(); i++)
            txnGroup[i] = txnAndSignerList.get(i).txn;

        for (TxnSigner txnSigner : txnSignerToIndices.keySet()) {
            List<Integer> indices = txnSignerToIndices.get(txnSigner);
            int[] indicesPrimitive = ArrayUtils.toPrimitive(indices.toArray(new Integer[0]));
            SignedTransaction[] signed = txnSigner.signTxnGroup(txnGroup, indicesPrimitive);
            for (int i = 0; i < indicesPrimitive.length; i++)
                tempSignedTxns.set(indicesPrimitive[i], signed[i]);
        }
        if (tempSignedTxns.contains(null))
            throw new IllegalArgumentException("some signer did not sign the transaction");

        this.signedTxns = tempSignedTxns;
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
            throw new Exception("transaction should be submitted successfully cause : " + rPost.message());

        this.status = Status.SUBMITTED;
        return this.getTxIDs();
    }

    // Simulate simulates the transaction group against the network.
    //
    // The composer's status must be SUBMITTED or lower before calling this method. Simulation will not
    // advance the status of the composer beyond SIGNED.
    //
    // The `request` argument can be used to customize the characteristics of the simulation.
    //
    // Returns a models.SimulateResponse and an ABIResult for each method call in this group.
    public SimulateResult simulate(Client client, SimulateRequest request) throws Exception {
        if (this.status.ordinal() > Status.SUBMITTED.ordinal()) {
            throw new Exception("Status must be SUBMITTED or lower in order to call Simulate()");
        }

        List<SignedTransaction> stxs = gatherSignatures();
        if (stxs == null) {
            throw new Exception("Error gathering signatures");
        }

        SimulateRequestTransactionGroup txnGroups = new SimulateRequestTransactionGroup();
        txnGroups.txns = stxs;
        request.txnGroups = new ArrayList<>();
        request.txnGroups.add(txnGroups);

        SimulateTransaction st = new SimulateTransaction(client);
        SimulateResponse simulateResponse = st.request(request).execute().body();

        if (simulateResponse == null) {
            throw new Exception("Error in simulation response");
        }

        List<ABIMethodResult> methodResults = new ArrayList<>();
        for (int i = 0; i < stxs.size(); i++) {
            SignedTransaction stx = stxs.get(i);
            String txID = stx.transactionID;
            PendingTransactionResponse pendingTransactionResponse = simulateResponse.txnGroups.get(0).txnResults.get(i).txnResult;

            ABIMethodResult methodResult = new ABIMethodResult();
            methodResult.setTxID(txID);
            methodResult.setRawReturnValue(new byte[0]);
            methodResult.setMethod(this.methodMap.get(i));

            methodResult = parseMethodResponse(methodResult.getMethod(), methodResult, pendingTransactionResponse);
            methodResults.add(methodResult);
        }

        SimulateResult result = new SimulateResult();
        result.setMethodResults(methodResults);
        result.setSimulateResponse(simulateResponse);

        return result;
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
     * this transaction, the txIDs of the submitted transactions, an array of results containing
     * one element for each method call transaction in this group, and the raw transaction response from algod.
     * If a method has no return value (void), then the method results array will contain null for that return value.
     */
    public ExecuteResult execute(AlgodClient client, int waitRounds) throws Exception {
        if (this.status == Status.COMMITTED)
            throw new IllegalArgumentException("status shows this is already committed");
        if (waitRounds < 0)
            throw new IllegalArgumentException("wait round for execute should be non-negative");
        this.submit(client);

        int indexToWait = 0;
        for (int i = 0; i < this.signedTxns.size(); i++) {
            if (this.methodMap.containsKey(i)) {
                indexToWait = i;
                break;
            }
        }

        PendingTransactionResponse txInfo =
                Utils.waitForConfirmation(client, this.signedTxns.get(indexToWait).transactionID, waitRounds);
        List<ReturnValue> retList = new ArrayList<>();

        this.status = Status.COMMITTED;

        for (int i = 0; i < this.transactionList.size(); i++) {
            if (!this.methodMap.containsKey(i))
                continue;

            PendingTransactionResponse currentTxInfo = txInfo;
            if (i != indexToWait) {
                Response<PendingTransactionResponse> resp =
                        client.PendingTransactionInformation(signedTxns.get(i).transactionID).execute();
                if (!resp.isSuccessful()) {
                    retList.add(new ReturnValue(
                            signedTxns.get(i).transactionID,
                            null,
                            null,
                            this.methodMap.get(i),
                            new Exception(resp.message()),
                            null
                    ));
                    continue;
                }
                currentTxInfo = resp.body();
            }

            if (this.methodMap.get(i).returns.type.equals(Method.Returns.VoidRetType)) {
                retList.add(new ReturnValue(
                        currentTxInfo.txn.transactionID,
                        null,
                        null,
                        this.methodMap.get(i),
                        null,
                        currentTxInfo
                ));
                continue;
            }

            if (currentTxInfo.logs.size() == 0)
                throw new IllegalArgumentException("App call transaction did not log a return value");
            byte[] retLine = currentTxInfo.logs.get(currentTxInfo.logs.size() - 1);
            if (!checkLogRet(retLine))
                throw new IllegalArgumentException("App call transaction did not log a return value");

            byte[] abiEncoded = Arrays.copyOfRange(retLine, ABI_RET_HASH.length, retLine.length);
            Object decoded = null;
            Exception parseError = null;
            try {
                ABIType methodRetType = this.methodMap.get(i).returns.parsedType;
                decoded = methodRetType.decode(abiEncoded);
            } catch (Exception e) {
                parseError = e;
            }
            retList.add(new ReturnValue(
                    currentTxInfo.txn.transactionID,
                    abiEncoded,
                    decoded,
                    this.methodMap.get(i),
                    parseError,
                    currentTxInfo
            ));
        }

        return new ExecuteResult(txInfo.confirmedRound, this.getTxIDs(), retList);
    }

    public static class SimulateResult {
        // The result of the transaction group simulation
        private SimulateResponse simulateResponse;
        // For each ABI method call in the executed group (created by the AddMethodCall method), this
        // list contains information about the method call's return value
        private List<ABIMethodResult> methodResults;

        // Getter and setter for simulateResponse
        public SimulateResponse getSimulateResponse() {
            return simulateResponse;
        }

        public void setSimulateResponse(SimulateResponse simulateResponse) {
            this.simulateResponse = simulateResponse;
        }

        // Getter and setter for methodResults
        public List<ABIMethodResult> getMethodResults() {
            return methodResults;
        }

        public void setMethodResults(List<ABIMethodResult> methodResults) {
            this.methodResults = methodResults;
        }
    }

    /**
     * Parses a single ABI Method transaction log into a ABI result object.
     *
     * @param method
     * @param methodResult
     * @param pendingTransactionResponse
     * @return An ABIMethodResult object
     */
    public ABIMethodResult parseMethodResponse(Method method, ABIMethodResult methodResult, PendingTransactionResponse pendingTransactionResponse) {
        ABIMethodResult returnedResult = methodResult;
        try {
            returnedResult.setTransactionInfo(pendingTransactionResponse);
            if (!method.returns.type.equals("void")) {
                List<byte[]> logs = pendingTransactionResponse.logs;
                if (logs == null || logs.isEmpty()) {
                    throw new Exception("App call transaction did not log a return value");
                }

                byte[] lastLog = logs.get(logs.size() - 1);
                if (lastLog.length < 4 || !hasPrefix(lastLog, ABI_RET_HASH)) {
                    throw new Exception("App call transaction did not log a return value");
                }

                returnedResult.setRawReturnValue(Arrays.copyOfRange(lastLog, 4, lastLog.length));
                returnedResult.setReturnValue(method.returns.parsedType.decode(returnedResult.getRawReturnValue()));
            }
        } catch (Exception e) {
            returnedResult.setDecodeError(e);
        }

        return returnedResult;
    }

    private static boolean hasPrefix(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkLogRet(byte[] logLine) {
        if (logLine.length < ABI_RET_HASH.length)
            return false;
        for (int i = 0; i < ABI_RET_HASH.length; i++) {
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
        public Object value;
        public Method method;
        public Exception parseError;
        public PendingTransactionResponse txInfo;

        public ReturnValue(String txID, byte[] rawValue, Object value, Method method, 
                           Exception parseError, PendingTransactionResponse txInfo) {
            this.txID = txID;
            this.rawValue = rawValue;
            this.value = value;
            this.method = method;
            this.parseError = parseError;
            this.txInfo = txInfo;
        }
    }
}
