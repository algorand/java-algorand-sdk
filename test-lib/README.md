# Vendored test dependency: falcon-det1024

This directory is a small file-based Maven repository holding a **pre-publication
build** of [falcon-det1024-java](https://github.com/Argimirodelpozo/falcon-det1024-java),
the JNA bindings for Algorand's deterministic Falcon-1024 (det1024) reference C
implementation.

It exists only so the post-quantum cucumber steps can produce real Falcon
signatures. The SDK itself contains no Falcon code: its post-quantum signers
take a signing callback, and `PQSteps` backs that callback with this library --
the same arrangement as the Python SDK (`algorand-falcon`) and the JavaScript
SDK (`falcon-1024`), both of which depend on a published package.

## Contents

- `com/algorand/falcon-det1024/0.1.1/` -- built from falcon-det1024-java at
  commit `9cb8ed318aeef65a51ababf633c971bbc303477a` (which declares
  `0.1.0-SNAPSHOT`; it is vendored here under the fixed version `0.1.1` so
  Maven does not reuse an earlier vendored build from its local cache).
  This is **not** an official 0.1.1 release.

## Platform support

The jar bundles the native library for the platform it was built on
(`linux-x86-64` here). On any other platform the post-quantum integration
scenarios will fail to load the native library; build and install the artifact
yourself until it is published:

```bash
git clone --recurse-submodules https://github.com/Argimirodelpozo/falcon-det1024-java
cd falcon-det1024-java && mvn install
```

then change the dependency version in `pom.xml` to `0.1.0-SNAPSHOT`.

## Removing this

Once falcon-det1024 is published to Maven Central:

1. delete this directory,
2. delete the `<repositories>` block in `pom.xml`,
3. bump the `com.algorand:falcon-det1024` dependency to the released version.
