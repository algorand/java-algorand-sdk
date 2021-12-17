#!/usr/bin/env bash
set -e

rootdir=`dirname $0`
pushd $rootdir

SKIP_TEST_CONTAINER=0
UPDATE_FEATURE_FILES_ONLY=0
TEST_BRANCH=master

function help {
  echo "Options:"
  echo "   -local          skip launching the test container."
  echo "   -feature-only   don't bring up test environment or launch test container."
  echo "   -test-branch    install feature files from this branch instead of the default branch."
}

function my_exit {
  popd
  exit $1
}

while [ "$1" != "" ]; do
    case "$1" in
        -local)
            SKIP_TEST_CONTAINER=1
            ;;
        -feature-only)
            UPDATE_FEATURE_FILES_ONLY=1
            ;;
        -test-branch)
            shift
            TEST_BRANCH=$1
            ;;
        *)
            echo "Unknown option $1"
            help
            my_exit 0
            ;;
    esac
    shift
done

# Reset test harness
rm -rf test-harness
git clone --single-branch --branch ${TEST_BRANCH} https://github.com/algorand/algorand-sdk-testing.git test-harness

## Copy feature files into the project resources

rm -rf src/test/resources/com/algorand/algosdk/integration
rm -rf src/test/resources/com/algorand/algosdk/unit
mkdir -p src/test/resources/com/algorand/algosdk/integration
mkdir -p src/test/resources/com/algorand/algosdk/unit

# The Java implementation of these is too tightly coupled with the
# integration tests, so add them to the integration tests instead.
mv test-harness/features/unit/offline.feature test-harness/features/integration/

cp -r test-harness/features/integration/* src/test/resources/com/algorand/algosdk/integration
cp -r test-harness/features/unit/* src/test/resources/com/algorand/algosdk/unit
cp -r test-harness/features/resources/* src/test/resources/

if [ "${UPDATE_FEATURE_FILES_ONLY}" == "1" ]; then
    my_exit 1
fi

# Start test harness environment
./test-harness/scripts/up.sh

if [ "${SKIP_TEST_CONTAINER}" == "1" ]; then
    my_exit 1
fi

# Build SDK testing environment
docker build -t java-sdk-testing -f Dockerfile "$(pwd)"

# Launch SDK testing
docker run -it \
     --network host \
     java-sdk-testing:latest 

my_exit 0
