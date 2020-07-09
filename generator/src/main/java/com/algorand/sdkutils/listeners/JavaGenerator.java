package com.algorand.sdkutils.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;

public class JavaGenerator implements Subscriber {

    public static final String TAB = "    ";

    String clientName;
    String modelPath;
    String modelPackage;
    String queryFilesDirectory;
    String queryPackage;
    String commonPath;
    String commonPackage;

    // JavaModelWriter is a manager of the model file writer
    // It is limited to only one file at a time.
    private JavaModelWriter javaModelWriter;

    JavaQueryWriter javaQueryWriter;

    // generatedPathsEntry holds the client class path query functions,
    // and the corresponding imports
    StringBuilder generatedPathsEntries;
    StringBuilder generatedPathsImports;

    // Used for the client file generation
    private String tokenName;
    private Boolean tokenOptional;

    private static TreeMap<String, String> enumDefinitions = new TreeMap<String, String>();

    public JavaGenerator(
            String clientName,
            String modelPath,
            String modelPackage,
            String queryFilesDirectory,
            String queryPackage,
            String commonPath,
            String commonPackage,
            String tokenName,
            Boolean tokenOptional,
            Publisher publisher) throws IOException {
        publisher.subscribeAll(this);

        this.clientName = clientName;
        this.modelPath = modelPath;
        this.modelPackage = modelPackage;
        this.queryFilesDirectory = queryFilesDirectory;
        this.queryPackage = queryPackage;
        this.commonPath = commonPath;
        this.commonPackage = commonPackage;

        this.tokenName = tokenName;
        this.tokenOptional = tokenOptional;

        javaModelWriter = new JavaModelWriter(this, modelPath);

        generatedPathsEntries = new StringBuilder();
        generatedPathsImports = new StringBuilder();
    }

    @Override
    public void onEvent(Events event) {
        switch(event) {
        case END_QUERY:
            javaQueryWriter.finalize();
            break;
        default:
            throw new RuntimeException("Unimplemented event! " + event);
        }
    }

    @Override
    public void onEvent(Events event, String [] notes) {
        switch(event) {
        case NEW_QUERY:
            javaQueryWriter = new JavaQueryWriter(
                    notes[0], notes[1], notes[2],
                    notes[3], notes[4], this);
            break;
        default:
            throw new RuntimeException("Unimplemented event for note! " + event);
        }
    }

    @Override
    public void onEvent(Events event, TypeDef type) {

        switch(event) {
        case NEW_PROPERTY:
            javaModelWriter.newProperty(type);
            break;
        case QUERY_PARAMETER:
            javaQueryWriter.addQueryProperty(type, true, false, false);
            break;
        case PATH_PARAMETER:
            javaQueryWriter.addQueryProperty(type, false, true, false);
            break;
        case BODY_CONTENT:
            javaQueryWriter.addQueryProperty(type, false, false, true);
            break;
        default:
            throw new RuntimeException("Unimplemented event for TypeDef! " + event);
        }
    }

    @Override
    public void onEvent(Events event, StructDef sDef) {
        switch(event) {
        case NEW_MODEL:
            javaModelWriter.newModel(sDef, this.modelPackage);
            break;
        case NEW_RETURN_TYPE:
            javaModelWriter.newModel(sDef, this.modelPackage);
            break;
        default:
            throw new RuntimeException("Unemplemented event for StructDef! " + event);
        }

    }

    public void terminate() {
        javaModelWriter.close();
        javaModelWriter = null;

        generateClientFile(
                clientName,
                generatedPathsImports,
                generatedPathsEntries,
                commonPackage,
                commonPath,
                tokenName,
                tokenOptional);

        generateEnumClasses(this.modelPath, this.modelPackage);

    }


