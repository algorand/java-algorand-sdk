package com.algorand.sdkutils.generators;

import java.io.*;
import java.util.Collection;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static DiffResult showDifferentces(
            String jsonA, String jsonB,
            String nameA, String nameB,
            String filterSubstring) throws JsonParseException, JsonMappingException, IOException {

        StringBuffer full = new StringBuffer();
        StringBuffer diff = new StringBuffer();

        jsonA = formatJson(jsonA);
        jsonB = formatJson(jsonB);

        StringTokenizer stA = new StringTokenizer(jsonA, "\n");
        StringTokenizer stB = new StringTokenizer(jsonB, "\n");

        DiffResult dr = new DiffResult();
        dr.pass = true;
        dr.filtered = false;

        while (stA.hasMoreTokens() && stB.hasMoreElements()) {
            String lineA = stA.nextToken();
            String lineB = stB.nextToken();

            boolean filteredLine = false;

            if (lineA.compareTo(lineB) != 0) {
                if (lineA.contains(filterSubstring) && lineB.contains(filterSubstring)) {
                    filteredLine = true;
                }
                if (filteredLine) {
                    dr.filtered = true;
                    full.append("*");
                    full.append(nameA + "\t>>>" + lineA + "\n");
                    full.append("*");
                    full.append(nameB + "\t>>>" + lineB + "\n");

                    diff.append("*");
                    diff.append(nameA + "\t>>>" + lineA + "\n");
                    diff.append("*");
                    diff.append(nameB + "\t>>>" + lineB + "\n");
                }
                else {
                    dr.pass = false;
                    full.append(nameA + "\t>>>" + lineA + "\n");
                    full.append(nameB + "\t>>>" + lineB + "\n");

                    diff.append(nameA + "\t>>>" + lineA + "\n");
                    diff.append(nameB + "\t>>>" + lineB + "\n");
                }
            }
            else {
                full.append(lineA + "\n");
            }
        }


        if (dr.filtered && dr.pass) {
            full.append("*"+filterSubstring+"\n");
            diff.append("*"+filterSubstring+"\n");
        }
        dr.fullOutput = full.toString();
        dr.justDiff = diff.toString();
        return dr;

    }

    public static String formatJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        Object jsonObject = mapper.readValue(json, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }

    public static JsonNode getRoot(InputStream fileIs) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;
        root = objectMapper.readTree(fileIs);
        return root;
    }

    public static String readFile(File file) throws IOException  {
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(fileReader);
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

    public static void writeFile(String filename, String content) throws IOException {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.append(content);
        bw.close();
    }
}
