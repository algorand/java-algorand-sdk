package com.algorand.sdkutils.listeners;

import com.algorand.com.algorand.velocity.StringHelpers;
import com.algorand.sdkutils.Main;
import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateGenerator implements Subscriber {
    protected static final Logger logger = LogManager.getLogger(TemplateGenerator.class.getName());

    // Called by Main.main
    public static void main(TemplateGeneratorArgs args, JCommander command) throws Exception {
        JsonNode root;
        try (FileInputStream fis = new FileInputStream(args.specfile)) {
            root = Utils.getRoot(fis);
        }

        logger.log(Level.WARN, "Helllloooo");
        logger.warn( "Helllloooo");
        logger.entry();
        logger.error("Did it again!");
        //logger.warn("Starting!");
        Publisher pub = new Publisher();
        TemplateGenerator subscriber = new TemplateGenerator(args, pub);
        OpenApiParser parser = new OpenApiParser(root, pub);
        parser.parse();
    }

    @com.beust.jcommander.Parameters(commandDescription = "Generate response test file(s).")
    public static class TemplateGeneratorArgs extends Main.CommonArgs {
        @com.beust.jcommander.Parameter(required = true, names = {"-t", "--templates"}, description = "Templates directory.")
        public File templatesDirectory;

        @com.beust.jcommander.Parameter(required = true, names = {"-o", "--output-dir"}, description = "Location to place response objects.")
        File outputDirectory;

        @Override
        public void validate(JCommander command) {
            super.validate(command);

            if (!this.templatesDirectory.isDirectory()) {
                throw new RuntimeException("Templates directory must be a valid directory: " + this.templatesDirectory.getAbsolutePath());
            }
            if (!this.outputDirectory.isDirectory()) {
                throw new RuntimeException("Output directory must be a valid directory: " + this.outputDirectory.getAbsolutePath());
            }
        }
    }

    /////////////////////////////

    private HashMap<StructDef, List<TypeDef>> responses = new HashMap<>();
    private HashMap<StructDef, List<TypeDef>> models = new HashMap<>();

    private List<TypeDef> activeList = null;
    private QueryDef activeQuery = null;
    private List<QueryDef> queries = new ArrayList<>();

    private final TemplateGeneratorArgs args;

    public TemplateGenerator(TemplateGeneratorArgs args, Publisher publisher) {
        this.args =args;
        publisher.subscribeAll(this);

        // Open templates?

        // Templates:
        // - Client
        // - Query
        // - Model
    }

    private VelocityContext getContext() {
        VelocityContext context = new VelocityContext();
        context.put("str", new StringHelpers());
        return context;
    }

    private void writeModelClass(Template template) {
        for (Map.Entry<StructDef, List<TypeDef>> model : models.entrySet()) {
            VelocityContext context = getContext();

            Set<String> types = new HashSet<>();
            for (TypeDef typeDef: model.getValue()) {
                types.add(typeDef.rawTypeName);
                if (typeDef.isOfType("array")) {
                    types.add("array");
                }
            }

            // Model package
            context.put("package", "com.algorand.algosdk.v2.client.model");
            context.put("def", model.getKey());
            context.put("props", model.getValue());
            context.put("types", types);

            StringWriter writer = new StringWriter();
            template.merge( context, writer );

            // TODO: Write to files.
            System.out.println(writer.toString());
            System.out.println("yey");
        }
    }

    private void writeQueryClass(Template t) {
        for (QueryDef query : queries) {
            VelocityContext context = getContext();

            // Make it easier to loop thorugh parameters without caring what they are.
            List<TypeDef> parameters = new ArrayList<>();
            parameters.addAll(query.queryParameters);
            parameters.addAll(query.pathParameters);
            parameters.addAll(query.bodyParameters);

            List<String> pathParts = Stream.of(query.path.split("/"))
                            .filter(part -> !part.equals(""))
                            .collect(Collectors.toList());

            context.put("params", parameters);
            context.put("path", pathParts);
            context.put("package", "com.algorand.algosdk.v2.client.algod");

            context.put("q", query);

            StringWriter writer = new StringWriter();
            t.merge( context, writer );

            // TODO: Write to files.
            System.out.println("====================");
            System.out.println(query.name + ".java");
            System.out.println("====================");
            System.out.println(writer.toString());
        }

    }

    @Override
    public void terminate() {
        logger.info("Done!");

        VelocityEngine velocityEngine = new VelocityEngine();
        //velocityEngine.init();
        Properties props = new Properties();
        props.put("file.resource.loader.path", args.templatesDirectory.getAbsolutePath());
        velocityEngine.init(props);

        //Template modelTemplate = velocityEngine.getTemplate("model.vm");
        //writeModelClass(modelTemplate);

        Template queryTemplate = velocityEngine.getTemplate("query.vm");
        writeQueryClass(queryTemplate);


        // Generate stuff.
        /*
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
        */
    }

    @Override
    public void onEvent(Publisher.Events event, TypeDef type) {
        switch(event) {
            case NEW_PROPERTY:
                activeList.add(type);
                //javaModelWriter.newProperty(type);
                break;
            case QUERY_PARAMETER:
                activeQuery.queryParameters.add(type);
                break;
            case PATH_PARAMETER:
                activeQuery.pathParameters.add(type);
                break;
            case BODY_CONTENT:
                activeQuery.bodyParameters.add(type);
                break;
            default:
                logger.info("unhandled event (Events, TypeDef): {}", event);
        }
    }

    @Override
    public void onEvent(Publisher.Events event, StructDef sDef) {
        activeList = new ArrayList<>();
        switch (event) {
            case NEW_MODEL:
                models.put(sDef, activeList);
                return;
            case REGISTER_RETURN_TYPE:
                // Java isn't handling aliases properly... check for aliases when generating code.
                if (sDef.aliasOf == null || sDef.aliasOf != "") {
                    return;
                }
                responses.put(sDef, activeList);
                return;
            default:
                logger.info("unhandled event (Events, StructDef): {}", event);
        }
    }

    //* Collect query events.
    @Override
    public void onEvent(Publisher.Events event, QueryDef qDef) {
        switch (event) {
            case NEW_QUERY:
                activeQuery = qDef;
                queries.add(qDef);
                return;
            default:
                logger.info("Unhandled event (Event, QueryDef) - {}", qDef.toString());
        }
    }

    //* Unused event.
    @Override
    public void onEvent(Publisher.Events event) {
        switch(event) {
            case END_QUERY:
                activeQuery = null;
                return;
            case END_MODEL:
                activeList = null;
                return;
            default:
                logger.info("Unhandled event (Events) - {}", event);
        }
    }

    /*
    @Override
    public void onEvent(Events event) {
        switch(event) {
        case END_QUERY:
            javaQueryWriter.finish();
            break;
        default:
            throw new RuntimeException("Unimplemented event! " + event);
        }
    }

    @Override
    public void onEvent(Events event, QueryDef query) {
        switch(event) {
        case NEW_QUERY:
            javaQueryWriter = new JavaQueryWriter(query, this);
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
            // Ignore aliases for now...
            if (sDef.aliasOf == null || sDef.aliasOf != "") {
                javaModelWriter.newModel(sDef, this.modelPackage);
            }
            break;
        default:
            throw new RuntimeException("Unemplemented event for StructDef! " + event);
        }

    }
     */
}
