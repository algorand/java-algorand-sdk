#!/usr/bin/env bash

set -e

docker build -t sdk-testing -f src/test/docker/Dockerfile "$(pwd)"

docker run -it \
     -v "$(pwd)":/opt/java-algorand-sdk \
     sdk-testing:latest 
