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

import com.algorand.algosdk.v2.client.common.Client;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utils {
	
	public static DiffResult showDifferentces(
			String jsonA, String jsonB, 
			String nameA, String nameB, 
			String filterSubstring) throws JsonParseException, JsonMappingException, IOException {
		
		StringBuffer full = new StringBuffer();
		StringBuffer diff = new StringBuffer();
		
		jsonA = formatJson(jsonA);
		jsonB = formatJson(jsonB);
		
		StringTokenizer stA = new StringTokenizer(jsonA, "\n");
		StringTokenizer stB = new StringTokenizer(jsonB, "\n");

		DiffResult dr = new DiffResult();
		dr.pass = true;
		dr.filtered = false;
		
		while (stA.hasMoreTokens() && stB.hasMoreElements()) {
			String lineA = stA.nextToken();
			String lineB = stB.nextToken();
			
			boolean filteredLine = false;
			
			if (lineA.compareTo(lineB) != 0) {
				if (lineA.contains(filterSubstring) && lineB.contains(filterSubstring)) {
					filteredLine = true;
				}
				if (filteredLine) {
					dr.filtered = true;
					full.append("*");
					full.append(nameA + "\t>>>" + lineA + "\n");
					full.append("*");
					full.append(nameB + "\t>>>" + lineB + "\n");
					
					diff.append("*");
					diff.append(nameA + "\t>>>" + lineA + "\n");
					diff.append("*");
					diff.append(nameB + "\t>>>" + lineB + "\n");
				}
				else {
					dr.pass = false;
					full.append(nameA + "\t>>>" + lineA + "\n");
					full.append(nameB + "\t>>>" + lineB + "\n");
					
					diff.append(nameA + "\t>>>" + lineA + "\n");
					diff.append(nameB + "\t>>>" + lineB + "\n");
				}
			}
			else {
				full.append(lineA + "\n");
			}
		}
		
		
		if (dr.filtered && dr.pass) {
			full.append("*"+filterSubstring+"\n");
			diff.append("*"+filterSubstring+"\n");
		}
		dr.fullOutput = full.toString();
		dr.justDiff = diff.toString();
		return dr;
		
	}
	
	public static String formatJson(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper()
        		   .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                   .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
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

	public static void generateIndexerClientFile(String genRoot) throws IOException {
		// Generate the client files
		// Indexer
		String imports = Utils.readFile(genRoot+"indexerImports.txt");
		imports = "import com.algorand.algosdk.crypto.Address;\n" + imports;		
		String methods = Utils.readFile(genRoot+"indexerPaths.txt"); 
		
		StringBuffer sb = new StringBuffer();
		sb.append("package com.algorand.algosdk.v2.client.common;\n\n");
		sb.append(imports);
		sb.append("\n");
		sb.append("public class IndexerClient extends Client {\n" + 
				"\n" + 
				"	public IndexerClient(String host, int port, String token) {\n" + 
				"		super(host, port, token, \"X-Indexer-API-Token\");\n" + 
				"	}\n");
		sb.append(methods);
		sb.append("}\n");
		Utils.writeFile("src/main/java/com/algorand/algosdk/v2/client/common/IndexerClient.java", sb.toString());
	}
	
	public static void generateAlgodClientFile(String genRoot) throws IOException {
		// Algod
		String imports = Utils.readFile(genRoot+"algodV2Imports.txt");
		imports = "import com.algorand.algosdk.crypto.Address;\n" + imports;
		String methods = Utils.readFile(genRoot+"algodV2Paths.txt");  

		StringBuffer sb = new StringBuffer();
		sb.append("package com.algorand.algosdk.v2.client.common;\n\n");
		sb.append(imports);
		sb.append("\n");
		sb.append("public class AlgodClient extends Client {\n" + 
				"\n" + 
				"	public AlgodClient(String host, int port, String token) {\n" + 
				"		super(host, port, token, \"X-Algo-API-Token\");\n" + 
				"	}\n");
		sb.append(methods);
		sb.append("}\n");
		Utils.writeFile("src/main/java/com/algorand/algosdk/v2/client/common/AlgodClient.java", sb.toString());
	}
}
