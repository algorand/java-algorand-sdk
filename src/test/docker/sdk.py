#!/usr/bin/env python3

import subprocess
import sys


default_dirs = { 
    'features_dir': '/opt/java-algorand-sdk/src/test/resources/features',
    'source': '/opt/java-algorand-sdk',
    'docker': '/opt/java-algorand-sdk/src/test/docker'
}

def setup_sdk():
    """
    Setup java cucumber environment.
    """    
    subprocess.check_call(['rm -rf target'], shell=True)
    subprocess.check_call(['mvn install -q -DskipTests=true'], shell=True)

    # get feature files/config files/python scripts
    subprocess.check_call(['git clone --single-branch --branch michelle/test https://github.com/algorand/algorand-sdk-testing.git temp'], shell=True)


def test_sdk():
    sys.stdout.flush()
    
    subprocess.check_call(['mvn test -q'], shell=True)
    # subprocess.check_call(['mvn test -Dcucumber.options="--tags @template"'], shell=True, cwd=sdk.default_dirs['cucumber'])
    # subprocess.check_call(['mvn test -Dcucumber.options="/opt/sdk-testing/features/template.feature"'], shell=True, cwd=sdk.default_dirs['cucumber'])
