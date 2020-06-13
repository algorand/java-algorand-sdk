package com.algorand.sdkutils.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.sdkutils.generated.QueryMapper;
 */
import com.fasterxml.jackson.databind.JsonNode;

public class TestGenerator extends Generator {
    static String callExternalCurl(String request) {
        StringBuffer bw = new StringBuffer();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("curl", 
                "-H", "X-Algo-API-Token: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 
                "-s", request);
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
        return line.substring(0, line.indexOf(","));
    }

    /*
	public static boolean testSamples(TestGenerator tg, BufferedReader br, Client client, boolean verbose) throws Exception {
		// create the directory golden if it does not exist
		File goldenDir = new File("golden");
		if (!goldenDir.exists()) {
			goldenDir.mkdir();
		}

		JsonNode paths = tg.root.get("paths");
		JsonNode pathNode = null;
		boolean failed = false;
		String operationId = "";
		int caseCounter = 0;
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
				System.out.println("Test cases in:\t" + pathString);
				pathNode = paths.get(pathString);
				operationId = pathNode.findValue("operationId").asText();
				caseCounter = 0;
			} else {
				// this is a test sample

				String[] columns = line.split(",");
				JsonNode paramNode = pathNode.findValue("parameters");

				// Get the constructor params
				ArrayList<String> al = new ArrayList<String>();
				{
					int cidx = 1;

					Iterator<Entry<String, JsonNode>> params = tg.getSortedParameters(paramNode);
					while (params.hasNext()) {
						JsonNode param = params.next().getValue();
						if (inPath(param)) {
							al.add(columns[cidx].trim());
						}
						cidx++;
					}
				}
				String args[] = al.toArray(new String[al.size()]);

				// sample source setup
				Iterator<Entry<String, JsonNode>> properties = tg.getSortedParameters(paramNode);
				int colIdx = 1;
				// SDK query setup
				String methodName = pathNode.findValue("operationId").asText();

				Query query = null;
				if (client.getClass() == AlgodClient.class) {
					query = QueryMapper.getClass(methodName, (AlgodClient)client, args);
				} else {
					query = QueryMapper.getClass(methodName, (IndexerClient)client, args);
				}


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
					if (inPath(parameter.getValue())) {
						continue;
					}
					QueryMapper.setValue(query, methodName, parameter.getKey(), value);
				}

				// Call the SDK
				String sdkResponse = null;
				try {
					sdkResponse = QueryMapper.lookup(query, methodName);
				} catch (Exception e) {
					System.err.println(line);
					try {
						System.err.println(
								query.getRequestUrl(client.getPort(), client.getHost()));
					} catch (Exception ee) {}
					throw e;
				}

				// get the url
				String httpUrl = "http://" + client.getHost() + ":" + client.getPort();
				if (columns[0].substring(1).isEmpty()) {
					httpUrl = query.getRequestUrl(client.getPort(), client.getHost());
				} else {
					httpUrl = httpUrl + columns[0].substring(1);
				}

				// Prepare the file: 
				File file = new File("golden/" + operationId + "_" + String.valueOf(caseCounter) + ".json");
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));

				// Call the node directly using curl
				System.out.println("\n******************************************\n" + httpUrl);
				bw.append(httpUrl + "\n");

				//callExternalCurl
				String curlResponse = callExternalCurl(httpUrl);
				bw.append(Utils.formatJson(curlResponse));

				// compare the results
				String filter = "round\"";
				DiffResult dr = Utils.showDifferentces(curlResponse, sdkResponse, "curl", "sdk", filter);


				if (!verbose) {
					System.out.print(dr.justDiff);
				} else {
					System.out.print(dr.fullOutput);
				}
				if (!dr.pass) {
					failed = true;
					System.out.println("Test " + caseCounter + " [FAILED!]");
				}else {
					System.out.println("Test " + caseCounter + " [PASSED!]");
				}
				caseCounter++;
				bw.close();
			}
		}
		return !failed;
	}
     */

    public TestGenerator (JsonNode root) {
        super(root);
    }
}
