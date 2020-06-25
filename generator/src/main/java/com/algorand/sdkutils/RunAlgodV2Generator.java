package com.algorand.sdkutils;

import java.io.File;

public class RunAlgodV2Generator {
    public static void main (String args[]) throws Exception {
        String specFilePath = "../../../go/src/github.com/algorand/go-algorand/daemon/algod/api/algod.oas2.json";
        if (args.length == 1) {
            specFilePath = args[0];
        }
        File specfile = new File(specFilePath);

        Main.Generate(
                "AlgodClient",
                specfile,
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/algod",
                "com.algorand.algosdk.v2.client.algod",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Algo-API-Token",
                false, 
                "");
    }
}

