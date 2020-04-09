package com.algorand.sdkutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.HttpUrl;

public class TestGen extends Generator {

	static String callExternalCurl(String request) {
		StringBuffer bw = new StringBuffer();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("curl", "-s", request);
        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                bw.append(line);
            }

            process.waitFor();
            return bw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	void writeQueryMapper(String sdkutilsPath) throws IOException {
		BufferedWriter bw = getFileWriter("QueryMapper", sdkutilsPath);
		bw.append("package com.algorand.sdkutils;\n" + 
				"\n" + 
				"import com.algorand.algosdk.v2.client.indexer.*;\n" + 
				"import com.algorand.algosdk.v2.client.connect.Client;\n" + 
				"import com.algorand.algosdk.v2.client.connect.Query;\n\n"
				+ "public class QueryMapper {\n" + 
				"\n");
		
		StringBuffer getClass = new StringBuffer();
		StringBuffer setValue = new StringBuffer();
		StringBuffer lookUp = new StringBuffer();
		
		getClass.append("	static Query getClass(String name, Client client) {\n" + 
				"		switch (name) {\n");
		setValue.append("	static void setValue(Query q, String className, String property, String value) {\n" + 
				"		switch (className) {\n");
		lookUp.append("	static String lookup(Query q, String className) {\n" + 
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
				String javaParamName = Generator.getCamelCase(parameter.getKey(), false);
				String javaSetParamName = Generator.getCamelCase(parameter.getKey(), true);
				String typeName = parameter.getValue().get("type").asText();
				setValue.append("			case \""+javaParamName+"\":\n" + 
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
				setValue.append("			break;\n" + 
						"");
			}
			setValue.append("			}\n");
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

	void writeTemplate(BufferedWriter bw) throws IOException {
		writeQueryMapper("/Users/shantkarakashian/go/src/github.com/algorand/java-algorand-sdk/src/main/java/com/algorand/sdkutils/");
		JsonNode paths = this.root.get("paths");
		Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
		while (pathIter.hasNext()) {
			Entry<String, JsonNode> path = pathIter.next();
			String pathString = path.getKey();
			JsonNode paramNode = path.getValue().findValue("parameters");
			Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode);
			bw.append(pathString + ": ");
			while (properties.hasNext()) {
				Entry<String, JsonNode> parameter = properties.next();
				bw.append(parameter.getKey());
				bw.append("(" + parameter.getValue().get("type").asText() + ")");
				if (isRequired(parameter.getValue())) {
					bw.append("[R]");
				}
				bw.append(", ");
			}
			bw.append("\n");
		}		
	}

	static String getPathFromLine(String line) {
		return line.substring(0, line.indexOf(":"));
	}

	static void testSamples(TestGen tg, BufferedReader br, Client client) throws IOException {
		JsonNode paths = tg.root.get("paths");
		JsonNode pathNode = null;
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			if (line.charAt(0) == '/') {
				String pathString = getPathFromLine(line);
				pathNode = paths.get(pathString);
				System.out.println(pathNode.toString());
			} else {
				// this is a test sample
				
				// sample source setup
				JsonNode paramNode = pathNode.findValue("parameters");
				Iterator<Entry<String, JsonNode>> properties = tg.getSortedParameters(paramNode);
				StringTokenizer st = new StringTokenizer(line, ",");
				
				// SDK query setup
				String methodName = pathNode.findValue("operationId").asText();

				Query query = QueryMapper.getClass(methodName, client);
				
				while (properties.hasNext()) {
					// sample source setup
					String value = "";
					if (st.hasMoreTokens()) {
						value = st.nextToken();
						value = value.trim();
					}
					Entry<String, JsonNode> parameter = properties.next();					
					if (value.isEmpty()) {
						continue;
					}
					
					// SDK query setup
					QueryMapper.setValue(query, methodName, parameter.getKey(), value);
				}
				
				// Call the SDK
				String sdkResponse = QueryMapper.lookup(query, methodName);
				
				// Call the node directly using curl
				QueryData qd = query.getRequestString();
				HttpUrl httpUrl = Client.getHttpUrl(qd, client.getPort(), client.getHost());

				//callExternalCurl
				String curlResponse = callExternalCurl(httpUrl.toString());
				
				System.out.println(httpUrl.toString()+"\n");
				System.out.println("SDK:\n" + sdkResponse + "\nCurl:\n" + curlResponse);
				if (curlResponse.compareTo(sdkResponse) != 0) {
					throw new RuntimeException("wrong result!");
				}
			}
		}
	}
	TestGen (JsonNode root) {
		super();
		this.root = root;
	}

	public static void main (String args[]) throws JsonProcessingException, IOException {
		if (args.length != 3) {
			System.out.println("usage: java TestGen specfile (template|test) (templateFile|testFile)");
			System.exit(0);
		}
		File f = new File(args[0]);
		FileInputStream fis = new FileInputStream(f);

		JsonNode root = getRoot(fis);	
		TestGen tg = new TestGen(root);

		if (args[1].compareTo("template") == 0) {
			File outFile = new File(args[2]);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			tg.writeTemplate(bw);
			bw.close();
			System.out.println("File written: " + args[2]);
		} else if (args[1].compareTo("test") == 0) {
			int port = 8980;
			String host = "localhost";
			Client client = new Client(host, port);
			
			File inFile = new File(args[2]);
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			testSamples(tg, br, client);
			br.close();
			System.out.println("File tested: " + args[2]);

		} else {
			System.err.println("Wrong argument: " + args[1]);
			return;
		}

	}

}
