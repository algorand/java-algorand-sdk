package com.algorand.sdkutils.listeners;

import com.algorand.velocity.StringHelpers;
import com.algorand.sdkutils.Main;
import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Publisher pub = new Publisher();
        TemplateGenerator subscriber = new TemplateGenerator(args, pub);
        OpenApiParser parser = new OpenApiParser(root, pub);
        parser.parse();
    }

    @com.beust.jcommander.Parameters(commandDescription = "Generate response test file(s).")
    public static class TemplateGeneratorArgs extends Main.CommonArgs {
        @com.beust.jcommander.Parameter(names = {"-p", "--propertyFile"}, description = "Property file to load into the template context for custom configuration.")
        public File propertyFile;

        @com.beust.jcommander.Parameter(required = true, names = {"-c", "--clientOutputDir"}, description = "Directory to write client file(s).")
        public File clientOutputDirectory;

        @com.beust.jcommander.Parameter(required = true, names = {"-q", "--queryOutputDir"}, description = "Directory to write query file(s).")
        public File queryOutputDirectory;

        @com.beust.jcommander.Parameter(required = true, names = {"-m", "--modelsOutputDir"}, description = "Directory to write model file(s).")
        public File modelsOutputDirectory;

        @com.beust.jcommander.Parameter(required = true, names = {"-t", "--templates"}, description = "Templates directory.")
        public File templatesDirectory;

        @com.beust.jcommander.Parameter(required = true, names = {"-o", "--output-dir"}, description = "Location to place response objects.")
        File outputDirectory;

        @Override
        public void validate(JCommander command) {
            super.validate(command);

            if (this.propertyFile != null && !this.propertyFile.isFile()) {
                throw new RuntimeException("Property file must be a file: " + this.propertyFile.getAbsolutePath());
            }
            if (!this.clientOutputDirectory.isDirectory()) {
                throw new RuntimeException("Client output directory must be a valid directory: " + this.clientOutputDirectory.getAbsolutePath());
            }
            if (!this.queryOutputDirectory.isDirectory()) {
                throw new RuntimeException("Query class output directory must be a valid directory: " + this.queryOutputDirectory.getAbsolutePath());
            }
            if (!this.modelsOutputDirectory.isDirectory()) {
                throw new RuntimeException("Model output directory must be a valid directory: " + this.modelsOutputDirectory.getAbsolutePath());
            }
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

    private Properties properties = new Properties();
    private final TemplateGeneratorArgs args;

    public TemplateGenerator(TemplateGeneratorArgs args, Publisher publisher) throws IOException {
        this.args =args;

        // Initialize property file if provided.
        if (args.propertyFile != null) {
            try (InputStream input = new FileInputStream(args.propertyFile)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to initialize property file.");
            }
        }

        publisher.subscribeAll(this);
    }

    private VelocityContext getContext() {
        VelocityContext context = new VelocityContext();
        context.put("str", new StringHelpers());
        context.put("propFile", this.properties);
        return context;
    }

    private String getFilename(Template t, VelocityContext c) {
        StringWriter writer = new StringWriter();
        t.merge(c, writer);
        return writer.toString().trim();
    }

    private void writeModelClass(Template template, Template filenameTemplate) {
        VelocityContext context = getContext();
        context.put("models", models);
        String lastFilename = "";
        for (Map.Entry<StructDef, List<TypeDef>> model : models.entrySet()) {
            context.put("def", model.getKey());
            context.put("props", model.getValue());

            String filename = getFilename(filenameTemplate, context);
            // If we see the same filename twice in a row, we're in single-file mode. Return before writing again.
            if (StringUtils.equals(filename, lastFilename)) {
                return;
            }
            lastFilename = filename;

            StringWriter writer = new StringWriter();
            template.merge( context, writer );

            // TODO: Write to files instead of stdout.
            System.out.println("====================");
            System.out.println(StringUtils.capitalize(model.getKey().name) + ".java");
            System.out.println("====================");
            System.out.println(writer.toString());
        }
    }

    private void writeQueryClass(Template template, Template filenameTemplate) {
        VelocityContext context = getContext();
        context.put("queries", queries);
        String lastFilename = "";
        for (QueryDef query : queries) {
            context.put("q", query);

            String filename = getFilename(filenameTemplate, context);
            // If we see the same filename twice in a row, we're in single-file mode. Return before writing again.
            if (StringUtils.equals(filename, lastFilename)) {
                return;
            }
            lastFilename = filename;

            StringWriter writer = new StringWriter();
            template.merge( context, writer );

            // TODO: Write to files instead of stdout.
            System.out.println("====================");
            System.out.println(filename);
            System.out.println("====================");
            System.out.println(writer.toString());
        }
    }

    @Override
    public void terminate() {
        logger.info("Generating files.");

        VelocityEngine velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.put("file.resource.loader.path", args.templatesDirectory.getAbsolutePath());
        velocityEngine.init(props);

        Template modelTemplate = velocityEngine.getTemplate("model.vm");
        Template modelFilenameTemplate = velocityEngine.getTemplate("model_filename.vm");
        writeModelClass(modelTemplate, modelFilenameTemplate);

        Template queryTemplate = velocityEngine.getTemplate("query.vm");
        Template queryFilenameTemplate = velocityEngine.getTemplate("query_filename.vm");
        writeQueryClass(queryTemplate, queryFilenameTemplate);
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
}
