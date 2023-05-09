package com.algorand.algosdk.transaction;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;
import com.algorand.algosdk.abi.TypeAddress;
import com.algorand.algosdk.abi.TypeTuple;
import com.algorand.algosdk.abi.TypeUint;
import com.algorand.algosdk.builder.transaction.ApplicationCallTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * MethodCallParams is an object that holds all parameters necessary to invoke {@link AtomicTransactionComposer#addMethodCall(MethodCallParams)}
 */
public class MethodCallParams {

    // if the abi type argument number > 15, then the abi types after 14th should be wrapped in a tuple
    private static final int MAX_ABI_ARG_TYPE_LEN = 15;

    private static final int FOREIGN_OBJ_ABI_UINT_SIZE = 8;

    public final Long appID;
    public final Transaction.OnCompletion onCompletion;
    public final Method method;
    public final List<Object> methodArgs;

    public final List<Address> foreignAccounts;
    public final List<Long> foreignAssets;
    public final List<Long> foreignApps;
    public final List<AppBoxReference> boxReferences;

    public final TEALProgram approvalProgram, clearProgram;
    public final StateSchema globalStateSchema, localStateSchema;
    public final Long extraPages;

    public final TxnSigner signer;

    // from com.algorand.algosdk.builder.transaction.TransactionParametersBuilder
    public final Address sender;
    public final BigInteger fee;
    public final BigInteger flatFee;
    public final BigInteger firstValid;
    public final BigInteger lastValid;
    public final byte[] note;
    public final byte[] lease;
    public final Address rekeyTo;
    public final String genesisID;
    public final Digest genesisHash;

    protected MethodCallParams(Long appID, Method method, List<Object> methodArgs, Address sender,
                               Transaction.OnCompletion onCompletion, byte[] note, byte[] lease, String genesisID, Digest genesisHash,
                               BigInteger firstValid, BigInteger lastValid, BigInteger fee, BigInteger flatFee,
                               Address rekeyTo, TxnSigner signer,
                               List<Address> fAccounts, List<Long> fAssets, List<Long> fApps, List<AppBoxReference> boxes,
                               TEALProgram approvalProgram, TEALProgram clearProgram,
                               StateSchema globalStateSchema, StateSchema localStateSchema, Long extraPages) {
        if (appID == null || method == null || sender == null || onCompletion == null || signer == null || genesisID == null || genesisHash == null || firstValid == null || lastValid == null || (fee == null && flatFee == null))
            throw new IllegalArgumentException("Method call builder error: some required field not added");
        if (fee != null && flatFee != null)
            throw new IllegalArgumentException("Cannot set both fee and flatFee");
        if (method.args.size() != methodArgs.size())
            throw new IllegalArgumentException("Method call error: incorrect method arg number provided");
        if (appID == 0) {
            if (approvalProgram == null || clearProgram == null || globalStateSchema == null || localStateSchema == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for application creation is missing: " +
                                "approvalProgram, clearProgram, globalStateSchema, localStateSchema"
                );
        } else if (onCompletion == Transaction.OnCompletion.UpdateApplicationOC) {
            if (approvalProgram == null || clearProgram == null)
                throw new IllegalArgumentException(
                        "One of the following required parameters for OnApplicationComplete.UpdateApplicationOC is missing: approvalProgram, clearProgram"
                );
            if (globalStateSchema != null || localStateSchema != null || extraPages != null)
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "globalStateSchema, localStateSchema, extraPages"
                );
        } else {
            if (approvalProgram != null || clearProgram != null || globalStateSchema != null || localStateSchema != null || extraPages != null) {
                throw new IllegalArgumentException(
                        "One of the following application creation parameters were set on a non-creation call: " +
                                "approvalProgram, clearProgram, globalStateSchema, localStateSchema, extraPages"
                );
            }
        }
        this.appID = appID;
        this.method = method;
        this.methodArgs = new ArrayList<>(methodArgs);
        this.sender = sender;
        this.onCompletion = onCompletion;
        this.note = note;
        this.lease = lease;
        this.genesisID = genesisID;
        this.genesisHash = genesisHash;
        this.firstValid = firstValid;
        this.lastValid = lastValid;
        this.fee = fee;
        this.flatFee = flatFee;
        this.rekeyTo = rekeyTo;
        this.signer = signer;
        this.foreignAccounts = new ArrayList<>(fAccounts);
        this.foreignAssets = new ArrayList<>(fAssets);
        this.foreignApps = new ArrayList<>(fApps);
        this.boxReferences = new ArrayList<>(boxes);
        this.approvalProgram = approvalProgram;
        this.clearProgram = clearProgram;
        this.globalStateSchema = globalStateSchema;
        this.localStateSchema = localStateSchema;
        this.extraPages = extraPages;
    }

    /**
     * Create the transactions which will carry out the specified method call.
     * <p>
     * The list of transactions returned by this function will have the same length as method.getTxnCallCount().
     */
    public List<TransactionWithSigner> createTransactions() {
        List<byte[]> encodedABIArgs = new ArrayList<>();
        encodedABIArgs.add(this.method.getSelector());

        List<Object> methodArgs = new ArrayList<>();
        List<ABIType> methodABIts = new ArrayList<>();

        List<TransactionWithSigner> transactionArgs = new ArrayList<>();
        List<Address> foreignAccounts = new ArrayList<>(this.foreignAccounts);
        List<Long> foreignAssets = new ArrayList<>(this.foreignAssets);
        List<Long> foreignApps = new ArrayList<>(this.foreignApps);
        List<AppBoxReference> boxReferences = new ArrayList<>(this.boxReferences);

        for (int i = 0; i < this.method.args.size(); i++) {
            Method.Arg argT = this.method.args.get(i);
            Object methodArg = this.methodArgs.get(i);
            if (argT.parsedType == null && methodArg instanceof TransactionWithSigner) {
                TransactionWithSigner twsConverted = (TransactionWithSigner) methodArg;
                if (!checkTransactionType(twsConverted, argT.type))
                    throw new IllegalArgumentException(
                            "expected transaction type " + argT.type
                                    + " not match with given " + twsConverted.txn.type.toValue()
                    );
                transactionArgs.add((TransactionWithSigner) methodArg);
            } else if (Method.RefArgTypes.contains(argT.type)) {
                int index;
                if (argT.type.equals(Method.RefTypeAccount)) {
                    TypeAddress abiAddressT = new TypeAddress();
                    Address accountAddress = (Address) abiAddressT.decode(abiAddressT.encode(methodArg));
                    index = populateForeignArrayIndex(accountAddress, foreignAccounts, this.sender);
                } else if (argT.type.equals(Method.RefTypeAsset) && methodArg instanceof BigInteger) {
                    TypeUint abiUintT = new TypeUint(64);
                    BigInteger assetID = (BigInteger) abiUintT.decode(abiUintT.encode(methodArg));
                    index = populateForeignArrayIndex(assetID.longValue(), foreignAssets, null);
                } else if (argT.type.equals(Method.RefTypeApplication) && methodArg instanceof BigInteger) {
                    TypeUint abiUintT = new TypeUint(64);
                    BigInteger appID = (BigInteger) abiUintT.decode(abiUintT.encode(methodArg));
                    index = populateForeignArrayIndex(appID.longValue(), foreignApps, this.appID);
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
                        "error: the type of method (" + this.method.name +") argument " + (i+1) + " is a " + methodArg.getClass().getName() + ", but " + argT.type + " not provided"
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
                .firstValid(this.firstValid)
                .lastValid(this.lastValid)
                .genesisHash(this.genesisHash)
                .genesisID(this.genesisID)
                .fee(this.fee)
                .flatFee(this.flatFee)
                .note(this.note)
                .lease(this.lease)
                .rekey(this.rekeyTo)
                .sender(this.sender)
                .applicationId(this.appID)
                .args(encodedABIArgs)
                .accounts(foreignAccounts)
                .foreignApps(foreignApps)
                .foreignAssets(foreignAssets)
                .boxReferences(boxReferences);

        Transaction tx = txBuilder.build();

        // must apply these fields manually, as they are not exposed in the base ApplicationCallTransactionBuilder
        tx.onCompletion = this.onCompletion;
        tx.approvalProgram = this.approvalProgram;
        tx.clearStateProgram = this.clearProgram;

        if (this.globalStateSchema != null)
            tx.globalStateSchema = this.globalStateSchema;
        if (this.localStateSchema != null)
            tx.localStateSchema = this.localStateSchema;
        if (this.extraPages != null)
            tx.extraPages = this.extraPages;

        TransactionWithSigner methodCall = new TransactionWithSigner(tx, this.signer);
        transactionArgs.add(methodCall);

        return transactionArgs;
    }

    private static boolean checkTransactionType(TransactionWithSigner tws, String txnType) {
        if (txnType.equals(Method.TxAnyType)) return true;
        return tws.txn.type.toValue().equals(txnType);
    }

    /**
     * Add a value to an application call's foreign array. The addition will be as compact as possible,
     * and this function will return an index that can be used to reference `objectToBeAdded` in `objectArray`.
     *
     * @param objectToBeAdded - The value to add to the array. If this value is already present in the array,
     *                        it will not be added again. Instead, the existing index will be returned.
     * @param objectArray     - The existing foreign array. This input may be modified to append `valueToAdd`.
     * @param zerothObject    - If provided, this value indicated two things: the 0 value is special for this
     *                        array, so all indexes into `objectArray` must start at 1; additionally, if `objectToBeAdded` equals
     *                        `zerothValue`, then `objectToBeAdded` will not be added to the array, and instead the 0 indexes will
     *                        be returned.
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
}
