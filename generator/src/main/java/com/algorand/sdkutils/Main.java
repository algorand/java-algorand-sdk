package com.algorand.sdkutils;

import com.algorand.sdkutils.generators.Generator;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.listeners.GoGenerator;
import com.algorand.sdkutils.listeners.Publisher;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Collection;

public class Main {
    public static void main (String args[]) {
        Options o = generateOptions();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(o, args);

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("generator", o);
                System.exit(0);
            }

            Main.Generate(
                    line.getOptionValue("n"),
                    new File(line.getOptionValue("s")),
                    line.getOptionValue("m"),
                    line.getOptionValue("mp"),
                    line.getOptionValue("p"),
                    line.getOptionValue("pp"),
                    line.getOptionValue("c"),
                    line.getOptionValue("cp"),
                    line.getOptionValue("t"),
                    !line.hasOption("tr"),
                    null);
        } catch (ParseException e) {
            System.out.println("Problem processing arguments: " + e.getMessage());
            System.out.println("\n\n");
            e.printStackTrace();
            System.out.println("\n\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("generator", o);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Problem generating code:" + e.getMessage());
            System.out.println("\n\n");
            e.printStackTrace();
            System.out.println("\n\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("generator", o);
            System.exit(0);
        }
    }

    /**
     * @param clientName     Name of the client class. i.e. IndexerClient
     * @param specfile       Full path to the OpenAPI v2 spec file.
     * @param modelPath      Path where generated model files will be placed.
     * @param modelPackage   Package name to put at the top of generated model files.
     * @param pathsPath      Path where generated path builder files will be placed.
     * @param pathsPackage   Package name to put at the top of generated path builder files.
     * @param commonPath     Path where the common files are, this is where the client class will be placed.
     * @param commonPackage  Package name to put at the top of generated client class.
     * @param tokenName      Name of the token used for this application. i.e. X-Algo-API-Token
     * @param tokenOptional  Whether or not a no-token version of the constructor should be created.
     * @param goDirectory    When specified, will generate go code.
     */
    public static void Generate(
            String clientName,
            File specfile,
            String modelPath,
            String modelPackage,
            String pathsPath,
            String pathsPackage,
            String commonPath,
            String commonPackage,
            String tokenName,
            Boolean tokenOptional,
            String goDirectory) throws Exception {

        JsonNode root;
        try (FileInputStream fis = new FileInputStream(specfile)) {
            root = Utils.getRoot(fis);
        }

        Generator g = null;
        Publisher publisher = new Publisher();

        if (goDirectory != null && !goDirectory.isEmpty()) {
            g = new Generator(root, publisher);
            new GoGenerator(goDirectory, "indexer", publisher); 
        } else {
            g = new Generator(root);
        }

        // Generate classes from the schemas
        // These are the non-premetive types for which classes are needed
        System.out.println("Generating " + modelPackage + " to " + modelPath);
        g.generateAlgodIndexerObjects(root, modelPath, modelPackage);
        g.generateEnumClasses(root, modelPath, modelPackage);

        // Generate classes from the return types which have more than one return element
        System.out.println("Generating " + modelPackage + " to " + modelPath);
        g.generateReturnTypes(root, modelPath, modelPackage);

        // Generate the algod methods
        File imports = Files.createTempFile("imports_file", "txt").toFile();
        File paths = Files.createTempFile("pathss_file", "txt").toFile();

        System.out.println("Generating " + pathsPackage + " to " + pathsPath);
        g.generateQueryMethods(
                pathsPath,
                pathsPackage,
                modelPackage,
                imports,
                paths);

        Collection<String> lines = Files.readAllLines(imports.toPath());
        lines.add("import com.algorand.algosdk.crypto.Address;");

        Utils.generateClientFile(
                clientName,
                lines,
                paths,
                commonPackage,
                commonPath,
                tokenName,
                tokenOptional);
        publisher.terminate();
    }

    private static Options generateOptions() {
        Options options = new Options();

        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Print help information.")
                .build());

        options.addOption(Option.builder("n")
                .longOpt("client-name")
                .desc("Name of the client class. i.e. IndexerClient")
                .hasArg()
                .argName("CLIENT NAME")
                .required()
                .build());

        options.addOption(Option.builder("s")
                .longOpt("specfile")
                .desc("Full path to the OpenAPI v2 spec file.")
                .hasArg()
                .argName("SPECFILE")
                .required()
                .build());

        options.addOption(Option.builder("m")
                .longOpt("model-path")
                .desc("Path where generated model files will be placed.")
                .hasArg()
                .argName("MODEL PATH")
                .required()
                .build());

        options.addOption(Option.builder("mp")
                .longOpt("model-package")
                .desc("Package name to put at the top of generated model files.")
                .hasArg()
                .argName("MODEL PACKAGE")
                .required()
                .build());

        options.addOption(Option.builder("p")
                .longOpt("paths-path")
                .desc("Path where generated path builder files will be placed.")
                .hasArg()
                .argName("PATH BUILDER PATH")
                .required()
                .build());

        options.addOption(Option.builder("pp")
                .longOpt("paths-package")
                .desc("Package name to put at the top of generated path builder files.")
                .hasArg()
                .argName("PATH BUILDER PACKAGE")
                .required()
                .build());

        options.addOption(Option.builder("c")
                .longOpt("common-path")
                .desc("Path where the common files are, this is where the client class will be placed.")
                .hasArg()
                .argName("COMMON PATH")
                .required()
                .build());

        options.addOption(Option.builder("cp")
                .longOpt("common-package")
                .desc("Package name to put at the top of generated client class.")
                .hasArg()
                .argName("COMMON PACKAGE")
                .required()
                .build());

        options.addOption(Option.builder("t")
                .longOpt("token-name")
                .desc("Name of the token used for this application. i.e. X-Algo-API-Token")
                .hasArg()
                .argName("TOKEN NAME")
                .required()
                .build());

        options.addOption(Option.builder("tr")
                .longOpt("token-required-flag")
                .desc("Whether or not a no-token version of the constructor should be created.")
                .build());

        return options;
    }
}
