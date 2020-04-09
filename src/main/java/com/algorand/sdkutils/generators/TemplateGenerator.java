package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

public class TemplateGenerator extends Generator{

	public TemplateGenerator(JsonNode root) {
		super(root);
	}

	public void writeTemplate(String filename) throws IOException {
		File outFile = new File(filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

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
			bw.append("\n\n\n");
		}		
		bw.close();
	}

}
