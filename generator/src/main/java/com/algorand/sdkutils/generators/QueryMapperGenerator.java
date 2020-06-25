package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;

public class QueryMapperGenerator extends Generator {

    JsonNode indexer;
    JsonNode algod;
    public QueryMapperGenerator(JsonNode indexerRoot, JsonNode algodRoot) {
        super(indexerRoot);
        this.indexer = indexerRoot;
        this.algod = algodRoot;
    }

    public void writeQueryMapper(String sdkutilsPath) throws IOException {
        BufferedWriter bw = getFileWriter("QueryMapper", sdkutilsPath);
        bw.append("package com.algorand.algosdk.unit.utils;\n\n" + 
                "import java.security.NoSuchAlgorithmException;\n" + 
                "import java.text.ParseException;\n" + 
                "import com.algorand.algosdk.v2.client.common.Utils;\n" + 
                "\n" + 
                "import com.algorand.algosdk.crypto.Address;\n" + 
                "import com.algorand.algosdk.util.Encoder;\n" +
                "import com.algorand.algosdk.v2.client.algod.*;\n" +
                "import com.algorand.algosdk.v2.client.indexer.*;\n" + 
                "import com.algorand.algosdk.v2.client.model.Enums;\n" + 
                "import com.algorand.algosdk.v2.client.common.*;\n\n" + 
                "public class QueryMapper {\n" + 
                "\n");

        StringBuffer getClass = new StringBuffer();
        StringBuffer setValue = new StringBuffer();
        StringBuffer lookUp = new StringBuffer();
        StringBuffer enumMappers = new StringBuffer();

        setValue.append("    public static void setValue(Query q, String className, String property, String value) throws ParseException, NoSuchAlgorithmException {\n" + 
                "        switch (className) {\n");
        lookUp.append("    public static String lookup(Query q, String className) throws Exception {\n" + 
                "        Response<?> resp = q.execute();\n" + 
                "        if (resp.body() == null) {\n" + 
                "            throw new RuntimeException(resp.message());\n" + 
                "        }\n" + 
                "        return resp.body().toString();\n" + 
                "    }\n\n");

        this.root = indexer;
        getClass.append("    public static Query getClass(String name, IndexerClient client, String args[]) throws NoSuchAlgorithmException {\n" + 
                "        switch (name) {\n");
        JsonNode paths = this.root.get("paths");
        Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
        while (pathIter.hasNext()) {
            getMappings(getClass, setValue, pathIter);
        }
        getClass.append("        }\n" + 
                "        return null;\n" + 
                "    }\n\n");

        this.root = algod;
        getClass.append("    public static Query getClass(String name, AlgodClient client, String args[]) throws NoSuchAlgorithmException {\n" + 
                "        switch (name) {\n");
        paths = this.root.get("paths");
        pathIter = paths.fields();
        while (pathIter.hasNext()) {
            getMappings(getClass, setValue, pathIter);
        }

        getClass.append("        }\n" + 
                "        return null;\n" + 
                "    }\n\n");
        setValue.append("\n" + 
                "        }\n" + 
                "    }\n\n");

        generateEnumMapper(root, enumMappers);
        bw.append(getClass);
        bw.append(setValue);
        bw.append(lookUp);
        bw.append(enumMappers);
        bw.append("}");
        bw.close();
    }

