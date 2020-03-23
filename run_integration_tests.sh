#!/usr/bin/env bash

set -e

rootdir=`dirname $0`
pushd $rootdir

# get feature files from algorand-sdk-testing repo
sudo rm -rf temp
git clone --single-branch --branch templates https://github.com/algorand/algorand-sdk-testing.git temp

# Install the sdk.py driver
cp sdk.py temp/docker
# Copy feature files into the project resources
mkdir -p src/test/resources/com/algorand/algosdk/integration
cp temp/features/integration/* src/test/resources/com/algorand/algosdk/integration
cp temp/features/offline.feature src/test/resources/com/algorand/algosdk/integration

# Build and execute the docker container
docker build -t sdk-testing -f Dockerfile "$(pwd)"
docker run -it \
     -v "$(pwd)":/opt/java-algorand-sdk \
     sdk-testing:latest 

