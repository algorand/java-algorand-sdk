package com.algorand.sdkutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.sdkutils.generators.TestGenerator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class RunIndexerTests extends TestGenerator{

	RunIndexerTests(JsonNode root) {
		super(root);
	}

	public static void main (String args[]) throws Exception {
		File f = new File("src/main/java/com/algorand/sdkutils/indexer.oas2.json");
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = Utils.getRoot(fis);	
		TestGenerator tg = new TestGenerator(root);


		int port = 8980;
		String host = "localhost";
		Client client = new IndexerClient(host, port, null);

		File inFile = new File("./src/main/java/com/algorand/sdkutils/indexerTests.csv");
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		boolean passed = testSamples(tg, br, client, false);
		if (!passed) {
			System.out.println("[FAILED]");
		} else {
			System.out.println("[PASSED]");
		}
		br.close();
		System.out.println("File tested: " + "./src/main/java/com/algorand/sdkutils/indexerTests.csv");
	}
}