    private void getMappings(StringBuffer getClass, StringBuffer setValue,
            Iterator<Entry<String, JsonNode>> pathIter) {
        Entry<String, JsonNode> path = pathIter.next();
        JsonNode privateTag = path.getValue().get("post") != null ? path.getValue().get("post").get("tags") : null;
        if (privateTag != null && privateTag.elements().next().asText().equals("private")) {
            return;
        }
        String className = path.getValue().findValue("operationId").asText();
        String javaClassName = Tools.getCamelCase(className, true);
        /*
         * TODO: this is a bug: function name should start with a small letter.
         * However, v2 was released with function names first letter cap. 
         * Will be good to fix in the future. 
         * 
         * Should use:  getCamelCase(className, false);
         */
        String methodName = Tools.getCamelCase(className, Character.isUpperCase(className.charAt(0)));

        // getClass
        getClass.append("        case \""+className+"\":\n" + 
                "            return client."+methodName+"(");

        //setValue
        setValue.append("        case \""+className+"\":\n" + 
                "            switch (property) {\n");

        JsonNode paramNode = path.getValue().findValue("parameters");
        Iterator<Entry<String, JsonNode>> properties = getSortedParameters(paramNode);

        // The parameters in the path are directly passed to the constructor.
        // The method with have in order arguments each assigned to the parameter in order. 
        int argCounter = 0;

        while (properties.hasNext()) {
            Entry<String, JsonNode> parameter = properties.next();
            String javaSetParamName = Tools.getCamelCase(parameter.getKey(), false);

            JsonNode typeNode = parameter.getValue().get("type") != null ? parameter.getValue() : parameter.getValue().get("schema");

            String typeName = typeNode.get("type").asText();
            Iterator<JsonNode> enumVals = parameter.getValue().get("enum") == null ? null : 
                parameter.getValue().get("enum").elements();
            String javaEnumName = Tools.getCamelCase(parameter.getKey(), true);
            String format = Generator.getTypeFormat(typeNode, parameter.getKey());

            if (inPath(parameter.getValue())) {
                if (argCounter > 0) {
                    getClass.append(", ");
                }
                switch (typeName) {
                case "integer":
                    getClass.append("Long.valueOf("+"args[" + argCounter + "])");
                    break;
                case "string":
                    if (format.contentEquals("Address")) {
                        getClass.append("new Address(args[" + argCounter + "])");
                        break;
                    }
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
            // Do not expose format property
            if (javaEnumName.equals("Format")) {
                continue;
            }
            setValue.append("            case \""+parameter.getKey()+"\":\n" + 
                    "                (("+javaClassName+")q)."+javaSetParamName+"(");
            switch (typeName) {
            case "integer":
                setValue.append("Long.valueOf(value));\n");
                break;
            case "string":
                switch (format) {
                case "RFC3339 String":
                    setValue.append("Utils.parseDate(value));\n");
                    break;
                case "Address":
                    setValue.append("new Address(value));\n");
                    break;
                case "byte":
                case "binary":
                    setValue.append("Encoder.decodeFromBase64(value));\n");
                    break;
                default:
                    if (enumVals != null) {
                        setValue.append("get" + javaEnumName + "(value));\n");
                    } else {
                        setValue.append("value);\n");
                    }
                }
                break;
            case "boolean":
                setValue.append("Boolean.valueOf(value));\n");
                break;
            }
            setValue.append("                break;\n");
        }
        getClass.append(");\n");
        setValue.append("            }\n            break;\n");
    }

    private void generateEnumMapper (JsonNode root, StringBuffer enumMappers) throws IOException {

        JsonNode parameters = root.get("parameters");
        Iterator<Entry<String, JsonNode>> classes = parameters.fields();
        while (classes.hasNext()) {
            Entry<String, JsonNode> cls = classes.next();
            if (cls.getValue().get("enum") != null) {
                // Do not expose format property
                if (cls.getKey().equals("format")) {
                    continue;
                }
                String enumName = Tools.getCamelCase(cls.getKey(), true);
                TypeDef enumType = getEnum(cls.getValue(), cls.getKey(), "");
                enumMappers.append("    public static " + enumType.javaTypeName + " get" + enumName + "(String val) {\n");
                enumMappers.append("        switch(val.toUpperCase()) {\n");
                JsonNode enumNode = cls.getValue().get("enum");
                Iterator<JsonNode> elmts = enumNode.elements();
                while(elmts.hasNext()) {
                    String val = elmts.next().asText();
                    String javaEnum = Tools.getCamelCase(val, true).toUpperCase();
                    enumMappers.append("        case \"" + javaEnum + "\":\n");
                    enumMappers.append("            return " + enumType.javaTypeName + "." + javaEnum + ";\n");
                }
                enumMappers.append("        default:\n            throw new RuntimeException(\"Enum value not recognized: \" + val +\"!\");\n");
                enumMappers.append("        }\n    }\n");
            }
        }
    }

}
