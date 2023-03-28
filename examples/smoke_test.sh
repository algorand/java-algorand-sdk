#!/usr/bin/env bash

export ALGOD_PORT="60000"
export INDEXER_PORT="59999"
export KMD_PORT="60001"

mvn clean compile assembly:single

# Loop over all files in the directory
for file in $(find . -name "*.java" -type f | sort -n); do
    # Check if the filename is not "ExampleUtils.java"
    if [[ $file != *"ExampleUtils.java" ]]; then
        # Remove the "./src/main/java/" from the file path
        file="${file//.\/src\/main\/java\//}"
        # Replace the "/" with "."
        file="${file//\//.}"
        # Remove the ".java" from the file path
        file="${file//.java/}"
        # Run the example
        java -cp target/sdk-extras-1.0-SNAPSHOT-jar-with-dependencies.jar $file
    fi
done
