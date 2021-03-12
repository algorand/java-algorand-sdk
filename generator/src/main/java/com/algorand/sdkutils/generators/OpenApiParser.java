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
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.Tools;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenApiParser {
    protected static final Logger logger = LogManager.getLogger();

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

        logger.debug("Parsing definitions.");
        this.generateAlgodIndexerObjects(root);

        // Generate classes from the return types which have more than one return element
        logger.debug("Parsing responses.");
        this.generateReturnTypes(root);

        // Generate the algod methods
        logger.debug("Parsing paths.");
        this.generateQueryMethods();

        publisher.terminate();
    }

    static String getTypeNameFromRef(JsonNode ref) {
        if (ref == null) return null;
        StringTokenizer st = new StringTokenizer(ref.asText(), "/");
        String ans = "";
        while (st.hasMoreTokens()) {
            ans = st.nextToken();
        }
        return ans;
    }

    // Get array type of base64 encoded bytes.
    // It provides the special getter/setter needed for this type
    static TypeDef getEnum(JsonNode prop, String propName, String goPropertyName, String openApiType, String openApiArrayType, String openApiFormat, String openApiAlgorandFormat, String openApiGoName) {
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

        String type = prop.get("type").asText();
        TypeDef td = new TypeDef(enumClassName, type, "enum", propName, goPropertyName, desc, isRequired(prop), null, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, openApiGoName);
        td.enumValues = enumValues;
        td.openApiType = type;
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

        // This is an OpenAPI 3.0 thing, no plans for real OpenAPI 3.0 support at the moment.
        if (refNode == null && prop.get("schema") != null) {
            refNode = prop.get("schema").get("$ref");
        }
        String refType = getTypeNameFromRef(refNode);

        // Handle reference type
        if (refNode != null) {
            // Need to check here if this type does not have a class of its own
            // No C/C++ style typedef in java, and this type could be a class with no properties
            prop = getFromRef(refNode.asText());
            if (desc.isEmpty()) {
                desc = prop.get("description") == null ? "" : prop.get("description").asText();
            }
            // TODO: Why does this need to be handled outside the main switch below?
            if (hasProperties(prop)) {
                return new TypeDef(refType, refType, "", propName, goName, desc, isRequired(prop), refType, null, null, null, null, goName);
            }
        }

        JsonNode typeNode = prop.get("type") != null ? prop : prop.get("schema");
        String format = getTypeFormat(typeNode, propName);
        String openApiType = typeNode.get("type").asText();
        String openApiFormat = typeNode.has("format") ? typeNode.get("format").asText() : null;
        String openApiAlgorandFormat = typeNode.has("x-algorand-format") ? typeNode.get("x-algorand-format").asText() : null;
        JsonNode arrayTypeNode = typeNode.get("items");
        String openApiArrayType = arrayTypeNode != null && arrayTypeNode.has("type") ? arrayTypeNode.get("type").asText() : null;

        if (prop.get("enum") != null) {
            return getEnum(prop, propName, goName, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        }

        if (!format.isEmpty() ) {
            switch (format) {
            case "uint64":
                return new TypeDef("java.math.BigInteger", openApiType, "", propName, goName, desc, isRequired(prop), null, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
            case "RFC3339 String":
                return new TypeDef("Date", "time", "", propName, goName, desc, isRequired(prop), null, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
            case "Address":
                return new TypeDef("Address", "address", "getterSetter", propName,
                        goName, desc, isRequired(prop), null, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);

            case "SignedTransaction":
                return new TypeDef("SignedTransaction", format, "", propName, goName, desc, isRequired(prop), null, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
            case "binary":
            case "byte":
            case "base64":
            case "digest":
            case "TEALProgram":
                if (openApiType.contentEquals("array")) {
                    String rawType = prop.get("items").get("type").asText();
                    if (forModel == false) {
                        throw new RuntimeException("array of byte[] cannot yet be used in a path or path query.");
                    }

                    if (format.equals("byte")) {
                        rawType = format;
                    }

                    // getterSetter typeName is only used in path.
                    return new TypeDef("", rawType, "getterSetter,array",
                            propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
                } else {
                    return new TypeDef("byte[]", "binary", "getterSetter",
                            propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
                }
            case "AccountID":
                break;
            case "BlockCertificate":
            case "BlockHeader":
                return new TypeDef("HashMap<String,Object>", openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
            }
        }
        switch (openApiType) {
        case "integer":
            String longName = asObject ? "Long" : "long";
            return new TypeDef(longName, openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        case "object":
            return new TypeDef("HashMap<String,Object>", openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        case "string":
            return new TypeDef("String", openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        case "boolean":
            String boolName = asObject ? "Boolean" : "boolean";
            return new TypeDef(boolName, openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        case "array":
            // Resolve references
            TypeDef typeName = getType(arrayTypeNode, asObject, propName, forModel);
            String oldArrayType = openApiType;
            if (typeName.isOfType("getterSetter")) {
                oldArrayType += ",getterSetter";
            }
            String resolvedArrayType = typeName.openApiType;
            String resolvedArrayFormat = typeName.openApiFormat;
            String resolvedAlgoFormat = typeName.openApiAlgorandFormat;
            if (StringUtils.isNotEmpty(typeName.openApiRefType)) {
                resolvedArrayType = typeName.openApiRefType;
                // TODO: is this needed?
                //resolvedArrayFormat = ???
                //resolvedAlgoFormat = typeName.openApiRefType;
            }
            return new TypeDef("List<" + typeName.javaTypeName + ">", typeName.rawTypeName,
                    oldArrayType, propName, goName, desc, isRequired(prop),
                    refType, openApiType, resolvedArrayType, openApiFormat, resolvedAlgoFormat, goName);
        default:
            return new TypeDef(openApiType, openApiType, "", propName, goName, desc, isRequired(prop), refType, openApiType, openApiArrayType, openApiFormat, openApiAlgorandFormat, goName);
        }
    }

    // getTypeFormat returns the additional type formatting information
    // There could be multiple such tags in the spec file. This method knows which
    // one is relevant here.
    public static String getTypeFormat(JsonNode typeNode, String propName) {
        if (typeNode == null) {
            return null;
        }
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
    static Map<String, JsonNode> getSortedProperties(JsonNode properties) {
        Iterator<Entry<String, JsonNode>> props = properties.fields();
        TreeMap<String, JsonNode> propMap = new TreeMap<String, JsonNode>();
        while (props.hasNext()) {
            Entry<String, JsonNode> e = props.next();
            propMap.put(e.getKey(), e.getValue());
        }
        return propMap;
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
        logger.debug("Generating ... {}", className);

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

        List<TypeDef> properties = new ArrayList<>();
        for (Map.Entry<String, JsonNode> prop : getSortedProperties(propertiesNode).entrySet()) {
            String jprop = prop.getKey();
            TypeDef typeObj = getType(prop.getValue(), true, jprop, true);
            properties.add(typeObj);
        }

        publisher.publish(event, new StructDef(className, desc, properties, requiredProperties, mutuallyExclusiveProperties));
        properties.forEach(p -> publisher.publish(Events.NEW_PROPERTY, p));
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
                    propType.isOfType("enum");
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
                returnType = OpenApiParser.getTypeNameFromRef(returnTypeNode.get("schema").get("$ref"));
            } else {
                returnType = OpenApiParser.getTypeNameFromRef(spec.get("responses").get("200").get("$ref"));
                returnType = Tools.getCamelCase(returnType, true);
            }
        }
        String desc = "";
        if (spec.has("description")) {
            desc = spec.get("description").asText();
        } else if (spec.has("summary")) {
            desc = spec.get("summary").asText();
        }
        logger.debug("Generating ... {}", className);
        Iterator<Entry<String, JsonNode>> properties = null;
        if ( paramNode != null) {
            properties = getSortedParameters(paramNode);
        }

        List<String> contentTypes = new ArrayList<>();
        if (spec.has("produces") && spec.get("produces").isArray()) {
            for (JsonNode value : spec.get("produces")) {
                if (value.isTextual()) {
                    contentTypes.add(value.asText());
                } else {
                    throw new RuntimeException("Unexpected content type: " + value.toString());
                }
            }
        }
        this.publisher.publish(Events.NEW_QUERY, new QueryDef(methodName, returnType, path, desc, httpMethod, contentTypes));

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
                    writeClass(cls.getKey(), cls.getValue(), cls.getValue().get("properties"), desc, Events.NEW_MODEL);
                    publisher.publish(Events.END_MODEL);
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
                        String realType = getTypeNameFromRef(rSchema.get("$ref"));
                        publisher.publish(Events.REGISTER_RETURN_TYPE, new StructDef(rtype.getKey(), realType));
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
                            desc, Events.REGISTER_RETURN_TYPE);
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
