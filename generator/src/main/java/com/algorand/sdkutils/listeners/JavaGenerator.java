package com.algorand.sdkutils.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;

public class JavaGenerator implements Subscriber {

    public static final String TAB = "    ";

    String clientName;
    String modelPath;
    String modelPackage;
    String queryFilesDirectory;
    String queryPackage;
    String commonPath;
    String commonPackage;



    // packageName is also the folder where the files sit
    private String packageName;
    // filesFolder is rootFolder/packageName
    private String filesFolder;

    // JavaModelWriter is a manager of the model file writer
    // It is limited to only one file at a time.
    private JavaModelWriter javaModelWriter;


    JavaQueryWriter javaQueryWriter;


    // Query files

    // This is the individual file for each path query
    private BufferedWriter queryWriter;

    // path parameters are collected and stored here
    // this is useful when all the path parameters should be available for 
    // constructing some functions.  
    private TreeMap<String, TypeDef> pathParameters;

    // queryFunctions hold all the query functions which go to one file 
    // written by queryWriter
    private StringBuffer queryFunctions;

    private String currentQueryName;
    private String currentQueryReturn;

    // imports that go to one query file written by queryWriter
    private TreeMap<String, Set<String>> imports;

    // pathDesc has the comments and the path string template
    private String pathDesc;
    // path encoded with place-holders from the spec
    private String path;
    // request method
    private String httpMethod;

    // client functions

    // clientFunctions hold all the functions that return the  
    // makers of the query items. It is to organize them alphabetically sorted
    // this is reset once at the beginning, and flushed at terminate. 
    private TreeMap<String, String> clientFunctions;

    // clientFunction holds a single client function as it is getting contructed
    // If is reset at each new query 
    private StringBuffer clientFunction;

    public JavaGenerator(
            String clientName,
            String modelPath,
            String modelPackage,
            String queryFilesDirectory,
            String queryPackage,
            String commonPath,
            String commonPackage,
            Publisher publisher) throws IOException {
        publisher.subscribeAll(this);

        this.clientName = clientName;
        this.modelPath = modelPath;
        this.modelPackage = modelPackage;
        this.queryFilesDirectory = queryFilesDirectory;
        this.queryPackage = queryPackage;
        this.commonPath = commonPath;
        this.commonPackage = commonPackage;

        javaModelWriter = new JavaModelWriter(this, modelPath);
        clientFunctions = new TreeMap<String, String>();
    }

    public void terminate() {
        javaModelWriter.close();
        javaModelWriter = null;

        writeClientFunctions();
    }

    private void writeClientFunctions() {
        BufferedWriter bw = newFile("applicationclient", filesFolder);
        append(bw, "package " + packageName + "\n\n");
        append(bw, "import (\n");
        append(bw, TAB + "\"context\"\n\n");
        append(bw, TAB + "\"github.com/algorand/go-algorand-sdk/client/v2/common\"\n");
        append(bw, ")\n\n");
        append(bw, "const indexerAuthHeader = \"X-Indexer-API-Token\"\n\n");
        append(bw, "type Client common.Client\n\n");

        append(bw, 
                "// get performs a GET request to the specific path against the server\n" +
                        "func (c *Client) get(ctx context.Context, response interface{}, path string, request interface{}, headers []*common.Header) error {\n" +
                        TAB + "return (*common.Client)(c).Get(ctx, response, path, request, headers)\n" +
                        "}\n\n" +

                "// MakeClient is the factory for constructing an IndexerClient for a given endpoint.\n" +
                "func MakeClient(address string, apiToken string) (c *Client, err error) {\n" +
                TAB + "commonClient, err := common.MakeClient(address, indexerAuthHeader, apiToken)\n" +
                TAB + "c = (*Client)(commonClient)\n" +
                TAB + "return\n" +
                "}\n\n");
        for (Entry<String, String> e : clientFunctions.entrySet()) {
            append(bw, e.getValue());
        }
        closeFile(bw);
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
                    notes[3], notes[4], queryFilesDirectory, queryPackage, modelPackage);
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


