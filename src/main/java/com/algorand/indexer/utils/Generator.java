package com.algorand.indexer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Generator {


	public static JsonNode getRoot(FileInputStream fileIs) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root;
		root = objectMapper.readTree(fileIs);
		return root;
	}
	
	static BufferedWriter getFileWriter(String className, String pkg) throws IOException {
		//System.out.println(System.getProperty("user.dir"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/main/java/com/algorand/indexer/" + pkg + "/" + className + ".java")));
		bw.append("package com.algorand.indexer." + pkg + ";\n\n");
		return bw;
	}
	
	static String getCamelCase(String name) {
		boolean capNext = false;
		char [] newName = new char[name.length()+1];
		int n = 0;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '-') {
				capNext = true;
				continue;
			}
			if (capNext) {
				newName[n++] = Character.toUpperCase(name.charAt(i));
				capNext = false;
			} else {
				newName[n++] = name.charAt(i);
			}
		}
		return new String(newName, 0, n);
	}
	
	static String getTypeNameFromRef(String ref) {
		StringTokenizer st = new StringTokenizer(ref, "/");
		String ans = "";
		while (st.hasMoreTokens()) {
			ans = st.nextToken();
		}
		return ans;
	}

	static String getType(JsonNode prop) {

		JsonNode typeNode = prop.findValue("type");
		String type = "";
		if (typeNode == null) {
			typeNode = prop.findValue("$ref");
			type = getTypeNameFromRef(typeNode.asText());
			return type;
		}
		// else, it is an array or a primitive/String type
		type = typeNode.asText();
		switch (type) {
		case "integer":
			return "long";
		case "string":
			return "String";
		case "array":
			JsonNode arrayTypeNode = prop.findValue("items");
			String typeName = getType(arrayTypeNode);
			return "List<" + typeName + ">";
		}
		return type;
	}

	static boolean writeProperties(StringBuffer buffer, JsonNode properties) {
		Iterator<Entry<String, JsonNode>> props = properties.fields();
		boolean importList = false;
		while (props.hasNext()) {
			Entry<String, JsonNode> prop = props.next();
			String jprop = prop.getKey();
			String javaName = getCamelCase(jprop);
			
			buffer.append("\n\t" + "@JsonProperty(\"" + jprop + "\")\n");
			buffer.append("\tpublic ");
			String type = getType(prop.getValue());
			if (type.contains("List<")) {
				importList = true;
			}
			buffer.append(type + " ");
			buffer.append(javaName + ";\n");
		}
		return importList;
	}
		
	public static void main(String [] args) throws JsonProcessingException, IOException {
		if (args.length != 2) {
			System.out.println("usage: java Generator specfile packageName");
			System.exit(0);
		}
		File f = new File(args[0]);
		FileInputStream fis = new FileInputStream(f);
		
		JsonNode root = getRoot(fis);
		JsonNode schemas = root.findPath("components").findPath("schemas");
		
		Iterator<Entry<String, JsonNode>> classes = schemas.fields();
		while (classes.hasNext()) {
			Entry<String, JsonNode> cls = classes.next();
			System.out.println(cls.getKey());
			BufferedWriter bw = getFileWriter(cls.getKey(), args[1]);
		
			StringBuffer imports = new StringBuffer();
			StringBuffer properties = new StringBuffer();
			boolean importList = false;
					
			importList = importList || writeProperties(properties, cls.getValue().findValue("properties"));
			
			if (importList) {
				imports.append("import java.util.List;\n\n");
			}
			imports.append("import com.fasterxml.jackson.annotation.JsonProperty;\n\n");
			
			bw.append(imports);
			bw.append("public class " + cls.getKey() + " {\n");
			bw.append(properties);
			bw.append("}\n");
			bw.close();
		}
	}
}
