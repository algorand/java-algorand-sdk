#!/usr/bin/env bash
set -e

curl -o indexer.json https://raw.githubusercontent.com/algorand/indexer/master/api/indexer.oas2.json
curl -o algod.json https://raw.githubusercontent.com/algorand/go-algorand/rel/stable/daemon/algod/api/algod.oas2.json

$TEMPLATE \
       -c  "src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "AlgodClient" \
       -p  "src/main/java/com/algorand/algosdk/v2/client/algod" \
       -pp "com.algorand.algosdk.v2.client.algod" \
       -t  "X-Algo-API-Token" \
       -tr \
       -s  "algod.json"

# There is one enum only defined by indexer. Run this second to avoid
# overwriting the second one.
$TEMPLATE \
       -c  "src/main/java/com/algorand/algosdk/v2/client/common" \
       -cp "com.algorand.algosdk.v2.client.common" \
       -m  "src/main/java/com/algorand/algosdk/v2/client/model" \
       -mp "com.algorand.algosdk.v2.client.model" \
       -n  "IndexerClient" \
       -p  "src/main/java/com/algorand/algosdk/v2/client/indexer" \
       -pp "com.algorand.algosdk.v2.client.indexer" \
       -t  "X-Indexer-API-Token" \
       -s  "indexer.json"
