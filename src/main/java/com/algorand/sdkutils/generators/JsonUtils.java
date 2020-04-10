package com.algorand.sdkutils.generators;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	
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

}
