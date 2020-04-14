package com.algorand.sdkutils.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
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

	public static boolean testSamples(TestGenerator tg, BufferedReader br, Client client, boolean verbose) throws IOException {
		JsonNode paths = tg.root.get("paths");
		JsonNode pathNode = null;
		boolean failed = false;
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			if (line.trim().replace(",", "").isEmpty()) {
				continue;
			}
			if (line.charAt(0) == '/') {
				String pathString = getPathFromLine(line);
				pathNode = paths.get(pathString);
			} else {
				// this is a test sample
				
				// sample source setup
				JsonNode paramNode = pathNode.findValue("parameters");
				Iterator<Entry<String, JsonNode>> properties = tg.getSortedParameters(paramNode);
				String[] columns = line.split(",");
				int colIdx = 0;
				// SDK query setup
				String methodName = pathNode.findValue("operationId").asText();

				Query query = QueryMapper.getClass(methodName, client);
				
				while (properties.hasNext()) {
					// sample source setup
					String value = "";
					if (colIdx < columns.length) {
						value = columns[colIdx];
						value = value.trim();
					}
					Entry<String, JsonNode> parameter = properties.next();		
					colIdx++;
					if (value.isEmpty()) {
						continue;
					}
					// SDK query setup
					QueryMapper.setValue(query, methodName, parameter.getKey(), value);
				}
				
				// Call the node directly using curl
				QueryData qd = query.getRequestString();
				HttpUrl httpUrl = Client.getHttpUrl(qd, client.getPort(), client.getHost());
				System.out.println("\n" + httpUrl.toString());
				
				//callExternalCurl
				String curlResponse = callExternalCurl(httpUrl.toString());
				
				// Call the SDK
				String sdkResponse = QueryMapper.lookup(query, methodName);
				String filter = "round\"";
				String diff = Utils.showDifferentces(curlResponse, sdkResponse, "curl", "sdk", verbose, filter);

				if (!verbose) {
					if (!diff.isEmpty()) {
						System.out.print(diff);
						if (diff.contentEquals("*" + filter + "\n")) {
							System.out.println("*PASS!");
						} else {
							System.out.println("FAIL!");
							failed = true;
						}
					} else {
						System.out.println("PASS!");
					}
				} else {
					System.out.print(diff);
				}
			}
		}
		return !failed;
	}

	public TestGenerator (JsonNode root) {
		super(root);
	}
}
