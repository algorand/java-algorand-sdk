package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class Generator {

	JsonNode root;

	static BufferedWriter getFileWriter(String className, String directory) throws IOException {
		//System.out.println(System.getProperty("user.dir"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory + "/" + className + ".java")));
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

	static String getType(JsonNode prop, boolean asObject) {

		if (prop.get("$ref") != null) {
			JsonNode typeNode = prop.get("$ref");
			String type = getTypeNameFromRef(typeNode.asText());
			return type;
		}

		JsonNode typeNode = prop.get("type") != null ? prop.get("type") : prop.get("schema").get("type");
		String type = typeNode.asText();
		switch (type) {
		case "integer":
			return asObject ? "Long" : "long";
		case "object":
		case "string":
			return "String";
		case "boolean":
			return asObject ? "Boolean" : "boolean";			
		case "array":
			JsonNode arrayTypeNode = prop.get("items");
			String typeName = getType(arrayTypeNode, asObject);
			return "List<" + typeName + ">";
		default:
			throw new RuntimeException("Unrecognized type: " + type);	
		}
	}

	static boolean needsClassImport(String type) {
		switch (type) {
		case "integer":
			return false;
		case "string":
			return false;
		default:	
			return true;
		}
	}

	static String formatComment(String comment, String tab) {
		StringBuffer sb = new StringBuffer();

		comment = comment.replace("\\[", "(");
		comment = comment.replace("\\]", ")");

		sb.append("\n"+tab+"/**");
		sb.append("\n"+tab+" * ");
		StringTokenizer st = new StringTokenizer(comment);
		int line = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (line + token.length() > 80) {
				line = 0;
				sb.append("\n"+tab+" * ");
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

	Iterator<Entry<String, JsonNode>> getSortedParameters(JsonNode properties) {
		TreeMap<String, JsonNode> tm = new TreeMap<String, JsonNode>();
		if (properties.isArray()) {
			ArrayNode jsonArrayNode = (ArrayNode) properties;
			for (int i = 0; i < jsonArrayNode.size(); i++) {
				JsonNode node = jsonArrayNode.get(i);
				JsonNode typeNode = null;
				if (node.get("$ref") != null) {
					typeNode = this.getFromRef(node.get("$ref").asText());
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
			String getterName = "get" + getCamelCase(jprop, true);
			String setterName = "set" + getCamelCase(jprop, true);
			String isSetName = javaName + "IsSet";
			String hasName = "has" + getCamelCase(jprop, true);
			String type = getType(prop.getValue(), false);
			String typeObj = getType(prop.getValue(), true);
			if (type.contains("List<")) {
				importList = true;
			}

			String desc = null;
			if (prop.getValue().get("description") != null) {
				desc = prop.getValue().get("description").asText();
				desc = formatComment(desc, "\t");
			}

			// private type
			if (desc != null) buffer.append(desc);
			buffer.append("\n\tprivate " + type + " " + javaName + ";\n");

			// private type is set boolean
			buffer.append("\tprivate boolean " + isSetName + ";\n");

			// public setter
			buffer.append("\t" + "@JsonProperty(\"" + jprop + "\")\n");
			buffer.append("\tpublic void " + setterName + "(" + type + " " + javaName + "){"
					+ "\n\t\tthis." + javaName + " = " + javaName + ";"
					+ "\n\t\t" + isSetName + " = true;\n"
					+ "\t}\n");

			// public getter
			buffer.append("\t" + "@JsonProperty(\"" + jprop + "\")\n");
			buffer.append("\tpublic " + typeObj + " " + getterName + "(){"
					+ "\n\t\treturn " + isSetName + " ? " + javaName + " : null;\n\t}");

			// public has
			String hasComment = "Check if has a value for " + javaName;
			hasComment = formatComment(hasComment, "\t");
			buffer.append(hasComment);
			buffer.append("\t@JsonIgnore");
			buffer.append("\n\tpublic boolean " + hasName + "(){\n\t\treturn " + isSetName + ";\n\t}\n");
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
						"			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);\n" + 
						"\n" + 
						"		} catch (JsonProcessingException e) {\n" + 
						"			throw new RuntimeException(e.getMessage());\n" + 
						"		}\n" + 
						"		return jsonStr;\n" + 
				"	}\n");
	}

	static void writeClass(String className, JsonNode propertiesNode, String desc, String directory, String pkg) throws IOException {
		System.out.println("Generating ... " + className);

		Iterator<Entry<String, JsonNode>> properties = getSortedProperties(propertiesNode);
		className = Generator.getCamelCase(className, true);
		BufferedWriter bw = getFileWriter(className, directory);
		bw.append("package " + pkg + ";\n\n");

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
		
		imports.append("import com.fasterxml.jackson.annotation.JsonIgnore;\n");
		imports.append("import com.fasterxml.jackson.annotation.JsonInclude.Include;\n");
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

	static String getQueryResponseMethod(String returnType, String getOrPost) {
		String ret = 
				"	public " + returnType + " lookup() {\n" + 
						"		String response;\n" + 
						"		try {\n" + 
						"			response = request(\"" + getOrPost + "\");\n" + 
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

	static boolean isRequired(JsonNode prop) {
		if (prop.get("required") != null) {
			return prop.get("required").asBoolean();
		}
		return false;
	}

	static boolean inPath(JsonNode prop) {
		if (prop.get("in") != null) {
			return prop.get("in").asText().compareTo("path") == 0;
		}
		return false;
	}

	static String processQueryParams(
			Iterator<Entry<String, JsonNode>> properties,
			String className, 			
			String path,
			String returnType,
			String getOrPost) {

		StringBuffer decls = new StringBuffer();
		StringBuffer bools = new StringBuffer();
		StringBuffer builders = new StringBuffer();
		StringBuffer requestMethod = new StringBuffer();

		// add the constructor
		builders.append("	public "+className+"(Client client) {\n" + 
				"		super(client);\n" + 
				"	}\n");
		requestMethod.append(Generator.getQueryResponseMethod(returnType, getOrPost));
		requestMethod.append("	public QueryData getRequestString() {\n" + 
				"		QueryData qd = new QueryData();\n");
		while (properties != null && properties.hasNext()) {
			Entry<String, JsonNode> prop = properties.next();
			String propName = Generator.getCamelCase(prop.getKey(), false);
			String setterName = Generator.getCamelCase(prop.getKey(), true);
			setterName = "set" + setterName;
			String propType = getType(prop.getValue(), false);

			decls.append("\tprivate " + propType + " " + propName + ";\n");
			bools.append("\tprivate boolean " + propName + "IsSet;\n");
			
			if (prop.getValue().get("description") != null) {
				String desc = prop.getValue().get("description").asText();
				desc = formatComment(desc, "\t");
				builders.append(desc + "\n");
			}
			builders.append("\tpublic " + className + " " + setterName + "(" + propType + " " + propName + ") {\n");
			builders.append("\t\tthis." + propName + " = " + propName + ";\n");
			builders.append("\t\tthis." + propName + "IsSet = true;\n");
			builders.append("\t\treturn this;\n");
			builders.append("\t}\n");

			if (inPath(prop.getValue())) {
				if (isRequired(prop.getValue())) {
					requestMethod.append("		if  (!this."+propName+"IsSet) {\n" + 
							"			throw new RuntimeException(\"" +
							propName+" is not set, and it is a required parameter.\");\n" + 
							"		}\n");
				}
				continue;
			}
			requestMethod.append("		if (this."+propName+"IsSet) {\n" + 
					"			qd.addQuery(\""+propName+"\", String.valueOf("+propName+"));\n" +
					"		}\n");
			if (isRequired(prop.getValue())) {
				requestMethod.append("		else {\n" + 
						"			throw new RuntimeException(\"" +
						propName+" is not set, and it is a required parameter.\");\n" + 
						"		}\n");
			}

		}

		ArrayList<String> al = getPathInserts(path);
		for (String str : al) {
			requestMethod.append(
					"		qd.addPathSegment(String.valueOf(" + str + "));\n");
		}


		requestMethod.append("\n" + 
				"		return qd;\n" + 
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

	JsonNode getFromRef(String ref) {
		StringTokenizer st = new StringTokenizer(ref, "/");
		JsonNode ans = root;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.charAt(0) == '#') {
				continue;
			}
			ans = ans.get(token);
		}
		return ans;
	}

	void writeQueryClass(
			JsonNode spec,
			String path,
			String directory,
			String pkg,
			String modelPkg) throws IOException { 

		String getOrPost;
		if (spec.get("post") != null) {
			spec = spec.get("post");
			getOrPost = "post";
		} else {
			spec = spec.get("get");
			getOrPost = "get";
		}

		String className = spec.get("operationId").asText();
		className = Generator.getCamelCase(className, true);
		JsonNode paramNode = spec.get("parameters");
		String returnType = "String";
		if (spec.get("responses").get("200").get("$ref") != null) { 
			returnType = spec.get("responses").get("200").get("$ref").asText();
			JsonNode returnTypeNode = this.getFromRef(returnType);
			if (returnTypeNode.get("schema").get("$ref") != null) {
				returnType = Generator.getTypeNameFromRef(returnTypeNode.get("schema").get("$ref").asText());
			} else if (returnTypeNode.get("schema").get("properties").size() == 1) {
				returnType = returnTypeNode.get("schema").get("properties").findValue("type").asText();
				returnType = Generator.getCamelCase(returnType, true);
			} else {
				returnType = Generator.getTypeNameFromRef(returnType);
				returnType = Generator.getCamelCase(returnType, true);
			}
		}
		String desc = spec.get("description") != null ? 
				spec.get("description").asText() : "";
				desc = desc + "\n" + path;
				System.out.println("Generating ... " + className);
				Iterator<Entry<String, JsonNode>> properties = null;
				if ( paramNode != null) {
					properties = getSortedParameters(paramNode);
				}

				BufferedWriter bw = getFileWriter(className, directory);
				bw.append("package " + pkg + ";\n\n");

				String imports = 
						"import java.io.IOException;\n\n" + 
								"import com.algorand.algosdk.v2.client.connect.Client;\n" + 
								"import com.algorand.algosdk.v2.client.connect.Query;\n" + 
								"import com.algorand.algosdk.v2.client.connect.QueryData;\n" + 
								"import com.fasterxml.jackson.databind.ObjectMapper;\n";
				if (needsClassImport(returnType.toLowerCase())) {
					imports = imports + "import " + modelPkg + "." + returnType + ";\n";
				}

				bw.append(imports);
				bw.append("\n");
				bw.append(Generator.formatComment(desc, ""));
				bw.append("\npublic class " + className + " extends Query {\n");
				bw.append(
						processQueryParams(
								properties, 
								className, 
								path,
								returnType, 
								getOrPost));
				bw.append("\n}");
				bw.close();
	}

	public static void generateAlgodIndexerObjects (JsonNode root, String rootPath, String pkg) throws IOException {

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
					writeClass(cls.getKey(), cls.getValue().get("properties"), desc, rootPath, pkg);
				}
	}

	public static void generateReturnTypes (JsonNode root, String rootPath, String pkg) throws IOException {
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

							if (rSchema.get("$ref") != null ) {
								// It refers to a defined class
								continue;
							}
							if (rSchema.get("properties").size() == 1) {
								continue;
							}
							writeClass(rtype.getKey(), rSchema.get("properties"), null, rootPath, pkg);
				}
	}

	public void generateIndexerMethods(String rootPath, String pkg, String modelPkg) throws IOException {
		JsonNode paths = this.root.get("paths");
		Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
		while (pathIter.hasNext()) {
			Entry<String, JsonNode> path = pathIter.next();
			writeQueryClass(path.getValue(), path.getKey(), rootPath, pkg, modelPkg);
		}		
	}

	public Generator (JsonNode root) {
		this.root = root;
	}
}
