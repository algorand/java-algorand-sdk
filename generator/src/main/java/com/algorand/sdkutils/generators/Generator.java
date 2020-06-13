package com.algorand.sdkutils.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Publisher.Events;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.algorand.sdkutils.utils.*;

public class Generator {
    public static final String TAB = "    ";

    protected JsonNode root;
    protected Publisher publisher;

    static BufferedWriter getFileWriter(String className, String directory) throws IOException {
        File f = new File(directory + "/" + className + ".java");
        f.getParentFile().mkdirs();
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        return bw;
    }

    static String getTypeNameFromRef(String ref) {
        StringTokenizer st = new StringTokenizer(ref, "/");
        String ans = "";
        while (st.hasMoreTokens()) {
            ans = st.nextToken();
        }
        return ans;
    }

    // Get TypeDef for Address type. 
    // It provides the special getter/setter needed for this type.
    static TypeDef getAddress(String propName,
            Map<String, Set<String>> imports, boolean forModel, String desc, boolean required) {
        addImport(imports, "com.algorand.algosdk.crypto.Address");
        if (forModel) {
            addImport(imports, "java.security.NoSuchAlgorithmException");
        }

        StringBuffer sb = new StringBuffer();
        String javaName = Tools.getCamelCase(propName, false);

        sb.append(TAB + "@JsonProperty(\"" + propName + "\")\n" + 
                "    public void " + javaName + "(String "+ javaName +") throws NoSuchAlgorithmException {\n" + 
                "        this."+ javaName +" = new Address("+ javaName +");\n" + 
                "    }\n" + 
                "    @JsonProperty(\""+ propName +"\")\n" + 
                "    public String "+ javaName +"() throws NoSuchAlgorithmException {\n" + 
                "        if (this."+ javaName +" != null) {\n" +
                "            return this."+ javaName +".encodeAsString();\n" +
                "        } else {\n" +
                "            return null;\n" +
                "        }\n" +
                "    }\n" + 
                "    public Address " + javaName + ";\n");
        return new TypeDef("Address", "address", sb.toString(), "getterSetter", propName, desc, required);
    }