    /**
     * Generate the client which wraps up all the builders, accepts the host/port/token, etc.
     *
     * clientName    - IndexerClient
     * importLines   - The contents of xxxxxImports.txt
     * paths         - The file at genRoot + xxxxxPaths.txt
     * packageName   - "com.algorand.algosdk.v2.client.common"
     * packagePath   - "src/main/java/com/algorand/algosdk/v2/client/common"
     * tokenName     - "X-Indexer-API-Token"
     * tokenOptional - Indicates that the token is optional and two constructors should be created.
     *
     * @param clientName Name of the client class. i.e. IndexerClient
     * @param importLines Lines to be added to the import section of the template. Generated from a previous step.
     * @param paths The generated methods for accessing the paths. Generated from a previous step.
     * @param packageName Name of the package containing the client.
     * @param packagePath Path where the client will go.
     * @param tokenName Name of the token used for this application. i.e. X-Algo-API-Token
     * @param tokenOptional Whether or not a no-token version of the constructor should be created.
     */
    private static void generateClientFile(
            String clientName,
            StringBuilder importLines,
            StringBuilder paths,
            String packageName,
            String packagePath,
            String tokenName,
            Boolean tokenOptional) {

        if (packagePath.endsWith("/")) {
            new Exception("Path shouldn't have a trailing slash.").printStackTrace();;
        }

        importLines.append("import com.algorand.algosdk.crypto.Address;");

        StringBuffer sb = new StringBuffer();
        sb.append("package " + packageName + ";\n\n");
        sb.append(importLines);
        sb.append("\n\n");
        sb.append("public class " + clientName + " extends Client {\n\n");

        sb.append("    /**\n");
        sb.append("     * Construct an " + clientName + " for communicating with the REST API.\n");
        sb.append("     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.\n");
        sb.append("     * @param port REST server port.\n");
        sb.append("     * @param token authentication token.\n");
        sb.append("     */\n");
        sb.append("    public " + clientName + "(String host, int port, String token) {\n" +
                "        super(host, port, token, \"" + tokenName + "\");\n" +
                "    }\n");

        if (tokenOptional) {
            sb.append("\n    /**\n");
            sb.append("     * Construct an " + clientName + " for communicating with the REST API.\n");
            sb.append("     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.\n");
            sb.append("     * @param port REST server port.\n");
            sb.append("     */\n");
            sb.append("    public " + clientName + "(String host, int port) {\n" +
                    "        super(host, port, \"\", \"" + tokenName + "\");\n" +
                    "    }\n\n");
        }

        sb.append(paths);
        sb.append("}\n");
        try {
            Utils.writeFile(packagePath + "/" + clientName + ".java", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedWriter newFile(String className, String directory) {
        File f = new File(directory + "/" + className + ".java");
        f.getParentFile().mkdirs();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bw;
    }


    public static void append(Writer bw, String text) {
        try {
            bw.append(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void append(StringBuilder sb, String text) {
        sb.append(text);
    }

    public static void append(BufferedWriter sb, StringBuilder text) {
        try {
            sb.append(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeFile(BufferedWriter bw) {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setImportOf(TypeDef typeObj, TreeMap<String, Set<String>> imports) {
        setImportOf(typeObj, imports, true);
    }
    public static void setImportOf(
            TypeDef typeObj,
            TreeMap<String, Set<String>> imports,
            boolean forModel) {

        if (typeObj.isOfType("array")) {
            Tools.addImport(imports, "java.util.ArrayList");
            Tools.addImport(imports, "java.util.List");
        }

        if (typeObj.isOfType("enum")) {
            if (!forModel) {
                Tools.addImport(imports, "com.algorand.algosdk.v2.client.model.Enums");
            }
            return;
        }


        switch (typeObj.rawTypeName) {
        case "object":
            if (typeObj.javaTypeName.equals("HashMap<String,Object>")) {
                Tools.addImport(imports, "java.util.HashMap");
            }
            break;
        case "SignedTransaction":
            Tools.addImport(imports, "com.algorand.algosdk.transaction.SignedTransaction");
            break;

        case "time":
            Tools.addImport(imports, "java.util.Date");
            Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Utils");
            break;
        }

        if (typeObj.rawTypeName.equals("binary") || typeObj.rawTypeName.equals("byte")) {
            Tools.addImport(imports, "com.algorand.algosdk.util.Encoder");
            return;
        }


        if (typeObj.javaTypeName.equalsIgnoreCase("Address") ||
                typeObj.javaTypeName.equalsIgnoreCase("List<Address>")) {
            if (forModel) {
                Tools.addImport(imports, "java.security.NoSuchAlgorithmException");
            }
            Tools.addImport(imports, "com.algorand.algosdk.crypto.Address");
            return;
        }
    }

    // Generate the enum file with accumulated and loaded enum definitions.
    public void generateEnumClasses (String rootPath, String pkg)  {

        BufferedWriter bw = Tools.getFileWriter("Enums", rootPath);
        try {
            bw.append("package " + pkg + ";\n\n");
            bw.append("import com.fasterxml.jackson.annotation.JsonProperty;\n\n");
            bw.append("public class Enums {\n\n");
            Iterator<String> classes = enumDefinitions.keySet().iterator();
            while (classes.hasNext()) {
                String className = classes.next();
                // Do not expose format property
                if (className.equals("format")) {
                    continue;
                }
                bw.append(enumDefinitions.get(className));
                bw.append("\n");
            }
            bw.append("}\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void storeEnumDefinition(TypeDef typeObj) {
        StringBuilder sb = new StringBuilder();
        String javaTypeName = Tools.getCamelCase(typeObj.propertyName, true);
        if (typeObj.doc != null && !typeObj.doc.isEmpty()) {
            sb.append(Tools.formatComment(typeObj.doc, TAB, true));
        }
        sb.append(TAB + "public enum " + javaTypeName + " {\n");

        Iterator<String> elmts = typeObj.enumValues.iterator();
        while(elmts.hasNext()) {
            String val = elmts.next();
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
        sb.append(TAB + TAB + javaTypeName + "(String name) {\n");
        sb.append(TAB + TAB + TAB + "this.serializedName = name;\n");
        sb.append(TAB + TAB + "}\n\n");
        sb.append(TAB + TAB + "@Override\n");
        sb.append(TAB + TAB + "public String toString() {\n");
        sb.append(TAB + TAB + TAB + "return this.serializedName;\n");
        sb.append(TAB + TAB + "}\n");

        sb.append(TAB + "}\n");
        javaTypeName = "Enums." + javaTypeName;

        // Check for conflicting duplicate classes
        String definition = sb.toString();
        String existingDef = enumDefinitions.get(javaTypeName);
        if (existingDef != null && existingDef.compareTo(definition) != 0) {
            // Could be the comment missing in one
            int ei = existingDef.indexOf("*/\n");
            if (ei == -1) ei = 0; else ei += 3;
            String existingBody = existingDef.substring(ei);
            int di = definition.indexOf("*/\n");
            if (di == -1) di = 0; else di += 3;
            String definitionBody = definition.substring(di);
            if (existingBody.equals(definitionBody)) {
                if (definition.length() < existingDef.length()) {
                    return;
                }
            } else {
                System.err.println(definition);
                System.err.println(existingDef);
                throw new RuntimeException("Conflicting enum classes.");

            }
        }
        JavaGenerator.enumDefinitions.put(javaTypeName, sb.toString());
    }
}



final class JavaQueryWriter {

    private static final String TAB = JavaGenerator.TAB;

    private StringBuilder decls;
    private StringBuilder builders;
    private StringBuilder constructorHeader;
    private StringBuilder constructorBody;
    private StringBuilder requestMethod;
    private ArrayList<String> constructorComments;

    private String className;
    private String httpMethod;
    private String path;
    private String discAndPath;

    StringBuilder generatedPathsEntryBody;

    boolean pAdded = false;
    boolean addFormatMsgpack = false;

    private JavaGenerator javaGen;

    TreeMap<String, Set<String>> imports;

    public JavaQueryWriter(
            String methodName,
            String returnType,
            String path,
            String desc,
            String httpMethod,
            JavaGenerator javaGenerator) {

        this.className = Tools.getCamelCase(methodName, true);
        decls = new StringBuilder();
        builders = new StringBuilder();
        constructorHeader = new StringBuilder();
        constructorBody = new StringBuilder();
        requestMethod = new StringBuilder();
        constructorComments = new ArrayList<String>();

        generatedPathsEntryBody = new StringBuilder();
        this.httpMethod = httpMethod;

        requestMethod.append(getQueryResponseMethod(returnType));
        requestMethod.append("    protected QueryData getRequestString() {\n");
        pAdded = false;
        addFormatMsgpack = false;

        this.path = path;
        discAndPath = desc + "\n" + path;

        this.javaGen = javaGenerator;

        imports = new TreeMap<String, Set<String>>();
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Client");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.HttpMethod");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Query");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.QueryData");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Response");
        if (needsClassImport(returnType.toLowerCase())) {
            Tools.addImport(imports, javaGen.modelPackage + "." + returnType);
        }

        javaGen.generatedPathsEntries.append(Tools.formatComment(discAndPath, TAB, true));
        javaGen.generatedPathsEntries.append("    public " + className + " " + methodName + "(");
    }

    public void addQueryProperty(TypeDef propType, boolean inQuery, boolean inPath, boolean inBody) {

        String propName = Tools.getCamelCase(propType.propertyName, false);
        String setterName = Tools.getCamelCase(propType.propertyName, false);

        String propCode = propType.propertyName;

        // Do not expose format property
        if (propType.javaTypeName.equals("Enums.Format")) {
            if (!className.equals("AccountInformation")) {
                // Don't set format to msgpack for AccountInformation
                addFormatMsgpack = true;
            }
            return;
        }

        if (!(propType.rawTypeName.equals("binary") && inBody)) {
            JavaGenerator.setImportOf(propType, imports, false);
        }

        // The parameters are either in the path or in the query

        // Populate generator structures for the in path parameters
        if (inPath){
            if (propType.isOfType("enum")) {
                throw new RuntimeException("Enum in paths is not supported! " + propName);
            }
            decls.append(TAB + "private " + propType.javaTypeName + " " + propName + ";\n");
            String desc = "";
            if (propType.doc != null) {
                desc = propType.doc;
                desc = Tools.formatComment("@param " + propName + " " + desc, TAB, false);
                constructorComments.add(desc);
            }

            constructorHeader.append(", " + propType.javaTypeName + " " + propName);
            constructorBody.append("        this." + propName + " = " + propName + ";\n");

            if (pAdded) {
                javaGen.generatedPathsEntries.append(",\n            ");
            }

            javaGen.generatedPathsEntries.append(propType.javaTypeName + " " + propName);
            generatedPathsEntryBody.append(", " + propName);
            pAdded = true;
        } else {

            String exception = getThrownException(inBody, propType.javaTypeName);

            if (propType.doc != null) {
                String desc = propType.doc;
                if (!exception.isEmpty()) {
                    desc = desc + "\n" + "@throws " + exception; 
                }
                desc = Tools.formatComment(desc, TAB, true);
                builders.append(desc);
            }

            if (propType.isOfType("enum")) {
                this.javaGen.storeEnumDefinition(propType);
            }

            String exceptionStm = exception.isEmpty() ? "" : "throws " + exception + " ";
            builders.append(TAB + "public " + className + " " + setterName + 
                    "(" + propType.javaTypeName + " " + propName + ") " + exceptionStm + "{\n");
            String valueOfString = getStringValueOfStatement(propType.javaTypeName, propName);

            if (inBody) {
                String valueOfByteA = getByteArrayValueOfStatement(propType.javaTypeName, propName);
                builders.append(TAB + TAB + "addToBody("+ valueOfByteA +");\n");
            } else {
                builders.append(TAB + TAB + "addQuery(\"" + propCode + "\", "+ valueOfString +");\n");
            }
            builders.append(TAB + TAB + "return this;\n");
            builders.append(TAB + "}\n");
            builders.append("\n");
        }

        if (propType.required) {
            if (inBody) {
                requestMethod.append("        if (qd.bodySegments.isEmpty()) {\n");
            } else if (inQuery) {
                requestMethod.append("        if (!qd.queries.containsKey(\"" + propName + "\")) {\n");
            } else if (inPath) {
                requestMethod.append("        if (this." + propName + " == null) {\n");
            }
            requestMethod.append("            throw new RuntimeException(\"" +
                    propCode + " is not set. It is a required parameter.\");\n        }\n");
        }
    }

    public void finalize() {

        javaGen.generatedPathsImports.append("import " + javaGen.queryPackage + "." + className + ";\n");

        javaGen.generatedPathsEntries.append(") {\n");
        javaGen.generatedPathsEntries.append("        return new "+className+"((Client) this");
        javaGen.generatedPathsEntries.append(generatedPathsEntryBody);
        javaGen.generatedPathsEntries.append(");\n    }\n\n");

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

        StringBuilder queryParamsCode = new StringBuilder();
        queryParamsCode.append(decls);
        if (!decls.toString().isEmpty()) {
            queryParamsCode.append("\n");
        }

        // constructor
        if (constructorComments.size() > 0) {
            queryParamsCode.append("    /**");
            for (String elt : constructorComments) {
                queryParamsCode.append("\n" + elt);
            }
            queryParamsCode.append("\n     */\n");
        }
        queryParamsCode.append("    public "+className+"(Client client");
        queryParamsCode.append(constructorHeader);
        queryParamsCode.append(") {\n        super(client, new HttpMethod(\""+httpMethod+"\"));\n");
        if (addFormatMsgpack) {
            queryParamsCode.append("        addQuery(\"format\", \"msgpack\");\n");
        }

        queryParamsCode.append(constructorBody);
        queryParamsCode.append("    }\n\n");

        queryParamsCode.append(builders);
        queryParamsCode.append(requestMethod);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(Tools.formatComment(discAndPath, "", true));
        sb.append("public class " + className + " extends Query {\n\n");
        sb.append(queryParamsCode);
        sb.append("\n}");

        BufferedWriter bw = JavaGenerator.newFile(className, javaGen.queryFilesDirectory);
        JavaGenerator.append(bw, "package " + javaGen.queryPackage + ";\n\n");
        JavaGenerator.append(bw, Tools.getImports(imports));
        JavaGenerator.append(bw, sb);
        JavaGenerator.closeFile(bw);

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

    String getByteArrayValueOfStatement(String propType, String propName) {
        switch (propType) {
        case "DryrunRequest":
            Tools.addImport(imports, "com.algorand.algosdk.util.Encoder");
            Tools.addImport(imports, "com.algorand.algosdk.v2.client.model.DryrunRequest");
            Tools.addImport(imports, "com.fasterxml.jackson.core.JsonProcessingException");
            return "Encoder.encodeToMsgPack("+propName+")";
        default:
            return propName;
        }
    }

    String getThrownException(boolean inBody, String propType) {
        switch (propType) {
        case "DryrunRequest":
            if (inBody) {
                return "JsonProcessingException";
            }
        default:
            return "";
        }
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

}

final class JavaModelWriter {

    public static final String TAB = JavaGenerator.TAB;

    // JavaModelWriter writes all the response and other structures into a single file
    private BufferedWriter modelFileWriter;

    // currentModelBuffer holds the model code as it is constructed
    // used for skipping models with no parameters.
    private StringBuilder currentModelBuffer;

    // pendingOpenStruct indicates if a struct is not closed yet,
    // expecting more parameters. This is useful to do away with the
    // end call. The end call is implicit at the time of a new struct or
    // at the time of terminate.
    private boolean pendingOpenFile;


    // Indicates if any property is added to this model
    // used for skipping models with no parameters.
    private boolean modelPropertyAdded;

    private String className;

    // property name is the key
    private TreeMap<String, TypeDef> properties;

    TreeMap<String, Set<String>> imports;


    private JavaGenerator javagen;
    private String folder;

    public JavaModelWriter(JavaGenerator javagen, String folder) {
        currentModelBuffer = null;
        pendingOpenFile = false;
        this.javagen = javagen;
        this.folder = folder;
    }

    public void close () {

        writeProperties(currentModelBuffer);
        writeCompareMethod(className, currentModelBuffer, getSortedProperties());

        JavaGenerator.append(currentModelBuffer, "}\n");

        JavaGenerator.append(modelFileWriter, Tools.getImports(imports));
        if (modelPropertyAdded) {
            JavaGenerator.append(modelFileWriter, currentModelBuffer);
        }
        modelPropertyAdded = false;

        if (modelFileWriter != null) {
            JavaGenerator.closeFile(modelFileWriter);
        }
        modelFileWriter = null;
    }

    public void newProperty(TypeDef type) {
        modelPropertyAdded = true;
        properties.put(Tools.getCamelCase(type.propertyName, false), type);
    }

    // newModel can write into one file at a time.
    // This is a limitation, but there is no need for more.
    // At any time, there can be one currentModelBuffer, and one JavaModelWriter
    public void newModel(StructDef sDef, String packageName) {

        if (pendingOpenFile) {
            this.close();
            pendingOpenFile = false;
        }
        this.imports = new TreeMap<String, Set<String>>();
        this.properties = new TreeMap<String, TypeDef>();

        if (modelFileWriter == null) {

            className = Tools.getCamelCase(sDef.name, true);

            modelFileWriter = JavaGenerator.newFile(className, folder);
            JavaGenerator.append(modelFileWriter, "package " + packageName + ";\n\n");

            Tools.addImport(imports, "java.util.Objects"); // used by Objects.deepEquals

            Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.PathResponse");
            Tools.addImport(imports, "com.fasterxml.jackson.annotation.JsonProperty");

            currentModelBuffer = new StringBuilder();
            if (sDef.doc != null) {
                JavaGenerator.append(currentModelBuffer, Tools.formatComment(sDef.doc, "", true));
            }
            JavaGenerator.append(currentModelBuffer, "public class " + className + " extends PathResponse {\n\n");
        }

        pendingOpenFile = true;
        modelPropertyAdded = false;
    }

    // Returns an iterator in sorted order of the properties (json nodes).
    Iterator<Entry<String, TypeDef>> getSortedProperties() {
        Iterator<Entry<String, TypeDef>>sortedProps = properties.entrySet().iterator();
        return sortedProps;
    }

    // Write the properties of the Model class.
    void writeProperties(StringBuilder buffer) {
        Iterator<Entry<String, TypeDef>> sorted = getSortedProperties();
        while (sorted.hasNext()) {
            Entry<String, TypeDef> prop = sorted.next();
            TypeDef typeObj = prop.getValue();

            JavaGenerator.setImportOf(typeObj, imports);

            String desc = null;
            if (typeObj.doc != null) {
                desc = typeObj.doc;
                desc = Tools.formatComment(desc, TAB, true);
            }

            // public type
            if (desc != null) buffer.append(desc);
            getPropertyWithJsonSetter(typeObj, buffer);
            buffer.append("\n");
        }
    }

    static void writeExpandedDefinitions(TypeDef typeObj, StringBuilder buffer,
            TreeMap<String, Set<String>> imports, boolean forModel){
        JavaGenerator.setImportOf(typeObj, imports, forModel);
        if (typeObj.javaTypeName.equalsIgnoreCase("Address")) {
            getAddress(typeObj, buffer);
        }

        if (typeObj.javaTypeName.equalsIgnoreCase("List<Address>")) {
            getAddressArray(typeObj, buffer);
        }

        // Enum is not defined here. It is defined separately in a
        // different file.

        if (typeObj.rawTypeName.equals("binary") ||
                typeObj.rawTypeName.equals("byte")) {
            if (typeObj.javaTypeName.equals("byte[]")) {
                getBase64Encoded(typeObj, buffer);
                return;
            }
            getBase64EncodedArray(typeObj, buffer, forModel);

        }

    }

    // getPropertyWithJsonSetter formats the property into java declaration type with
    // the appropriate json annotation.
    void getPropertyWithJsonSetter(TypeDef typeObj, StringBuilder buffer) {
        String jprop = typeObj.propertyName;
        String javaName = Tools.getCamelCase(jprop, false);
        if (typeObj.isOfType("getterSetter")) {
            writeExpandedDefinitions(typeObj, buffer, imports, true);
            return;
        }

        if (typeObj.isOfType("enum")) {
            this.javagen.storeEnumDefinition(typeObj);
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
    }

    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static void getBase64EncodedArray(TypeDef typeObj, StringBuilder sb,
            boolean forModel) {
        if (forModel == false) {
            throw new RuntimeException("array of byte[] cannot yet be used in a path or path query.");
        }

        String propName = typeObj.propertyName;
        String javaName = Tools.getCamelCase(propName, false);

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
    }

    // Get base64 encoded byte[] type.
    // It provides the special getter/setter needed for this type
    static void getBase64Encoded(TypeDef typeObj, StringBuilder sb) {

        String propName = typeObj.propertyName;
        String javaName = Tools.getCamelCase(propName, false);
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
    }

    // Get TypeDef for Address type.
    // It provides the special getter/setter needed for this type.
    static void getAddress(TypeDef typeObj, StringBuilder buffer) {

        String propName = typeObj.propertyName;
        String javaName = Tools.getCamelCase(propName, false);

        buffer.append(TAB + "@JsonProperty(\"" + propName + "\")\n" +
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
    }

    static void getAddressArray(TypeDef typeObj, StringBuilder buffer){
        String propName = typeObj.propertyName;
        String javaName = Tools.getCamelCase(propName, false);
        buffer.append(
                "    @JsonProperty(\"" + propName + "\")\n" +
                        "    public void accounts(List<String> " + javaName + ") throws NoSuchAlgorithmException {\n" +
                        "        this." + javaName + " = new ArrayList<Address>();\n" +
                        "        for (String val : " + javaName + ") {\n" +
                        "            this." + javaName + ".add(new Address(val));\n" +
                        "        }\n" +
                        "    }\n" +
                        "    @JsonProperty(\"" + propName + "\")\n" +
                        "    public List<String> " + javaName + "() throws NoSuchAlgorithmException {\n" +
                        "        ArrayList<String> ret = new ArrayList<String>();\n" +
                        "        for (Address val : this." + javaName + ") {\n" +
                        "            ret.add(val.encodeAsString());\n" +
                        "        }\n" +
                        "        return ret;\n" +
                        "    }\n" +
                        "    public List<Address> " + javaName + " = new ArrayList<Address>();\n");
    }

    // Writes the compare methods by adding a comparator for each class member.
    static void writeCompareMethod(String className,
            StringBuilder buffer, Iterator<Entry<String, TypeDef>> properties) {
        buffer.append("    @Override\n" +
                "    public boolean equals(Object o) {\n" +
                "\n" +
                "        if (this == o) return true;\n" +
                "        if (o == null) return false;\n");
        buffer.append("\n");
        buffer.append("        " + className + " other = (" + className + ") o;\n");
        while (properties.hasNext()) {
            Entry<String, TypeDef> prop = properties.next();
            String jprop = prop.getKey();
            String javaName = Tools.getCamelCase(jprop, false);
            buffer.append("        if (!Objects.deepEquals(this." + javaName + ", other." + javaName + ")) return false;\n");
        }
        buffer.append("\n        return true;\n    }\n");
    }
}
