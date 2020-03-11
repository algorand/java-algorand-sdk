#!/usr/bin/env bash

set -e

rm -rf temp
rm -rf src/test/resources/features

git clone --single-branch --branch michelle/test https://github.com/algorand/algorand-sdk-testing.git temp
cp src/test/docker/sdk.py temp/docker
mv temp/features src/test/resources/features

docker build -t sdk-testing -f src/test/docker/Dockerfile "$(pwd)"

docker run -it \
     -v "$(pwd)":/opt/java-algorand-sdk \
     sdk-testing:latest 
