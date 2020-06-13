package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.algorand.sdkutils.utils.Tools;
import com.fasterxml.jackson.databind.JsonNode;

public class TemplateGenerator extends Generator{

    public TemplateGenerator(JsonNode root) {
        super(root);
    }

    public void writeTemplate(String filename) throws IOException {
        File outFile = new File(filename+".csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

        File outFile2 = new File(filename+"Notes.txt");
        BufferedWriter notes = new BufferedWriter(new FileWriter(outFile2));

        JsonNode paths = this.root.get("paths");
        Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
        while (pathIter.hasNext()) {
            Entry<String, JsonNode> path = pathIter.next();
            String pathString = path.getKey();

            // Get the description
            String desc = null;
            String httpMethod;
            Iterator<String> fields = path.getValue().fieldNames();
            httpMethod = fields.next();

            if (path.getValue().get(httpMethod).get("description") != null) {
                desc = path.getValue().get(httpMethod).get("description").asText();
            }

            if (desc != null) {
                notes.append(Tools.formatComment(desc, "", true)+"\n");
            }
            notes.append(pathString+"\n");

            JsonNode paramNode = path.getValue().findValue("parameters");
            Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode);
            bw.append(pathString + ", ");
            while (properties.hasNext()) {
                Entry<String, JsonNode> parameter = properties.next();
                bw.append(parameter.getKey());

                JsonNode typeNode = parameter.getValue().get("type") != null ? parameter.getValue() : parameter.getValue().get("schema");
                bw.append("(" + typeNode.get("type").asText() + ")");
                if (isRequired(parameter.getValue())) {
                    bw.append("[R]");
                }
                bw.append(", ");
                if (parameter.getValue().get("description") != null) {
                    notes.append(Tools.formatComment(parameter.getValue().get("description").asText(), "\t", true)+"\n");
                    notes.append("\t"+parameter.getKey()+"\n");
                }
            }
            bw.append("\n\n\n");
            notes.append("\n\n");
        }
        notes.close();
        bw.close();
    }

}
