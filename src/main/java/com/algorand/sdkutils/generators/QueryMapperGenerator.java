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
				"import com.algorand.algosdk.v2.client.common.Client;\n" + 
				"import com.algorand.algosdk.v2.client.common.Query;\n\n"
				+ "public class QueryMapper {\n" + 
				"\n");
		
		StringBuffer getClass = new StringBuffer();
		StringBuffer setValue = new StringBuffer();
		StringBuffer lookUp = new StringBuffer();
		
		getClass.append("	public static Query getClass(String name, Client client, String args[]) {\n" + 
				"		switch (name) {\n");
		setValue.append("	public static void setValue(Query q, String className, String property, String value) {\n" + 
				"		switch (className) {\n");
		lookUp.append("	public static String lookup(Query q, String className) throws Exception {\n" + 
				"		switch (className) {\n");
		
		JsonNode paths = this.root.get("paths");
		Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
		while (pathIter.hasNext()) {
			Entry<String, JsonNode> path = pathIter.next();
			String className = path.getValue().findValue("operationId").asText();
			String javaClassName = Generator.getCamelCase(className, true);
			String methodName = Generator.getCamelCase(className, false);
			
			// getClass
			getClass.append("		case \""+className+"\":\n" + 
					"			return client."+methodName+"(");
			
			//setValue
			setValue.append("		case \""+className+"\":\n" + 
					"			switch (property) {\n");
			
			//lookUp
			lookUp.append("		case \""+className+"\":\n" + 
					"			return (("+javaClassName+")q).execute().body().toString();\n");
			
			JsonNode paramNode = path.getValue().findValue("parameters");
			Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode);
			
			// The parameters in the path are directly passed to the constructor.
			// The method with have in order arguments each assigned to the parameter in order. 
			int argCounter = 0;
			
			while (properties.hasNext()) {
				Entry<String, JsonNode> parameter = properties.next();
				String javaSetParamName = Generator.getCamelCase(parameter.getKey(), false);
				String typeName = parameter.getValue().get("type").asText();
				
				if (inPath(parameter.getValue())) {
					if (argCounter > 0) {
						getClass.append(", ");
					}
					switch (typeName) {
					case "integer":
						getClass.append("Long.valueOf("+"args[" + argCounter + "])");
						break;
					case "string":
						getClass.append("args[" + argCounter + "]");
						break;
					case "boolean":
						getClass.append("Boolean.valueOf("+"args[" + argCounter + "])");
						break;
						default:
							throw new RuntimeException("Unknow type: " + typeName);
					}

					argCounter++;
					continue;
				}
				
				setValue.append("			case \""+parameter.getKey()+"\":\n" + 
						"				(("+javaClassName+")q)."+javaSetParamName+"(");
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
			getClass.append(");\n");			
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
