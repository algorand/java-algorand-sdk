package com.algorand.sdkutils.generators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

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

	public static String readFile(File file) throws IOException  {
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
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
		file.getParentFile().mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.append(content);
		bw.close();
	}


	/**
	 * Generate the client which wraps up all the builders, accepts the host/port/token, etc.
     *
	 * clientName    - IndexerClient
	 * importLines   - The contents of xxxxxImports.txt
	 * paths         - The file at genRoot + xxxxxPaths.txt
	 * packageName   - "com.algorand.algosdk.v2.client.common"
	 * packagePath   - "src/main/java/com/algorand/algosdk/v2/client/common"
	 * tokenName     - "X-Indexer-API-Token"
	 * tokenOptional - Indicates that the token is optional and two constructors should be created.
     *
	 * @param clientName Name of the client class. i.e. IndexerClient
	 * @param importLines Lines to be added to the import section of the template. Generated from a previous step.
	 * @param paths The generated methods for accessing the paths. Generated from a previous step.
	 * @param packageName Name of the package containing the client.
	 * @param packagePath Path where the client will go.
	 * @param tokenName Name of the token used for this application. i.e. X-Algo-API-Token
	 * @param tokenOptional Whether or not a no-token version of the constructor should be created.
	 */
	public static void generateClientFile(String clientName, Collection<String> importLines, File paths, String packageName, String packagePath, String tokenName, Boolean tokenOptional) throws Exception {

		if (packagePath.endsWith("/")) {
		    throw new Exception("Path shouldn't have a trailing slash.");
		}

		String imports = StringUtils.join(importLines, "\n");

		String methods = Utils.readFile(paths);

		StringBuffer sb = new StringBuffer();
		sb.append("package " + packageName + ";\n\n");
		sb.append(imports);
		sb.append("\n\n");
		sb.append("public class " + clientName + " extends Client {\n\n");

		sb.append("	public " + clientName + "(String host, int port, String token) {\n" +
				"		super(host, port, token, \"" + tokenName + "\");\n" +
				"	}\n");

		if (tokenOptional) {
			sb.append("	public " + clientName + "(String host, int port) {\n" +
					"		super(host, port, \"\", \"" + tokenName + "\");\n" +
					"	}\n");
		}

		sb.append(methods);
		sb.append("}\n");
		Utils.writeFile(packagePath + "/" + clientName + ".java", sb.toString());
	}
}
