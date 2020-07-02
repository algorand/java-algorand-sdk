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

import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;

public class JavaGenerator implements Subscriber {
    static final String TAB = "    ";

    // packageName is also the folder where the files sit
    private String packageName;
    // filesFolder is rootFolder/packageName
    private String filesFolder;

    // ModelWriter is a manager of the model file writer
    // It is limited to only one file at a time.
    private ModelWriter modelWriter;

    // Query files

    // This is the individual file for each path query
    private BufferedWriter queryWriter;

    // path parameters are collected and stored here
    // this is useful when all the path parameters should be available for 
    // constructing some functions.  
    private TreeMap<String, String> pathParameters;

    // queryFunctions hold all the query functions which go to one file 
    // written by queryWriter
    private StringBuffer queryFunctions;

    private String currentQueryName;
    private String currentQueryReturn;

    // imports that go to one query file written by queryWriter
    private Map<String, Set<String>> imports;

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

    public JavaGenerator(String rootFolder, String packageName, Publisher publisher) throws IOException {
        publisher.subscribeAll(this);

        modelWriter = null;
        clientFunctions = new TreeMap<String, String>();
        filesFolder = rootFolder + File.separatorChar + packageName;
        modelWriter = new ModelWriter(this, filesFolder);
        this.packageName = packageName;
    }

