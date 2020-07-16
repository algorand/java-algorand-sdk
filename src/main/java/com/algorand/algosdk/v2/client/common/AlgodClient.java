package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.v2.client.algod.HealthCheck;
import com.algorand.algosdk.v2.client.algod.Metrics;
import com.algorand.algosdk.v2.client.algod.SwaggerJSON;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactionsByAddress;
import com.algorand.algosdk.v2.client.algod.GetBlock;
import com.algorand.algosdk.v2.client.algod.GetSupply;
import com.algorand.algosdk.v2.client.algod.GetStatus;
import com.algorand.algosdk.v2.client.algod.WaitForBlock;
import com.algorand.algosdk.v2.client.algod.RawTransaction;
import com.algorand.algosdk.v2.client.algod.TransactionParams;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactions;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.algod.GetApplicationByID;
import com.algorand.algosdk.v2.client.algod.GetAssetByID;
import com.algorand.algosdk.v2.client.algod.TealCompile;
import com.algorand.algosdk.v2.client.algod.TealDryrun;
import com.algorand.algosdk.crypto.Address;

public class AlgodClient extends Client {

    /**
     * Construct an AlgodClient for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     * @param token authentication token.
     */
    public AlgodClient(String host, int port, String token) {
        super(host, port, token, "X-Algo-API-Token");
    }
    /**
     * Returns OK if healthy.
     * /health
     */
    public HealthCheck HealthCheck() {
        return new HealthCheck((Client) this);
    }

    /**
     * Return metrics about algod functioning.
     * /metrics
     */
    public Metrics Metrics() {
        return new Metrics((Client) this);
    }

    /**
     * Returns the entire swagger spec in json.
     * /swagger.json
     */
    public SwaggerJSON SwaggerJSON() {
        return new SwaggerJSON((Client) this);
    }

    /**
     * Given a specific account public key, this call returns the accounts status,
     * balance and spendable amounts
     * /v2/accounts/{address}
     */
    public AccountInformation AccountInformation(Address address) {
        return new AccountInformation((Client) this, address);
    }

    /**
     * Get the list of pending transactions by address, sorted by priority, in
     * decreasing order, truncated at the end at MAX. If MAX = 0, returns all pending
     * transactions.
     * /v2/accounts/{address}/transactions/pending
     */
    public GetPendingTransactionsByAddress GetPendingTransactionsByAddress(Address address) {
        return new GetPendingTransactionsByAddress((Client) this, address);
    }

    /**
     * Get the block for the given round.
     * /v2/blocks/{round}
     */
    public GetBlock GetBlock(Long round) {
        return new GetBlock((Client) this, round);
    }

    /**
     * Get the current supply reported by the ledger.
     * /v2/ledger/supply
     */
    public GetSupply GetSupply() {
        return new GetSupply((Client) this);
    }

    /**
     * Gets the current node status.
     * /v2/status
     */
    public GetStatus GetStatus() {
        return new GetStatus((Client) this);
    }

    /**
     * Waits for a block to appear after round {round} and returns the node's status at
     * the time.
     * /v2/status/wait-for-block-after/{round}
     */
    public WaitForBlock WaitForBlock(Long round) {
        return new WaitForBlock((Client) this, round);
    }

    /**
     * Broadcasts a raw transaction to the network.
     * /v2/transactions
     */
    public RawTransaction RawTransaction() {
        return new RawTransaction((Client) this);
    }

    /**
     * Get parameters for constructing a new transaction
     * /v2/transactions/params
     */
    public TransactionParams TransactionParams() {
        return new TransactionParams((Client) this);
    }

    /**
     * Get the list of pending transactions, sorted by priority, in decreasing order,
     * truncated at the end at MAX. If MAX = 0, returns all pending transactions.
     * /v2/transactions/pending
     */
    public GetPendingTransactions GetPendingTransactions() {
        return new GetPendingTransactions((Client) this);
    }

    /**
     * Given a transaction id of a recently submitted transaction, it returns
     * information about it. There are several cases when this might succeed:
     * - transaction committed (committed round > 0) - transaction still in the pool
     * (committed round = 0, pool error = "") - transaction removed from pool due to
     * error (committed round = 0, pool error != "")
     * Or the transaction may have happened sufficiently long ago that the node no
     * longer remembers it, and this will return an error.
     * /v2/transactions/pending/{txid}
     */
    public PendingTransactionInformation PendingTransactionInformation(String txid) {
        return new PendingTransactionInformation((Client) this, txid);
    }

    /**
     * Given a application id, it returns application information including creator,
     * approval and clear programs, global and local schemas, and global state.
     * /v2/applications/{application-id}
     */
    public GetApplicationByID GetApplicationByID(Long applicationId) {
        return new GetApplicationByID((Client) this, applicationId);
    }

    /**
     * Given a asset id, it returns asset information including creator, name, total
     * supply and special addresses.
     * /v2/assets/{asset-id}
     */
    public GetAssetByID GetAssetByID(Long assetId) {
        return new GetAssetByID((Client) this, assetId);
    }

    /**
     * Given TEAL source code in plain text, return base64 encoded program bytes and
     * base32 SHA512_256 hash of program bytes (Address style).
     * /v2/teal/compile
     */
    public TealCompile TealCompile() {
        return new TealCompile((Client) this);
    }

    /**
     * Executes TEAL program(s) in context and returns debugging information about the
     * execution.
     * /v2/teal/dryrun
     */
    public TealDryrun TealDryrun() {
        return new TealDryrun((Client) this);
    }

}
