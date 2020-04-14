package com.algorand.sdkutils.generators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	public static String showDifferentces(
			String jsonA, String jsonB, 
			String nameA, String nameB, 
			boolean full,
			String filterSubstring) throws JsonParseException, JsonMappingException, IOException {
		StringBuffer sb = new StringBuffer();
		jsonA = formatJson(jsonA);
		jsonB = formatJson(jsonB);
		
		StringTokenizer stA = new StringTokenizer(jsonA, "\n");
		StringTokenizer stB = new StringTokenizer(jsonB, "\n");
		boolean filtered = false;
		while (stA.hasMoreTokens() && stB.hasMoreElements()) {
			String lineA = stA.nextToken();
			String lineB = stB.nextToken();
			
			if (lineA.compareTo(lineB) != 0) {
				boolean lineFiltered = false;
				if (lineA.contains(filterSubstring) && lineB.contains(filterSubstring)) {
					filtered = true;
					if (!full) {
						continue;
					}
				}
				if (lineFiltered) sb.append("*");
				sb.append(nameA + "\t>>>" + lineA + "\n");
				if (lineFiltered) sb.append("*");
				sb.append(nameB + "\t>>>" + lineB + "\n");				
			}
			else if (full){
				sb.append(lineA+"\n");
			}
		}
		if (filtered) {
			sb.append("*"+filterSubstring+"\n");
		}
		return sb.toString();
	}
	
	public static String formatJson(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Object jsonObject = mapper.readValue(json, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
	}
	
	public static JsonNode getRoot(FileInputStream fileIs) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root;
		root = objectMapper.readTree(fileIs);
		return root;
	}
	
	public static String readFile(String filename) throws IOException  {
		
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File (filename));
		} catch (FileNotFoundException e) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(fileReader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}

	public static void writeFile(String filename, String content) throws IOException {
		File file = new File(filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.append(content);
		bw.close();
	}

	public static void generateClientFile(String genRoot) throws IOException {
		// Generate the client file
		String clientTemplate = Utils.readFile("src/main/java/com/algorand/sdkutils/generators/Client.java.template");
		String imports = Utils.readFile(genRoot+"algodV2Imports.txt") +  
				Utils.readFile(genRoot+"indexerImports.txt");
		String methods = Utils.readFile(genRoot+"algodV2Paths.txt") +  
				Utils.readFile(genRoot+"indexerPaths.txt");
		clientTemplate = clientTemplate.replace("IMPORTSGOHERE", imports);
		clientTemplate = clientTemplate.replace("METHODSGOHERE", methods);
		Utils.writeFile("src/main/java/com/algorand/algosdk/v2/client/common/Client.java", clientTemplate);
	}
}
