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
    pass


def test_sdk():
    sys.stdout.flush()
    subprocess.check_call(['mvn test -Dskip.integration.tests=false -e'], shell=True, cwd=default_dirs['source'])
