#!/usr/bin/env python3

import subprocess
import sys

default_dirs = { 
    'features_dir': 'src/test/resources/features',
    'source': '/opt/java-algorand-sdk/',
    'docker': '/opt/java-algorand-sdk/src/test/docker'
}

def setup_sdk():
    """
    Setup java cucumber environment.
    """    
    subprocess.check_call(['mvn clean'], shell=True)
    subprocess.check_call(['mvn install -q -DskipTests=true'], shell=True)
    pass


def test_sdk():
    sys.stdout.flush()
    #subprocess.check_call(['mvn test -Dskip.integration.tests=false -e'], shell=True, cwd=default_dirs['source'])
    #feature_file = default_dirs['source'] + default_dirs['features_dir'] + '/template.feature'
    #print("\n\nfeature file: %s\n\n" % feature_file)
    subprocess.check_call(['mvn test -Dskip.integration.tests=false -e'], shell=True, cwd=default_dirs['source'])

    #subprocess.check_call(['mvn test -Dcucumber.options="--tags \\"not @crosstest\\""'], shell=True, cwd=default_dirs['source'])
    #subprocess.check_call(['mvn test -Dcucumber.options="--tags @template"'], shell=True, cwd=JAVA['cucumber'])

    #subprocess.check_call(['mvn test -Dcucumber.options="/opt/sdk-testing/features/template.feature"'], shell=True, cwd=JAVA['cucumber'])

