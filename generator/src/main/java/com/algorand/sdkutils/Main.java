package com.algorand.sdkutils;

import com.algorand.sdkutils.generators.Generator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main (String args[]) throws JsonProcessingException, IOException {
        // Arguments:
        //     - specfile
        //     - model path: src/main/java/com/algorand/algosdk/v2/client/model
        //     - model package: com.algorand.algosdk.v2.client.model
        //     - client path: src/main/java/com/algorand/algosdk/v2/client/algod
        //     - client package: com.algorand.algosdk.v2.client.algod

        File f = null;
        try {
            f = new File(args[0]);
        } catch (Exception e){
            System.err.println("Couldn't read the algod.oas2.json file. The path should be passed as an argument.");
            System.exit(1);
        }
        FileInputStream fis = new FileInputStream(f);

        JsonNode root = Utils.getRoot(fis);
        String rootPath = "src/main/java/com/algorand/algosdk/";
        String genRoot = "src/main/java/com/algorand/sdkutils/generated/";

        Generator g = new Generator(root);

        // Generate classes from the schemas
        // com.algorand.algosdk.v2.client.model
        String pkg = "com.algorand.algosdk.v2.client.model";
        System.out.println("Generating " + pkg + " to " + rootPath+"/v2/client/model");
        Generator.generateAlgodIndexerObjects(root, rootPath+"/v2/client/model", pkg);
        Generator.generateEnumClasses(root, rootPath+"/v2/client/model", pkg);

        // Generate classes from the return types which have more than one return element
        // com.algorand.algosdk.v2.client.model
        System.out.println("Generating " + pkg + " to " + rootPath+"/v2/client/model");
        Generator.generateReturnTypes(root, rootPath+"/v2/client/model", pkg);

        // Generate the algod methods
        // com.algorand.algosdk.v2.client.indexer
        String apkg = "com.algorand.algosdk.v2.client.algod";
        System.out.println("Generating " + apkg + " to " + rootPath+"/v2/client/algod");
        g.generateQueryMethods(rootPath+"/v2/client/algod",
                apkg, pkg,
                genRoot+"algodV2Imports.txt",
                genRoot+"algodV2Paths.txt");

        Utils.generateAlgodClientFile(genRoot);

    }
}
