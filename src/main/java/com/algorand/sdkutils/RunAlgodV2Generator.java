package com.algorand.sdkutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.algorand.sdkutils.generators.Generator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RunAlgodV2Generator {

	public static void main (String args[]) throws JsonProcessingException, IOException {

		///Users/shantkarakashian/go/src/github.com/algorand/openapi-server-generator/scripts/algod.oas2.yml src/main/java/com/algorand/algosdk/
		File f = new File("src/main/java/com/algorand/sdkutils/algod.oas2.json");
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
		
		Utils.generateClientFile(genRoot);

	}
}

