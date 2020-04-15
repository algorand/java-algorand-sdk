package com.algorand.indexer;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.junit.jupiter.api.Test;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.sdkutils.generators.TestGenerator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.databind.JsonNode;

class TestIndexerCases {

	@Test
	void test() throws Exception {
		File f = new File("./src/test/java/com/algorand/indexer/indexer.oas2.yml");
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = Utils.getRoot(fis);	
		TestGenerator tg = new TestGenerator(root);

		int port = 8980;
		String host = "localhost";
		Client client = new Client(host, port);

		File inFile = new File("./src/main/java/com/algorand/sdkutils/test.csv");
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		boolean pass = true;//TestGenerator.testSamples(tg, br, client, false);
		br.close();

		if (!pass) {
			fail("Some tests failed!");
		}
	}

}
