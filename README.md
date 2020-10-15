
| IMPORTANT <br /> Version 1.1.6 modifies one of the mnemonic words. Please let your users know that if they have the word "setupIfNeeded" in their passphrase, they should change it to "setup". Everything else remains the same.|
|---| 

# java-algorand-sdk

[![Build Status](https://travis-ci.com/algorand/java-algorand-sdk.svg?branch=master)](https://travis-ci.com/algorand/java-algorand-sdk?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.algorand/algosdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.algorand/algosdk/)

AlgoSDK is a Java library for communicating and interacting with the Algorand network. It contains a REST client for accessing `algod` instances over the web,
and also exposes functionality for generating keypairs, mnemonics, creating transactions, signing transactions, and serializing data across the network.


# Prerequisites

Java 7+ and Android `minSdkVersion` 16+

# Installation

Maven:

```xml
<dependency>
    <groupId>com.algorand</groupId>
    <artifactId>algosdk</artifactId>
    <version>1.5.1</version>
</dependency>
```

# Quickstart

```java
package com.algorand.algosdk.example;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.NodeStatus;


public class Main {

    public static void main(String args[]) throws Exception {
        final String ALGOD_API_ADDR = "http://localhost:8080";
        final String ALGOD_API_TOKEN = "d6f33a522f465ff12f0d263f2c3b707ac2f560bacad4d859914ada7e827902b3";

        AlgodClient client = new AlgodClient();
        client.setBasePath(ALGOD_API_ADDR);
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(ALGOD_API_TOKEN);

        AlgodApi algodApiInstance = new AlgodApi(client);
        try {
            NodeStatus status = algodApiInstance.getStatus();
            System.out.println("Algorand network status: " + status);
        } catch (ApiException e) {
            System.err.println("Exception when calling algod#getStatus");
            e.printStackTrace();
        }
    }

}
```
This prints:
```
Algorand network status: class NodeStatus {
    catchupTime: 0
    lastConsensusVersion: v4
    lastRound: 260318
    nextConsensusVersion: v4
    nextConsensusVersionRound: 260319
    nextConsensusVersionSupported: true
    timeSinceLastRound: 3620331759
}
```

# Documentation

Javadoc can be found at [https://algorand.github.io/java-algorand-sdk](https://algorand.github.io/java-algorand-sdk). <br /> 
Additional resources and code samples are located at [https://developer.algorand.org](https://developer.algorand.org).

# Cryptography

AlgoSDK depends on `org.bouncycastle:bcprov-jdk15on:1.61` for `Ed25519` signatures, `sha512/256` digests, and deserializing `X.509`-encoded `Ed25519` private keys.
The latter is the only explicit dependency on an external crypto library - all other references are abstracted through the JCA.

# Java 9+

When using cryptographic functionality, and Java9+, you may run into the following warning:
```
WARNING: Illegal reflective access by org.bouncycastle.jcajce.provider.drbg.DRBG
```
This is known behavior, caused by more restrictive language features in Java 9+, that Bouncy Castle has yet to support. This warning can be suppressed safely. We will monitor
cryptographic packages for updates or alternative implementations.

# Contributing to this Project

## build

This project uses Maven.

### **To build**
```
~$ mvn package
```

**To run the example project**
Use the following command in the examples directory, be sure to update your algod network address and the API token
parameters (see examples/README for more information):
```
~$ mvn exec:java -Dexec.mainClass="com.algorand.algosdk.example.Main" -Dexec.args="127.0.0.1:8080 ***X-Algo-API-Token***"
```

### **To test**
We are using separate version targets for production and testing to allow using JUnit5 for tests. Some IDEs, like IDEA
do not support this very well. To workaround the issue a special `ide` profile should be enabled if your IDE does not
support mixed `target` and `testTarget` versions. Regardless of IDE support, the tests can be run from the command line.
In this case `clean` is used in case an incremental build was made by the IDE with Java8.
```
~$ mvn clean test
```

There is also a special integration test environment, and shared tests. To run these use the Makefile:
```
~$ make docker-test
```

## deploying artifacts

The generated pom file provides maven compatibility and deploy capabilities.
```
mvn clean install
mvn clean deploy -P github,default
mvn clean site -P github,default  # for javadoc
mvn clean deploy -P release,default
```

# Android Support

Significant work has been taken to ensure Android compatibility (in particular for `minSdkVersion` 16). Note that the
default crypto provider on Android does not provide `ed25519` signatures, so you will need to provide your own (e.g. `BouncyCastle`).

# Algod V2 and Indexer Code Generation
The classes `com.algorand.algosdk.v2.client.algod.\*`, `com.algorand.algosdk.v2.client.indexer.\*`, `com.algorand.algosdk.v2.client.common.AlgodClient`, and `com.algorand.algosdk.v2.client.common.IndexerClient` are generated from OpenAPI specifications in: `algod.oas2.json` and `indexer.oas2.json`.

The specification files can be obtained from:
- [algod.oas2.json](https://github.com/algorand/go-algorand/blob/master/daemon/algod/api/algod.oas2.json)
- [indexer.oas2.json](https://github.com/algorand/indexer/blob/master/api/indexer.oas2.json)

A testing framework can also be generated with: `com.algorand.sdkutils.RunQueryMapperGenerator` and the tests run from `com.algorand.sdkutils.RunAlgodV2Tests` and `com.algorand.sdkutils.RunIndexerTests`

## Regenerate the Client Code

To actually regenerate the code, use `run_generator.sh` with paths to the `*.oas2.json` files mentioned above.

# Updating the `kmd` REST client
The `kmd` REST client has not been upgraded to use the new code generation, it is still largely autogenerated by `swagger-codegen`. [https://github.com/swagger-api/swagger-codegen]

To regenerate the clients, first, check out the latest `swagger-codegen` from the github repo. (In particular, the Homebrew version
is out of date and fails to handle raw byte arrays properly). Note OpenAPI 2.0 doesn't support unsigned types. Luckily we don't have any
uint32 types in algod, so we can do a lossless type-mapping fromt uint64->int64 (Long) -> BigInteger:

```
curl http://localhost:8080/swagger.json | sed -e 's/uint32/int64/g' > temp.json
swagger-codegen generate -i temp.json -l java -c config.json
```

`config.json` looks like:
```json
{
  "library": "okhttp-gson",
  "java8": false,
  "hideGenerationTimestamp": true,
  "serializableModel": false,
  "supportJava6": true,
  "invokerPackage": "com.algorand.algosdk.{kmd or algod}.client",
  "apiPackage": "com.algorand.algosdk.{kmd or algod}.client.api",
  "modelPackage": "com.algorand.algosdk.{kmd or algod}.client.model"
}
```

Make sure you convert all `uint32` types to `Long` types.

The generated code (as of April 2019) has one circular dependency involving
`client.Pair`. The `client` package depends on `client.auth`, but `client.auth`
uses `client.Pair` which is in the `client` package. One more problem is that
`uint64` is not a valid format in OpenAPI 2.0; however, we need to send large
integers to the `algod` API (`kmd` is fine). To resolve this, we do the
following manual pass on generated code:

- Move `Pair.java` into the `client.lib` package
- Find-and-replace `Integer` with `BigInteger` (for uint64), `Long` (for uint32), etc. in `com.algorand.algosdk.algod` and subpackages (unnecessary for algod)
- Run an `Optimize Imports` operation on generated code, to minimize dependencies.

Note that msgpack-java is good at using the minimal representation.
