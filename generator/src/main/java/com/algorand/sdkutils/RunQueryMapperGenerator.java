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

		File f1 = null;
		try {
			f1 = new File(args[0]);
		} catch (Exception e){
			System.err.println("Couldn't read the indexer.oas2.json file. The path should be passed as the first argument.");
			System.exit(1);
		}
		FileInputStream fis1 = new FileInputStream(f1);
		JsonNode indexerRoot = Utils.getRoot(fis1);

		File f2 = null;
		try {
			f2 = new File(args[1]);
		} catch (Exception e){
			System.err.println("Couldn't read the algod.oas2.json file. The path should be passed as the second argument.");
			System.exit(1);
		}
		FileInputStream fis2 = new FileInputStream(f2);
		JsonNode algodRoot = Utils.getRoot(fis2);

		
		QueryMapperGenerator qmg = new QueryMapperGenerator(indexerRoot, algodRoot);
		qmg.writeQueryMapper("src/test/java/com/algorand/algosdk/unit/utils");
		
		TemplateGenerator tg = new TemplateGenerator(indexerRoot);
		tg.writeTemplate("./src/main/java/com/algorand/sdkutils/indexerTemplate");
		
		tg = new TemplateGenerator(algodRoot);
		tg.writeTemplate("./src/main/java/com/algorand/sdkutils/algodV2Template");
	}

}