    public void terminate() {
        modelWriter.close();
        modelWriter = null;

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
            endQuery();
            break;
        default:
            throw new RuntimeException("Unimplemented event! " + event);
        }
    }

    @Override
    public void onEvent(Events event, String [] notes) {
        switch(event) {
        case NEW_QUERY:
            newQuery(notes[0], notes[1], notes[2], notes[3], notes[4]);
            break;
        default:
            throw new RuntimeException("Unimplemented event for note! " + event);
        }
    }

    @Override
    public void onEvent(Events event, TypeDef type) {
        switch(event) {
        case NEW_PROPERTY:
            modelWriter.newProperty(type);
            break;
        case QUERY_PARAMETER:
            addQueryParameter(type);
            break;
        case PATH_PARAMETER:
            addPathParameter(type);
            break;
        case BODY_CONTENT:
            // This is not really a path parameter, but will behave like one in most situation of code generation
            addPathParameter(type);
            break;
        default:
            throw new RuntimeException("Unimplemented event for TypeDef! " + event);
        }
    }

    @Override
    public void onEvent(Events event, StructDef sDef) {
        switch(event) {
        case NEW_MODEL:
            modelWriter.newModel(sDef, "responsemodels", "models");
            break;
        case NEW_RETURN_TYPE:
            modelWriter.newModel(sDef, "responsemodels", "models");
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

                switch(pathParameters.get(propName)) {
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

        pathParameters = new TreeMap<String, String>();
        queryFunctions = new StringBuffer();
        imports = new TreeMap<String, Set<String>>();

        if (queryWriter != null) {
            throw new RuntimeException("Query writer should be closed!");
        }

        pathParameters = new TreeMap<String, String>();

        // Also need to create the struct for the parameters
        modelWriter.newModel(new StructDef(currentQueryName + "Params", "", null, null), "filtermodels", "models");

        // Add the entry into the applicationClient file
        clientFunction = new StringBuffer();
        clientFunction.append("func (c *Client) " + currentQueryName + "(");
    }

    private void addPathParameter(TypeDef type) {
        String gotype = goType(type.rawTypeName, type.isOfType("array"));
        String propName = Tools.getCamelCase(
                type.goPropertyName.isEmpty() ? type.propertyName : type.goPropertyName, 
                        false);
        pathParameters.put(propName, gotype);

        // client functions
        if (pathParameters.size() > 1) {
            clientFunction.append(", ");
        }
        clientFunction.append(propName + " " + gotype);
    }

    private void addQueryParameter(TypeDef type) {

        // Also need to add this to the path struct (model)
        modelWriter.newProperty(type, Annotation.URL);
        String propName = type.goPropertyName.isEmpty() ? type.propertyName : type.goPropertyName;
        String funcName = Tools.getCamelCase(propName, true);
        String paramName = Tools.getCamelCase(propName, false);
        String desc = Tools.formatCommentGo(type.doc, funcName, "");
        TypeConverter typeConv = goType(type.rawTypeName, type.isOfType("array"), 
                true, propName);


        append(queryFunctions, desc);
        append(queryFunctions, 
                "func (s *" + currentQueryName + ") " + 
                        funcName + "(" + paramName + " " + typeConv.type + ") " + 
                        "*" + currentQueryName + " {\n");
        append(queryFunctions, TAB + "s.p." + funcName + " = " + typeConv.converter + "\n");
        append(queryFunctions, TAB + "return s\n}\n\n");
    }
    private void endQuery() {

        // client functions
        clientFunction.append(") *" + currentQueryName + " {\n");
        clientFunction.append(TAB + "return &" + currentQueryName + "{");

        Tools.addImport("A", "context");
        if (pathParameters.size() > 0) {
            Tools.addImport("A", "fmt");
        }
        Tools.addImport("C", "github.com/algorand/go-algorand-sdk/client/v2/common");
        Tools.addImport("C", "github.com/algorand/go-algorand-sdk/client/v2/common/models");

        queryWriter = newFile(currentQueryName, filesFolder);
        append(queryWriter, 
                "package " + packageName + "\n\n" +
                "import (\n");
        append(queryWriter, Tools.getImports());
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
        append(queryWriter, TAB + "c" + spaces(formattingWidth - 1) + "*Client\n");
        if (modelWriter.modelPropertyAdded()) {
            append(queryWriter, TAB + "p" + spaces(formattingWidth - 1) + "models." + currentQueryName + "Params\n");
        }

        clientFunction.append("c: c");

        Iterator<Entry<String, String>> pps = pathParameters.entrySet().iterator();
        while(pps.hasNext()) {
            Entry<String, String> pp = pps.next();
            append(queryWriter, TAB + pp.getKey() + 
                    spaces(formattingWidth - pp.getKey().length()) + pp.getValue() + "\n");
            clientFunction.append(", " + pp.getKey() + ": " + pp.getKey());
        }
        append(queryWriter, "}\n\n");

        // client functions
        clientFunction.append("}\n}\n\n");

        clientFunctions.put(currentQueryName, 
                Tools.formatCommentGo(pathDesc, "", "") + clientFunction.toString());

        append(queryWriter, queryFunctions.toString());
        append(queryWriter, getDoFunction());
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

    public static void append(StringBuffer sb, String text) {
        sb.append(text);
    }

    public static void append(BufferedWriter sb, StringBuffer text) {
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

}

final class ModelWriter {
    // modelWriter writes all the response and other structures into a single file
    private BufferedWriter modelWriter;

    // currentModelBuffer holds the model code as it is constructed
    // used for skipping models with no parameters. 
    private StringBuffer currentModelBuffer;

    // pendingOpenStruct indicates if a struct is not closed yet, 
    // expecting more parameters. This is useful to do away with the 
    // end call. The end call is implicit at the time of a new struct or 
    // at the time of terminate. 
    private boolean pendingOpenStruct;


    // Indicates if any property is added to this model
    // used for skipping models with no parameters.  
    private boolean modelPropertyAdded;

    private String className;

    // property name is the key
    private TreeMap<String, TypeDef> properties;

    HashMap<String, Set<String>> imports;


    private JavaGenerator javagen;
    private String folder;
    private String filename;

    public ModelWriter(JavaGenerator javagen, String folder) {
        currentModelBuffer = null;
        pendingOpenStruct = false;
        this.javagen = javagen;
        this.folder = folder;
        this.filename = "";
    }

    public void close () {
        if (pendingOpenStruct) {
            currentModelBuffer.append("}\n");
        }
        pendingOpenStruct = false;

        JavaGenerator.append(modelWriter, Tools.getImports(imports));

        writeCompareMethod(className, body, properties);

        bw.append(body);
        bw.append("}\n");
        bw.close();


        if (modelPropertyAdded) {
            JavaGenerator.append(modelWriter, currentModelBuffer);
        }
        modelPropertyAdded = false;

        if (modelWriter != null) {
            JavaGenerator.closeFile(modelWriter);
        }
        modelWriter = null;
    }

    public boolean modelPropertyAdded() {
        return modelPropertyAdded;
    }

    public void newProperty(TypeDef type) {
        modelPropertyAdded = true;
        properties.put(Tools.getCamelCase(type.propertyName, true), type);
    }

    // newModel can write into one file at a time.
    // This is a limitation, but there is no need for more.
    // At any time, there can be one currentModelBuffer, and one modelWriter
    public void newModel(StructDef sDef, String filename, String packageName) {
        if (filename.compareTo(this.filename) != 0) {
            this.close();
        }
        if (pendingOpenStruct) {
            JavaGenerator.append(currentModelBuffer, "}\n\n");
        }
        if (modelPropertyAdded) {
            JavaGenerator.append(modelWriter, currentModelBuffer);
        }
        if (modelWriter == null) {
            this.filename = filename;
            String className = Tools.getCamelCase(filename, true);
            modelWriter = JavaGenerator.newFile(className, folder);
            JavaGenerator.append(modelWriter, "package " + packageName + ";\n\n");

            imports = new HashMap<>();

            Tools.addImport(imports, "java.util.Objects"); // used by Objects.deepEquals

            Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.PathResponse");
            Tools.addImport(imports, "com.fasterxml.jackson.annotation.JsonProperty");

            currentModelBuffer = new StringBuffer();
            if (sDef.doc != null) {
                JavaGenerator.append(currentModelBuffer, Tools.formatComment(sDef.doc, "", true));
            }
            JavaGenerator.append(currentModelBuffer, "public class " + className + " extends PathResponse {\n\n");
        }

        pendingOpenStruct = true;
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

            if (typeObj.isOfType("array")) {
                Tools.addImport(imports, "java.util.ArrayList");
                Tools.addImport(imports, "java.util.List");
            }

            String desc = null;
            if (typeObj.doc != null) {
                desc = typeObj.doc;
                desc = Tools.formatComment(desc, JavaGenerator.TAB + "", true);
            }

            // public type
            if (desc != null) buffer.append(desc);
            buffer.append(getPropertyWithJsonSetter(typeObj, javaName));
            buffer.append("\n");
        }
    }


    // getPropertyWithJsonSetter formats the property into java declaration type with 
    // the appropriate json annotation.
    static String getPropertyWithJsonSetter(TypeDef typeObj, String javaName){
        StringBuilder buffer = new StringBuilder();
        String jprop = typeObj.propertyName;

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
            buffer.append(JavaGenerator.TAB + "@JsonProperty(\"" + jprop + "\")\n");
            buffer.append(JavaGenerator.TAB + "public " + typeObj.javaTypeName + " " + javaName);
            if (typeObj.isOfType("array")) {
                buffer.append(" = new Array" + typeObj.javaTypeName + "()");
            }
            buffer.append(";\n");
        }
        return buffer.toString();
    }


    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getEnum(JsonNode prop, String propName, String goPropertyName) {
        JsonNode enumNode = prop.get("enum");
        if (enumNode == null) {
            throw new RuntimeException("Cannot find enum info in node: " + prop.toString());
        }
        StringBuilder sb = new StringBuilder();
        String enumClassName = Tools.getCamelCase(propName, true);
        sb.append(TAB + "public enum " + enumClassName + " {\n");

        Iterator<JsonNode> elmts = enumNode.elements();
        List<String> enumValues = new ArrayList<>();
        while(elmts.hasNext()) {
            String val = elmts.next().asText();
            enumValues.add(val);
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

        TypeDef td = new TypeDef(enumClassName, prop.get("type").asText(),
                sb.toString(), "enum", propName, goPropertyName, desc, isRequired(prop));
        td.enumValues = enumValues;
        return td;
    }


    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getBase64EncodedArray(String propName, String goPropertyName, String rawType,
            boolean forModel, String desc, boolean required) {
        if (forModel == false) {
            throw new RuntimeException("array of byte[] cannot yet be used in a path or path query.");
        }

        String javaName = Tools.getCamelCase(propName, false);
        StringBuilder sb = new StringBuilder();

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
        return new TypeDef("", "binary", sb.toString(), "getterSetter,array", 
                propName, goPropertyName, desc, required);
    }

    // Get base64 encoded byte[] type.
    // It provides the special getter/setter needed for this type
    static TypeDef getBase64Encoded(String propName, String goPropertyName, String rawType,
            boolean forModel, String desc, boolean required) {
        String javaName = Tools.getCamelCase(propName, false);
        StringBuilder sb = new StringBuilder();
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
        return new TypeDef("byte[]", "binary", "getterSetter", 
                propName, goPropertyName, desc, required);
    }

    // Get TypeDef for Address type. 
    // It provides the special getter/setter needed for this type.
    static TypeDef getAddress(String propName, String goPropertyName,
            boolean forModel, String desc, boolean required) {

        StringBuilder sb = new StringBuilder();
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
        return new TypeDef("Address", "address", sb.toString(), "getterSetter", propName, 
                goPropertyName, desc, required);
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

        requestMethod.append(OpenApiParser.getQueryResponseMethod(returnType));
        requestMethod.append("    protected QueryData getRequestString() {\n");
        boolean pAdded = false;
        boolean addFormatMsgpack = false;

        while (properties != null && properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String propName = Tools.getCamelCase(prop.getKey(), false);
            String setterName = Tools.getCamelCase(prop.getKey(), false);
            TypeDef propType = getType(prop.getValue(), true, prop.getKey(), false);

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
            JsonNode returnTypeNode = this.getFromRef(returnType);
            if (returnTypeNode.get("schema").get("$ref") != null) {
                returnType = OpenApiParser.getTypeNameFromRef(returnTypeNode.get("schema").get("$ref").asText());
            } else {
                returnType = OpenApiParser.getTypeNameFromRef(returnType);
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

        Map<String, Set<String>> imports = new HashMap<String, Set<String>>();
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Client");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.HttpMethod");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Query");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.QueryData");
        Tools.addImport(imports, "com.algorand.algosdk.v2.client.common.Response");
        if (needsClassImport(returnType.toLowerCase())) {
            Tools.addImport(imports, modelPkg + "." + returnType);
        }

        generatedPathsEntry.append(Tools.formatComment(discAndPath, TAB, true));

        generatedPathsEntry.append("    public " + className + " " + methodName + "(");
        String [] strarray = {className, returnType, path, desc, httpMethod};
        this.publisher.publish(Events.NEW_QUERY, strarray);

        String queryParamsCode = processQueryParams(
                generatedPathsEntry,
                properties,
                className,
                path,
                returnType,
                httpMethod,
                imports);

        if (legacyMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(Tools.formatComment(discAndPath, "", true));
            sb.append("public class " + className + " extends Query {\n\n");
            sb.append(queryParamsCode);
            sb.append("\n}");

            BufferedWriter bw = getFileWriter(className, directory);
            bw.append("package " + pkg + ";\n\n");
            bw.append(Tools.getImports(imports));
            bw.append(sb);
            bw.close();
        }

        publisher.publish(Events.END_QUERY);
    }


    // Writes the compare methods by adding a comparator for each class member. 
    static void writeCompareMethod(String className, StringBuilder buffer, Iterator<Entry<String, JsonNode>> properties) {
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


    // Generate all the enum classes in the spec file. 
    public void generateEnumClasses (JsonNode root, String rootPath, String pkg) throws IOException {
        if (javaMode) {
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
                    TypeDef enumType = getEnum(cls.getValue(), cls.getKey(), "");
                    bw.append(enumType.def);
                    bw.append("\n");
                }
            }
            bw.append("}\n");
            bw.close();
        }
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
}
