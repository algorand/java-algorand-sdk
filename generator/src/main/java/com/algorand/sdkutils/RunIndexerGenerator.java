package com.algorand.sdkutils;

import java.io.File;

public class RunIndexerGenerator {

    public static void main (String args[]) throws Exception {
        String specFilePath = "../../indexer/api/indexer.oas2.json";
        if (args.length == 1) {
            specFilePath = args[0];
        }
        File specfile = new File(specFilePath);

        Generate.generate(
                "IndexerClient",
                specfile,
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/indexer",
                "com.algorand.algosdk.v2.client.indexer",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Indexer-API-Token",
                true,
                "./go-sdk");
    }
}
