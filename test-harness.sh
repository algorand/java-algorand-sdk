#!/usr/bin/env bash
set -euo pipefail

START=$(date "+%s")

THIS=$(basename "$0")
ENV_FILE=".test-env"
TEST_DIR="src/test/resources/com/algorand/algosdk"
TEST_RESOURCES_DIR="src/test/resources/"

set -a
source "$ENV_FILE"
set +a

rootdir=$(dirname "$0")
pushd "$rootdir"

## Reset test harness
if [ -d "$SDK_TESTING_HARNESS" ]; then
  pushd "$SDK_TESTING_HARNESS"
  ./scripts/down.sh
  popd
  rm -rf "$SDK_TESTING_HARNESS"
else
  echo "$THIS: directory $SDK_TESTING_HARNESS does not exist - NOOP"
fi

git clone --depth 1 --single-branch --branch "$SDK_TESTING_BRANCH" "$SDK_TESTING_URL" "$SDK_TESTING_HARNESS"


if [[ $OVERWRITE_TESTING_ENVIRONMENT == 1 ]]; then
  echo "$THIS: OVERWRITE replaced $SDK_TESTING_HARNESS/.env with $ENV_FILE:"
  cp "$ENV_FILE" "$SDK_TESTING_HARNESS"/.env
fi

## Copy feature files into the project resources
if [[ $REMOVE_LOCAL_FEATURES == 1 ]]; then
  echo "$THIS: OVERWRITE wipes clean $TEST_DIR/features"
  if [[ $VERBOSE_HARNESS == 1 ]]; then
    ( tree $TEST_DIR/integration && tree $TEST_DIR/unit && tree $TEST_RESOURCES_DIR && echo "$THIS: see the previous for files deleted" ) || true
  fi
  rm -rf $TEST_DIR/integration
  rm -rf $TEST_DIR/unit
  rm -rf $TEST_RESOURCES_DIR
fi
mkdir -p $TEST_DIR/integration
mkdir -p $TEST_DIR/unit
mkdir -p $TEST_RESOURCES_DIR

# The Java implementation of these is too tightly coupled with the
# integration tests, so add them to the integration tests instead.
mv "$SDK_TESTING_HARNESS"/features/unit/offline.feature test-harness/features/integration/

cp -r "$SDK_TESTING_HARNESS"/features/integration/*  $TEST_DIR/integration
cp -r "$SDK_TESTING_HARNESS"/features/unit/* $TEST_DIR/unit
cp -r "$SDK_TESTING_HARNESS"/features/resources/* $TEST_RESOURCES_DIR

if [[ $VERBOSE_HARNESS == 1 ]]; then
  ( tree $TEST_DIR/integration && tree $TEST_DIR/unit && tree $TEST_RESOURCES_DIR && echo "$THIS: see the previous for files copied over" ) || true
fi
echo "$THIS: seconds it took to get to end of cloning and copying: $(($(date "+%s") - START))s"

## Start test harness environment
pushd "$SDK_TESTING_HARNESS"
./scripts/up.sh
popd
echo "$THIS: seconds it took to finish testing sdk's up.sh: $(($(date "+%s") - START))s"
echo ""
echo "--------------------------------------------------------------------------------"
echo "|"
echo "|    To run sandbox commands, cd into $SDK_TESTING_HARNESS/.sandbox             "
echo "|"
echo "--------------------------------------------------------------------------------"
