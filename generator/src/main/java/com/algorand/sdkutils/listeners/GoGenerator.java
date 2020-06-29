package com.algorand.sdkutils.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;

public class GoGenerator extends Subscriber {

    enum Annotation {
        JSON,
        CODEC,
        URL
    }
    
    static final String TAB = "\t";

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


    public GoGenerator(String rootFolder, String packageName, Publisher publisher) throws IOException {
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

    // Constructs the Do function, which returns the response object 
    private StringBuffer getDoFunction() {
        StringBuffer sb = new StringBuffer();
        String desc = Tools.formatCommentGo("Do performs HTTP request", "", "");
        sb.append(desc);
        sb.append("func (s *" + currentQueryName + ") Do(ctx context.Context,\n" + 
                TAB + "headers ...*common.Header) (response models." + currentQueryReturn + ", err error) {\n");

        sb.append(TAB + "err = s.c." + this.httpMethod + "(ctx, &response,\n");
        sb.append(TAB + TAB + processPath());
        if (queryFunctions.length() == 0) {
            sb.append(", nil, headers)\n");
        } else {
            sb.append(", s.p, headers)\n");
        }

        sb.append(TAB + "return\n}\n");
        return sb;
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
            modelWriter.newProperty(type, Annotation.JSON);
            break;
        case QUERY_PARAMETER:
            addQueryParameter(type);
            break;
        case PATH_PARAMETER:
            addPathParameter(type);
            break;
        case BODY_CONTENT:
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
        modelWriter.newModel(new StructDef(currentQueryName + "Params", ""), "filtermodels", "models");

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

    private StringBuffer spaces (int c) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < c; i++) {
            sb.append(' ');
        }
        return sb;
    }
    private void endQuery() {

        // client functions
        clientFunction.append(") *" + currentQueryName + " {\n");
        clientFunction.append(TAB + "return &" + currentQueryName + "{");

        addImport("A", "context");
        if (pathParameters.size() > 0) {
            addImport("A", "fmt");
        }
        addImport("C", "github.com/algorand/go-algorand-sdk/client/v2/common");
        addImport("C", "github.com/algorand/go-algorand-sdk/client/v2/common/models");

        queryWriter = newFile(currentQueryName, filesFolder);
        append(queryWriter, 
                "package " + packageName + "\n\n" +
                "import (\n");
        append(queryWriter, getImports());
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

    public static BufferedWriter newFile(String filename, String folder) {
        filename = filename.substring(0,1).toLowerCase() + filename.substring(1);
        String pathName = folder + 
                File.separatorChar +
                filename +
                ".go";
        try {
            return new BufferedWriter(new FileWriter(pathName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    String goType(String type, boolean array) {
        return goType(type, array, false, "").type;
    }

    class TypeConverter {
        public String type;
        public String converter;

        public TypeConverter(String type, String converter) {
            this.type = type;
            this.converter = converter;
        }
    }

    TypeConverter goType(String type, boolean array, boolean asType, String paramName) {

        String goType = "";
        String converter = paramName;

        switch (type) {
        case "boolean":
            goType = "bool";
            break;
        case "integer":
            goType = "uint64";
            break;
        case "string":
            goType = type;
            break;        
        case "Account":
            goType = "string";
            break;
        case "address":
            goType = asType ? "types.Address" : "string";
            if (asType) {
                addImport("C", "github.com/algorand/go-algorand-sdk/types");
                converter = paramName + ".String()";
            }
            break;

        case "time":
            goType = asType ? "time.Time" : "string";
            if (asType) {
                addImport("A", "time");
                converter = paramName + ".Format(time.RFC3339)"; 
            }
            break;

        case "byteArray":
            goType = asType ? "[]byte" : "string";
            if (asType) {
                addImport("A", "encoding/base64");
                converter = "base64.StdEncoding.EncodeToString(" + paramName +")";
            }
            break;
        case "object":
        case "SignedTransaction":
            if (array) {
                goType =  "map[string]interface{}";
            } else {
                goType =  "*map[string]interface{}";
            }
            break;

        case "Asset":
        case "AssetHolding":
        case "AssetParams":
        case "AccountParticipation":
        case "Application":
        case "ApplicationLocalState":
        case "ApplicationStateSchema":
        case "ApplicationParams":
        case "BlockRewards":
        case "BlockUpgradeState":
        case "BlockUpgradeVote":
        case "MiniAssetHolding":
        case "TealValue":
        case "TealKeyValue":
        case "Transaction":
        case "TransactionAssetConfig":
        case "TransactionAssetFreeze":
        case "TransactionAssetTransfer":
        case "TransactionKeyreg":
        case "TransactionPayment":
        case "TransactionSignature":
        case "TransactionSignatureLogicsig":
        case "TransactionSignatureMultisig":
        case "TransactionSignatureMultisigSubsignature":
        case "ApplicationLocalStates":
        case "EvalDelta":
        case "DryrunState":
        case "AccountStateDelta":
        case "VersionBuild":
        case "DryrunApp":
        case "DryrunSource":
        case "DryrunTxnResult":
        case "EvalDeltaKeyValue":
        case "StaleDelta":            
            goType = type;
            break;
        default:
            goType = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";            
        }
        if (array) {
            return new TypeConverter("[]" + goType, converter);
        }
        return new TypeConverter(goType, converter);
    }

    public static String goAnnotation(String propertyName, Annotation annotation, boolean required) {
        String annType = "";
        switch (annotation) {
        case JSON:
            annType = "json";
            break;
        case URL:
            annType = "url";
            break;
        default:

        }

        String val =  "`"+annType+":\"" + propertyName;
        if (!required) {
            val = val + ",omitempty";
        }
        val = val + "\"`\n";
        return val;
    }

    // Imports are collected and organized before printed as import statements.
    // addImports adds a needed import class. 
    void addImport(String category, String imp) {
        if (imports.get(category) == null) {
            imports.put(category, new TreeSet<String>());
        }
        imports.get(category).add(imp);
    }

    // getImports organizes the imports and returns the block of import statements
    // The statements are unique, and organized. 
    StringBuffer getImports() {
        StringBuffer sb = new StringBuffer();

        Set<String> catA = imports.get("A");
        if (catA != null) {
            for (String imp : catA) {
                sb.append(TAB + "\"" + imp + "\"\n");
            }
            sb.append("\n");
        }

        Set<String> catB = imports.get("B");
        if (catB != null) {
            for (String imp : catB) {
                sb.append(TAB + "\"" + imp + "\"\n");
            }
            sb.append("\n");
        }

        Set<String> catC = imports.get("C");
        if (catC != null) {
            for (String imp : catC) {
                sb.append(TAB + "\"" + imp + "\"\n");
            }
        }
        return sb;
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

    private GoGenerator gogen;
    private String folder;
    private String filename;
    
    public ModelWriter(GoGenerator gogen, String folder) {
        currentModelBuffer = null;
        pendingOpenStruct = false;
        this.gogen = gogen;
        this.folder = folder;
        this.filename = "";
    }
    
    public void close () {
        if (pendingOpenStruct) {
            currentModelBuffer.append("}\n");
        }
        pendingOpenStruct = false;
        
        if (modelPropertyAdded) {
            GoGenerator.append(modelWriter, currentModelBuffer);
        }
        modelPropertyAdded = false;
        
        if (modelWriter != null) {
            GoGenerator.closeFile(modelWriter);
        }
        modelWriter = null;
    }
    
    public boolean modelPropertyAdded() {
        return modelPropertyAdded;
    }

    public void newProperty(TypeDef type, GoGenerator.Annotation annType) {
        modelPropertyAdded = true;
        String propName = type.goPropertyName.isEmpty() ? type.propertyName : type.goPropertyName;
        propName = Tools.getCamelCase(propName, true);
        GoGenerator.append(currentModelBuffer, "\n");
        GoGenerator.append(currentModelBuffer, Tools.formatCommentGo(type.doc, propName, GoGenerator.TAB));
        GoGenerator.append(currentModelBuffer, GoGenerator.TAB + propName + " ");
        GoGenerator.append(currentModelBuffer, gogen.goType(type.rawTypeName, type.isOfType("array")) + " ");
        GoGenerator.append(currentModelBuffer, GoGenerator.goAnnotation(type.propertyName, annType, type.required));
        if (type.propertyName.charAt(0) == 'A') {
            return;
        }
    }
    
    // newModel can write into one file at a time.
    // This is a limitation, but there is no need for more.
    // At any time, there can be one currentModelBuffer, and one modelWriter
    public void newModel(StructDef sDef, String filename, String packageName) {
        if (filename.compareTo(this.filename) != 0) {
            this.close();
        }
        if (pendingOpenStruct) {
            GoGenerator.append(currentModelBuffer, "}\n\n");
        }
        if (modelPropertyAdded) {
            GoGenerator.append(modelWriter, currentModelBuffer);
        }
        if (modelWriter == null) {
            modelWriter = GoGenerator.newFile(filename, folder);
            GoGenerator.append(modelWriter, "package " + packageName + "\n\n");
            this.filename = filename;
        }
        currentModelBuffer = new StringBuffer();
        if (sDef.doc != null) {
            GoGenerator.append(currentModelBuffer, Tools.formatCommentGo(sDef.doc, sDef.name, ""));
        }
        GoGenerator.append(currentModelBuffer, "type " + sDef.name + " struct {");
        pendingOpenStruct = true;
        modelPropertyAdded = false;
    }
}
