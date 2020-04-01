package com.algorand.indexer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

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

		JsonNode typeNode = prop.get("type");
		String type = "";
		if (typeNode == null) {
			typeNode = prop.get("$ref");
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
			JsonNode arrayTypeNode = prop.get("items");
			String typeName = getType(arrayTypeNode);
			return "List<" + typeName + ">";
		}
		return type;
	}

	static String formatComment(String comment, String tab) {
		StringBuffer sb = new StringBuffer();
		
		comment = comment.replace("\\[", "(");
		comment = comment.replace("\\]", ")");
		
		sb.append("\n"+tab+"/*\n"+tab+"\t");
		StringTokenizer st = new StringTokenizer(comment);
		int line = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (line + token.length() > 80) {
				line = 0;
				sb.append("\n\t"+tab);
			} 
			sb.append(token + " ");
			line += token.length() + 1;
		}
		sb.append("\n"+tab+" */");
		return sb.toString();
	}
	
	static Iterator<Entry<String, JsonNode>> getSortedProperties(JsonNode properties) {
		Iterator<Entry<String, JsonNode>> props = properties.fields();
		TreeMap<String, JsonNode> propMap = new TreeMap<String, JsonNode>();
		while (props.hasNext()) {
			Entry<String, JsonNode> e = props.next();
			propMap.put(e.getKey(), e.getValue());
		}
		Iterator<Entry<String, JsonNode>>sortedProps = propMap.entrySet().iterator();
		return sortedProps;
	}
	
	static boolean writeProperties(StringBuffer buffer, Iterator<Entry<String, JsonNode>> properties) {
		boolean importList = false;
		while (properties.hasNext()) {
			Entry<String, JsonNode> prop = properties.next();
			String jprop = prop.getKey();
			String javaName = getCamelCase(jprop);
			String desc;
			if (prop.getValue().get("description") != null) {
				desc = prop.getValue().get("description").asText();
				desc = formatComment(desc, "\t");
				buffer.append(desc);
			}
			
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
		
	static void writeCompareMethod(String className, StringBuffer buffer, Iterator<Entry<String, JsonNode>> properties) {
		buffer.append("	@Override\n" + 
				"	public boolean equals(Object o) {\n" + 
				"\n" + 
				"		if (this == o) return true;\n" + 
				"		if (o == null) return false;\n");
		buffer.append("\n"); 
		buffer.append("		" + className + " other = (" + className + ") o;\n");
		while (properties.hasNext()) {
			Entry<String, JsonNode> prop = properties.next();
			String jprop = prop.getKey();
			String javaName = getCamelCase(jprop);
			buffer.append("		if (!Objects.deepEquals(this." + javaName + ", other." + javaName + ")) return false;\n");
		}
		buffer.append("\n		return true;\n	}\n");
	}
	
	static void writeToStringMethod(StringBuffer buffer) {
		buffer.append(
				"	@Override\n" + 
				"	public String toString() {\n" + 
				"		ObjectMapper om = new ObjectMapper(); \n" + 
				"		String jsonStr;\n" + 
				"		try {\n" + 
				"			jsonStr = om.writeValueAsString(this);\n" + 
				"		} catch (JsonProcessingException e) {\n" + 
				"			throw new RuntimeException(e.getMessage());\n" + 
				"		}\n" + 
				"		return jsonStr;\n" + 
				"	}\n");
	}
	
	static void writeClass(String className, JsonNode propertiesNode, String desc, String directory) throws IOException {
		System.out.println("Generating ... " + className);

		Iterator<Entry<String, JsonNode>> properties = getSortedProperties(propertiesNode);
		BufferedWriter bw = getFileWriter(className, directory);
	
		StringBuffer imports = new StringBuffer();
		StringBuffer body = new StringBuffer();
		boolean importList = false;
				
		importList = importList || writeProperties(body, properties);
		body.append("\n");
		properties = getSortedProperties(propertiesNode);
		writeCompareMethod(className, body, properties);
		body.append("\n");		
		writeToStringMethod(body);
		
		if (importList) {
			imports.append("import java.util.List;\n");
		}
		imports.append("import java.util.Objects;\n"); // used by Objects.deepEquals
		imports.append("\n");
		imports.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
		imports.append("import com.fasterxml.jackson.core.JsonProcessingException;\n"); // used by toString
		imports.append("import com.fasterxml.jackson.databind.ObjectMapper;\n"); // used by toString
		imports.append("\n");		
		
		bw.append(imports);
		if (desc != null) {
			bw.append(desc);
			bw.append("\n");
		}
		bw.append("public class " + className + " {\n");
		bw.append(body);
		bw.append("}\n");
		bw.close();
	}
	
	public static void main(String [] args) throws JsonProcessingException, IOException {
		if (args.length != 2) {
			System.out.println("usage: java Generator specfile packageName");
			System.exit(0);
		}
		File f = new File(args[0]);
		FileInputStream fis = new FileInputStream(f);
		
		JsonNode root = getRoot(fis);
		
		// Generate classes for the schemas
		JsonNode schemas = root.get("components").get("schemas");
		Iterator<Entry<String, JsonNode>> classes = schemas.fields();
		while (classes.hasNext()) {
			Entry<String, JsonNode> cls = classes.next();
			String desc = null;
			if (cls.getValue().get("description") != null) {
					desc = cls.getValue().get("description").asText();
					desc = formatComment(desc, "");
			}
			writeClass(cls.getKey(), cls.getValue().get("properties"), desc, args[1]);
		}
		
		// Generate classes for the return types which have more than one return type
		JsonNode returns = root.get("components").get("responses");
		Iterator<Entry<String, JsonNode>> returnTypes = returns.fields();
		while (returnTypes.hasNext()) {
			Entry<String, JsonNode> rtype = returnTypes.next();
			System.out.println("looking at: " + rtype.getKey());
			JsonNode rSchema = rtype.getValue().get("content").get("application/json").get("schema");
			if (rSchema == null 
					|| rSchema.get("properties") == null 
					|| rSchema.get("properties").size() == 1) {
				continue;
			}
			writeClass(rtype.getKey(), rSchema.get("properties"), null, args[1]);
		}
	}
}
