package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.abi.TypeAddress;
import com.algorand.algosdk.abi.TypeUint;
import com.algorand.algosdk.abi.TypeTuple;
import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AtomicTransactionComposer {
    public enum Status {
        BUILDING,
        BUILT,
        SIGNED,
        SUBMITTED,
        COMMITTED
    }

    public static final int MAX_GROUP_SIZE = 16;
    // if the abi type argument number > 15, then the abi types after 14th should be wrapped in a tuple
    private static final int MAX_ABI_ARG_TYPE_LEN = 15;

    private static final int FOREIGN_OBJ_ABI_UINT_SIZE = 8;

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
     * Add a value to an application call's foreign array. The addition will be as compact as possible,
     * and this function will return an index that can be used to reference `objectToBeAdded` in `objectArray`.
     *
     * @param objectToBeAdded - The value to add to the array. If this value is already present in the array,
     *   it will not be added again. Instead, the existing index will be returned.
     * @param objectArray - The existing foreign array. This input may be modified to append `valueToAdd`.
     * @param zerothObject - If provided, this value indicated two things: the 0 value is special for this
     *   array, so all indexes into `objectArray` must start at 1; additionally, if `objectToBeAdded` equals
     *   `zerothValue`, then `objectToBeAdded` will not be added to the array, and instead the 0 indexes will
     *   be returned.
     * @return An index that can be used to reference `valueToAdd` in `array`.
     */
    private static <T> int populateForeignArrayIndex(T objectToBeAdded, List<T> objectArray, T zerothObject) {
        if (objectToBeAdded.equals(zerothObject))
            return 0;
        int startFrom = zerothObject == null ? 0 : 1;
        int searchInListIndex = objectArray.indexOf(objectToBeAdded);
        if (searchInListIndex != -1)
            return startFrom + searchInListIndex;
        objectArray.add(objectToBeAdded);
        return objectArray.size() - 1 + startFrom;
    }

    private static boolean checkTransactionType(TransactionWithSigner tws, String txnType) {
        if (txnType.equals(Method.TxAnyType)) return true;
        return tws.txn.type.toValue().equals(txnType);
    }

    /**
     * Add a smart contract method call to this atomic group.
     * <p>
     * An error will be thrown if the composer's status is not BUILDING, if adding this transaction
     * causes the current group to exceed MAX_GROUP_SIZE, or if the provided arguments are invalid
     * for the given method.
     */
    public void addMethodCall(MethodCallParams methodCall) {
        if (!this.status.equals(Status.BUILDING))
            throw new IllegalArgumentException("Atomic Transaction Composer must be in BUILDING stage");
        if (this.transactionList.size() + methodCall.method.getTxnCallCount() > MAX_GROUP_SIZE)
            throw new IllegalArgumentException(
                    "Atomic Transaction Composer cannot exceed MAX_GROUP_SIZE = " + MAX_GROUP_SIZE + " transactions"
            );

        List<byte[]> encodedABIArgs = new ArrayList<>();
        encodedABIArgs.add(methodCall.method.getSelector());

        List<Object> methodArgs = new ArrayList<>();
        List<ABIType> methodABIts = new ArrayList<>();

        List<TransactionWithSigner> tempTransWithSigner = new ArrayList<>();
        List<Address> foreignAccounts = methodCall.foreignAccounts;
        List<Long> foreignAssets = methodCall.foreignAssets;
        List<Long> foreignApps = methodCall.foreignApps;

        Address senderAddress;
        try {
            senderAddress = new Address(methodCall.sender);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        for (int i = 0; i < methodCall.method.args.size(); i++) {
            Method.Arg argT = methodCall.method.args.get(i);
            Object methodArg = methodCall.methodArgs.get(i);
            if (argT.parsedType == null && methodArg instanceof TransactionWithSigner) {
                TransactionWithSigner twsConverted = (TransactionWithSigner) methodArg;
                if (!checkTransactionType(twsConverted, argT.type))
                    throw new IllegalArgumentException(
                            "expected transaction type " + argT.type
                                    + " not match with given " + twsConverted.txn.type.toValue()
                    );
                tempTransWithSigner.add((TransactionWithSigner) methodArg);
            } else if (Method.RefArgTypes.contains(argT.type)) {
                int index;
                if (argT.type.equals(Method.RefTypeAccount)) {
                    TypeAddress abiAddressT = new TypeAddress();
                    Address accountAddress = (Address) abiAddressT.decode(abiAddressT.encode(methodArg));
                    index = populateForeignArrayIndex(accountAddress, foreignAccounts, senderAddress);
                } else if (argT.type.equals(Method.RefTypeAsset) && methodArg instanceof BigInteger) {
                    TypeUint abiUintT = new TypeUint(64);
                    BigInteger assetID = (BigInteger) abiUintT.decode(abiUintT.encode(methodArg));
                    index = populateForeignArrayIndex(assetID.longValue(), foreignAssets, null);
                } else if (argT.type.equals(Method.RefTypeApplication) && methodArg instanceof BigInteger) {
                    TypeUint abiUintT = new TypeUint(64);
                    BigInteger appID = (BigInteger) abiUintT.decode(abiUintT.encode(methodArg));
                    index = populateForeignArrayIndex(appID.longValue(), foreignApps, methodCall.appID);
                } else
                    throw new IllegalArgumentException(
                            "cannot add method call in AtomicTransactionComposer: ForeignArray arg type not matching"
                    );
                methodArgs.add(index);
                methodABIts.add(new TypeUint(FOREIGN_OBJ_ABI_UINT_SIZE));
            } else if (argT.parsedType != null) {
                methodArgs.add(methodArg);
                methodABIts.add(argT.parsedType);
            } else
                throw new IllegalArgumentException(
                        "error: the type of method argument is a transaction-type, but no transaction-with-signer provided"
                );
        }

        if (methodArgs.size() > MAX_ABI_ARG_TYPE_LEN) {
            List<ABIType> wrappedABITypeList = new ArrayList<>();
            List<Object> wrappedValueList = new ArrayList<>();

            for (int i = MAX_ABI_ARG_TYPE_LEN - 1; i < methodArgs.size(); i++) {
                wrappedABITypeList.add(methodABIts.get(i));
                wrappedValueList.add(methodArgs.get(i));
            }

            TypeTuple tupleT = new TypeTuple(wrappedABITypeList);
            methodABIts = methodABIts.subList(0, MAX_ABI_ARG_TYPE_LEN - 1);
            methodABIts.add(tupleT);
            methodArgs = methodArgs.subList(0, MAX_ABI_ARG_TYPE_LEN - 1);
            methodArgs.add(wrappedValueList);
        }

        for (int i = 0; i < methodArgs.size(); i++)
            encodedABIArgs.add(methodABIts.get(i).encode(methodArgs.get(i)));

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
                .lease(methodCall.lease)
                .accounts(foreignAccounts)
                .foreignApps(foreignApps)
                .foreignAssets(foreignAssets);

        if (methodCall.rekeyTo != null)
            txBuilder.rekey(methodCall.rekeyTo);

        Transaction tx = txBuilder.build();

        tx.onCompletion = methodCall.onCompletion;
        tx.approvalProgram = methodCall.approvalProgram;
        tx.clearStateProgram = methodCall.clearProgram;

        if (methodCall.globalInts != null && methodCall.globalBytes != null)
            tx.globalStateSchema = new StateSchema(methodCall.globalInts, methodCall.globalBytes);

        if (methodCall.localInts != null && methodCall.globalBytes != null)
            tx.localStateSchema = new StateSchema(methodCall.localInts, methodCall.localBytes);

        if (methodCall.extraPages != null)
            tx.extraPages = methodCall.extraPages;

        this.transactionList.addAll(tempTransWithSigner);
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
                    parseError,
                    currentTxInfo
            ));
        }

        return new ExecuteResult(txInfo.confirmedRound, this.getTxIDs(), retList);
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
        public Exception parseError;
        public PendingTransactionResponse txInfo;

        public ReturnValue(String txID, byte[] rawValue, Object value,
                           Exception parseError, PendingTransactionResponse txInfo) {
            this.txID = txID;
            this.rawValue = rawValue;
            this.value = value;
            this.parseError = parseError;
            this.txInfo = txInfo;
        }
    }
}
