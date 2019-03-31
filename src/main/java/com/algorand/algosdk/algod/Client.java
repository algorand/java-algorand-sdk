package com.algorand.algosdk.algod;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.algorand.algosdk.algod.model.*;

/**
 * Client is the primary interface for accessing the AlgoD REST API. Use ClientFactory to synthesize a Client.
 * Under the hood, Client is for the most part generated using swagger-codegen from the openAPI spec, and subsequently touched up.
 * @see com.algorand.algosdk.algod.ClientFactory#create(String, String)
 */
public interface Client {
    /**
     * Get account information.
     * Given a specific account public key, this call returns the accounts status, balance and spendable amounts
     * @param address An account public key (required)
     * @return Call&lt;Account&gt;
     */
    @GET("v1/account/{address}")
    Call<Account> accountInformation(
            @retrofit2.http.Path("address") String address
    );

    /**
     * Get the block for the given round.
     *
     * @param round The round from which to fetch block information. (required)
     * @return Call&lt;Block&gt;
     */
    @GET("v1/block/{round}")
    Call<Block> getBlock(
            @retrofit2.http.Path("round") Long round
    );

    /**
     * Get a list of unconfirmed transactions currently in the transaction pool.
     * Get the list of pending transactions, sorted by priority, in decreasing order, truncated at the end at MAX. If MAX &#x3D; 0, returns all pending transactions.
     * @param max Truncated number of transactions to display. If max&#x3D;0, returns all pending txns. (optional)
     * @return Call&lt;PendingTransactions&gt;
     */
    @GET("v1/transactions/pending")
    Call<PendingTransactions> getPendingTransactions(
            @retrofit2.http.Path("max") Long max
    );

    /**
     * Gets the current node status.
     *
     * @return Call&lt;NodeStatus&gt;
     */
    @GET("v1/status")
    Call<NodeStatus> getStatus();


    /**
     * Get the current supply reported by the ledger.
     *
     * @return Call&lt;Supply&gt;
     */
    @GET("v1/ledger/supply")
    Call<Supply> getSupply();


    /**
     *
     * Retrieves the current version
     * @return Call&lt;Version&gt;
     */
    @GET("versions")
    Call<Version> getVersion();


    /**
     * Returns OK if healthy.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("health")
    Call<Void> healthCheck();


    /**
     * Get a specific pending transaction.
     * Given a transaction id of a recently submitted transaction, it returns information about it.  There are several cases when this might succeed: - transaction committed (committed round &gt; 0) - transaction still in the pool (committed round &#x3D; 0, pool error &#x3D; \&quot;\&quot;) - transaction removed from pool due to error (committed round &#x3D; 0, pool error !&#x3D; \&quot;\&quot;) Or the transaction may have happened sufficiently long ago that the node no longer remembers it, and this will return an error.
     * @param txid A transaction id (required)
     * @return Call&lt;Transaction&gt;
     */
    @GET("v1/transaction/pending/{txid}")
    Call<Transaction> pendingTransactionInformation(
            @retrofit2.http.Path("txid") String txid
    );

    /**
     * Broadcasts a raw transaction to the network.
     *
     * @param body The byte encoded signed transaction to broadcast to network (required)
     * @return Call&lt;TransactionID&gt;
     */
    @Headers({
            "Content-TxType:application/x-binary"
    })
    @POST("v1/transactions")
    Call<TransactionID> rawTransaction(
            @retrofit2.http.Body String body
    );

    /**
     * Get the suggested fee
     *
     * @return Call&lt;TransactionFee&gt;
     */
    @GET("v1/transactions/fee")
    Call<TransactionFee> suggestedFee();


    /**
     * Gets the current swagger spec.
     * Returns the entire swagger spec in json.
     * @return Call&lt;String&gt;
     */
    @GET("swagger.json")
    Call<String> swaggerJSON();


    /**
     * Get a specific confirmed transaction.
     * Given a wallet address and a transaction id, it returns the confirmed transaction information. This call scans up to config.Protocol.MaxTxnLife blocks in the past.
     * @param address An account public key (required)
     * @param txid A transaction id (required)
     * @return Call&lt;Transaction&gt;
     */
    @GET("v1/account/{address}/transaction/{txid}")
    Call<Transaction> transactionInformation(
            @retrofit2.http.Path("address") String address,
            @retrofit2.http.Path("txid") String txid
    );

    /**
     * Get parameters for constructing a new transaction
     *
     * @return Call&lt;TransactionParams&gt;
     */
    @GET("v1/transactions/params")
    Call<TransactionParams> transactionParams();


    /**
     * Get a list of confirmed transactions.
     * Returns the list of confirmed transactions between firstRound and lastRound
     * @param address An account public key (required)
     * @param firstRound Do not fetch any transactions before this round. (required)
     * @param lastRound Do not fetch any transactions after this round. (required)
     * @return Call&lt;TransactionList&gt;
     */
    @GET("v1/account/{address}/transactions")
    Call<TransactionList> transactions(
            @retrofit2.http.Path("address") String address,
            @retrofit2.http.Path("firstRound") Long firstRound,
            @retrofit2.http.Path("lastRound") Long lastRound
    );

    /**
     * Gets the node status after waiting for the given round.
     * Waits for a block to appear after round {round} and returns the node&#x27;s status at the time.
     * @param round The round to wait until returning status (required)
     * @return Call&lt;NodeStatus&gt;
     */
    @GET("v1/status/wait-for-block-after/{round}/")
    Call<NodeStatus> waitForBlock(
            @retrofit2.http.Path("round") Long round
    );

}
