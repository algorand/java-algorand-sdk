# java-algorand-sdk

AlgoSDK is a Java library for communicating and interacting with the Algorand network. It contains a REST client for accessing `algod` instances over the web,
and also exposes functionality for generating keypairs, mnemonics, creating transactions, signing transactions, and serializing data across the network.

This SDK supports Java 7 and can be made compatible with Android `minSdkVersion` 16.

# Installation

Maven (coming soon).

Download the jars (coming soon).

To build from source:
```
bazel build //:algosdk
bazel build //:algosdk_rest_api
```

# Documentation

Javadoc (coming soon).

# Quickstart

```java
package com.algorand.algosdk.example;

import com.algorand.algosdk.algod.client.ApiClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.DefaultApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.NodeStatus;


public class Main {

    public static void main(String args[]) throws Exception {
        final String ALGOD_API_ADDR = "http://localhost:8080";
        final String ALGOD_API_TOKEN = "d6f33a522f465ff12f0d263f2c3b707ac2f560bacad4d859914ada7e827902b3";

        ApiClient client = new ApiClient().setBasePath(ALGOD_API_ADDR);
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(ALGOD_API_TOKEN);

        DefaultApi algodApiInstance = new DefaultApi(client);
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

# Longer Example

Take a look at `com.algorand.algodsdk.example`. For instance:

```java
final String ALGOD_API_ADDR = "http://localhost:8080";
final String ALGOD_API_TOKEN = "d6f33a522f465ff12f0d263f2c3b707ac2f560bacad4d859914ada7e827902b3";
final String SRC_ACCOUNT = "viable grain female caution grant mind cry mention pudding oppose orchard people forget similar social gossip marble fish guitar art morning ring west above concert";
final String DEST_ADDR = "KV2XGKMXGYJ6PWYQA5374BYIQBL3ONRMSIARPCFCJEAMAHQEVYPB7PL3KU";

ApiClient client = new ApiClient();
client.setBasePath(ALGOD_API_ADDR);
ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
api_key.setApiKey(ALGOD_API_TOKEN);
DefaultApi algodApiInstance = new DefaultApi(client);

// set up crypto, in order to sign transactions
if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
    Security.addProvider(new BouncyCastleProvider());
}

// get suggested fee for transaction
long fee = 1;
try {
    TransactionParams params = algodApiInstance.transactionParams();
    fee = params.getFee();
    System.out.println("Suggested Fee: " + fee);
} catch (ApiException e) {
    System.err.println("Exception when calling algod#transactionParams");
    e.printStackTrace();
}

// Generate a new transaction using specified account
Account src = new Account(SRC_ACCOUNT);
long amount = 100;
long firstRound = 300;
long lastRound = 400;
Transaction tx = new Transaction(src.getAddress(), new Address(DEST_ADDR), fee, amount, firstRound, lastRound);
SignedTransaction signedTx = src.signTransaction(tx);
System.out.println("Signed transaction with txid: " + signedTx.transactionID);

// send the transaction to the network. This will be rejected since the source account has no money.
try {
    byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
    TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);
    System.out.println("Successfully sent tx with id: " + id);
} catch (ApiException e) {
    // This is generally expected, but should give us an informative error message.
    System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
}
```

# Cryptography

To use the library, the SDK requires you specify a JCA provider for `Ed25519` signatures and `SHA-512/256` checksums. This SDK
is tested with `BouncyCastle 1.61` (bouncycastle.org). This SDK also bundles Bouncy Castle for your convenience. In your applications,
setup the SDK, like the following example code:

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
...
// set up crypto, in order to sign transactions
if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
    Security.addProvider(new BouncyCastleProvider());
}
// now, do work
```

In addition, AlgoSDK depends on `org.bouncycastle:bcprov-jdk15on:1.61` for deserializing `X.509`-encoded `Ed25519` private keys. This is the only
dependency on an external crypto library - all other references are abstracted through the JCA.


# Contributing to this Project

This project uses `bazel`. In addition, it uses the `maven_install` bazel rule to fetch `maven` dependencies.
More information can be found at https://blog.bazel.build/2019/03/31/rules-jvm-external-maven.html.

We should monitor the progress of features such as artifact checksumming.

To run the example project:
```
bazel run //examples/src/main/java/com/algorand/algosdk/example:example
```

To build the main library:
`bazel build //:algosdk`

To build the REST client libraries:
`bazel build //:algosdk_rest_api`

To test:
`bazel test //...`

To build specific libraries (pick and choose, e.g. for account library)
`bazel build //src/main/java/com/algorand/algosdk/account:account`

## Updating the `algod` and `kmd` REST clients
The `algod` and `kmd` REST clients are largely autogenerated by `swagger-codegen`. [https://github.com/swagger-api/swagger-codegen]

To regenerate the clients, first, check out the latest `swagger-codegen` from the github repo. (In particular, the Homebrew version
is out of date and fails to handle raw byte arrays properly). Then:

```
swagger-codegen generate -i http://path-of-algod-or-kmd-api/swagger.json -l java -c config.json
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

Finally, make sure to update all (hardcoded) `BUILD.bazel` files appropriately. In particular, the generated code
(as of April 2019) has one circular dependency involving `client.Pair`. The `client` package depends on `client.auth`, but
`client.auth` uses `client.Pair` which is in the `client` package. One more problem is that `uint64` is not a valid format
in OpenAPI 2.0; however, we need to send large integers to the `algod` API (`kmd` is fine).
To resolve this, we do the following manual pass on generated code:

- Move `Pair.java` into the `client.lib` package (with appropriate `BUILD.bazel` file and renaming);
- Find-and-replace `Integer` with `Long` in `com.algorand.algosdk.algod` and subpackages;
- Run an `Optimize Imports` operation on generated code, to minimize dependencies.


## Maven Support

We also maintain a `pom.xml` file for Maven support. Make sure to update the maven coordinates tags
appropriately, in synch with the actual dependencies. We should monitor the status of `maven_install` development
because tags may no longer be needed in the future. An example `BUILD.bazel` file might look like:
```bazel
java_library(
    name = "model",
    srcs = glob(["*.java"]),
    visibility = ["//visibility:public"],
    exports = [
    ],
    deps = [
        "@mavenforswagger//:io_swagger_swagger_annotations",
        "@mavenforswagger//:org_apache_commons_commons_lang3",
        "@mavenforswagger//:com_google_code_gson_gson",
    ],
    tags = [
        "maven_coordinates=io.swagger:swagger-annotations:1.5.18",
        "maven_coordinates=org.apache.commons:commons-lang3:3.6",
        "maven_coordinates=com.google.code.gson:gson:2.8.1",
    ]
)
```

To build the `pom.xml` file with `bazel` (with dependencies on `bazel-common` and `skylib`)
```
bazel build //:algosdk_pom
bazel build //:algosdk_rest_api_pom
```


# Android Support

Significant work has been taken to ensure Android compatibility (in particular for `minSdkVersion` 16). Note that the
default crypto provider on Android does not provide `ed25519` signatures, so you will need to provide your own (e.g. `BouncyCastle`).