    // Get base64 encoded byte[] type.
    // It provides the special getter/setter needed for this type
    static TypeDef getBase64Encoded(String propName, String rawType,
            Map<String, Set<String>> imports, boolean forModel, String desc, boolean required) {
        if (imports != null) {
            addImport(imports, "com.algorand.algosdk.util.Encoder");
        }
        String javaName = Tools.getCamelCase(propName, false);
        StringBuffer sb = new StringBuffer();
        sb.append("    @JsonProperty(\"" + propName + "\")\n" +
                "    public void " + javaName + "(String base64Encoded) {\n" +
                "        this."+ javaName +" = Encoder.decodeFromBase64(base64Encoded);\n" +
                "    }\n" +
                "    @JsonProperty(\""+ propName +"\")\n" +
                "    public String "+ javaName +"() {\n" +
                "        return Encoder.encodeToBase64(this."+ javaName +");\n" +
                "    }\n" +
                "    public byte[] "+ javaName +";\n");
        // getterSetter typeName is only used in path.
        return new TypeDef("byte[]", "byteArray", sb.toString(), "getterSetter", propName, desc, required);
    }

    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getBase64EncodedArray(String propName, String rawType,
            Map<String, Set<String>> imports, boolean forModel, String desc, boolean required) {
        if (forModel == false) {
            throw new RuntimeException("array of byte[] cannot yet be used in a path or path query.");
        }
        addImport(imports, "com.algorand.algosdk.util.Encoder");
        addImport(imports, "java.util.ArrayList");
        addImport(imports, "java.util.List");

        String javaName = Tools.getCamelCase(propName, false);
        StringBuffer sb = new StringBuffer();

        sb.append("    @JsonProperty(\"" + propName + "\")\n" +
                "    public void " + javaName + "(List<String> base64Encoded) {\n" +
                "         this." + javaName + " = new ArrayList<byte[]>();\n" +
                "         for (String val : base64Encoded) {\n" +
                "             this." + javaName + ".add(Encoder.decodeFromBase64(val));\n" +
                "         }\n" +
                "     }\n" +
                "     @JsonProperty(\"" + propName + "\")\n" +
                "     public List<String> " + javaName + "() {\n" +
                "         ArrayList<String> ret = new ArrayList<String>();\n" +
                "         for (byte[] val : this." + javaName + ") {\n" +
                "             ret.add(Encoder.encodeToBase64(val));\n" +
                "         }\n" +
                "         return ret; \n" +
                "     }\n" +
                "    public List<byte[]> " + javaName + ";\n");
        // getterSetter typeName is only used in path.
        return new TypeDef("", rawType, sb.toString(), "getterSetter,array", propName, desc, required);
    }

    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getEnum(JsonNode prop, String propName) {
        JsonNode enumNode = prop.get("enum");
        if (enumNode == null) {
            throw new RuntimeException("Cannot find enum info in node: " + prop.toString());
        }
        StringBuffer sb = new StringBuffer();
        String enumClassName = Tools.getCamelCase(propName, true);
        sb.append(TAB + "public enum " + enumClassName + " {\n");

        Iterator<JsonNode> elmts = enumNode.elements();
        while(elmts.hasNext()) {
            String val = elmts.next().asText();
            sb.append(TAB + TAB + "@JsonProperty(\"" + val + "\") ");
            String javaEnum = Tools.getCamelCase(val, true).toUpperCase();
            sb.append(javaEnum);
            sb.append("(\"" + val + "\")");
            if (elmts.hasNext()) {
                sb.append(",\n");
            } else {
                sb.append(";\n\n");
            }
        }
        sb.append(TAB + TAB + "final String serializedName;\n");
        sb.append(TAB + TAB + "" + enumClassName + "(String name) {\n");
        sb.append(TAB + TAB + TAB + "this.serializedName = name;\n");
        sb.append(TAB + TAB + "}\n\n");
        sb.append(TAB + TAB + "@Override\n");
        sb.append(TAB + TAB + "public String toString() {\n");
        sb.append(TAB + TAB + TAB + "return this.serializedName;\n");
        sb.append(TAB + TAB + "}\n");

        sb.append(TAB + "}\n");
        enumClassName = "Enums." + enumClassName;
        String desc = prop.get("description") == null ? "" : prop.get("description").asText();
        return new TypeDef(enumClassName, prop.get("type").asText(), 
                sb.toString(), "enum", propName, desc, isRequired(prop));
    }

