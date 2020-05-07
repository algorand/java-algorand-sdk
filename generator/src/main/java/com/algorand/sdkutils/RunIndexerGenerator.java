package com.algorand.sdkutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import com.algorand.sdkutils.generators.Generator;
import com.algorand.sdkutils.generators.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RunIndexerGenerator {

	public static void main (String args[]) throws Exception {
		File specfile = new File("../../indexer/api/indexer.oas2.json");
		Main.Generate(
				"IndexerClient",
				specfile,
				"../src/main/java/com/algorand/algosdk/v2/client/model",
				"com.algorand.algosdk.v2.client.model",
				"../src/main/java/com/algorand/algosdk/v2/client/indexer",
				"com.algorand.algosdk.v2.client.indexer",
				"../src/main/java/com/algorand/algosdk/v2/client/common/",
				"com.algorand.algosdk.v2.client.common",
				"X-Indexer-API-Token",
				true);
	}
}
