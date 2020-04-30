package com.algorand.sdkutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.generators.QueryMapperGenerator;
import com.algorand.sdkutils.generators.TemplateGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RunQueryMapperGenerator {
	public static void main (String args[]) throws JsonProcessingException, IOException {

		File f1 = new File("src/main/java/com/algorand/sdkutils/indexer.oas2.json");
		FileInputStream fis1 = new FileInputStream(f1);
		JsonNode indexerRoot = Utils.getRoot(fis1);

		File f2 = new File("src/main/java/com/algorand/sdkutils/algod.oas2.json");
		FileInputStream fis2 = new FileInputStream(f2);
		JsonNode algodRoot = Utils.getRoot(fis2);

		
		QueryMapperGenerator qmg = new QueryMapperGenerator(indexerRoot, algodRoot);
		qmg.writeQueryMapper("src/main/java/com/algorand/sdkutils/generated/");
		
		TemplateGenerator tg = new TemplateGenerator(indexerRoot);
		tg.writeTemplate("./src/main/java/com/algorand/sdkutils/indexerTemplate");
		
		tg = new TemplateGenerator(algodRoot);
		tg.writeTemplate("./src/main/java/com/algorand/sdkutils/algodV2Template");
	}

}