    // getType returns the type fron the JsonNode
    TypeDef getType(
            JsonNode prop, 
            boolean asObject,
            Map<String, Set<String>> imports,
            String propName, boolean forModel) {        
        String desc = prop.get("description") == null ? "" : prop.get("description").asText();  
        if (prop.get("$ref") != null) {
            JsonNode typeNode = prop.get("$ref");
            String type = getTypeNameFromRef(typeNode.asText());
            // Need to check here if this type does not have a class of its own 
            // No C/C++ style typedef in java, and this type could be a class with no properties
            prop = getFromRef(typeNode.asText());
            if (desc.isEmpty()) {
                desc = prop.get("description") == null ? "" : prop.get("description").asText(); 
            }
            if (hasProperties(prop)) {
                return new TypeDef(type, type, propName, desc, isRequired(prop));
            }
        }

        if (prop.get("enum") != null) {
            if (!forModel && !propName.equals("format")) {
                addImport(imports, "com.algorand.algosdk.v2.client.model.Enums");
            }
            return getEnum(prop, propName);
        }

        JsonNode typeNode = prop.get("type") != null ? prop : prop.get("schema");
        String type = typeNode.get("type").asText();
        String format = getTypeFormat(typeNode, propName);
        if (!format.isEmpty() ) {
            switch (format) {
            case "uint64":
                return new TypeDef("java.math.BigInteger", type, propName, desc, isRequired(prop));
            case "RFC3339 String":
                addImport(imports, "java.util.Date");
                addImport(imports, "com.algorand.algosdk.v2.client.common.Utils");
                return new TypeDef("Date", "time", propName, desc, isRequired(prop));
            case "Address":
                return getAddress(propName, imports, forModel, desc, isRequired(prop));
            case "SignedTransaction":
                addImport(imports, "com.algorand.algosdk.transaction.SignedTransaction");
                return new TypeDef("SignedTransaction", type, propName, desc, isRequired(prop));
            case "binary":
                return getBase64Encoded(propName, type, null, forModel, desc, isRequired(prop));
            case "byte":
            case "base64":
            case "digest":
                if (type.contentEquals("array")) {
                    type = prop.get("items").get("type").asText(); 
                    return getBase64EncodedArray(propName, type, imports, forModel, desc, isRequired(prop));
                } else {
                    return getBase64Encoded(propName, type, imports, forModel, desc, isRequired(prop));
                }
            case "AccountID":
                break;
            case "BlockCertificate":
            case "BlockHeader":
                addImport(imports, "java.util.HashMap");
                return new TypeDef("HashMap<String,Object>", type, propName, desc, isRequired(prop));
            }
        }
        switch (type) {
        case "integer":
            String longName = asObject ? "Long" : "long"; 
            return new TypeDef(longName, type, propName, desc, isRequired(prop));
        case "object":
        case "string":
            return new TypeDef("String", type, propName, desc, isRequired(prop));
        case "boolean":
            String boolName = asObject ? "Boolean" : "boolean"; 
            return new TypeDef(boolName, type, propName, desc, isRequired(prop));
        case "array":
            JsonNode arrayTypeNode = prop.get("items");
            TypeDef typeName = getType(arrayTypeNode, asObject, imports, propName, forModel);
            return new TypeDef("List<" + typeName.javaTypeName + ">", typeName.rawTypeName,
                    typeName.def, type, propName, desc, isRequired(prop));
        default:
            throw new RuntimeException("Unrecognized type: " + type);
        }
    }

    // getTypeFormat returns the additional type formatting information
    // There could be multiple such tags in the spec file. This method knows which 
    // one is relevant here. 
    public static String getTypeFormat(JsonNode typeNode, String propName) {
        String format = typeNode.get("x-algorand-format") != null ? typeNode.get("x-algorand-format").asText() : "";
        String type = typeNode.get("type").asText();
        format = typeNode.get("format") != null && format.isEmpty() ? typeNode.get("format").asText() : format;
        format = typeNode.get("x-go-name") != null && format.isEmpty() ? typeNode.get("x-go-name").asText() : format;
        if ((propName.equals("address") || 
                propName.contentEquals("account-id") || 
                propName.contentEquals("AccountID")) &&
                type.equals("string")) {
            format = "Address";
        }
        if (format.equals("base64")) {
            format = "byte";
        }
        return format;
    }

    static String getStringValueOfStatement(String propType, String propName) {
        switch (propType) {
        case "Date":
            return "Utils.getDateString(" + propName + ")";
        case "byte[]":
            return "Encoder.encodeToBase64(" + propName + ")";
        default:
            return "String.valueOf("+propName+")";
        }
    }

    // Imports are collected and organized before printed as import statements.
    // addImports adds a needed import class. 
    static void addImport(Map<String, Set<String>> imports, String imp) {
        String key = imp.substring(0, imp.indexOf('.'));
        if (imports.get(key) == null) {
            imports.put(key, new TreeSet<String>());
        }
        imports.get(key).add(imp);
    }

