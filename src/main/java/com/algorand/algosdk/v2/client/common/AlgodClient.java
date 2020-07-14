package com.algorand.algosdk.v2.client.common;

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
