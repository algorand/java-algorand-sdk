package com.algorand.sdkutils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.algorand.sdkutils.generators.OpenApiParser;
import com.algorand.sdkutils.generators.Utils;
import com.algorand.sdkutils.listeners.GoGenerator;
import com.algorand.sdkutils.listeners.JavaGenerator;
import com.algorand.sdkutils.listeners.Publisher;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.databind.JsonNode;


/**
 *
 * Usage examples:
 * --algodspec go-algorand/daemon/algod/api/algod.oas2.json
 * --indexerspec indexer/api/indexer.oas2.json
 * -m application_responsemodels
 * --filter DryrunRequest,DryrunSource,ApplicationStateSchema,ApplicationLocalStates,ApplicationLocalState,TealKeyValue,TealValue,AccountStateDelta,EvalDeltaKeyValue,EvalDelta,Application,ApplicationParams,DryrunState,DryrunTxnResult,CompileResponse,DryrunResponse
 */
public class RunAlgodV2AndIndexerGenerators {

    @Parameters(commandDescription = "Generate the Java Client SDK.")
    public static class GeneratorArgs {
        @Parameter(names = {"-h", "--help"}, help = true)
        public boolean help = false;

        @Parameter(required = false,
                names = {"--algodspec"},
                description = "Full path to the first OpenAPI v2 spec file.")
        public String aSpecFilePath =
        "../../../go/src/github.com/algorand/go-algorand/daemon/algod/api/algod.oas2.json";

        @Parameter(required = false,
                names = {"--indexerspec"},
                description = "Full path to the second OpenAPI v2 spec file.")
        public String iSpecFilePath =
        "../../indexer/api/indexer.oas2.json";

        @Parameter(required = false,
                names = {"-m"},
                description = "Name of the models file for Go. By default: \"responsemodels\".")
        public String modelsFilename = "responsemodels";

        @Parameter(required = false,
                names = {"--filter"},
                description = "classlist is a list of class names. If provided, only thses classes" +
                "will be processed. Otherwise, all the classes are processed.Path where generated paths files will be placed.")
        public List<String> filterList = new ArrayList<String>();

        public void validate(JCommander command) {
            if (this.help) {
                command.usage();
                System.exit(0);
            }
        }
    }


    public static void main (String args[]) throws Exception {

        GeneratorArgs gArgs = new GeneratorArgs();
        try {
            JCommander.newBuilder()
            .addObject(gArgs)
            .build()
            .parse(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JCommander.newBuilder()
            .addObject(gArgs)
            .build().usage();
            System.exit(1);
        }

        Publisher publisher = new Publisher();

        HashSet<String> filterList = new HashSet<String>();
        for (String filter : gArgs.filterList) {
            filterList.add(filter);
        }

        JsonNode root;
        try (FileInputStream fis = new FileInputStream(gArgs.aSpecFilePath)) {
            root = Utils.getRoot(fis);
        }

        OpenApiParser g = new OpenApiParser(root, publisher, filterList);
        new GoGenerator("go-sdk", "algod", gArgs.modelsFilename, publisher);
        new JavaGenerator(
                "AlgodClient",
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/algod",
                "com.algorand.algosdk.v2.client.algod",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Algo-API-Token",
                false,
                publisher);

        g.parse();

        publisher = new Publisher();
        try (FileInputStream fis = new FileInputStream(gArgs.iSpecFilePath)) {
            root = Utils.getRoot(fis);
        }
        g = new OpenApiParser(root, publisher);
        new GoGenerator("go-sdk", "indexer", gArgs.modelsFilename, publisher);
        new JavaGenerator(
                "IndexerClient",
                "../src/main/java/com/algorand/algosdk/v2/client/model",
                "com.algorand.algosdk.v2.client.model",
                "../src/main/java/com/algorand/algosdk/v2/client/indexer",
                "com.algorand.algosdk.v2.client.indexer",
                "../src/main/java/com/algorand/algosdk/v2/client/common",
                "com.algorand.algosdk.v2.client.common",
                "X-Indexer-API-Token",
                true,
                publisher);

        g.parse();
    }
}
