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
import java.nio.file.Path;
import java.util.*;

public class TemplateGenerator implements Subscriber {
    protected static final Logger logger = LogManager.getLogger(TemplateGenerator.class.getName());

    /**
     * Main function to run the template generator. This should be factored out since every generator does something
     * identical.
     */
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

    /**
     * Arguments for the TemplateGenerator.
     */
    @com.beust.jcommander.Parameters(commandDescription = "Generate response test file(s).")
    public static class TemplateGeneratorArgs extends Main.CommonArgs {
        @com.beust.jcommander.Parameter(names = {"-p", "--propertyFile"}, description = "Property file to load into the template context for custom configuration.")
        public File propertyFile;

        @com.beust.jcommander.Parameter(names = {"-c", "--clientOutputDir"}, description = "Directory to write client file(s).")
        public File clientOutputDirectory;

        @com.beust.jcommander.Parameter(names = {"-q", "--queryOutputDir"}, description = "Directory to write query file(s).")
        public File queryOutputDirectory;

        @com.beust.jcommander.Parameter(names = {"-m", "--modelsOutputDir"}, description = "Directory to write model file(s).")
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
            /*
            // Make these optional in case to allow implicit partial generation.
            if (!this.clientOutputDirectory.isDirectory()) {
                throw new RuntimeException("Client output directory must be a valid directory: " + this.clientOutputDirectory.getAbsolutePath());
            }
            if (!this.queryOutputDirectory.isDirectory()) {
                throw new RuntimeException("Query class output directory must be a valid directory: " + this.queryOutputDirectory.getAbsolutePath());
            }
            if (!this.modelsOutputDirectory.isDirectory()) {
                throw new RuntimeException("Model output directory must be a valid directory: " + this.modelsOutputDirectory.getAbsolutePath());
            }
             */
            if (!this.templatesDirectory.isDirectory()) {
                throw new RuntimeException("Templates directory must be a valid directory: " + this.templatesDirectory.getAbsolutePath());
            }
            if (!this.outputDirectory.isDirectory()) {
                throw new RuntimeException("Output directory must be a valid directory: " + this.outputDirectory.getAbsolutePath());
            }
        }
    }

    /////////////////////////////

    // Return types. Usually aliases to model types.
    private HashMap<StructDef, List<TypeDef>> responses = new HashMap<>();

    // Model definitions.
    private HashMap<StructDef, List<TypeDef>> models = new HashMap<>();

    // Query definitions.
    private List<QueryDef> queries = new ArrayList<>();

    // Temporary variables used while building up results from the parser events.
    private List<TypeDef> activeList = null;
    private QueryDef activeQuery = null;

    // Properties provided to the generator. Available to templates with '$propFile.<user-defined-property>'
    private final Properties properties = new Properties();

    // The generator CLI arguments object.
    private final TemplateGeneratorArgs args;

    /**
     * Constructor.
     */
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

    /**
     * Create and configure a {@link org.apache.velocity.app.VelocityEngine}.
     * @return a configured {@link org.apache.velocity.app.VelocityEngine}.
     */
    private VelocityEngine getEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();

        // Tell the engine where templates are located.
        velocityEngine.setProperty("file.resource.loader.path", args.templatesDirectory.getAbsolutePath());

        // Strict mode causes template generation fail if there is an unknown reference.
        velocityEngine.setProperty("runtime.references.strict", true);

        return velocityEngine;
    }

    /**
     * Create and initialize a {@link org.apache.velocity.VelocityContext}. The context includes the following values:
     *      - str: ${@link StringHelpers} helper class with string manipulation functions.
     *      - propFile: Properties file provided when calling the TemplateGenerator.
     *      - models: Map of model definition / list of it's properties (properties are also in the definition).
     *      - queries: List of query definitions including the parameters.
     * @return a configured {@link org.apache.velocity.VelocityContext}.
     */
    private VelocityContext getContext() {
        VelocityContext context = new VelocityContext();
        context.put("str", new StringHelpers());
        context.put("propFile", this.properties);
        context.put("models", models);
        context.put("queries", queries);
        context.put("responses", responses);

        Set<String> uniqueTypes = new HashSet<>();
        for (Map.Entry<StructDef, List<TypeDef>> model : models.entrySet()) {
            for (TypeDef type : model.getKey().properties) {
                uniqueTypes.add(type.rawTypeName);
            }
        }
        context.put("uniqueTypes", uniqueTypes);

        // Get all types
        return context;
    }

    /**
     * Simple wrapper to process a filename template and get the result. The context must be provided because the
     * current object is needed so the template can derive a filename.
     * @return filename to use for generation.
     */
    private String getFilename(Template t, VelocityContext c) {
        StringWriter writer = new StringWriter();
        t.merge(c, writer);
        return writer.toString().trim();
    }

    private void processClassTemplate(Template template, Template filenameTemplate) throws IOException {
        if(args.modelsOutputDirectory == null || !args.modelsOutputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Disabling model generation. Provide modelOutputDirectory option to enable.");
        }

        VelocityContext context = getContext();
        String filename = getFilename(filenameTemplate, context);
        logger.info("Writing file: {}", filename);
        Path target = Path.of(args.modelsOutputDirectory.getAbsolutePath(), filename);
        try (FileWriter writer = new FileWriter(target.toFile())) {
            template.merge(context, writer);
        }
    }

    private void processModelTemplate(Template template, Template filenameTemplate) throws IOException {
        if(args.modelsOutputDirectory == null || !args.modelsOutputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Disabling model generation. Provide modelOutputDirectory option to enable.");
        }

        VelocityContext context = getContext();
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

            logger.info("Writing file: {}", filename);
            Path target = Path.of(args.modelsOutputDirectory.getAbsolutePath(), filename);
            try (FileWriter writer = new FileWriter(target.toFile())) {
                template.merge(context, writer);
            }
        }
    }

    private void processQueryTemplate(Template template, Template filenameTemplate) throws IOException {
        if(args.queryOutputDirectory == null || !args.queryOutputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Disabling query generation. Provide queryOutputDirectory option to enable.");
        }

        VelocityContext context = getContext();
        String lastFilename = "";
        for (QueryDef query : queries) {
            context.put("q", query);

            String filename = getFilename(filenameTemplate, context);
            // If we see the same filename twice in a row, we're in single-file mode. Return before writing again.
            if (StringUtils.equals(filename, lastFilename)) {
                return;
            }
            lastFilename = filename;

            logger.info("Writing file: {}", filename);
            Path target = Path.of(args.modelsOutputDirectory.getAbsolutePath(), filename);
            try (FileWriter writer = new FileWriter(target.toFile())) {
                template.merge(context, writer);
            }
        }
    }

    @Override
    public void terminate() {
        logger.info("Generating files.");

        VelocityEngine velocityEngine = getEngine();

        try {
            Template modelTemplate = velocityEngine.getTemplate("model.vm");
            Template modelFilenameTemplate = velocityEngine.getTemplate("model_filename.vm");
            processModelTemplate(modelTemplate, modelFilenameTemplate);
        } catch (Exception e) {
            logger.warn("Unable to write models: {}", e.getMessage());
        }

        try {
            Template queryTemplate = velocityEngine.getTemplate("query.vm");
            Template queryFilenameTemplate = velocityEngine.getTemplate("query_filename.vm");
            processQueryTemplate(queryTemplate, queryFilenameTemplate);
        } catch (Exception e) {
            logger.warn("Unable to write queries: {}", e.getMessage());
        }

        try {
            Template clientTemplate = velocityEngine.getTemplate("client.vm");
            Template clientFilenameTemplate = velocityEngine.getTemplate("client_filename.vm");
            processClassTemplate(clientTemplate, clientFilenameTemplate);
        } catch (Exception e) {
            logger.warn("Unable to write clients: {}", e.getMessage());
        }
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
