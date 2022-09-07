package com.algorand.algosdk.integration;

import com.algorand.algosdk.v2.client.common.Client;
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

    enum ClientEnum {
        AlgodV2Client,
        IndexerV2Client
    }

    @Given("an algod v2 client connected to {string} port {int} with token {string}")
    public void an_algod_v2_client_connected_to_port_with_token(String host, Integer port, String token) {
        v2Client = new AlgodClient(host, port, token);
    }

    @Given("an indexer v2 client")
    public void indexer_v2_client() {
        v2IndexerClient = new IndexerClient("localhost", 59999);
    }

    public Client according_to(ClientEnum clientName) {
        switch (clientName) {
            case AlgodV2Client:
                return v2Client;
            case IndexerV2Client:
                return v2IndexerClient;
            default:
                throw new IllegalArgumentException("client enum can't be found in Clients class");
        }
    }
}
