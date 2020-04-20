package com.algorand.sdkutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.sdkutils.generators.TestGenerator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class RunIndexerTests extends TestGenerator{

	RunIndexerTests(JsonNode root) {
		super(root);
	}

	public static void main (String args[]) throws Exception {
		File f = new File("../openapi-server-generator/scripts/indexer.oas2.yml");
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = Utils.getRoot(fis);	
		TestGenerator tg = new TestGenerator(root);


		int port = 8980;
		String host = "localhost";
		Client client = new Client(host, port);

		File inFile = new File("./src/main/java/com/algorand/sdkutils/test.csv");
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		testSamples(tg, br, client, true);
		br.close();
		System.out.println("File tested: " + "./src/main/java/com/algorand/sdkutils/test.csv");
	}
}