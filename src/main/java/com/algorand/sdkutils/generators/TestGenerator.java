package com.algorand.sdkutils.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.algorand.sdkutils.generated.QueryMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.HttpUrl;

public class TestGenerator extends Generator {
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
	

	static String getPathFromLine(String line) {
		return line.substring(0, line.indexOf(":"));
	}

	protected static void testSamples(TestGenerator tg, BufferedReader br, Client client) throws IOException {
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
	public TestGenerator (JsonNode root) {
		super(root);
	}
}
