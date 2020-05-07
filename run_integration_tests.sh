#!/usr/bin/env bash
set -e

rootdir=`dirname $0`
pushd $rootdir

# Reset test harness
rm -rf test-harness
git clone --single-branch --branch develop https://github.com/algorand/algorand-sdk-testing.git test-harness

rm test-harness/features/integration/indexer.feature
rm test-harness/features/unit/v2*feature

## Copy feature files into the project resources
rm -rf src/test/resources/com/algorand/algosdk/integration
rm -rf src/test/resources/com/algorand/algosdk/unit
mkdir -p src/test/resources/com/algorand/algosdk/integration
mkdir -p src/test/resources/com/algorand/algosdk/unit
cp -r test-harness/features/integration/* src/test/resources/com/algorand/algosdk/integration
cp -r test-harness/features/unit/* src/test/resources/com/algorand/algosdk/unit

# Build SDK testing environment
docker build -t java-sdk-testing -f Dockerfile "$(pwd)"

# Start test harness environment
./test-harness/scripts/up.sh

# Launch SDK testing
docker run -it \
     --network host \
     java-sdk-testing:latest 
