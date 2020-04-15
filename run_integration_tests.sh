#!/usr/bin/env bash
set -e

rootdir=`dirname $0`
pushd $rootdir

./install_harness.sh

# Build SDK testing environment
docker build -t java-sdk-testing -f Dockerfile "$(pwd)"

# Start test harness environment
./test-harness/scripts/up.sh

# Launch SDK testing
docker run -it \
     --network host \
     java-sdk-testing:latest 
