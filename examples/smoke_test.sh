#!/usr/bin/env bash

export ALGOD_PORT="60000"
export INDEXER_PORT="59999"
export KMD_PORT="60001"

mvn clean compile assembly:single
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.ASAExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.ATC
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.AccountExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.AppExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.AtomicTransfers
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.CodecExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.Debug
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.IndexerExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.KMDExamples
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.LSig
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.Overview
java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar com.algorand.examples.Participation

