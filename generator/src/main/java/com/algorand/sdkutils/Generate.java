package com.algorand.sdkutils;

import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.listeners.GoGenerator;
import com.algorand.sdkutils.listeners.JavaGenerator;
import com.algorand.sdkutils.listeners.Publisher;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Collection;

public class Generate {
    /**
     * This is deprecated. Use OpenApiParser:parse() instead.
     *
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
    @Deprecated
    public static void generate(
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

        Publisher publisher = new Publisher();
        OpenApiParser g = new OpenApiParser(root, publisher);

        if (goDirectory != null && !goDirectory.isEmpty()) {
            new GoGenerator(goDirectory, "indexer", "responsemodels", publisher);
        } else {
	    new JavaGenerator(
                clientName,
                modelPath,
                modelPackage,
                pathsPath,
                pathsPackage,
                commonPath,
                commonPackage,
                tokenName,
                tokenOptional,
                publisher);
        }
	
        // Generate classes from the schemas
        // These are the non-premetive types for which classes are needed
        System.out.println("Generating " + modelPackage + " to " + modelPath);
        g.generateAlgodIndexerObjects(root);

        // Generate classes from the return types which have more than one return element
        System.out.println("Generating " + modelPackage + " to " + modelPath);
        g.generateReturnTypes(root);

        // Generate the algod methods
        File imports = Files.createTempFile("imports_file", "txt").toFile();
        File paths = Files.createTempFile("pathss_file", "txt").toFile();

        System.out.println("Generating " + pathsPackage + " to " + pathsPath);
        g.generateQueryMethods();

        Collection<String> lines = Files.readAllLines(imports.toPath());
        lines.add("import com.algorand.algosdk.crypto.Address;");

        publisher.terminate();       
    }
}
