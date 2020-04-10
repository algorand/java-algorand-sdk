package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

public class QueryMapperGenerator extends Generator {

	public QueryMapperGenerator(JsonNode root) {
		super(root);
	}

	public void writeQueryMapper(String sdkutilsPath) throws IOException {
		BufferedWriter bw = getFileWriter("QueryMapper", sdkutilsPath);
		bw.append("package com.algorand.sdkutils.generated;\n" + 
				"\n" + 
				"import com.algorand.algosdk.v2.client.indexer.*;\n" + 
				"import com.algorand.algosdk.v2.client.connect.Client;\n" + 
				"import com.algorand.algosdk.v2.client.connect.Query;\n\n"
				+ "public class QueryMapper {\n" + 
				"\n");
		
		StringBuffer getClass = new StringBuffer();
		StringBuffer setValue = new StringBuffer();
		StringBuffer lookUp = new StringBuffer();
		
		getClass.append("	public static Query getClass(String name, Client client) {\n" + 
				"		switch (name) {\n");
		setValue.append("	public static void setValue(Query q, String className, String property, String value) {\n" + 
				"		switch (className) {\n");
		lookUp.append("	public static String lookup(Query q, String className) {\n" + 
				"		switch (className) {\n");
		
		JsonNode paths = this.root.get("paths");
		Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
		while (pathIter.hasNext()) {
			Entry<String, JsonNode> path = pathIter.next();
			String className = path.getValue().findValue("operationId").asText();
			String javaClassName = Generator.getCamelCase(className, true);
			
			// getClass
			getClass.append("		case \""+className+"\":\n" + 
					"			return new "+javaClassName+"(client);\n");
			
			//setValue
			setValue.append("		case \""+className+"\":\n" + 
					"			switch (property) {\n");
			
			//lookUp
			lookUp.append("		case \""+className+"\":\n" + 
					"			return (("+javaClassName+")q).lookup().toString();\n");
			
			JsonNode paramNode = path.getValue().findValue("parameters");
			Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode);
			while (properties.hasNext()) {
				Entry<String, JsonNode> parameter = properties.next();
				String javaSetParamName = Generator.getCamelCase(parameter.getKey(), true);
				String typeName = parameter.getValue().get("type").asText();
				setValue.append("			case \""+parameter.getKey()+"\":\n" + 
						"				(("+javaClassName+")q).set"+javaSetParamName+"(");
				switch (typeName) {
				case "integer":
					setValue.append("Long.valueOf(value));\n");
					break;
				case "string":
					setValue.append("value);\n");
					break;
				case "boolean":
					setValue.append("Boolean.valueOf(value));\n");
					break;
				}
				setValue.append("				break;\n");
			}
			setValue.append("			}\n			break;\n");
		}
		
		getClass.append("		}\n" + 
				"		return null;\n" + 
				"	}\n\n");
		setValue.append("\n" + 
				"		}\n" + 
				"	}\n\n");
		lookUp.append("		}\n" + 
				"		return null;\n" + 
				"	}\n");
		
		bw.append(getClass);
		bw.append(setValue);
		bw.append(lookUp);
		bw.append("}");
		bw.close();
	}
}
