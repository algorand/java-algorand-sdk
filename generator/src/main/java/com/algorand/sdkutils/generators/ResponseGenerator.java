package com.algorand.sdkutils.generators;

import com.algorand.algosdk.account.Account;
import com.algorand.sdkutils.Main;
import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Subscriber;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseGenerator implements Subscriber {
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

        @com.beust.jcommander.Parameter(required = true, names = {"-o", "--output-dir"}, description = "Location to place response objects.")
        File outputDirectory;

        @Override
        public void validate(JCommander command) {
            super.validate(command);

            if (!this.outputDirectory.isDirectory()) {
                throw new RuntimeException("Output directory must be a valid directory: " + this.outputDirectory.getAbsolutePath());
            }
        }
    }

    /////////////////////////////

    private final ResponseGeneratorArgs args;

    // Used to build up a representation of the types defined by the spec.
    private HashMap<StructDef, List<TypeDef>> responses = new HashMap<>();
    private HashMap<StructDef, List<TypeDef>> models = new HashMap<>();
    private List<TypeDef> activeList = null;

    private ObjectMapper mapper = new ObjectMapper();
    private Random random = new Random();
    private String randomString = "oZSrqon3hT7qQqrHNUle"; // This string was generated randomly from random.org/strings

    public ResponseGenerator(ResponseGeneratorArgs args, Publisher publisher) {
        this.args = args;
        publisher.subscribeAll(this);
    }

    private void export(StructDef def, List<TypeDef> properties) {
        // Export the object.
        System.out.println("Print out a generated " + def.name);

        List<ObjectNode> nodes = getObject(def, properties);

        try {
            for (ObjectNode node : nodes) {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
                System.out.println(json);
            }
        } catch (JsonProcessingException e) {
            System.err.println("An exception occurred parsing JSON object for: " + def.name + "\n\n\n\n");

            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Generate one or more fully defined ObjectNode's for the provided object.
     *
     * If the object has mutually exclusive fields, multiple objects will be generated. One with each of the options.
     */
    public List<ObjectNode> getObject(StructDef def, List<TypeDef> properties) {
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

    /**
     * Generate a node containing randomized property data. If the node is a nested object there may be multiple
     * representations of the data.
     */
    public List<JsonNode> getData(StructDef parent, TypeDef prop) {
        // Hook into an interesting spot...
        /*
        if (!parent.mutuallyExclusiveProperties.isEmpty()) {
            System.out.println("here");
        }
         */
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
        if (prop.rawTypeName.equals("binary")) {
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

        System.out.println("Looking up reference for: " + prop.rawTypeName);
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
        System.out.println("Found one reference for: " + lookup.getKey().name);

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
        findEntry(args.filter, false)
                .forEach(entry -> export(entry.getKey(), entry.getValue()));
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

    @Override
    public void onEvent(Publisher.Events event, TypeDef type) {
        if (event == Publisher.Events.NEW_PROPERTY) {
            activeList.add(type);
        } else {
            System.out.println("unhandled event: " + event);
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
            System.out.println("unhandled event: " + event);
        }
    }

    /**
     * Unused event.
     */
    @Override
    public void onEvent(Publisher.Events event) {
        //System.out.println("(event) - " + event);
    }

    /**
     * Unused event.
     */
    @Override
    public void onEvent(Publisher.Events event, String[] notes) {
        //System.out.println("(event, []notes) - " + String.join(",", notes));
    }

    /*
    // This prints out the path and all of its parameters
    private static void describeModel(OpenApi3 model) {
        System.out.printf("Title: %s\n", model.getInfo().getTitle());
        for (Path path : model.getPaths().values()) {
            System.out.printf("Path %s:\n", Overlay.of(path).getPathInParent());
            for (Operation op : path.getOperations().values()) {
                System.out.printf("  %s: [%s] %s\n", Overlay.of(op).getPathInParent().toUpperCase(),
                        op.getOperationId(), op.getSummary());
                for (Parameter param : op.getParameters()) {
                    System.out.printf("    %s[%s]:, %s - %s\n", param.getName(), param.getIn(), getParameterType(param),
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
