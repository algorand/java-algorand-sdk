package com.algorand.algosdk.integration;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import io.cucumber.java.en.Given;

import java.util.HashMap;
import java.util.Map;

public class Clients {
    AlgodClient v2Client = null;

    @Deprecated
    Map<Integer, IndexerClient> indexerClients = new HashMap<>();

    IndexerClient v2IndexerClient = null;

    @Given("an algod v2 client connected to {string} port {int} with token {string}")
    public void an_algod_v2_client_connected_to_port_with_token(String host, Integer port, String token) {
        v2Client = new AlgodClient(host, port, token);
    }

    @Given("an indexer v2 client")
    public void indexer_v2_client() {
        v2IndexerClient = new IndexerClient("localhost", 59999);
    }
}
