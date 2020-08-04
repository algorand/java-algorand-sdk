package com.algorand.sdkutils.listeners;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.util.Encoder;
import com.algorand.sdkutils.Main;
import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseGenerator implements Subscriber {
    protected static final Logger logger = LogManager.getLogger();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(ResponseGeneratorArgs args, JCommander command) throws Exception {
        // kaizen parser test
        //OpenApi3 model = new OpenApi3Parser().parse(args.specfile, true);
        //describeResponses(model);

        JsonNode root;
        try (FileInputStream fis = new FileInputStream(args.specfile)) {
            root = Utils.getRoot(fis);
        }

        Publisher pub = new Publisher();
        ResponseGenerator subscriber = new ResponseGenerator(args, pub);
        OpenApiParser parser = new OpenApiParser(root, pub);
        parser.parse();
    }

    @com.beust.jcommander.Parameters(commandDescription = "Generate response test file(s).")
    public static class ResponseGeneratorArgs extends Main.CommonArgs {
        @com.beust.jcommander.Parameter(names = {"-f", "--filter"}, description = "Only generate response files for objects which contain the given value as a substring.")
        public String filter;

        @com.beust.jcommander.Parameter(names = {"-l", "--list"}, description = "List types which could be generated instead of generating them.")
        boolean list;

        @com.beust.jcommander.Parameter(names = {"-o", "--output-dir"}, description = "Location to place response objects.")
        File outputDirectory;

        @com.beust.jcommander.Parameter(names = {"-p", "--prefix"}, description = "Prefix to resulting response files.")
        String prefix;

        @Override
        public void validate(JCommander command) {
            super.validate(command);

            // list mode validation
            if (list) {
                if(this.outputDirectory != null) {
                    throw new RuntimeException("Illegal paremeter combination: do not combine '--list' with '--output-dir'.");
                }
            }
            // generate mode validation
            else {
                if (!this.outputDirectory.isDirectory()) {
                    throw new RuntimeException("Output directory must be a valid directory: " + this.outputDirectory.getAbsolutePath());
                }
            }
        }
    }

    /////////////////////////////

    private final ResponseGeneratorArgs args;

    // Used to build up a representation of the types defined by the spec.
    private HashMap<StructDef, List<TypeDef>> responses = new HashMap<>();
    private HashMap<StructDef, List<TypeDef>> models = new HashMap<>();
    private List<TypeDef> activeList = null;
    private List<QueryDef> queries = new ArrayList<>();

    private Random random = new Random();
    private String randomString = "oZSrqon3hT7qQqrHNUle"; // This string was generated randomly from random.org/strings
    private List<byte[]> signedTransactions = new ArrayList<>();

    private static class ExportType {
        final QueryDef query;
        final StructDef struct;
        final List<TypeDef> properties;

        public ExportType(QueryDef query, StructDef struct, List<TypeDef> properties) {
            this.query = query;
            this.struct = struct;
            this.properties = properties;
        }
    }

    private InputStream getResourceAsStream(ClassLoader loader, String resource) {
        final InputStream in = loader.getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    public ResponseGenerator(ResponseGeneratorArgs args, Publisher publisher) {
        this.args = args;
        publisher.subscribeAll(this);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = "stxn";

        // initialize signed transactions
        try (
                InputStream in = getResourceAsStream(loader, path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String filename;

            while ((filename = br.readLine()) != null) {
                // process resource file
                InputStream fis = getResourceAsStream (loader, path + "/" + filename);
                byte[] data = new byte[fis.available()];
                fis.read(data);
                this.signedTransactions.add(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize transactions.");
        }
    }

    private void export(ExportType export) {
        // Export the object.
        List<ObjectNode> nodes = getObject(export.struct, export.properties);

        try (Stream<Path> existing = Files.list(args.outputDirectory.toPath())){
            List<Path> existingFiles = existing.collect(Collectors.toList());

            String prefix = args.prefix + "_" + export.struct.name + "_";

            // See how many files are already there to initialize the count.
            long num = existingFiles.stream()
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .count();

            // Write the files.
            for (ObjectNode node : nodes) {
                String extension = "";
                String data = "";
                if (export.query.contentType != null && export.query.contentType.stream().anyMatch(s -> s.contains("msgp"))) {
                    extension = ".base64";
                    data = Encoder.encodeToBase64(Encoder.encodeToMsgPack(node));
                } else {
                    extension = ".json";
                    data = Encoder.encodeToJson(node);
                }
                Path output = new File(args.outputDirectory, prefix + (num++) + extension).toPath();

                logger.info("Exporting example of %s to: %s", export.struct.name, output.getFileName());
                Files.write(output, data.getBytes());
            }
        } catch (JsonProcessingException e) {
            System.err.println("An exception occurred parsing JSON object for: " + export.struct.name + "\n\n\n\n");

            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An exception occurred checking output directory: " + export.struct.name + "\n\n\n\n");

            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Generate one or more fully defined ObjectNode's for the provided object.
     *
     * If the object has mutually exclusive fields, multiple objects will be generated. One with each of the options.
     */
    private List<ObjectNode> getObject(StructDef def, List<TypeDef> properties) {
        List<ObjectNode> nodes = new ArrayList<>();

        // No exclusions
        if (def.mutuallyExclusiveProperties.isEmpty()) {
            nodes.addAll(getObjectWithExclusions(def, properties, ImmutableList.of()));
            return nodes;
        }

        // Each exclusion combination
        for (String field : def.mutuallyExclusiveProperties) {
            List exclusions = def.mutuallyExclusiveProperties.stream()
                    .filter(f -> !f.equals(field))
                    .collect(Collectors.toList());
            nodes.addAll(getObjectWithExclusions(def, properties, exclusions));
        }
        return nodes;
    }

    private List<ObjectNode> getObjectWithExclusions(StructDef def, List<TypeDef> properties, List<String> exclusions) {
        List<ObjectNode> nodes = new ArrayList<>();
        nodes.add(mapper.createObjectNode());
        for (TypeDef prop : properties) {
            // Skip properties in the exclusion list
            if (!exclusions.contains(prop.propertyName)) {
                List<JsonNode> propertyNodes = new ArrayList<>();

                boolean isArray = prop.isOfType("array");
                if (isArray) {
                    // TODO: Array size as part of format.
                    int num = random.nextInt(10) + 1;
                    ArrayNode arr = mapper.createArrayNode();
                    while (arr.size() < num) {
                        // Add all because 'getData' may return more than one value.
                        arr.addAll(getData(def, prop));
                    }
                    propertyNodes.add(arr);
                    //node.set(prop.propertyName, arr);
                } else {
                    propertyNodes.addAll(getData(def, prop));
                    //node.set(prop.propertyName, getData(def, prop));
                }

                // If there are multiple properties, fan out the result object.
                while (nodes.size() < propertyNodes.size()) {
                    ObjectNode object = nodes.get(0);
                    nodes.add(object.deepCopy());
                }

                // Copy the propertyNodes into the results. In some cases there will just be one new property node,
                // it should be copied into each of the result nodes.
                for (int i = 0; i < nodes.size(); i++) {
                    nodes.get(i).set(prop.propertyName, propertyNodes.get(i % propertyNodes.size()));
                }
            }
        }
        return nodes;
    }

    public JsonNode makeSignedTransactionNode() {
        byte[] randomTxn = signedTransactions.get(random.nextInt(signedTransactions.size()));
        return new BinaryNode(randomTxn);
    }

    /**
     * Generate a node containing randomized property data. If the node is a nested object there may be multiple
     * representations of the data.
     */
    private List<JsonNode> getData(StructDef parent, TypeDef prop) {
        if (prop.enumValues != null) {
            int idx = random.nextInt(prop.enumValues.size());
            return ImmutableList.of(new TextNode(prop.enumValues.get(idx)));
        }
        if (prop.rawTypeName.equals("string")) {
            // TODO: String format from spec.
            return ImmutableList.of(new TextNode(randomString));
        }
        if (prop.rawTypeName.equals("integer")) {
            // TODO: Int range from spec.
            return ImmutableList.of(new IntNode(random.nextInt(1000000) + 1));
        }
        if (prop.rawTypeName.equals("boolean")) {
            return ImmutableList.of(BooleanNode.valueOf(random.nextBoolean()));
        }
        if (prop.rawTypeName.equals("binary") || prop.rawTypeName.equals("byte")) {
            // TODO: Binary data limits from spec.
            byte[] data = new byte[random.nextInt(500) + 1];
            random.nextBytes(data);
            return ImmutableList.of(new BinaryNode(data));
        }
        if (prop.rawTypeName.equals("address")) {
            try {
                String account = new Account().getAddress().toString();
                return ImmutableList.of(new TextNode(account));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to generate account.");
            }
        }
        if (prop.rawTypeName.equals("object")) {
            //throw new RuntimeException("Not supported, nested objects must be handled with $ref types.");
            // TODO: proper object handling? There could be nested properties but our parser isn't currently handling that.
            ObjectNode node = mapper.createObjectNode();
            node.put("today", "today, 'object' is a string!");
            return ImmutableList.of(node);
        }
        if (prop.rawTypeName.equals("SignedTransaction")) {
            return ImmutableList.of(makeSignedTransactionNode());
        }

        List<Map.Entry<StructDef, List<TypeDef>>> matches = findEntry(prop.rawTypeName, true);

        if (matches.size() == 0) {
            throw new RuntimeException("Unable to find reference: " + prop.rawTypeName);
        }
        if (matches.size() > 1) {
            throw new RuntimeException("Ambiguous reference '"+prop.rawTypeName+"', found multiple definitions: " +
                    matches.stream()
                            .map(e -> e.getKey().name)
                            .collect(Collectors.joining(", ")));
        }

        Map.Entry<StructDef, List<TypeDef>> lookup = matches.iterator().next();

        // Generate objects and cast to JsonNode.
        return getObject(lookup.getKey(), lookup.getValue()).stream()
                .map(node -> (JsonNode)node)
                .collect(Collectors.toList());
    }

    /**
     * Called after parsing has completed.
     */
    @Override
    public void terminate() {
        // Flatten aliases
        findEntry(args.filter, false)
                .forEach(entry -> {
                    String aliasOf = entry.getKey().aliasOf;

                    if (StringUtils.isNotEmpty(aliasOf)) {
                        // Lookup the real object to generate the response file.
                        List<Map.Entry<StructDef, List<TypeDef>>> entries = findEntry(aliasOf, true);
                        if (entries.size() != 1) {
                            logger.error("Failed to find single alias for '%s'.", aliasOf);
                            return;
                        }
                        entry.setValue(entries.get(0).getValue());
                    }
                });

        filterQueries(args.filter, false)
                .forEach(entry -> {
                    // list mode
                    if(args.list) {
                        System.out.println("* " + entry.struct.name);
                    }
                    // generate mode
                    else {
                        try {
                            export(entry);
                        } catch (Exception e) {
                            logger.error("Unable to generate: %s", entry.struct);
                            e.printStackTrace();
                        }
                    }
                });
        /*
        findEntry(args.filter, false)
                .forEach(entry -> {
                    // list mode
                    if(args.list) {
                        System.out.println("* " + entry.getKey().name);
                    }
                    // generate mode
                    else {
                        try {
                            export(entry.getKey(), entry.getValue());
                        } catch (Exception e) {
                            logger.error("Unable to generate: %s", entry.getKey());
                            e.printStackTrace();
                        }
                    }
                });
         */
    }

    /**
     *
     * @param name
     * @param strict when in strict mode the name must be an exact case sensitive match.
     * @return
     */
    private List<Map.Entry<StructDef, List<TypeDef>>> findEntry(String name, boolean strict) {
        return Stream.of(responses, models)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .filter(entry -> {
                    if (StringUtils.isNotEmpty(name)) {
                        if (strict) {
                            return entry.getKey().name.equals(name);
                        } else {
                            return entry.getKey().name.contains(name);
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<ExportType> filterQueries(String name, boolean strict) {
        return queries.stream()
                .filter(q -> {
                    if (StringUtils.isNotEmpty(name)) {
                        if (strict) {
                            return q.returnType.equals(name);
                        } else {
                            return q.returnType.contains(name);
                        }
                    }
                    // no filter.
                    return true;
                })
                .flatMap(q -> findEntry(q.returnType, true).stream()
                        .map(entry -> new ExportType(q, entry.getKey(), entry.getValue()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public void onEvent(Publisher.Events event, TypeDef type) {
        if (event == Publisher.Events.NEW_PROPERTY) {
            activeList.add(type);
        } else {
            logger.info("unhandled event: %s", event);
        }
    }

    @Override
    public void onEvent(Publisher.Events event, StructDef sDef) {
        activeList = new ArrayList<>();
        if (event == Publisher.Events.NEW_MODEL) {
            models.put(sDef, activeList);
        } else if (event == Publisher.Events.NEW_RETURN_TYPE){
            responses.put(sDef, activeList);
        } else {
            logger.info("unhandled event: %s", event);
        }
    }

    /**
     * Collect query events.
     */
    @Override
    public void onEvent(Publisher.Events event, QueryDef query) {
        if (event == Publisher.Events.NEW_QUERY) {
            this.queries.add(query);
        }
        logger.info("Unhandled event (event, []notes) - %s", query.toString());
    }

    /**
     * Unused event.
     */
    @Override
    public void onEvent(Publisher.Events event) {
        logger.info("unhandled event: %s", event);
    }

    /*
    // This prints out the path and all of its parameters
    private static void describeModel(OpenApi3 model) {
        logger.info("Title: %s\n", model.getInfo().getTitle());
        for (Path path : model.getPaths().values()) {
            logger.info("Path %s:\n", Overlay.of(path).getPathInParent());
            for (Operation op : path.getOperations().values()) {
                logger.info("  %s: [%s] %s\n", Overlay.of(op).getPathInParent().toUpperCase(),
                        op.getOperationId(), op.getSummary());
                for (Parameter param : op.getParameters()) {
                    logger.info("    %s[%s]:, %s - %s\n", param.getName(), param.getIn(), getParameterType(param),
                            param.getDescription());
                }
            }
        }
    }

    private static String getParameterType(Parameter param) {
        Schema schema = param.getSchema();
        return schema != null ? schema.getType() : "unknown";
    }
    */


}
