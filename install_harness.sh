#!/usr/bin/env bash
set -e

rootdir=`dirname $0`
pushd $rootdir

# Reset test harness
rm -rf test-harness
git clone --single-branch --branch develop https://github.com/algorand/algorand-sdk-testing.git test-harness

## Copy feature files into the project resources
mkdir -p src/test/resources/com/algorand/algosdk/integration
cp test-harness/features/integration/* src/test/resources/com/algorand/algosdk/integration
cp test-harness/features/offline.feature src/test/resources/com/algorand/algosdk/integration
