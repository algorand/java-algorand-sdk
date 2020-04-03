package com.algorand.indexer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


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
		return bw;
	}
	
	static String getCamelCase(String name, boolean firstCap) {
		boolean capNext = firstCap;
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

	static Iterator<Entry<String, JsonNode>> getSortedParameters(JsonNode properties, JsonNode parameterDefs ) {
		TreeMap<String, JsonNode> tm = new TreeMap<String, JsonNode>();
		if (properties.isArray()) {
			ArrayNode jsonArrayNode = (ArrayNode) properties;
			for (int i = 0; i < jsonArrayNode.size(); i++) {
				JsonNode node = jsonArrayNode.get(i);
				JsonNode typeNode = null;
				if (node.get("$ref") != null) {
					String typeName = Generator.getTypeNameFromRef(node.get("$ref").asText());
					typeNode = parameterDefs.get(typeName);
				} else {
					typeNode = node;
				}
				tm.put(typeNode.get("name").asText(), typeNode);
			}
			Iterator<Entry<String, JsonNode>>sortedParams = tm.entrySet().iterator();
			return sortedParams;
		}
		return null;
	}
	
	static boolean writeProperties(StringBuffer buffer, Iterator<Entry<String, JsonNode>> properties) {
		boolean importList = false;
		while (properties.hasNext()) {
			Entry<String, JsonNode> prop = properties.next();
			String jprop = prop.getKey();
			String javaName = getCamelCase(jprop, false);
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
			String javaName = getCamelCase(jprop, false);
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
		className = Generator.getCamelCase(className, true);
		BufferedWriter bw = getFileWriter(className, directory);
		bw.append("package com.algorand.indexer." + directory + ";\n\n");

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
	
	static ArrayList<String> getPathInserts(String path) {
		ArrayList<String> nPath = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(path, "/");
		while (st.hasMoreTokens()) {
			String elt = st.nextToken();
			if (elt.charAt(0) == '{') {
				String jName = Generator.getCamelCase(elt.substring(1, elt.length()-1), false);
				nPath.add(jName);
			} else {
				nPath.add("\"" + elt + "\"");
			}
		}
		return nPath;
	}
	
	static String getQueryResponseMethod(String returnType) {
		String ret = 
				"	public " + returnType + " lookup() {\n" + 
				"		String response;\n" + 
				"		try {\n" + 
				"			response = request();\n" + 
				"		} catch (Exception e) {\n" + 
				"			// TODO Auto-generated catch block\n" + 
				"			e.printStackTrace();\n" + 
				"			return null;\n" + 
				"		}\n" + 
				"		ObjectMapper mapper = new ObjectMapper();\n" + 
				"		" + returnType + " resp;\n" + 
				"		try {\n" + 
				"			resp = mapper.readValue(response, " + returnType + ".class);\n" + 
				"		} catch (IOException e) {\n" + 
				"			// TODO Auto-generated catch block\n" + 
				"			e.printStackTrace();\n" + 
				"			return null;\n" + 
				"		}\n" + 
				"		return resp;\n" + 
				"	}\n" + 
				"";
		return ret;
	}
	
	static String processQueryParams(
			Iterator<Entry<String, JsonNode>> properties,
			JsonNode parameterDefs, 
			String className, 			
			String path,
			String returnType) {
		
		StringBuffer decls = new StringBuffer();
		StringBuffer bools = new StringBuffer();
		StringBuffer builders = new StringBuffer();
		StringBuffer requestMethod = new StringBuffer();
		
		// add the constructor
		builders.append("	public "+className+"(Client client) {\n" + 
				"		super(client);\n" + 
				"	}\n");
		requestMethod.append(Generator.getQueryResponseMethod(returnType));
		requestMethod.append("	protected String getRequestString() {\n" + 
				"		StringBuffer sb = new StringBuffer();\n");
		
		ArrayList<String> al = getPathInserts(path);
		for (String str : al) {
			requestMethod.append(
					"		sb.append(\"/\");\n" +
					"		sb.append(" + str + ");\n");

		}
		requestMethod.append(
				"		sb.append(\"?\");\n\n");
		requestMethod.append(
				"		boolean added = false;\n\n");
		
		while (properties.hasNext()) {
			Entry<String, JsonNode> prop = properties.next();
			String propName = Generator.getCamelCase(prop.getKey(), false);
			String setterName = Generator.getCamelCase(prop.getKey(), true);
			setterName = "set" + setterName;
			String propType = getType(prop.getValue());

			decls.append("\tprivate " + propType + " " + propName + ";\n");
			bools.append("\tprivate boolean " + propName + "IsSet;\n");
			builders.append("\tpublic " + className + " " + setterName + "(" + propType + " " + propName + ") {\n");
			builders.append("\t\tthis." + propName + " = " + propName + ";\n");
			builders.append("\t\tthis." + propName + "IsSet = true;\n");
			builders.append("\t\treturn this;\n");
			builders.append("\t}\n");
			
			requestMethod.append(
					"		if (this."+propName+"IsSet) {\n" + 
					"			if (added) {\n" + 
					"				sb.append(\"&\");\n" + 
					"			}\n" + 
					"			sb.append(\""+propName+"=\");\n" + 
					"			sb.append("+propName+");\n" +
					"			added = true;\n" + 
					"		}\n");
		}
		
		requestMethod.append("\n" + 
				"		return sb.toString();\n" + 
				"	}");
		
		StringBuffer ans = new StringBuffer();
		ans.append(decls);
		ans.append("\n");
		ans.append(bools);
		ans.append("\n");
		ans.append(builders);
		ans.append("\n");		
		ans.append(requestMethod);
		return ans.toString();
	}

	static void writeQueryClass(
			JsonNode spec,
			JsonNode parameterDefs,
			String path,
			String directory,
			String subDir) throws IOException { 

		String className = spec.get("get").get("operationId").asText();
		className = Generator.getCamelCase(className, true);
		JsonNode paramNode = spec.get("get").get("parameters");
		String returnType = spec.get("get").get("responses").get("200").get("$ref").asText();
		returnType = Generator.getTypeNameFromRef(returnType);
		returnType = Generator.getCamelCase(returnType, true);
		String desc = spec.get("get").get("description").asText();
		
		System.out.println("Generating ... " + className);
		Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode, parameterDefs);
		
		BufferedWriter bw = getFileWriter(className, directory+"/"+subDir);
		bw.append("package com.algorand.indexer." + directory + "." + subDir + ";\n\n");

		String imports = 
				"import java.io.IOException;\n\n" + 
				"import com.algorand.indexer.client.Client;\n" + 
				"import com.algorand.indexer.client.Query;\n" + 
				"import com.fasterxml.jackson.databind.ObjectMapper;\n" +
				"import com.algorand.indexer." + directory + "." + returnType + ";\n";
		
		bw.append(imports);
		bw.append("\n");
		bw.append(Generator.formatComment(desc, ""));
		bw.append("\npublic class " + className + " extends Query {\n");
		bw.append(
				processQueryParams(
						properties, 
						parameterDefs, 
						className, 
						path,
						returnType));
		bw.append("\n}");
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
		JsonNode schemas = root.get("components") != null ? 
				root.get("components").get("schemas") : 
					root.get("definitions");
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
		JsonNode returns = root.get("components") != null ? 
				root.get("components").get("responses") : 
					root.get("responses");
		Iterator<Entry<String, JsonNode>> returnTypes = returns.fields();
		while (returnTypes.hasNext()) {
			Entry<String, JsonNode> rtype = returnTypes.next();
			System.out.println("looking at: " + rtype.getKey());
			JsonNode rSchema = rtype.getValue().get("content") != null ? 
					rtype.getValue().get("content").get("application/json").get("schema") : 
						rtype.getValue().get("schema"); 
						
			if (rSchema == null 
					|| rSchema.get("properties") == null 
					|| rSchema.get("properties").size() == 1) {
				continue;
			}
			writeClass(rtype.getKey(), rSchema.get("properties"), null, args[1]);
		}
		
		
		// Generate the query classes
		JsonNode paths = root.get("paths");
		JsonNode parameters = root.get("parameters");		
		Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
		while (pathIter.hasNext()) {
			Entry<String, JsonNode> path = pathIter.next();
			writeQueryClass(path.getValue(), parameters, path.getKey(), args[1], "lookup");
		}
	}
}
