package com.algorand.algosdk.v2.client.common;

import com.algorand.algosdk.v2.client.indexer.SearchForApplications;
import com.algorand.algosdk.v2.client.indexer.LookupApplicationByID;
import com.algorand.algosdk.crypto.Address;

public class IndexerClient extends Client {

    /**
     * Construct an IndexerClient for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     * @param token authentication token.
     */
    public IndexerClient(String host, int port, String token) {
        super(host, port, token, "X-Indexer-API-Token");
    }

    /**
     * Construct an IndexerClient for communicating with the REST API.
     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port REST server port.
     */
    public IndexerClient(String host, int port) {
        super(host, port, "", "X-Indexer-API-Token");
    }

    /**
     * Search for applications
     * /v2/applications
     */
    public SearchForApplications searchForApplications() {
        return new SearchForApplications((Client) this);
    }

    /**
     * Lookup application.
     * /v2/applications/{application-id}
     */
    public LookupApplicationByID lookupApplicationByID(Long applicationId) {
        return new LookupApplicationByID((Client) this, applicationId);
    }

}
