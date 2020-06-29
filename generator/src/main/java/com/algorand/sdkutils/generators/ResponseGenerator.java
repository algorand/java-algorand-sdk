package com.algorand.sdkutils.generators;

import com.algorand.algosdk.account.Account;
import com.algorand.sdkutils.Main;
import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Subscriber;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseGenerator implements Subscriber {
    @Parameters(commandDescription = "Generate response test file(s).")
    public static class ResponseGeneratorArgs extends Main.CommonArgs {
        @Parameter(names = {"-f", "--filter"}, description = "Only generate response files for objects which contain the given value as a substring.")
        public String filter;

        @Parameter(required = true, names = {"-o", "--output-dir"}, description = "Location to place response objects.")
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

        ObjectNode node = getObject(def, properties);

        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            System.err.println("An exception occurred parsing JSON object for: " + def.name + "\n\n\n\n");

            e.printStackTrace();
            System.exit(1);
        }
    }

    public ObjectNode getObject(StructDef def, List<TypeDef> properties) {
        ObjectNode node = mapper.createObjectNode();
        for (TypeDef prop : properties) {
            boolean isArray = prop.isOfType("array");
            if (isArray) {
                // TODO: Array size as part of format.
                int num = random.nextInt(10) + 1;
                ArrayNode arr = mapper.createArrayNode();
                for (int i = 0; i < num; i++) {
                    arr.add(getData(prop));
                }
                node.set(prop.propertyName, arr);
            } else {
                node.set(prop.propertyName, getData(prop));
            }
        }
        return node;
    }

    private final static List<String> binaryTypes = ImmutableList.of("binary", "byteArray");
    public JsonNode getData(TypeDef prop) {
        if (prop.doc.contains("transaction signature")) {
            System.out.println("here");
        }
        if (prop.enumValues != null) {
            int idx = random.nextInt(prop.enumValues.size());
            return new TextNode(prop.enumValues.get(idx));
        }
        if (prop.rawTypeName.equals("string")) {
            // TODO: String format from spec.
            return new TextNode(randomString);
        }
        if (prop.rawTypeName.equals("integer")) {
            // TODO: Int range from spec.
            return new IntNode(random.nextInt(1000000) + 1);
        }
        if (prop.rawTypeName.equals("boolean")) {
            return BooleanNode.valueOf(random.nextBoolean());
        }
        if (binaryTypes.contains(prop.rawTypeName)) {
            // TODO: Binary data limits from spec.
            byte[] data = new byte[random.nextInt(500) + 1];
            random.nextBytes(data);
            return new BinaryNode(data);
        }
        if (prop.rawTypeName.equals("address")) {
            try {
                String account = new Account().getAddress().toString();
                return new TextNode(account);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to generate account.");
            }
        }
        if (prop.rawTypeName.equals("object")) {
            //throw new RuntimeException("Not supported, nested objects must be handled with $ref types.");
            // TODO: proper object handling? There could be nested properties but our parser isn't currently handling that.
            return new TextNode("today, 'object' is a string!");
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
        return getObject(lookup.getKey(), lookup.getValue());
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
     *re @param strict when in strict mode the name must be an exact case sensitive match.
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

}
