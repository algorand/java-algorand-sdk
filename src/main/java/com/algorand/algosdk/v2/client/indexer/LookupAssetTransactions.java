package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.*;
import com.algorand.algosdk.v2.client.model.Enums;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;

import java.util.Date;


/**
 * Lookup transactions for an asset. Transactions are returned oldest to newest.
 * /v2/assets/{asset-id}/transactions
 */
public class LookupAssetTransactions extends Query {

    private Long assetId;

    /**
     * @param assetId
     */
    public LookupAssetTransactions(Client client, Long assetId) {
        super(client, new HttpMethod("get"));
        this.assetId = assetId;
    }

    /**
     * Only include transactions with this address in one of the transaction fields.
     */
    public LookupAssetTransactions address(Address address) {
        addQuery("address", String.valueOf(address));
        return this;
    }

    /**
     * Combine with the address parameter to define what type of address to search for.
     */
    public LookupAssetTransactions addressRole(Enums.AddressRole addressRole) {
        addQuery("address-role", String.valueOf(addressRole));
        return this;
    }

    /**
     * Include results after the given time. Must be an RFC 3339 formatted string.
     */
    public LookupAssetTransactions afterTime(Date afterTime) {
        addQuery("after-time", Utils.getDateString(afterTime));
        return this;
    }

    /**
     * Include results before the given time. Must be an RFC 3339 formatted string.
     */
    public LookupAssetTransactions beforeTime(Date beforeTime) {
        addQuery("before-time", Utils.getDateString(beforeTime));
        return this;
    }

    /**
     * Results should have an amount greater than this value. MicroAlgos are the
     * default currency unless an asset-id is provided, in which case the asset will be
     * used.
     */
    public LookupAssetTransactions currencyGreaterThan(Long currencyGreaterThan) {
        addQuery("currency-greater-than", String.valueOf(currencyGreaterThan));
        return this;
    }

    /**
     * Results should have an amount less than this value. MicroAlgos are the default
     * currency unless an asset-id is provided, in which case the asset will be used.
     */
    public LookupAssetTransactions currencyLessThan(Long currencyLessThan) {
        addQuery("currency-less-than", String.valueOf(currencyLessThan));
        return this;
    }

    /**
     * Combine with address and address-role parameters to define what type of address
     * to search for. The close to fields are normally treated as a receiver, if you
     * would like to exclude them set this parameter to true.
     */
    public LookupAssetTransactions excludeCloseTo(Boolean excludeCloseTo) {
        addQuery("exclude-close-to", String.valueOf(excludeCloseTo));
        return this;
    }

    /**
     * Maximum number of results to return. There could be additional pages even if the
     * limit is not reached.
     */
    public LookupAssetTransactions limit(Long limit) {
        addQuery("limit", String.valueOf(limit));
        return this;
    }

    /**
     * Include results at or before the specified max-round.
     */
    public LookupAssetTransactions maxRound(Long maxRound) {
        addQuery("max-round", String.valueOf(maxRound));
        return this;
    }

    /**
     * Include results at or after the specified min-round.
     */
    public LookupAssetTransactions minRound(Long minRound) {
        addQuery("min-round", String.valueOf(minRound));
        return this;
    }

    /**
     * The next page of results. Use the next token provided by the previous results.
     */
    public LookupAssetTransactions next(String next) {
        addQuery("next", String.valueOf(next));
        return this;
    }

    /**
     * Specifies a prefix which must be contained in the note field.
     */
    public LookupAssetTransactions notePrefix(byte[] notePrefix) {
        addQuery("note-prefix", Encoder.encodeToBase64(notePrefix));
        return this;
    }

    /**
     * Include results which include the rekey-to field.
     */
    public LookupAssetTransactions rekeyTo(Boolean rekeyTo) {
        addQuery("rekey-to", String.valueOf(rekeyTo));
        return this;
    }

    /**
     * Include results for the specified round.
     */
    public LookupAssetTransactions round(Long round) {
        addQuery("round", String.valueOf(round));
        return this;
    }

    /**
     * SigType filters just results using the specified type of signature:
     *   sig - Standard
     *   msig - MultiSig
     *   lsig - LogicSig
     */
    public LookupAssetTransactions sigType(Enums.SigType sigType) {
        addQuery("sig-type", String.valueOf(sigType));
        return this;
    }

    public LookupAssetTransactions txType(Enums.TxType txType) {
        addQuery("tx-type", String.valueOf(txType));
        return this;
    }

    /**
     * Lookup the specific transaction by ID.
     */
    public LookupAssetTransactions txid(String txid) {
        addQuery("txid", String.valueOf(txid));
        return this;
    }

   /**
    * Execute the query.
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<TransactionsResponse> execute() throws Exception {
        Response<TransactionsResponse> resp = baseExecute();
        resp.setValueType(TransactionsResponse.class);
        return resp;
    }

   /**
    * Execute the query with custom headers, there must be an equal number of keys and values
    * or else an error will be generated.
    * @param headers an array of header keys
    * @param values an array of header values
    * @return the query response object.
    * @throws Exception
    */
    @Override
    public Response<TransactionsResponse> execute(String[] headers, String[] values) throws Exception {
        Response<TransactionsResponse> resp = baseExecute(headers, values);
        resp.setValueType(TransactionsResponse.class);
        return resp;
    }

    protected QueryData getRequestString() {
        if (this.assetId == null) {
            throw new RuntimeException("asset-id is not set. It is a required parameter.");
        }
        addPathSegment(String.valueOf("v2"));
        addPathSegment(String.valueOf("assets"));
        addPathSegment(String.valueOf(assetId));
        addPathSegment(String.valueOf("transactions"));

        return qd;
    }
}
