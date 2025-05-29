package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.v2.client.algod.HealthCheck;
import com.algorand.algosdk.v2.client.algod.GetReady;
import com.algorand.algosdk.v2.client.algod.Metrics;
import com.algorand.algosdk.v2.client.algod.GetGenesis;
import com.algorand.algosdk.v2.client.algod.SwaggerJSON;
import com.algorand.algosdk.v2.client.algod.GetVersion;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.algod.AccountAssetInformation;
import com.algorand.algosdk.v2.client.algod.AccountApplicationInformation;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactionsByAddress;
import com.algorand.algosdk.v2.client.algod.GetBlock;
import com.algorand.algosdk.v2.client.algod.GetBlockTxids;
import com.algorand.algosdk.v2.client.algod.GetBlockHash;
import com.algorand.algosdk.v2.client.algod.GetTransactionProof;
import com.algorand.algosdk.v2.client.algod.GetBlockLogs;
import com.algorand.algosdk.v2.client.algod.GetSupply;
import com.algorand.algosdk.v2.client.algod.GetStatus;
import com.algorand.algosdk.v2.client.algod.WaitForBlock;
import com.algorand.algosdk.v2.client.algod.RawTransaction;
import com.algorand.algosdk.v2.client.algod.SimulateTransaction;
import com.algorand.algosdk.v2.client.algod.TransactionParams;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactions;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.algod.GetLedgerStateDelta;
import com.algorand.algosdk.v2.client.algod.GetTransactionGroupLedgerStateDeltasForRound;
import com.algorand.algosdk.v2.client.algod.GetLedgerStateDeltaForTransactionGroup;
import com.algorand.algosdk.v2.client.algod.GetStateProof;
import com.algorand.algosdk.v2.client.algod.GetLightBlockHeaderProof;
import com.algorand.algosdk.v2.client.algod.GetApplicationByID;
import com.algorand.algosdk.v2.client.algod.GetApplicationBoxes;
import com.algorand.algosdk.v2.client.algod.GetApplicationBoxByName;
import com.algorand.algosdk.v2.client.algod.GetAssetByID;
import com.algorand.algosdk.v2.client.algod.UnsetSyncRound;
import com.algorand.algosdk.v2.client.algod.GetSyncRound;
import com.algorand.algosdk.v2.client.algod.SetSyncRound;
import com.algorand.algosdk.v2.client.algod.TealCompile;
import com.algorand.algosdk.v2.client.algod.TealDisassemble;
import com.algorand.algosdk.v2.client.algod.TealDryrun;
import com.algorand.algosdk.v2.client.algod.GetBlockTimeStampOffset;
import com.algorand.algosdk.v2.client.algod.SetBlockTimeStampOffset;
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
     * Construct an AlgodClient with custom token key for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     * @param token authentication token.
     * @param tokenKey authentication token key.
     */
    public AlgodClient(String host, int port, String token, String tokenKey) {
        super(host, port, token, tokenKey);
    }

    /**
     * Returns OK if healthy.
     * /health
     */
    public HealthCheck HealthCheck() {
        return new HealthCheck((Client) this);
    }

    /**
     * Returns OK if healthy and fully caught up.
     * /ready
     */
    public GetReady GetReady() {
        return new GetReady((Client) this);
    }

    /**
     * Return metrics about algod functioning.
     * /metrics
     */
    public Metrics Metrics() {
        return new Metrics((Client) this);
    }

    /**
     * Returns the entire genesis file in json.
     * /genesis
     */
    public GetGenesis GetGenesis() {
        return new GetGenesis((Client) this);
    }

    /**
     * Returns the entire swagger spec in json.
     * /swagger.json
     */
    public SwaggerJSON SwaggerJSON() {
        return new SwaggerJSON((Client) this);
    }

    /**
     * Retrieves the supported API versions, binary build versions, and genesis
     * information.
     * /versions
     */
    public GetVersion GetVersion() {
        return new GetVersion((Client) this);
    }

    /**
     * Given a specific account public key, this call returns the account's status,
     * balance and spendable amounts
     * /v2/accounts/{address}
     */
    public AccountInformation AccountInformation(Address address) {
        return new AccountInformation((Client) this, address);
    }

    /**
     * Given a specific account public key and asset ID, this call returns the
     * account's asset holding and asset parameters (if either exist). Asset parameters
     * will only be returned if the provided address is the asset's creator.
     * /v2/accounts/{address}/assets/{asset-id}
     */
    public AccountAssetInformation AccountAssetInformation(Address address,
            Long assetId) {
        return new AccountAssetInformation((Client) this, address, assetId);
    }

    /**
     * Given a specific account public key and application ID, this call returns the
     * account's application local state and global state (AppLocalState and AppParams,
     * if either exists). Global state will only be returned if the provided address is
     * the application's creator.
     * /v2/accounts/{address}/applications/{application-id}
     */
    public AccountApplicationInformation AccountApplicationInformation(Address address,
            Long applicationId) {
        return new AccountApplicationInformation((Client) this, address, applicationId);
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
     * Get the top level transaction IDs for the block on the given round.
     * /v2/blocks/{round}/txids
     */
    public GetBlockTxids GetBlockTxids(Long round) {
        return new GetBlockTxids((Client) this, round);
    }

    /**
     * Get the block hash for the block on the given round.
     * /v2/blocks/{round}/hash
     */
    public GetBlockHash GetBlockHash(Long round) {
        return new GetBlockHash((Client) this, round);
    }

    /**
     * Get a proof for a transaction in a block.
     * /v2/blocks/{round}/transactions/{txid}/proof
     */
    public GetTransactionProof GetTransactionProof(Long round,
            String txid) {
        return new GetTransactionProof((Client) this, round, txid);
    }

    /**
     * Get all of the logs from outer and inner app calls in the given round
     * /v2/blocks/{round}/logs
     */
    public GetBlockLogs GetBlockLogs(Long round) {
        return new GetBlockLogs((Client) this, round);
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
     * the time. There is a 1 minute timeout, when reached the current status is
     * returned regardless of whether or not it is the round after the given round.
     * /v2/status/wait-for-block-after/{round}
     */
    public WaitForBlock WaitForBlock(Long round) {
        return new WaitForBlock((Client) this, round);
    }

    /**
     * Broadcasts a raw transaction or transaction group to the network.
     * /v2/transactions
     */
    public RawTransaction RawTransaction() {
        return new RawTransaction((Client) this);
    }

    /**
     * Simulates a raw transaction or transaction group as it would be evaluated on the
     * network. The simulation will use blockchain state from the latest committed
     * round.
     * /v2/transactions/simulate
     */
    public SimulateTransaction SimulateTransaction() {
        return new SimulateTransaction((Client) this);
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
     * Given a transaction ID of a recently submitted transaction, it returns
     * information about it. There are several cases when this might succeed:
     * - transaction committed (committed round > 0)
     * - transaction still in the pool (committed round = 0, pool error = "")
     * - transaction removed from pool due to error (committed round = 0, pool error !=
     * "")
     * Or the transaction may have happened sufficiently long ago that the node no
     * longer remembers it, and this will return an error.
     * /v2/transactions/pending/{txid}
     */
    public PendingTransactionInformation PendingTransactionInformation(String txid) {
        return new PendingTransactionInformation((Client) this, txid);
    }

    /**
     * Get ledger deltas for a round.
     * /v2/deltas/{round}
     */
    public GetLedgerStateDelta GetLedgerStateDelta(Long round) {
        return new GetLedgerStateDelta((Client) this, round);
    }

    /**
     * Get ledger deltas for transaction groups in a given round.
     * /v2/deltas/{round}/txn/group
     */
    public GetTransactionGroupLedgerStateDeltasForRound GetTransactionGroupLedgerStateDeltasForRound(Long round) {
        return new GetTransactionGroupLedgerStateDeltasForRound((Client) this, round);
    }

    /**
     * Get a ledger delta for a given transaction group.
     * /v2/deltas/txn/group/{id}
     */
    public GetLedgerStateDeltaForTransactionGroup GetLedgerStateDeltaForTransactionGroup(String id) {
        return new GetLedgerStateDeltaForTransactionGroup((Client) this, id);
    }

    /**
     * Get a state proof that covers a given round
     * /v2/stateproofs/{round}
     */
    public GetStateProof GetStateProof(Long round) {
        return new GetStateProof((Client) this, round);
    }

    /**
     * Gets a proof for a given light block header inside a state proof commitment
     * /v2/blocks/{round}/lightheader/proof
     */
    public GetLightBlockHeaderProof GetLightBlockHeaderProof(Long round) {
        return new GetLightBlockHeaderProof((Client) this, round);
    }

    /**
     * Given a application ID, it returns application information including creator,
     * approval and clear programs, global and local schemas, and global state.
     * /v2/applications/{application-id}
     */
    public GetApplicationByID GetApplicationByID(Long applicationId) {
        return new GetApplicationByID((Client) this, applicationId);
    }

    /**
     * Given an application ID, return all Box names. No particular ordering is
     * guaranteed. Request fails when client or server-side configured limits prevent
     * returning all Box names.
     * /v2/applications/{application-id}/boxes
     */
    public GetApplicationBoxes GetApplicationBoxes(Long applicationId) {
        return new GetApplicationBoxes((Client) this, applicationId);
    }

    /**
     * Given an application ID and box name, it returns the round, box name, and value
     * (each base64 encoded). Box names must be in the goal app call arg encoding form
     * 'encoding:value'. For ints, use the form 'int:1234'. For raw bytes, use the form
     * 'b64:A=='. For printable strings, use the form 'str:hello'. For addresses, use
     * the form 'addr:XYZ...'.
     * /v2/applications/{application-id}/box
     */
    public GetApplicationBoxByName GetApplicationBoxByName(Long applicationId) {
        return new GetApplicationBoxByName((Client) this, applicationId);
    }

    /**
     * Given a asset ID, it returns asset information including creator, name, total
     * supply and special addresses.
     * /v2/assets/{asset-id}
     */
    public GetAssetByID GetAssetByID(Long assetId) {
        return new GetAssetByID((Client) this, assetId);
    }

    /**
     * Unset the ledger sync round.
     * /v2/ledger/sync
     */
    public UnsetSyncRound UnsetSyncRound() {
        return new UnsetSyncRound((Client) this);
    }

    /**
     * Gets the minimum sync round for the ledger.
     * /v2/ledger/sync
     */
    public GetSyncRound GetSyncRound() {
        return new GetSyncRound((Client) this);
    }

    /**
     * Sets the minimum sync round on the ledger.
     * /v2/ledger/sync/{round}
     */
    public SetSyncRound SetSyncRound(Long round) {
        return new SetSyncRound((Client) this, round);
    }

    /**
     * Given TEAL source code in plain text, return base64 encoded program bytes and
     * base32 SHA512_256 hash of program bytes (Address style). This endpoint is only
     * enabled when a node's configuration file sets EnableDeveloperAPI to true.
     * /v2/teal/compile
     */
    public TealCompile TealCompile() {
        return new TealCompile((Client) this);
    }

    /**
     * Given the program bytes, return the TEAL source code in plain text. This
     * endpoint is only enabled when a node's configuration file sets
     * EnableDeveloperAPI to true.
     * /v2/teal/disassemble
     */
    public TealDisassemble TealDisassemble() {
        return new TealDisassemble((Client) this);
    }

    /**
     * Executes TEAL program(s) in context and returns debugging information about the
     * execution. This endpoint is only enabled when a node's configuration file sets
     * EnableDeveloperAPI to true.
     * /v2/teal/dryrun
     */
    public TealDryrun TealDryrun() {
        return new TealDryrun((Client) this);
    }

    /**
     * Gets the current timestamp offset.
     * /v2/devmode/blocks/offset
     */
    public GetBlockTimeStampOffset GetBlockTimeStampOffset() {
        return new GetBlockTimeStampOffset((Client) this);
    }

    /**
     * Sets the timestamp offset (seconds) for blocks in dev mode. Providing an offset
     * of 0 will unset this value and try to use the real clock for the timestamp.
     * /v2/devmode/blocks/offset/{offset}
     */
    public SetBlockTimeStampOffset SetBlockTimeStampOffset(Long offset) {
        return new SetBlockTimeStampOffset((Client) this, offset);
    }

}
