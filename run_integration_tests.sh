#!/usr/bin/env bash

set -e

rootdir=`dirname $0`
pushd $rootdir

# Reset test harness
rm -rf test-harness
git clone --single-branch --branch will/indexer https://github.com/algorand/algorand-sdk-testing.git test-harness

## Copy feature files into the project resources
mkdir -p src/test/resources/com/algorand/algosdk/integration
cp test-harness/features/integration/* src/test/resources/com/algorand/algosdk/integration
cp test-harness/features/offline.feature src/test/resources/com/algorand/algosdk/integration

# Build SDK testing environment
docker build -t java-sdk-testing -f Dockerfile "$(pwd)"

# Start test harness environment
./test-harness/scripts/up.sh

# Launch SDK testing
docker run -it \
     --network host \
     java-sdk-testing:latest 
