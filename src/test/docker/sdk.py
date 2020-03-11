#!/usr/bin/env python3

import argparse
import git
import os
import pprint
import shutil
import subprocess
import tarfile
import time
import json
import urllib.request
from os.path import expanduser, join
import sys



default_dirs = { 
    'features_dir': '/opt/java-algorand-sdk/src/test/resources/features',
    'url': 'https://github.com/algorand/java-algorand-sdk.git',
    'source': '/opt/java-algorand-sdk',
    'temp': '/opt/java-algorand-sdk/src/test/resources'
}

version = "current"
# version = "past"

def setup_sdk():
    """
    Setup java cucumber environment.
    """    
    # cd $TRAVIS_BUILD_DIR
    # mvn package -q -DskipTests
    # ALGOSDK_VERSION=$(mvn -q -Dexec.executable=echo  -Dexec.args='${project.version}' --non-recursive exec:exec)
    # cd - # back to 'java_cucumber'
    # find "${TRAVIS_BUILD_DIR}/target" -type f -name "*.jar" -exec mvn install:install-file -q -Dfile={} -DpomFile="${TRAVIS_BUILD_DIR}/pom.xml" \;
    # mvn versions:use-dep-version -DdepVersion=$ALGOSDK_VERSION -Dincludes=com.algorand:algosdk -DforceVersion=true -q
    
    # delete target
    subprocess.check_call(['rm -rf target'], shell=True)
    subprocess.check_call(['mvn install -q -DskipTests=true'], shell=True)

def test_sdk():
    sys.stdout.flush()
    
    subprocess.check_call(['mvn test -q'], shell=True)
    # subprocess.check_call(['mvn test -Dcucumber.options="--tags @template"'], shell=True, cwd=sdk.default_dirs['cucumber'])
    # subprocess.check_call(['mvn test -Dcucumber.options="/opt/sdk-testing/features/template.feature"'], shell=True, cwd=sdk.default_dirs['cucumber'])
