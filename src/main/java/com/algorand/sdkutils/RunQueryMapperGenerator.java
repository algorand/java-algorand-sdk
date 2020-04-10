package com.algorand.sdkutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.algorand.sdkutils.generators.JsonUtils;
import com.algorand.sdkutils.generators.QueryMapperGenerator;
import com.algorand.sdkutils.generators.TemplateGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RunQueryMapperGenerator {
	public static void main (String args[]) throws JsonProcessingException, IOException {

		File f = new File("../openapi-server-generator/scripts/indexer.oas2.yml");
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = JsonUtils.getRoot(fis);	
		QueryMapperGenerator qmg = new QueryMapperGenerator(root);
		
		TemplateGenerator tg = new TemplateGenerator(root);

		qmg.writeQueryMapper("src/main/java/com/algorand/sdkutils/generated/");
		tg.writeTemplate("./src/main/java/com/algorand/sdkutils/template");
	}

}