    private StringBuffer processPath() {
        StringBuffer pathSB = new StringBuffer();
        StringBuffer paramSB = new StringBuffer();

        if (pathParameters.size() > 0) {
            pathSB.append("fmt.Sprintf(");
        }
        pathSB.append("\"");

        StringTokenizer st = new StringTokenizer(path, "/");

        while (st.hasMoreTokens()) {
            pathSB.append("/");
            String elt = st.nextToken();
            if (elt.charAt(0) == '{') {

                // Get the property name
                // The property name can be different from the name in the path placeholder
                // The reason for this is the x-go-name property overriding the property name
                // This is expected to be only case difference
                String propName = Tools.getCamelCase(elt.substring(1, elt.length()-1), false);
                if (pathParameters.get(propName) == null) {
                    // The name is overridden. Find the name
                    for (String name : pathParameters.keySet()) {
                        if (name.toUpperCase().equals(propName.toUpperCase())) {
                            propName = name;
                        }
                    }
                }

                switch(pathParameters.get(propName).javaTypeName) {
                case "string":
                    pathSB.append("%s");
                    break;
                case "uint64":
                    pathSB.append("%d");
                    break;
                default:
                    throw new RuntimeException("Unhandled Sprintf type.");
                }
                paramSB.append(", s." + propName);
            } else {
                pathSB.append(elt);
            }
        }

        pathSB.append("\"" + paramSB);
        if (pathParameters.size() > 0) {
            pathSB.append(")");
        }

        return pathSB;
    }





    private void newQuery(
            String className,
            String returnTypeName,
            String path,
            String desc,
            String httpMethod) {

        this.pathDesc = path + "\n" + desc;
        this.path = path;
        this.httpMethod = httpMethod;
        currentQueryName = Tools.getCamelCase(className, true);
        currentQueryReturn = Tools.getCamelCase(returnTypeName, true);

        pathParameters = new TreeMap<String, TypeDef>();
        queryFunctions = new StringBuffer();
        imports = new TreeMap<String, Set<String>>();

        if (queryWriter != null) {
            throw new RuntimeException("Query writer should be closed!");
        }

        // Add the entry into the applicationClient file
        clientFunction = new StringBuffer();
        clientFunction.append("func (c *Client) " + currentQueryName + "(");
    }

    private void addPathParameter(TypeDef type) {
        String propName = Tools.getCamelCase(
                type.goPropertyName.isEmpty() ? type.propertyName : type.goPropertyName, 
                        false);
        pathParameters.put(propName, type);

        // client functions
        if (pathParameters.size() > 1) {
            clientFunction.append(", ");
        }
        clientFunction.append(propName + " " + type);
    }

    private void addQueryParameter(TypeDef type) {
        // Also need to add this to the path struct (model)
        javaModelWriter.newProperty(type);
        String propName = type.goPropertyName.isEmpty() ? type.propertyName : type.goPropertyName;
        String funcName = Tools.getCamelCase(propName, true);
        String paramName = Tools.getCamelCase(propName, false);
        String desc = Tools.formatCommentGo(type.doc, funcName, "");


    }
    private void endQuery() {

        // client functions
        clientFunction.append(") *" + currentQueryName + " {\n");
        clientFunction.append(TAB + "return &" + currentQueryName + "{");

        queryWriter = newFile(currentQueryName, filesFolder);
        append(queryWriter, 
                "package " + packageName + "\n\n" +
                "import (\n");

        append(queryWriter, ")\n\n");

        append(queryWriter, Tools.formatCommentGo(pathDesc, currentQueryName, ""));
        append(queryWriter, "type " + currentQueryName + " struct {\n");

        int formattingWidth = 1;
        for (String key : pathParameters.keySet()) {
            if (key.length() > formattingWidth) {
                formattingWidth = key.length();
            }
        }
        formattingWidth += 1;
        if (javaModelWriter.modelPropertyAdded()) {
        }

        clientFunction.append("c: c");

        Iterator<Entry<String, TypeDef>> pps = pathParameters.entrySet().iterator();
        while(pps.hasNext()) {
            Entry<String, TypeDef> pp = pps.next();
            clientFunction.append(", " + pp.getKey() + ": " + pp.getKey());
        }
        append(queryWriter, "}\n\n");

        // client functions
        clientFunction.append("}\n}\n\n");

        clientFunctions.put(currentQueryName, 
                Tools.formatCommentGo(pathDesc, "", "") + clientFunction.toString());

        append(queryWriter, queryFunctions.toString());
        closeFile(queryWriter);
        queryWriter = null;
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

        if (typeObj.rawTypeName.equals("binary")) {
                Tools.addImport(imports, "com.algorand.algosdk.util.Encoder");
            return;
        }


        if (typeObj.javaTypeName.equalsIgnoreCase("Address")) {
            if (forModel) {
                Tools.addImport(imports, "java.security.NoSuchAlgorithmException");
            }
            Tools.addImport(imports, "com.algorand.algosdk.crypto.Address");
            return;
        }
    }
}

