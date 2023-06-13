# This script bumps up the version in setup.py for new releases.
# Usage: python bump_version.py {new_version} (--file_path <path-to setup.py>)

import argparse
import re
import xml.etree.ElementTree as ET


def bump_version(new_version, file_path):
    with open(file_path, "r") as file:
        read_me = file.read()

    # Replace first instance of <version></version>
    new_read_me = re.sub(
        '<version>[0-9]+\.[0-9]+\.[-a-z0-9]+</version>',
        f'<version>{new_version}</version>',
        read_me, 1
    )

    with open(file_path, "w") as file:
        file.write(new_read_me)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="updates the version for a release",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("new_version", help="New Version as major.minor.patch")
    parser.add_argument(
        "--read_me", default="README.md", help="path to README.md"
    )
    parser.add_argument(
        "--pom_xml", default="pom.xml", help="path to pom.xml"
    )

    args = parser.parse_args()

    bump_version(args.new_version, args.read_me)
    bump_version(args.new_version, args.pom_xml)

