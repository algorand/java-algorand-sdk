#!/usr/bin/env bash

set -e

rm -rf temp
rm -rf src/test/resources/features
git clone --single-branch --branch michelle/test https://github.com/algorand/algorand-sdk-testing.git temp
cp src/test/docker/sdk.py temp/docker
mv temp/features src/test/resources/features

# rootdir=`dirname $0`
# pushd $rootdir
docker build -t sdk-testing -f src/test/docker/Dockerfile "$(pwd)"

# Example running local java sdk code in the container
docker run -it \
     -v "$(pwd)":/opt/java-algorand-sdk \
     sdk-testing:latest 
#      /bin/bash -c "\
#            GO111MODULE=off && \
#            ls && \
#            temp/docker/setup.py --algod-config temp/config_future --local && \
#            temp/docker/test.py --algod-config temp/config_future --network-dir /opt/testnetwork"

#            ./scripts/docker/test.sh --java"

# docker run -it -v $(pwd):/opt/sdk-testing sdk-testing:latest /bin/bash -c "GO111MODULE=off && ./scripts/docker/setup.py --algod-config scripts/config_future && ./scripts/docker/test.py --algod-config scripts/config_future --sdk-testing /opt/sdk-testing/ --network-dir /opt/testnetwork"

# docker run -it -v $(pwd):/opt/sdk-testing sdk-testing:latest
