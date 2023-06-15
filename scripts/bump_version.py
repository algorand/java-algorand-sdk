# This script bumps up the version in `pom.xml` and `README.md` for new releases.
# Usage: python bump_version.py {new_version} (--read_me <path-to-README.md> --pom_xml <path-to-pom-xml>)

import argparse
import re

def bump_version(new_version, file_path):
    with open(file_path, "r") as file:
        content = file.read()

    # Replace first instance of <version></version>
    new_content = re.sub(
        '<version>[0-9]+\.[0-9]+\.[-a-z0-9]+</version>',
        f'<version>{new_version}</version>',
        content, 1
    )

    with open(file_path, "w") as file:
        file.write(new_content)

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