    // getImports organizes the imports and returns the block of import statements
    // The statements are unique, and organized. 
    static String getImports(Map<String, Set<String>> imports) {
        StringBuffer sb = new StringBuffer();

        Set<String> java = imports.get("java");
        if (java != null) {
            for (String imp : java) {
                sb.append("import " + imp + ";\n");
            }
            if (imports.get("com") != null) {
                sb.append("\n");
            }
        }

        Set<String> com = imports.get("com");
        if (com != null) {
            for (String imp : com) {
                sb.append("import " + imp + ";\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    // getPropertyWithJsonSetter formats the property into java declaration type with 
    // the appropriate json annotation.
    static String getPropertyWithJsonSetter(TypeDef typeObj, String javaName, String jprop){
        StringBuffer buffer = new StringBuffer();

        if (typeObj.isOfType("getterSetter")) {
            return typeObj.def.toString();
        }

        switch (typeObj.javaTypeName) {
        case "java.util.DateTime":
            throw new RuntimeException("Parsing of time is not yet implemented!");
        case "String":
        case "Long":
        case "long":
        case "boolean":
        case "java.math.BigInteger":
        default: // List and Models with Json properties
            buffer.append(TAB + "@JsonProperty(\"" + jprop + "\")\n");
            buffer.append(TAB + "public " + typeObj.javaTypeName + " " + javaName);
            if (typeObj.isOfType("array")) {
                buffer.append(" = new Array" + typeObj.javaTypeName + "()");
            }
            buffer.append(";\n");
        }
        return buffer.toString();
    }

    // returns true if the type needs an import statement. 
    // Not needed for primitive types. 
    static boolean needsClassImport(String type) {
        switch (type) {
        case "integer":
            return false;
        case "string":
            return false;
        default:
            return true;
        }
    }

    // Returns an iterator in sorted order of the properties (json nodes). 
    static Iterator<Entry<String, JsonNode>> getSortedProperties(JsonNode properties) {
        Iterator<Entry<String, JsonNode>> props = properties.fields();
        TreeMap<String, JsonNode> propMap = new TreeMap<String, JsonNode>();
        while (props.hasNext()) {
            Entry<String, JsonNode> e = props.next();
            propMap.put(e.getKey(), e.getValue());
        }
        Iterator<Entry<String, JsonNode>>sortedProps = propMap.entrySet().iterator();
        return sortedProps;
    }

    // Returns an iterator in sorted order of the parameters (json nodes). 
    Iterator<Entry<String, JsonNode>> getSortedParameters(JsonNode properties) {
        TreeMap<String, JsonNode> tm = new TreeMap<String, JsonNode>();
        if (properties == null) {
            return tm.entrySet().iterator();
        }

        if (properties.isArray()) {
            ArrayNode jsonArrayNode = (ArrayNode) properties;
            for (int i = 0; i < jsonArrayNode.size(); i++) {
                JsonNode node = jsonArrayNode.get(i);
                JsonNode typeNode = null;
                if (node.get("$ref") != null) {
                    typeNode = this.getFromRef(node.get("$ref").asText());
                } else {
                    typeNode = node;
                }
                tm.put(typeNode.get("name").asText(), typeNode);
            }
            Iterator<Entry<String, JsonNode>>sortedParams = tm.entrySet().iterator();
            return sortedParams;
        }
        return null;
    }

    // Write the properties of the Model class.
    void writeProperties(StringBuffer buffer, Iterator<Entry<String, JsonNode>> properties, Map<String, Set<String>> imports) {
        while (properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String jprop = prop.getKey();
            String javaName = Tools.getCamelCase(jprop, false);
            String goName = prop.getValue().get("x-go-name") == null ? jprop : prop.getValue().get("x-go-name").asText();
            TypeDef typeObj = getType(prop.getValue(), true, imports, goName, true);
            publisher.publish(Events.NEW_PROPERTY, typeObj);
            if (typeObj.isOfType("array")) {
                addImport(imports, "java.util.ArrayList");
                addImport(imports, "java.util.List");
            }

            String desc = null;
            if (prop.getValue().get("description") != null) {
                desc = prop.getValue().get("description").asText();
                desc = Tools.formatComment(desc, TAB + "", true);
            }

            // public type
            if (desc != null) buffer.append(desc);
            buffer.append(getPropertyWithJsonSetter(typeObj, javaName, jprop));
            buffer.append("\n");
        }
    }

    // Writes the compare methods by adding a comparator for each class member. 
    static void writeCompareMethod(String className, StringBuffer buffer, Iterator<Entry<String, JsonNode>> properties) {
        buffer.append("    @Override\n" +
                "    public boolean equals(Object o) {\n" +
                "\n" +
                "        if (this == o) return true;\n" +
                "        if (o == null) return false;\n");
        buffer.append("\n");
        buffer.append("        " + className + " other = (" + className + ") o;\n");
        while (properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String jprop = prop.getKey();
            String javaName = Tools.getCamelCase(jprop, false);
            buffer.append("        if (!Objects.deepEquals(this." + javaName + ", other." + javaName + ")) return false;\n");
        }
        buffer.append("\n        return true;\n    }\n");
    }

    // writeClass writes the Model class. 
    // This is the root method for writing the complete class. 
    void writeClass(String className, 
            JsonNode propertiesNode, 
            String desc, 
            String directory, 
            String pkg,
            Events event) throws IOException {
        System.out.println("Generating ... " + className);
        publisher.publish(event, new StructDef(className, desc));

        Iterator<Entry<String, JsonNode>> properties = getSortedProperties(propertiesNode);
        className = Tools.getCamelCase(className, true);
        BufferedWriter bw = getFileWriter(className, directory);
        bw.append("package " + pkg + ";\n\n");

        HashMap<String, Set<String>> imports = new HashMap<String, Set<String>>();
        StringBuffer body = new StringBuffer();

        writeProperties(body, properties, imports);

        properties = getSortedProperties(propertiesNode);
        writeCompareMethod(className, body, properties);

        addImport(imports, "java.util.Objects"); // used by Objects.deepEquals

        addImport(imports, "com.algorand.algosdk.v2.client.common.PathResponse");
        addImport(imports, "com.fasterxml.jackson.annotation.JsonProperty");

        bw.append(getImports(imports));
        if (desc != null) {
            bw.append(Tools.formatComment(desc, "", true));
        }
        bw.append("public class " + className + " extends PathResponse {\n\n");
        bw.append(body);
        bw.append("}\n");
        bw.close();
    }

    // getPathInserts converts the path string into individual tokens which correspond
    // to the class members in the generated code. 
    // These are used to set the path segments in the constructor. 
    static ArrayList<String> getPathInserts(String path) {
        ArrayList<String> nPath = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(path, "/");
        while (st.hasMoreTokens()) {
            String elt = st.nextToken();
            if (elt.charAt(0) == '{') {
                String jName = Tools.getCamelCase(elt.substring(1, elt.length()-1), false);
                nPath.add(jName);
            } else {
                nPath.add("\"" + elt + "\"");
            }
        }
        return nPath;
    }

    static String getQueryResponseMethod(String returnType) {
        String ret =
                "    @Override\n" +
                        "    public Response<" + returnType + "> execute() throws Exception {\n" +
                        "        Response<" + returnType + "> resp = baseExecute();\n" +
                        "        resp.setValueType(" + returnType + ".class);\n" +
                        "        return resp;\n" +
                        "    }\n\n";
        return ret;
    }

    static boolean isRequired(JsonNode prop) {
        if (prop.get("required") != null) {
            return prop.get("required").asBoolean();
        }
        return false;
    }
    static boolean inPath(JsonNode prop) {
        if (prop.get("in") != null) {
            return prop.get("in").asText().compareTo("path") == 0;
        }
        return false;
    }
    static boolean inBody(JsonNode prop) {
        if (prop.get("in") != null) {
            return prop.get("in").asText().compareTo("body") == 0;
        }
        return false;
    }
    static boolean hasProperties(JsonNode itemNode) {
        if (itemNode.get("properties") == null) {
            return false;
        }
        return true;
    }

    // Query parameters need be in builder methods.
    // processQueryParameters do all the processing of the parameters. 
    String processQueryParams(
            StringBuffer generatedPathsEntry,
            Iterator<Entry<String, JsonNode>> properties,
            String className,
            String path,
            String returnType,
            String httpMethod,
            Map<String, Set<String>> imports) {

        StringBuffer decls = new StringBuffer();
        StringBuffer builders = new StringBuffer();
        StringBuffer constructorHeader = new StringBuffer();
        StringBuffer constructorBody = new StringBuffer();
        StringBuffer requestMethod = new StringBuffer();
        ArrayList<String> constructorComments = new ArrayList<String>();

        StringBuffer generatedPathsEntryBody = new StringBuffer();

        requestMethod.append(Generator.getQueryResponseMethod(returnType));
        requestMethod.append("    protected QueryData getRequestString() {\n");
        boolean pAdded = false;
        boolean addFormatMsgpack = false;

        while (properties != null && properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String propName = Tools.getCamelCase(prop.getKey(), false);
            String setterName = Tools.getCamelCase(prop.getKey(), false);
            String goName = prop.getValue().get("x-go-name") == null ? prop.getKey() : prop.getValue().get("x-go-name").asText();
            TypeDef propType = getType(prop.getValue(), true, imports, goName, false);

            // Do not expose format property
            if (propType.javaTypeName.equals("Enums.Format")) {
                if (!className.equals("AccountInformation")) {
                    // Don't set format to msgpack for AccountInformation
                    addFormatMsgpack = true;
                }
                continue;
            }
            String propCode = prop.getKey();

            // The parameters are either in the path or in the query

            // Populate generator structures for the in path parameters
            if (inPath(prop.getValue())) {
                if (propType.isOfType("enum")) {
                    throw new RuntimeException("Enum in paths is not supported! " + propName);
                }
                decls.append(TAB + "private " + propType.javaTypeName + " " + propName + ";\n");
                String desc = "";
                if (prop.getValue().get("description") != null) {
                    propType.doc = desc;
                    desc = prop.getValue().get("description").asText();
                    desc = Tools.formatComment("@param " + propName + " " + desc, TAB, false);
                    constructorComments.add(desc);
                }

                constructorHeader.append(", " + propType.javaTypeName + " " + propName);
                constructorBody.append("        this." + propName + " = " + propName + ";\n");

                if (pAdded) {
                    generatedPathsEntry.append(",\n            ");
                }
                generatedPathsEntry.append(propType.javaTypeName + " " + propName);
                generatedPathsEntryBody.append(", " + propName);
                pAdded = true;
                
                publisher.publish(Events.PATH_PARAMETER, propType);
                
                continue;
            }

            if (prop.getValue().get("description") != null) {
                String desc = prop.getValue().get("description").asText();
                propType.doc = desc;
                desc = Tools.formatComment(desc, TAB, true);
                builders.append(desc);
            }
            builders.append(TAB + "public " + className + " " + setterName + "(" + propType.javaTypeName + " " + propName + ") {\n");
            String valueOfString = getStringValueOfStatement(propType.javaTypeName, propName);

            if (inBody(prop.getValue())) {
                builders.append(TAB + TAB + "addToBody("+ propName +");\n");
                publisher.publish(Events.BODY_CONTENT, propType);
            } else {
                builders.append(TAB + TAB + "addQuery(\"" + propCode + "\", "+ valueOfString +");\n");
                publisher.publish(Events.QUERY_PARAMETER, propType);
            }
            builders.append(TAB + TAB + "return this;\n");
            builders.append(TAB + "}\n");
            builders.append("\n");

            if (isRequired(prop.getValue())) {
                if (inBody(prop.getValue())) {
                    requestMethod.append("        if (qd.bodySegments.isEmpty()) {\n");
                } else {
                    requestMethod.append("        if (!qd.queries.containsKey(\"" + propName + "\")) {\n");
                }
                requestMethod.append("            throw new RuntimeException(\"" +
                        propCode + " is not set. It is a required parameter.\");\n        }\n");
            }

        }

        generatedPathsEntry.append(") {\n");
        generatedPathsEntry.append("        return new "+className+"((Client) this");
        generatedPathsEntry.append(generatedPathsEntryBody);
        generatedPathsEntry.append(");\n    }\n\n");

        // Add the path construction code.
        // The path is constructed in the end, while the query params are added as the
        ArrayList<String> al = getPathInserts(path);
        for (String str : al) {
            requestMethod.append(
                    "        addPathSegment(String.valueOf(" + str + "));\n");
        }

        requestMethod.append("\n" +
                "        return qd;\n" +
                "    }");

        StringBuffer ans = new StringBuffer();
        ans.append(decls);
        if (!decls.toString().isEmpty()) {
            ans.append("\n");
        }

        // constructor
        if (constructorComments.size() > 0) {
            ans.append("    /**");
            for (String elt : constructorComments) {
                ans.append("\n" + elt);
            }
            ans.append("\n     */\n");
        }
        ans.append("    public "+className+"(Client client");
        ans.append(constructorHeader);
        ans.append(") {\n        super(client, new HttpMethod(\""+httpMethod+"\"));\n");
        if (addFormatMsgpack) {
            ans.append("        addQuery(\"format\", \"msgpack\");\n");
        }

        ans.append(constructorBody);
        ans.append("    }\n\n");

        ans.append(builders);
        ans.append(requestMethod);
        return ans.toString();
    }

    JsonNode getFromRef(String ref) {
        StringTokenizer st = new StringTokenizer(ref, "/");
        JsonNode ans = root;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.charAt(0) == '#') {
                continue;
            }
            ans = ans.get(token);
        }
        return ans;
    }

    // Write the class of a path expression
    // This is the root method for preparing the complete class
    void writeQueryClass(
            StringBuffer generatedPathsEntry,
            JsonNode spec,
            String path,
            String directory,
            String pkg,
            String modelPkg) throws IOException {

        String httpMethod;
        Iterator<String> fields = spec.fieldNames();
        httpMethod = fields.next();
        spec = spec.get(httpMethod);

        String className = spec.get("operationId").asText();
        String methodName = Tools.getCamelCase(className, false);
        className = Tools.getCamelCase(className, true);

        JsonNode paramNode = spec.get("parameters");
        String returnType = "String";
        if (spec.get("responses").get("200").get("$ref") != null) {
            returnType = spec.get("responses").get("200").get("$ref").asText();
            JsonNode returnTypeNode = this.getFromRef(returnType);
            if (returnTypeNode.get("schema").get("$ref") != null) {
                returnType = Generator.getTypeNameFromRef(returnTypeNode.get("schema").get("$ref").asText());
            } else {
                returnType = Generator.getTypeNameFromRef(returnType);
                returnType = Tools.getCamelCase(returnType, true);
            }
        }
        String desc = spec.get("description") != null ? spec.get("description").asText() : "";
        String discAndPath = desc + "\n" + path;
        System.out.println("Generating ... " + className);
        Iterator<Entry<String, JsonNode>> properties = null;
        if ( paramNode != null) {
            properties = getSortedParameters(paramNode);
        }

        BufferedWriter bw = getFileWriter(className, directory);
        bw.append("package " + pkg + ";\n\n");

        Map<String, Set<String>> imports = new HashMap<String, Set<String>>();
        addImport(imports, "com.algorand.algosdk.v2.client.common.Client");
        addImport(imports, "com.algorand.algosdk.v2.client.common.HttpMethod");
        addImport(imports, "com.algorand.algosdk.v2.client.common.Query");
        addImport(imports, "com.algorand.algosdk.v2.client.common.QueryData");
        addImport(imports, "com.algorand.algosdk.v2.client.common.Response");
        if (needsClassImport(returnType.toLowerCase())) {
            addImport(imports, modelPkg + "." + returnType);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append(Tools.formatComment(discAndPath, "", true));
        generatedPathsEntry.append(Tools.formatComment(discAndPath, TAB, true));
        generatedPathsEntry.append("    public " + className + " " + methodName + "(");
        String [] strarray = {className, returnType, path, desc};
        this.publisher.publish(Events.NEW_QUERY, strarray);
        
        sb.append("public class " + className + " extends Query {\n\n");
        sb.append(
                processQueryParams(
                        generatedPathsEntry,
                        properties,
                        className,
                        path,
                        returnType,
                        httpMethod,
                        imports));
        sb.append("\n}");
        bw.append(getImports(imports));
        bw.append(sb);
        bw.close();
        
        publisher.publish(Events.END_QUERY);
    }

    // Generate all the enum classes in the spec file. 
    public void generateEnumClasses (JsonNode root, String rootPath, String pkg) throws IOException {

        BufferedWriter bw = getFileWriter("Enums", rootPath);
        bw.append("package " + pkg + ";\n\n");
        bw.append("import com.fasterxml.jackson.annotation.JsonProperty;\n\n");
        bw.append("public class Enums {\n\n");
        JsonNode parameters = root.get("parameters");
        Iterator<Entry<String, JsonNode>> classes = parameters.fields();
        while (classes.hasNext()) {
            Entry<String, JsonNode> cls = classes.next();
            if (cls.getValue().get("enum") != null) {

                // Do not expose format property
                if (cls.getKey().equals("format")) {
                    continue;
                }

                if (cls.getValue().get("description") != null) {
                    String comment = null;
                    comment = cls.getValue().get("description").asText();
                    bw.append(Tools.formatComment(comment, "", true));
                }
                TypeDef enumType = getEnum(cls.getValue(), cls.getKey());
                bw.append(enumType.def);
                bw.append("\n");
            }
        }
        bw.append("}\n");
        bw.close();
    }

    // Generate all the Indexer or algod model classes 
    public void generateAlgodIndexerObjects (JsonNode root, String rootPath, String pkg) throws IOException {
        JsonNode schemas = root.get("components") != null ? 
                root.get("components").get("schemas") : 
                    root.get("definitions");
                Iterator<Entry<String, JsonNode>> classes = schemas.fields();
                while (classes.hasNext()) {
                    Entry<String, JsonNode> cls = classes.next();
                    String desc = null;
                    if (!hasProperties(cls.getValue())) {
                        // If it has no properties, no class is needed for this type.
                        continue;
                    }
                    if (cls.getValue().get("description") != null) {
                        desc = cls.getValue().get("description").asText();
                    }
                    writeClass(cls.getKey(), cls.getValue().get("properties"), 
                            desc, rootPath, pkg, Events.NEW_MODEL);
                }
    }

    // Generate all the Indexer or algod return type classes 
    public void generateReturnTypes (JsonNode root, String rootPath, String pkg) throws IOException {
        JsonNode returns = root.get("components") != null ? 
                root.get("components").get("responses") : 
                    root.get("responses");
                Iterator<Entry<String, JsonNode>> returnTypes = returns.fields();
                while (returnTypes.hasNext()) {
                    Entry<String, JsonNode> rtype = returnTypes.next();
                    System.out.println("looking at: " + rtype.getKey());
                    JsonNode rSchema = rtype.getValue().get("content") != null ?
                            rtype.getValue().get("content").get("application/json").get("schema") :
                                rtype.getValue().get("schema");

                            if (rSchema.get("$ref") != null ) {
                                // It refers to a defined class
                                continue;
                            }
                            writeClass(rtype.getKey(), rSchema.get("properties"), 
                                    null, rootPath, pkg, Events.NEW_RETURN_TYPE);
                }
    }

    // Generate all the path expression classes
    public void generateQueryMethods(
            String rootPath, 
            String pkg,
            String modelPkg, 
            File gpImpDirFile, 
            File gpMethodsDirFile) throws IOException {
        // GeneratedPaths file
        try (   BufferedWriter gpImports = new BufferedWriter(new FileWriter(gpImpDirFile));
                BufferedWriter gpMethods = new BufferedWriter(new FileWriter(gpMethodsDirFile))) {
            StringBuffer gpBody = new StringBuffer();

            JsonNode paths = this.root.get("paths");
            Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
            while (pathIter.hasNext()) {
                Entry<String, JsonNode> path = pathIter.next();
                JsonNode privateTag = path.getValue().get("post") != null ? path.getValue().get("post").get("tags") : null;
                if (privateTag != null && privateTag.elements().next().asText().equals("private")) {
                    continue;
                }
                writeQueryClass(gpBody, path.getValue(), path.getKey(), rootPath, pkg, modelPkg);

                // Fill GeneratedPaths class
                String className = Tools.getCamelCase(path.getValue().findPath("operationId").asText(), true);
                gpImports.append("import " + pkg + "." + className + ";\n");
            }
            gpMethods.append(gpBody);
        }
    }


    public Generator (JsonNode root) {
        this.root = root;
        this.publisher = new Publisher();
    }

    public Generator (JsonNode root, Publisher publisher) {
        this.root = root;
        this.publisher = publisher;
    }
}
