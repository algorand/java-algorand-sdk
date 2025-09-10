# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Build
```bash
mvn package
```

### Testing
```bash
# Run unit tests only
make unit
# or: mvn test -Dcucumber.filter.tags="@unit"

# Run integration tests only  
make integration
# or: mvn test -Dtest=com.algorand.algosdk.integration.RunCucumberIntegrationTest -Dcucumber.filter.tags="@integration"

# Run all tests (unit + integration)
make ci-test

# Run tests with Docker test harness
make docker-test

# Start/stop test harness manually
make harness
make harness-down
```

### Examples
The `examples/` directory contains example code:
```bash
cd examples/
mvn package
java -cp target/sdk-extras-1.0-SNAPSHOT.jar com.algorand.examples.Example
```

## Architecture Overview

This is the official Java SDK for Algorand blockchain, providing client libraries for interacting with algod and indexer nodes.

### Core Package Structure

- **`account/`** - Account management, keypair generation, and logic signature accounts
- **`crypto/`** - Core cryptographic primitives (Ed25519, addresses, signatures, multisig)
- **`transaction/`** - Transaction types, builders, and atomic transaction composer
- **`v2/client/`** - REST clients for algod and indexer APIs (auto-generated from OpenAPI specs)
- **`builder/transaction/`** - Fluent transaction builders for all transaction types
- **`abi/`** - Application Binary Interface support for smart contracts
- **`mnemonic/`** - BIP39 mnemonic phrase utilities
- **`util/`** - Encoding/decoding utilities (MessagePack, Base64, etc.)

### Key Components

#### Transaction System
- **Transaction Builder Pattern**: All transaction types use fluent builders (e.g., `PaymentTransactionBuilder`, `ApplicationCallTransactionBuilder`)
- **Atomic Transaction Composer**: High-level interface for building transaction groups with ABI method calls
- **Transaction Types**: Payment, asset transfer, application calls, key registration, state proof, heartbeat

#### Client Architecture
- **AlgodClient**: REST client for algod daemon (node operations, transaction submission)
- **IndexerClient**: REST client for indexer service (historical queries, account lookups)
- **Generated Code**: Most v2 client classes are auto-generated from OpenAPI specifications

#### Cryptography
- **Ed25519**: Primary signature algorithm using BouncyCastle
- **Multisig**: M-of-N threshold signatures
- **Logic Signatures**: Delegated signing using TEAL programs
- **Address**: 32-byte public key hash with checksum

## Code Generation

The v2 client code (`v2.client.algod.*`, `v2.client.indexer.*`, `v2.client.model.*`) is generated from OpenAPI specs:
- algod.oas2.json - algod daemon API
- indexer.oas2.json - indexer service API

To regenerate clients, use the `generate_java.sh` script from the [generator](https://github.com/algorand/generator/) repository.

## Testing Framework

Uses Cucumber BDD testing with separate unit and integration test suites:
- **Unit tests**: Fast tests using mocked clients (`@unit` tags)  
- **Integration tests**: Full integration with test harness (`@integration` tags)
- **Test harness**: Dockerized Algorand network for integration testing

Test files are organized under:
- `src/test/java/com/algorand/algosdk/unit/` - Unit test step definitions
- `src/test/java/com/algorand/algosdk/integration/` - Integration test step definitions
- `src/test/resources/` - Test data and response fixtures
- `test-harness/features/` - Cucumber feature files

## Development Notes

- **Java 8+ Compatibility**: Maintains Java 8 compatibility with Android support (minSdkVersion 26+)
- **Dependencies**: Uses Jackson for JSON, MessagePack for serialization, BouncyCastle for crypto
- **Maven Profiles**: Includes IDE profile for IntelliJ compatibility with mixed Java versions
- **Generated Content**: Do not manually edit generated client code - regenerate from specs instead