package com.algorand.sdkutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.algorand.sdkutils.generators.Generator;
import com.algorand.sdkutils.generators.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RunIndexerGenerator {

	public static void main (String args[]) throws JsonProcessingException, IOException {

		///Users/shantkarakashian/go/src/github.com/algorand/openapi-server-generator/scripts/indexer.oas2.yml ./src/main/java/com/algorand/algosdk/
		File f = new File("../openapi-server-generator/scripts/indexer.oas2.yml");
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = JsonUtils.getRoot(fis);	
		String rootPath = "src/main/java/com/algorand/algosdk/";

		Generator g = new Generator(root);

		// Generate classes from the schemas
		// com.algorand.algosdk.v2.client.model
		String pkg = "com.algorand.algosdk.v2.client.model";
		System.out.println("Generating " + pkg + " to " + rootPath+"/v2/client/model");
		Generator.generateAlgodIndexerObjects(root, rootPath+"/v2/client/model", pkg);

		// Generate classes from the return types which have more than one return element
		// com.algorand.algosdk.v2.client.model		
		System.out.println("Generating " + pkg + " to " + rootPath+"/v2/client/model");
		Generator.generateReturnTypes(root, rootPath+"/v2/client/model", pkg);

		// Generate the indexer methods
		// com.algorand.algosdk.v2.client.indexer		
		String ipkg = "com.algorand.algosdk.v2.client.indexer";
		System.out.println("Generating " + ipkg + " to " + rootPath+"/v2/client/indexer");
		g.generateIndexerMethods(rootPath+"/v2/client/indexer", ipkg, pkg);
	}
}
