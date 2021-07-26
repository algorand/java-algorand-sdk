#!/usr/bin/env bash
set -e

# Navigate back to the project directory
rootdir=`dirname $0`
pushd $rootdir/.. > /dev/null

$GENERATOR \
       java \
       -c  "src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "AlgodClient" \
       -p  "src/main/java/com/algorand/algosdk/v2/client/algod" \
       -pp "com.algorand.algosdk.v2.client.algod" \
       -t  "X-Algo-API-Token" \
       -tr \
       -s  "$ALGOD_SPEC"

# There is one enum only defined by indexer. Run this second to avoid
# overwriting the second one.
$GENERATOR \
       java \
       -c  "src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "IndexerClient" \
       -p  "src/main/java/com/algorand/algosdk/v2/client/indexer" \
       -pp "com.algorand.algosdk.v2.client.indexer" \
       -t  "X-Indexer-API-Token" \
       -s  "$INDEXER_SPEC"