final class JavaQueryWriter {

    public static final String TAB = JavaGenerator.TAB;

    StringBuilder decls;
    StringBuilder builders;
    StringBuilder constructorHeader;
    StringBuilder constructorBody;
    StringBuilder requestMethod;
    ArrayList<String> constructorComments;

    String className;
    String httpMethod;
    String path;
    String discAndPath;

    StringBuilder generatedPathsEntryBody;
    StringBuilder generatedPathsEntry;

    boolean pAdded = false;
    boolean addFormatMsgpack = false;

    String queryFilesDirectory;
    String queryPackage;
    String modelPackage;

    TreeMap<String, Set<String>> imports;

    public JavaQueryWriter(
            String className,
            String returnType,
            String path,
            String desc,	    
            String httpMethod,
            String queryFilesDirectory,
            String queryPackage,
            String modelPackage) {

        if (className.equals("RawTransaction")) {
            System.out.println("bla");
        }

        this.className = className;
        decls = new StringBuilder();
        builders = new StringBuilder();
        constructorHeader = new StringBuilder();
        constructorBody = new StringBuilder();
        requestMethod = new StringBuilder();
        constructorComments = new ArrayList<String>();

        generatedPathsEntryBody = new StringBuilder();
        this.httpMethod = httpMethod;
        this.generatedPathsEntry = new StringBuilder();
        this.queryFilesDirectory = queryFilesDirectory;
        this.modelPackage = modelPackage;
        this.queryPackage = queryPackage;

        requestMethod.append(getQueryResponseMethod(returnType));
        requestMethod.append("    protected QueryData getRequestString() {\n");
        pAdded = false;
        addFormatMsgpack = false;

        this.path = path;
        discAndPath = desc + "\n" + path;

        imports = new TreeMap<String, Set<String>>();
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Client");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.HttpMethod");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Query");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.QueryData");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Response");
        if (needsClassImport(returnType.toLowerCase())) {
            Tools.addImport(imports, modelPackage + "." + returnType);
        }
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
                generatedPathsEntry.append(",\n            ");
            }

            generatedPathsEntry.append(propType.javaTypeName + " " + propName);
            generatedPathsEntryBody.append(", " + propName);
            pAdded = true;
        }

        if (propType.doc != null) {
            String desc = propType.doc;
            desc = Tools.formatComment(desc, TAB, true);
            builders.append(desc);
        }
        builders.append(TAB + "public " + className + " " + setterName + "(" + propType.javaTypeName + " " + propName + ") {\n");
        String valueOfString = getStringValueOfStatement(propType.javaTypeName, propName);

        if (inBody) {
            builders.append(TAB + TAB + "addToBody("+ propName +");\n");
        } else {
            builders.append(TAB + TAB + "addQuery(\"" + propCode + "\", "+ valueOfString +");\n");
        }
        builders.append(TAB + TAB + "return this;\n");
        builders.append(TAB + "}\n");
        builders.append("\n");

        if (propType.required) {
            if (inBody) {
                requestMethod.append("        if (qd.bodySegments.isEmpty()) {\n");
            } else {
                requestMethod.append("        if (!qd.queries.containsKey(\"" + propName + "\")) {\n");
            }
            requestMethod.append("            throw new RuntimeException(\"" +
                    propCode + " is not set. It is a required parameter.\");\n        }\n");
        }
    }

    public void finalize() {

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

        BufferedWriter bw = JavaGenerator.newFile(className, queryFilesDirectory);
        JavaGenerator.append(bw, "package " + queryPackage + ";\n\n");
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

    public boolean modelPropertyAdded() {
        return modelPropertyAdded;
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


    ////////////////////xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx


    // Write the properties of the Model class.
    void writeProperties(StringBuilder buffer) {
        Iterator<Entry<String, TypeDef>> sorted = getSortedProperties();
        while (sorted.hasNext()) {
            Entry<String, TypeDef> prop = sorted.next();
            String javaName = prop.getKey();
            TypeDef typeObj = prop.getValue();

            JavaGenerator.setImportOf(typeObj, imports);

            String desc = null;
            if (typeObj.doc != null) {
                desc = typeObj.doc;
                desc = Tools.formatComment(desc, TAB + "", true);
            }

            // public type
            if (desc != null) buffer.append(desc);
            getPropertyWithJsonSetter(typeObj, buffer, imports);
            buffer.append("\n");
        }
    }

    static void writeExpandedDefinitions(TypeDef typeObj, StringBuilder buffer,
            TreeMap<String, Set<String>> imports, boolean forModel){
        JavaGenerator.setImportOf(typeObj, imports, forModel);
        if (typeObj.javaTypeName.equalsIgnoreCase("Address")) {
            getAddress(typeObj, buffer, forModel);
        }
        if (typeObj.isOfType("enum")) {
            getEnum(typeObj, buffer);
        }
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
    static void getPropertyWithJsonSetter(TypeDef typeObj, StringBuilder buffer,
            TreeMap<String, Set<String>> imports){
        String jprop = typeObj.propertyName;
        String javaName = Tools.getCamelCase(jprop, false);
        if (typeObj.isOfType("getterSetter")) {
            writeExpandedDefinitions(typeObj, buffer, imports, true);
            return;
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
    static void getEnum(TypeDef typeObj, StringBuilder sb) {
        String javaTypeName = Tools.getCamelCase(typeObj.goPropertyName, true);
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
        sb.append(TAB + TAB + "" + javaTypeName + "(String name) {\n");
        sb.append(TAB + TAB + TAB + "this.serializedName = name;\n");
        sb.append(TAB + TAB + "}\n\n");
        sb.append(TAB + TAB + "@Override\n");
        sb.append(TAB + TAB + "public String toString() {\n");
        sb.append(TAB + TAB + TAB + "return this.serializedName;\n");
        sb.append(TAB + TAB + "}\n");

        sb.append(TAB + "}\n");
        javaTypeName = "Enums." + javaTypeName;
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
    static void getAddress(TypeDef typeObj, StringBuilder buffer,
            boolean forModel){

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


    // Query parameters need be in builder methods.
    // processQueryParameters do all the processing of the parameters. 
    String processQueryParams(
            StringBuilder generatedPathsEntry,
            Iterator<Entry<String, JsonNode>> properties,
            String className,
            String path,
            String returnType,
            String httpMethod,
            Map<String, Set<String>> imports) {

        StringBuilder decls = new StringBuilder();
        StringBuilder builders = new StringBuilder();
        StringBuilder constructorHeader = new StringBuilder();
        StringBuilder constructorBody = new StringBuilder();
        StringBuilder requestMethod = new StringBuilder();
        ArrayList<String> constructorComments = new ArrayList<String>();

        StringBuilder generatedPathsEntryBody = new StringBuilder();

        //        requestMethod.append(getQueryResponseMethod(returnType));
        requestMethod.append("    protected QueryData getRequestString() {\n");
        boolean pAdded = false;
        boolean addFormatMsgpack = false;

        while (properties != null && properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String propName = Tools.getCamelCase(prop.getKey(), false);
            String setterName = Tools.getCamelCase(prop.getKey(), false);
            TypeDef propType = null;// = getType(prop.getValue(), true, prop.getKey(), false);

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
            if (true){//inPath(prop.getValue())) {
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

                //publisher.publish(Events.PATH_PARAMETER, propType);

                continue;
            }

            if (prop.getValue().get("description") != null) {
                String desc = prop.getValue().get("description").asText();
                propType.doc = desc;
                desc = Tools.formatComment(desc, TAB, true);
                builders.append(desc);
            }
            builders.append(TAB + "public " + className + " " + setterName + "(" + propType.javaTypeName + " " + propName + ") {\n");
            //            String valueOfString = getStringValueOfStatement(propType.javaTypeName, propName);

            if (true) {//inBody(prop.getValue())) {
                builders.append(TAB + TAB + "addToBody("+ propName +");\n");
                ///publisher.publish(Events.BODY_CONTENT, propType);
            } else {
                //                builders.append(TAB + TAB + "addQuery(\"" + propCode + "\", "+ valueOfString +");\n");
                ///publisher.publish(Events.QUERY_PARAMETER, propType);
            }
            builders.append(TAB + TAB + "return this;\n");
            builders.append(TAB + "}\n");
            builders.append("\n");

            if (true) {//isRequired(prop.getValue())) {
                if (true) {//inBody(prop.getValue())) {
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
        ArrayList<String> al = null;//getPathInserts(path);
        for (String str : al) {
            requestMethod.append(
                    "        addPathSegment(String.valueOf(" + str + "));\n");
        }

        requestMethod.append("\n" +
                "        return qd;\n" +
                "    }");

        StringBuilder ans = new StringBuilder();
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



    // Write the class of a path expression
    // This is the root method for preparing the complete class
    void writeQueryClass(
            StringBuilder generatedPathsEntry,
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

        /*
         * TODO: this is a bug: function name should start with a small letter.
         * However, v2 was released with function names first letter cap. 
         * Will be good to fix in the future. 
         * 
         * Should use:  getCamelCase(className, false);
         */
        String methodName = Tools.getCamelCase(className, Character.isUpperCase(className.charAt(0)));
        className = Tools.getCamelCase(className, true);

        JsonNode paramNode = spec.get("parameters");
        String returnType = "String";
        if (spec.has("responses") && spec.get("responses").has("200") &&
                spec.get("responses").get("200").get("$ref") != null) {
            returnType = spec.get("responses").get("200").get("$ref").asText();
            JsonNode returnTypeNode = null;//this.getFromRef(returnType);
            if (returnTypeNode.get("schema").get("$ref") != null) {
                returnType = null;// OpenApiParser.getTypeNameFromRef(returnTypeNode.get("schema").get("$ref").asText());
            } else {
                returnType = null;//OpenApiParser.getTypeNameFromRef(returnType);
                returnType = Tools.getCamelCase(returnType, true);
            }
        }
        String desc = spec.get("description") != null ? spec.get("description").asText() : "";
        String discAndPath = desc + "\n" + path;
        System.out.println("Generating ... " + className);
        Iterator<Entry<String, JsonNode>> properties = null;
        if ( paramNode != null) {
            properties = null;//getSortedParameters(paramNode);
        }

        Map<String, Set<String>> imports = new HashMap<String, Set<String>>();
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Client");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.HttpMethod");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Query");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.QueryData");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Response");
        if (true){//needsClassImport(returnType.toLowerCase())) {
            Tools.addImport(imports, modelPkg + "." + returnType);
        }

        generatedPathsEntry.append(Tools.formatComment(discAndPath, TAB, true));

        generatedPathsEntry.append("    public " + className + " " + methodName + "(");
        String [] strarray = {className, returnType, path, desc, httpMethod};
        //this.publisher.publish(Events.NEW_QUERY, strarray);

        String queryParamsCode = processQueryParams(
                generatedPathsEntry,
                properties,
                className,
                path,
                returnType,
                httpMethod,
                imports);

        if (true) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(Tools.formatComment(discAndPath, "", true));
            sb.append("public class " + className + " extends Query {\n\n");
            sb.append(queryParamsCode);
            sb.append("\n}");

            BufferedWriter bw = null;//getFileWriter(className, directory);
            bw.append("package " + pkg + ";\n\n");
            bw.append(Tools.getImports(imports));
            bw.append(sb);
            bw.close();
        }

        //publisher.publish(Events.END_QUERY);
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


    // Generate all the enum classes in the spec file. 
    public void generateEnumClasses (JsonNode root, String rootPath, String pkg) throws IOException {
        if (true) {
            BufferedWriter bw = null;//getFileWriter("Enums", rootPath);
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
                    //TypeDef enumType = getEnum(cls.getValue(), cls.getKey(), "");
                    //bw.append(enumType.def);
                    bw.append("\n");
                }
            }
            bw.append("}\n");
            bw.close();
        }
    }

}
