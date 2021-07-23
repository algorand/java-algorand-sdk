#!/usr/bin/env bash

# Note: This script currently assumes that you are in a java sdk source
# directory named 'java-algorand-sdk' and that the generator is checked
# out at '../generator'.
#
# Regenerates the client code from the spec files.
#
# Builds and executes the generator CLI with parameters for the Java SDK.
#
#
# Example:
#    indexer repo cloned to ~/algorand/indexer
#    go-algorand repo cloned to ~/go/src/github.com/algorand/go-algorand
#
#    ./run_generator.sh \
#         -indexer-spec ~/algorand/indexer/api/indexer.oas2.json \
#         -algod-spec ~/go/src/github.com/algorand/go-algorand/daemon/algod/api/algod.oas2.json
#
set -e

rootdir=`dirname $0`
pushd $rootdir/../generator > /dev/null

function help {
  echo "Options:"
  echo "   -indexer-spec  full path to indexer.oas3.yml"
  echo "   -algod-spec    full path to algod.oas3.yml"
}

function my_exit {
  popd > /dev/null
  exit $1
}

INDEXER_SPEC=""
ALGOD_SPEC=""

while [ "$1" != "" ]; do
    case "$1" in
        -indexer-spec)
            shift
            INDEXER_SPEC=$1
            ;;
        -algod-spec)
            shift
            ALGOD_SPEC=$1
            ;;
        *)
            echo "Unknown option $1"
            help
            my_exit 0
            ;;
    esac
    shift
done

if [[ ! -f "$INDEXER_SPEC" ]]; then
  echo "Could not open indexer spec file at: '$INDEXER_SPEC'"
  help
  my_exit 1
fi

if [[ ! -f "$ALGOD_SPEC" ]]; then
  echo "Could not open algod spec file at: '$ALGOD_SPEC'"
  help
  my_exit 1
fi

if [[ -z "${NO_BUILD}" ]]; then
  mvn clean package -DskipTests
fi

java -jar target/generator-*-jar-with-dependencies.jar \
       java \
       -c  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "AlgodClient" \
       -p  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/algod" \
       -pp "com.algorand.algosdk.v2.client.algod" \
       -t  "X-Algo-API-Token" \
       -tr \
       -s  "$ALGOD_SPEC"

# There is one enum only defined by indexer. Run this second to avoid
# overwriting the second one.
java -jar target/generator-*-jar-with-dependencies.jar \
       java \
       -c  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "IndexerClient" \
       -p  "../java-algorand-sdk/src/main/java/com/algorand/algosdk/v2/client/indexer" \
       -pp "com.algorand.algosdk.v2.client.indexer" \
       -t  "X-Indexer-API-Token" \
       -s  "$INDEXER_SPEC"
