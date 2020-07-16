package com.algorand.sdkutils.generators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Publisher.Events;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class OpenApiParser {
    public static final String TAB = "    ";

    protected JsonNode root;
    protected Publisher publisher;

    protected ObjectMapper mapper = new ObjectMapper();

    // List of classes which will be processed.
    // If empty, all classes will be processed.
    private HashSet<String> filterList;

    /**
     * Parse the file and drive the publisher.
     */
    public void parse() throws Exception {
        // TODO: Verify compatible OpenAPI version.

        System.out.println("Parsing definitions.");
        this.generateAlgodIndexerObjects(root);

        // Generate classes from the return types which have more than one return element
        System.out.println("Parsing responses.");
        this.generateReturnTypes(root);

        // Generate the algod methods
        System.out.println("Parsing paths.");
        this.generateQueryMethods();

        publisher.terminate();
    }

    static String getTypeNameFromRef(String ref) {
        StringTokenizer st = new StringTokenizer(ref, "/");
        String ans = "";
        while (st.hasMoreTokens()) {
            ans = st.nextToken();
        }
        return ans;
    }

    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getEnum(JsonNode prop, String propName, String goPropertyName) {
        JsonNode enumNode = prop.get("enum");
        if (enumNode == null) {
            throw new RuntimeException("Cannot find enum info in node: " + prop.toString());
        }
        String enumClassName = Tools.getCamelCase(propName, true);

        Iterator<JsonNode> elmts = enumNode.elements();
        List<String> enumValues = new ArrayList<>();

        while(elmts.hasNext()) {
            String val = elmts.next().asText();
            enumValues.add(val);
        }
        enumClassName = "Enums." + enumClassName;
        String desc = prop.get("description") == null ? "" : prop.get("description").asText();

        TypeDef td = new TypeDef(enumClassName, prop.get("type").asText(),
                "enum", propName, goPropertyName, desc, isRequired(prop));
        td.enumValues = enumValues;
        return td;
    }

    // getType returns the type fron the JsonNode
    TypeDef getType(
            JsonNode prop,
            boolean asObject,
            String propName, boolean forModel) {

        String desc = prop.get("description") == null ? "" : prop.get("description").asText();
        String goName = prop.get("x-go-name") != null ? prop.get("x-go-name").asText() : "";
        JsonNode refNode = prop.get("$ref");
        if (refNode == null && prop.get("schema") != null) {
            refNode = prop.get("schema").get("$ref");
        }
        if (refNode != null) {
            String type = getTypeNameFromRef(refNode.asText());
            // Need to check here if this type does not have a class of its own
            // No C/C++ style typedef in java, and this type could be a class with no properties
            prop = getFromRef(refNode.asText());
            if (desc.isEmpty()) {
                desc = prop.get("description") == null ? "" : prop.get("description").asText();
            }
            if (hasProperties(prop)) {
                return new TypeDef(type, type, "", propName, goName, desc, isRequired(prop));
            }
        }

        if (prop.get("enum") != null) {
            return getEnum(prop, propName, goName);
        }

        JsonNode typeNode = prop.get("type") != null ? prop : prop.get("schema");
        String type = typeNode.get("type").asText();
        String format = getTypeFormat(typeNode, propName);
        if (!format.isEmpty() ) {
            switch (format) {
            case "uint64":
                return new TypeDef("java.math.BigInteger", type, "", propName, goName, desc, isRequired(prop));
            case "RFC3339 String":
                return new TypeDef("Date", "time", "", propName, goName, desc, isRequired(prop));
            case "Address":
                return new TypeDef("Address", "address", "getterSetter", propName,
                        goName, desc, isRequired(prop));

            case "SignedTransaction":
                return new TypeDef("SignedTransaction", format, "", propName, goName, desc, isRequired(prop));
            case "binary":
            case "byte":
            case "base64":
            case "digest":
                if (type.contentEquals("array")) {
                    type = prop.get("items").get("type").asText();
                    if (forModel == false) {
                        throw new RuntimeException("array of byte[] cannot yet be used in a path or path query.");
                    }

                    if (format.equals("byte")) {
                        type = format;
                    }

                    // getterSetter typeName is only used in path.
                    return new TypeDef("", type, "getterSetter,array",
                            propName, goName, desc, isRequired(prop));
                } else {
                    return new TypeDef("byte[]", "binary", "getterSetter",
                            propName, goName, desc, isRequired(prop));
                }
            case "AccountID":
                break;
            case "BlockCertificate":
            case "BlockHeader":
                return new TypeDef("HashMap<String,Object>", type, "", propName, goName, desc, isRequired(prop));
            }
        }
        switch (type) {
        case "integer":
            String longName = asObject ? "Long" : "long";
            return new TypeDef(longName, type, "", propName, goName, desc, isRequired(prop));
        case "object":
            return new TypeDef("HashMap<String,Object>", type, "", propName, goName, desc, isRequired(prop));
        case "string":
            return new TypeDef("String", type, "", propName, goName, desc, isRequired(prop));
        case "boolean":
            String boolName = asObject ? "Boolean" : "boolean";
            return new TypeDef(boolName, type, "", propName, goName, desc, isRequired(prop));
        case "array":
            JsonNode arrayTypeNode = typeNode.get("items");
            TypeDef typeName = getType(arrayTypeNode, asObject, propName, forModel);
            if (typeName.isOfType("getterSetter")) {
                type = type + ",getterSetter";
            }
            return new TypeDef("List<" + typeName.javaTypeName + ">", typeName.rawTypeName,
                    type, propName, goName, desc, isRequired(prop));
        default:
            return new TypeDef(type, type, "", propName, goName, desc, isRequired(prop));
        }
    }

    // getTypeFormat returns the additional type formatting information
    // There could be multiple such tags in the spec file. This method knows which
    // one is relevant here.
    public static String getTypeFormat(JsonNode typeNode, String propName) {
        String format = typeNode.get("x-algorand-format") != null ? typeNode.get("x-algorand-format").asText() : "";
        String type = typeNode.get("type").asText();
        format = typeNode.get("format") != null && format.isEmpty() ? typeNode.get("format").asText() : format;
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

    // Imports are collected and organized before printed as import statements.
    // addImports adds a needed import class.
    static void addImport(Map<String, Set<String>> imports, String imp) {
        String key = imp.substring(0, imp.indexOf('.'));
        if (imports.get(key) == null) {
            imports.put(key, new TreeSet<String>());
        }
        imports.get(key).add(imp);
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

    // writeClass writes the Model class.
    // This is the root method for writing the complete class.
    void writeClass(String className,
            JsonNode parentNode,
            JsonNode propertiesNode,
            String desc,
            Events event) throws IOException {
        System.out.println("Generating ... " + className);

        // Collect any required fields for this definition.
        Set<String> requiredProperties = new HashSet<>();
        if (parentNode.has("required") && parentNode.get("required").isArray()) {
            Iterator<JsonNode> required = parentNode.get("required").elements();
            while (required.hasNext()) {
                JsonNode r = required.next();
                requiredProperties.add(r.asText());
            }
        }

        // Collect any mutually exclusive fields for this definition.
        Set<String> mutuallyExclusiveProperties = new HashSet<>();
        if (parentNode.has("mutually-exclusive") && parentNode.get("mutually-exclusive").isArray()) {
            Iterator<JsonNode> exclusive = parentNode.get("mutually-exclusive").elements();
            while (exclusive.hasNext()) {
                JsonNode r = exclusive.next();
                mutuallyExclusiveProperties.add(r.asText());
            }
        }

        publisher.publish(event, new StructDef(className, desc, requiredProperties, mutuallyExclusiveProperties));

        Iterator<Entry<String, JsonNode>> properties = getSortedProperties(propertiesNode);

        while (properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String jprop = prop.getKey();
            TypeDef typeObj = getType(prop.getValue(), true, jprop, true);
            publisher.publish(Events.NEW_PROPERTY, typeObj);
        }
    }

    static boolean isRequired(JsonNode prop) {
        if (prop.get("required") != null) {
            if (prop.get("required").isBoolean()) {
                return prop.get("required").asBoolean();
            }
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
    void processQueryParams(
            Iterator<Entry<String, JsonNode>> properties) {

        while (properties != null && properties.hasNext()) {
            Entry<String, JsonNode> prop = properties.next();
            String propName = Tools.getCamelCase(prop.getKey(), false);
            TypeDef propType = getType(prop.getValue(), true, prop.getKey(), false);

            // The parameters are either in the path or in the query

            // Populate generator structures for the in path parameters
            if (inPath(prop.getValue())) {
                if (propType.isOfType("enum")) {
                    throw new RuntimeException("Enum in paths is not supported! " + propName);
                }
                publisher.publish(Events.PATH_PARAMETER, propType);
                continue;
            }
            if (inBody(prop.getValue())) {
                publisher.publish(Events.BODY_CONTENT, propType);
            } else {
                publisher.publish(Events.QUERY_PARAMETER, propType);
            }
        }
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
            JsonNode spec,
            String path) throws IOException {

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
        String desc = "";
        if (spec.has("description")) {
            desc = spec.get("description").asText();
        } else if (spec.has("summary")) {
            desc = spec.get("summary").asText();
        }
        System.out.println("Generating ... " + className);
        Iterator<Entry<String, JsonNode>> properties = null;
        if ( paramNode != null) {
            properties = getSortedParameters(paramNode);
        }

        String [] strarray = {methodName, returnType, path, desc, httpMethod};
        this.publisher.publish(Events.NEW_QUERY, strarray);

        processQueryParams(properties);

        publisher.publish(Events.END_QUERY);
    }

    // Generate all the Indexer or algod model classes
    public void generateAlgodIndexerObjects (JsonNode root) throws IOException {
        JsonNode schemas = root.get("components") !=
                null ? root.get("components").get("schemas") : root.get("definitions");
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
                    String className = Tools.getCamelCase(cls.getKey(), true);
                    if (!filterList.isEmpty() && !filterList.contains(className)) {
                        continue;
                    }
                    writeClass(cls.getKey(), cls.getValue(), cls.getValue().get("properties"),
                            desc, Events.NEW_MODEL);
                }
    }

    // Generate all the Indexer or algod return type classes
    public void generateReturnTypes (JsonNode root) throws IOException {
        JsonNode returns = root.get("components") !=
                null ? root.get("components").get("responses") : root.get("responses");
                // If they are defined inline, there will not be a return types section.
                if (returns == null) return;
                Iterator<Entry<String, JsonNode>> returnTypes = returns.fields();
                while (returnTypes.hasNext()) {
                    Entry<String, JsonNode> rtype = returnTypes.next();
                    JsonNode rSchema = null;
                    if (rtype.getValue().has("content")) {
                        rSchema = rtype.getValue().get("content").get("application/json").get("schema");
                    } else {
                        rSchema = rtype.getValue().get("schema");
                    }
                    if (rSchema.get("$ref") != null ) {
                        // It refers to a defined class, create an alias
                        String realType = getTypeNameFromRef(rSchema.get("$ref").asText());
                        publisher.publish(Events.NEW_RETURN_TYPE, new StructDef(rtype.getKey(), realType));
                        continue;
                    }
                    String className = Tools.getCamelCase(rtype.getKey(), true);
                    if (!filterList.isEmpty() && !filterList.contains(className)) {
                        continue;
                    }
                    String desc = "";
                    if (rtype.getValue().has("description") &&
                            !rtype.getValue().get("description").asText().equals("(empty)")) {
                        desc = rtype.getValue().get("description").asText();
                    }

                    writeClass(rtype.getKey(), rtype.getValue(), rSchema.get("properties"),
                            desc, Events.NEW_RETURN_TYPE);
                }
    }

    // Generate all the path expression classes
    public void generateQueryMethods() throws IOException {
        // GeneratedPaths file
        JsonNode paths = this.root.get("paths");
        Iterator<Entry<String, JsonNode>> pathIter = paths.fields();
        while (pathIter.hasNext()) {
            Entry<String, JsonNode> path = pathIter.next();
            JsonNode privateTag = path.getValue().get("post") !=
                    null ? path.getValue().get("post").get("tags") : null;
                    if (privateTag != null && privateTag.elements().next().asText().equals("private")) {
                        continue;
                    }
                    String className = Tools.getCamelCase(
                            path.getValue().get(path.getValue().fieldNames().next()).get("operationId").asText(), true);
                    if (!filterList.isEmpty() && !filterList.contains(className)) {
                        continue;
                    }
                    writeQueryClass(path.getValue(), path.getKey());
        }
    }

    /**
     * This is used for Java mode. Deprecated because the Java specific code should be removed from the parser.
     */
    @Deprecated
    public OpenApiParser(JsonNode root) {
        this.root = root;
        this.publisher = new Publisher();
    }

    public OpenApiParser(JsonNode root, Publisher publisher) {
        this.root = root;
        this.publisher = publisher;
        this.filterList = new HashSet<String>();
    }

    public OpenApiParser(JsonNode root, Publisher publisher, HashSet<String> filterList) {
        this.root = root;
        this.publisher = publisher;
        this.filterList = filterList;

    }
}
