package com.algorand.sdkutils;

import java.io.FileInputStream;

import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.listeners.GoGenerator;
import com.algorand.sdkutils.listeners.JavaGenerator;
import com.algorand.sdkutils.listeners.Publisher;
import com.fasterxml.jackson.databind.JsonNode;

public class RunAlgodV2AndIndexerGenerators {
    public static void main (String args[]) throws Exception {
        String aSpecFilePath = "../../../go/src/github.com/algorand/go-algorand/daemon/algod/api/algod.oas2.json";
        String iSpecFilePath = "../../indexer/api/indexer.oas2.json";

        Publisher publisher = new Publisher();
        
        if (args.length == 2) {
            aSpecFilePath = args[0];
            iSpecFilePath = args[1];
        }

        JsonNode root;
        try (FileInputStream fis = new FileInputStream(aSpecFilePath)) {
            root = Utils.getRoot(fis);
        }

        OpenApiParser g = new OpenApiParser(root, publisher);
        new GoGenerator("go-sdk", "algod", publisher);
        new JavaGenerator(
                "AlgodClient",
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/algod",
                "com.algorand.algosdk.v2.client.algod",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Algo-API-Token",
                false, 
                publisher);
        
        g.parse();

        publisher = new Publisher();
        try (FileInputStream fis = new FileInputStream(iSpecFilePath)) {
            root = Utils.getRoot(fis);
        }
        g = new OpenApiParser(root, publisher);
        new GoGenerator("go-sdk", "indexer", publisher);
        new JavaGenerator(
                "IndexerClient",
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/indexer",
                "com.algorand.algosdk.v2.client.indexer",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Indexer-API-Token",
                true, 
                publisher);
        
        g.parse();
    }
}

