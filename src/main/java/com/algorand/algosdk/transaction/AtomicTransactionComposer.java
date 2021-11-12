package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.abi.Type;
import com.algorand.algosdk.abi.TypeTuple;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.account.LogicSigAccount;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtomicTransactionComposer {
    public enum AtomicTxComposerStatus {
        BUILDING,
        BUILT,
        SIGNED,
        SUBMITTED,
        COMMITTED
    }

    private static final int MAX_GROUP_SIZE = 16;

    private static final byte[] ABI_RET_HASH = new byte[]{0x15, 0x1f, 0x7c, 0x75};

    public AtomicTxComposerStatus status;
    public Map<Integer, Method> methodMap = new HashMap<>();
    public List<TransactionWithSigner> transactionList = new ArrayList<>();
    public List<SignedTransaction> signedTxns = new ArrayList<>();

    public AtomicTransactionComposer() {
        this.status = AtomicTxComposerStatus.BUILDING;
    }

    /**
     * Get the status of this composer's transaction group.
     */
    public AtomicTxComposerStatus getStatus() {
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
     *
     * An error will be thrown if the composer's status is not BUILDING, or if adding this transaction
     * causes the current group to exceed MAX_GROUP_SIZE.
     */
    public void addTransaction(TransactionWithSigner txnAndSigner) {
        for (byte b : txnAndSigner.txn.group.getBytes()) {
            if (b != 0)
                throw new IllegalArgumentException("Atomic Transaction Composer must not be non-zero");
        }
        if (this.status != AtomicTxComposerStatus.BUILDING)
            throw new IllegalArgumentException("Atomic Transaction Composer only add transaction in BUILDING stage");
        if (this.transactionList.size() == MAX_GROUP_SIZE)
            throw new IllegalArgumentException("Atomic Transaction Composer cannot exceed MAX_GROUP_SIZE == 16 transactions");
        this.transactionList.add(txnAndSigner);
    }

    /**
     * Add a smart contract method call to this atomic group.
     *
     * An error will be thrown if the composer's status is not BUILDING, if adding this transaction
     * causes the current group to exceed MAX_GROUP_SIZE, or if the provided arguments are invalid
     * for the given method.
     */
    public void addMethodCall(MethodCallOption methodCall) {
        if (this.status.compareTo(AtomicTxComposerStatus.BUILDING) != 0)
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
            List<Type> abiTypes = new ArrayList<>();
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

        Transaction tx = ApplicationCallTransactionBuilder.Builder()
                .sender(methodCall.sender)
                .suggestedParams(methodCall.suggestedParams)
                .applicationId(methodCall.appID)
                .args(encodedABIArgs)
                .rekey(methodCall.rekeyTo)
                .note(ArrayUtils.toPrimitive(methodCall.note))
                .lease(ArrayUtils.toPrimitive(methodCall.lease))
                .build();
        tx.onCompletion = methodCall.onCompletion;

        this.transactionList.add(new TransactionWithSigner(tx, methodCall.signer));
        this.methodMap.put(this.transactionList.size() - 1, methodCall.method);
    }

    /**
     * Finalize the transaction group and returned the finalized transactions.
     *
     * The composer's status will be at least BUILT after executing this method.
     */
    public List<TransactionWithSigner> buildGroup() throws IOException {
        if (this.status.compareTo(AtomicTxComposerStatus.BUILT) >= 0)
            return this.transactionList;

        List<Transaction> groupTxns = new ArrayList<>();
        for (TransactionWithSigner t : this.transactionList) groupTxns.add(t.txn);
        Digest groupID = TxGroup.computeGroupID(groupTxns.toArray(new Transaction[0]));
        for (TransactionWithSigner transactionWithSigner : this.transactionList)
            transactionWithSigner.txn.assignGroupID(groupID);

        this.status = AtomicTxComposerStatus.BUILT;
        return this.transactionList;
    }

    /**
     * Obtain signatures for each transaction in this group. If signatures have already been obtained,
     * this method will return cached versions of the signatures.
     *
     * The composer's status will be at least SIGNED after executing this method.
     *
     * An error will be thrown if signing any of the transactions fails.
     *
     * @return an array of signed transactions.
     */
    public List<SignedTransaction> gatherSignatures() throws IOException, NoSuchAlgorithmException {
        if (this.status.compareTo(AtomicTxComposerStatus.SIGNED) >= 0)
            return this.signedTxns;

        List<TransactionWithSigner> txnAndSignerList = this.buildGroup();
        for (TransactionWithSigner tws : txnAndSignerList) {
            SignedTransaction stxn = tws.signer.signTxn(tws.txn);
            this.signedTxns.add(stxn);
        }

        this.status = AtomicTxComposerStatus.SIGNED;
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
     *
     * The composer's status must be SUBMITTED or lower before calling this method. If submission is
     * successful, this composer's status will update to SUBMITTED.
     *
     * Note: a group can only be submitted again if it fails.
     *
     * @return If the submission is successful, resolves to a list of TxIDs of the submitted transactions.
     */
    public List<String> submit(AlgodClient client) throws Exception {
        if (this.status.compareTo(AtomicTxComposerStatus.SUBMITTED) > 0)
            throw new IllegalArgumentException("Atomic Transaction Composer cannot submit committed transaction");
        else if (this.status.compareTo(AtomicTxComposerStatus.SUBMITTED) == 0)
            return this.getTxIDs();

        List<SignedTransaction> stxn = this.gatherSignatures();
        client.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stxn)).execute();

        this.status = AtomicTxComposerStatus.SUBMITTED;
        return this.getTxIDs();
    }

    /**
     * Send the transaction group to the network and wait until it's committed to a block. An error
     * will be thrown if submission or execution fails.
     *
     * The composer's status must be SUBMITTED or lower before calling this method, since execution is
     * only allowed once. If submission is successful, this composer's status will update to SUBMITTED.
     * If the execution is also successful, this composer's status will update to COMMITTED.
     *
     * Note: a group can only be submitted again if it fails.
     *
     * @return If the execution is successful, resolves to an object containing the confirmed round for
     * this transaction, the txIDs of the submitted transactions, and an array of results containing
     * one element for each method call transaction in this group. If a method has no return value
     * (void), then the method results array will contain null for that method's return value.
     */
    public ExecuteResult execute(AlgodClient client) throws Exception {
        if (this.status.compareTo(AtomicTxComposerStatus.SUBMITTED) > 0)
            throw new IllegalArgumentException("status shows this is already committed");
        this.submit(client);

        // TODO
        return null;
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
        public byte[] rawValue;
        public ABIValue value;
        public Exception parseError;

        public ReturnValue(byte[] rawValue, ABIValue value, Exception parseError) {
            this.rawValue = rawValue;
            this.value = value;
            this.parseError = parseError;
        }
    }

    public static class TransactionSigner {
        protected enum SignedBy {
            BasicAccount,
            LogicSigAccount,
            MultiSigAccount
        }

        protected TransactionSigner.SignedBy signedBy;
        protected Account acc;
        protected LogicSigAccount lsa;
        protected List<Account> msigAccounts = new ArrayList<>();
        protected MultisigAddress msigAddr;

        public TransactionSigner(Account acc) {
            this.signedBy = SignedBy.BasicAccount;
            this.acc = acc;
        }

        public TransactionSigner(LogicSigAccount lsa) {
            this.signedBy = SignedBy.LogicSigAccount;
            this.lsa = lsa;
        }

        public TransactionSigner(MultisigAddress msigAddr, byte[][] sks) throws NoSuchAlgorithmException {
            this.signedBy = SignedBy.MultiSigAccount;
            this.msigAddr = msigAddr;
            for (byte[] sk : sks) this.msigAccounts.add(new Account(sk));
        }

        public SignedTransaction[] signTxnGroup(Transaction[] txnGroup, int[] indicesToSign)
                throws NoSuchAlgorithmException, IOException {
            List<SignedTransaction> signedTxns = new ArrayList<>();
            for (int index : indicesToSign)
                signedTxns.add(this.signTxn(txnGroup[index]));
            return signedTxns.toArray(new SignedTransaction[0]);
        }

        public SignedTransaction signTxn(Transaction txn) throws NoSuchAlgorithmException, IOException {
            if (this.signedBy == SignedBy.BasicAccount) {
                return acc.signTransaction(txn);
            } else if (this.signedBy == SignedBy.LogicSigAccount) {
                return lsa.signLogicSigTransaction(txn);
            } else if (this.signedBy == SignedBy.MultiSigAccount) {
                List<SignedTransaction> multiStxns = new ArrayList<>();
                for (Account acc : this.msigAccounts)
                    multiStxns.add(acc.signMultisigTransaction(this.msigAddr, txn));
                return Account.mergeMultisigTransactions(multiStxns.toArray(new SignedTransaction[0]));
            } else
                throw new IllegalArgumentException("atomic transaction signer cannot infer sign transaction by who");
        }
    }

    public interface MethodArgument {}

    public static class ABIValue implements MethodArgument {
        Type abiType;
        Object value;

        public ABIValue(Type abiType, Object value) {
            abiType.encode(value);
            this.abiType = abiType;
            this.value = value;
        }
    }

    public static class TransactionWithSigner implements MethodArgument {
        public Transaction txn;
        public TransactionSigner signer;

        public TransactionWithSigner(Transaction txn, TransactionSigner signer) {
            this.txn = txn;
            this.signer = signer;
        }
    }
}
