# 2.1.0

## What's Changed
Supports new devmode block timestamp offset endpoints.

### Bugfixes
* Bug-Fix: update label github action to v3 by @michaeltchuang in https://github.com/algorand/java-algorand-sdk/pull/531
### Enhancements
* algod REST API: Add test for /v2/teal/disassemble by @michaeldiamant in https://github.com/algorand/java-algorand-sdk/pull/433
* Documentation: Adds examples to be pulled into docs by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/506
* Fix: improve error message for mismatched args by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/511
* api: Regenerate Client Interfaces and implement cucumber tests. by @winder in https://github.com/algorand/java-algorand-sdk/pull/555
* DevOps: Add CODEOWNERS to restrict workflow editing by @onetechnical in https://github.com/algorand/java-algorand-sdk/pull/559

## New Contributors
* @michaeltchuang made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/531

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/2.0.0...2.1.0

# 2.0.0

## What's Changed

### Breaking changes

* Remove `TxGroup.assignGroupID(Transaction[] txns, Address address)` in favor
  of `TxGroup.assignGroupID(Address address, Transaction ...txns)`.
* Remove `Account.transactionWithSuggestedFeePerByte` in favor of `Account.setFeeByFeePerByte`.
* Remove deprecated methods in `Transaction.java`, mark public `Transaction` constructor as hidden in favor of `com.algorand.algosdk.builder.transaction`.
* Remove deprecated `Transaction.setFee` and `Transaction.setLease` methods.
* Remove v1 algod API (`com.algorand.algosdk.algod.client`) due to API end-of-life (2022-12-01). Instead, use v2 algod API (`com.algorand.algosdk.v2.client.algod`).
* Remove `cost` field in `DryrunTxnResult` in favor of 2 fields:  `budget-added` and `budget-consumed`. `cost` can be derived by `budget-consumed - budget-added`.
* Remove logicsig templates, `com/algorand/algosdk/resource/langspec.json`, `com.algorand.algosdk.logic` and methods in `com.algorand.algosdk.crypto` depending on `langspec.json`.
* Remove the deprecated `MethodCallParams` public constructor in favor of `com.algorand.algosdk.builder.transaction.MethodCallTransactionBuilder`.
* Remove unused generated types:  `CatchpointAbortResponse`, `CatchpointStartResponse`.

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.22.0...2.0.0

# 1.22.0

### Bugfixes
* BugFix: Fix incorrect reference to global schema by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/427
* Bug-Fix: parsing type strings for tuples containing static arrays of tuples by @ahangsu in https://github.com/algorand/java-algorand-sdk/pull/431
### Enhancements
* REST API: Add KV counts to NodeStatusResponse by @github-actions in https://github.com/algorand/java-algorand-sdk/pull/428
* Enhancement: Migrate v1 algod dependencies to v2 in cucumber tests by @ahangsu in https://github.com/algorand/java-algorand-sdk/pull/425
* Enhancement: Allowing zero length in static array by @ahangsu in https://github.com/algorand/java-algorand-sdk/pull/432


**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.21.1...1.22.0

# 1.21.1

## What's Changed
### Bugfixes
* SDK: Fix transaction decoding with boxes by @jasonpaulos in https://github.com/algorand/java-algorand-sdk/pull/422

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.21.0...1.21.1

# 1.21.0

## What's Changed
### New Features
* Boxes: Add support for Boxes by @michaeldiamant in https://github.com/algorand/java-algorand-sdk/pull/345

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.20.0...1.21.0

# 1.20.0

### Enhancements
* REST API: Add algod block hash endpoint, add indexer block header-only param. by @winder in https://github.com/algorand/java-algorand-sdk/pull/413

# 1.19.0

### Enhancements
* Deprecation: Add deprecation tags to v1 `algod` API by @algochoi in https://github.com/algorand/java-algorand-sdk/pull/388
### Other
* Regenerate code with the latest specification file (b243e19e) by @github-actions in https://github.com/algorand/java-algorand-sdk/pull/387

## New Contributors
* @github-actions made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/387

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.18.0...1.19.0

# 1.18.0

## What's Changed
### Bugfixes
* Bug-Fix: Pass verbosity to the harness and sandbox by @tzaffi in https://github.com/algorand/java-algorand-sdk/pull/371
### New Features
* StateProofs: Add State Proof support. by @winder in https://github.com/algorand/java-algorand-sdk/pull/360
### Enhancements
* Enhancement: Use Sandbox for Testing by @tzaffi in https://github.com/algorand/java-algorand-sdk/pull/363
* Enhancement: Deprecating use of langspec by @ahangsu in https://github.com/algorand/java-algorand-sdk/pull/367

## New Contributors
* @tzaffi made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/363

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.17.0...1.18.0

# 1.18.0-beta-1
## What's Changed
### Bugfixes
* Bug-Fix: Pass verbosity to the harness and sandbox by @tzaffi in https://github.com/algorand/java-algorand-sdk/pull/371
### New Features
* StateProofs: Add State Proof support. by @winder in https://github.com/algorand/java-algorand-sdk/pull/360
### Enhancements
* Enhancement: Use Sandbox for Testing by @tzaffi in https://github.com/algorand/java-algorand-sdk/pull/363
* Enhancement: Deprecating use of langspec by @ahangsu in https://github.com/algorand/java-algorand-sdk/pull/367

## New Contributors
* @tzaffi made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/363

**Full Changelog**: https://github.com/algorand/java-algorand-sdk/compare/1.17.0...1.18.0-beta-1

# 1.17.0
### New Features
* DevTools: adding source map decoder by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/352
### Enhancements
* Github-Actions: Adding pr title and label checks by @algojack in https://github.com/algorand/java-algorand-sdk/pull/339
* Enhancement: Add UNKNOWN enum type to HTTP client enums. by @winder in https://github.com/algorand/java-algorand-sdk/pull/351
* AVM:  Consolidate TEAL and AVM versions by @michaeldiamant in https://github.com/algorand/java-algorand-sdk/pull/348
* Testing: Modify cucumber steps to use dev mode network  by @michaeldiamant in https://github.com/algorand/java-algorand-sdk/pull/350
### Other
* Ignore copied over txt test resource files by @michaeldiamant in https://github.com/algorand/java-algorand-sdk/pull/342

# 1.16.0

## What's Changed
* adding method to abi result object by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/334
* adding tests and methods for getMethodByName by @barnjamin in https://github.com/algorand/java-algorand-sdk/pull/337

# 1.15.0

## What's Changed
* Rerun code generation based on new models by @Eric-Warehime in https://github.com/algorand/java-algorand-sdk/pull/321
* Excluding gh-pages branch from cicd by @algojack in https://github.com/algorand/java-algorand-sdk/pull/325
* Build: Add SDK code generation workflow by @Eric-Warehime in https://github.com/algorand/java-algorand-sdk/pull/327
* Add branch info to sdk gen action by @Eric-Warehime in https://github.com/algorand/java-algorand-sdk/pull/330
* Generate updated client API code by @algoidurovic in https://github.com/algorand/java-algorand-sdk/pull/318
* Introduce unified `MethodCallTransactionBuilder` & deprecate `MethodCallParams.Builder` by @jasonpaulos in https://github.com/algorand/java-algorand-sdk/pull/324

## New Contributors
* @Eric-Warehime made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/321
* @algoidurovic made their first contribution in https://github.com/algorand/java-algorand-sdk/pull/318

# 1.14.0
## Added
- Add foreign app address to dryrun creator (#315)
- Add appTrace and lsigTrace DryrunTxnResult printer utility functions. (#305)
- Add createDryrun helper function. (#284)
- Add error message with atomic transaction composer (#311)
## Changed
- Update generated client code. (#307)

# 1.13.0
- Unlimited assets regenerated code. (#302)

# 1.13.0-beta-1
- Unlimited assets regenerated code. (#302)

# 1.12.0
- Add new key reg txn field (#266)
- C2C Feature and Testing (#290)
- Implement circle ci (#293)
- Update README.md (#297)
- Update langspec for teal6 (#298)

# 1.11.0
- Support Foreign objects as ABI arguments (#283)
- Upgrade `jackson` packages to resolve PRs on vulnerability (#281)
- ABI Interaction Support for JAVA SDK (#268)
- Bug fix for `logs` on `PendingTransactionResponse` (#275)
- Add WaitForConfirmation function (#274)
- Better error message on encoding exception. (#258)
- Fix ABI source code position for ABI feature (#260)
- Add ABI encoding support (#255)

# 1.11.0-beta-2
- Support Foreign objects as ABI arguments (#283)
- Upgrade `jackson` packages to resolve PRs on vulnerability (#281)

# 1.11.0-beta-1
- ABI Interaction Support for JAVA SDK (#268)
- Bug fix for `logs` on `PendingTransactionResponse` (#275)
- Add WaitForConfirmation function (#274)
- Better error message on encoding exception. (#258)
- Revert "Revert "Fix ABI source code position for ABI feature (#260)""
- Revert "Revert "Add ABI encoding support (#255)""

# 1.10.0
- Feature/sign rekey lsig msig (#250)
- Support parsing msgpack with numeric key (#262)

# 1.9.0
- Support AVM 1.0
  - Update TEAL langspec to v5
  - Add function to get app address
  - Regenerate REST API types
  - Support new cucumber tests
  - Enable new cucumber tests
- Allow non-base64 metadata hash and force 32 bytes
- mark contract templates as @Deprecated
- Added pretty print option to Object Mapper instance

# 1.8.0
- Added toSeed() method in Account.java
- Regenerate  HTTP Client (Asset b64 fields + App extra pages)
- Set default header for base execute

# 1.7.0
- Implement dynamic opcode accounting, backward jumps, loops, callsub, retsub
- Implement ability to pool fees
- Update asset URL length to 96 bytes
- Implement ability to pay for extra pages
- Don't override values with lookupParams/suggestedParams

# 1.6.0
- Add static qualifiers to json creators for onCompletion enum serialization.
- Corrected Exception message for Keccak-256 hash function.
- Add TEAL 3 support.
- Regenerated comment.
- Fix custom token key comment.
- Update .gitignore.
- Regenerate Indexer Client.
- Regenerate client code, implement new cucumber tests.
- Fix base32 decode bug.
- Add secure vars and docker login.
- Don't override values with lookupParams/suggestedParams.
- New constructors for v2 and Indexer that also accepts tokenKey.
- Updated README example.

# 1.5.1
- Add custom header option to v2 client, new 'execute' method on each endpoint request.

# 1.5.0
- Add new execute method to  support for Algorand Smart Contract Applications.

# 1.4.2
- Accept URI formatting in AlgodClient and IndexerClient to specify connection scheme.

# 1.4.1
- Fix Algod V2 Client encoding issue.
- Add missing TransactionBuilder helper for working with suggested parameters.

# 1.4.0
- Clients for Indexer V2 and algod API V2

# 1.3.1
- Fix javadoc and build pom.xml.

# 1.3.0
- Added additional Algorand Smart Contracts (ASC)
    - Added support for Dynamic Fee contract
    - Added support for Limit Order contract
    - Added support for Periodic Payment contract
- Added new builder patterns for creating Transactions, see com.algorand.algosdk.builder.transaction.*
- Add missing getters to AssetHolding model object.

# 1.2.1
# Added
- Added asset decimals field.

# 1.2.0
# Added
- Added support for Algorand Standardized Assets (ASA)
- Added support for Algorand Smart Contracts (ASC)
    - Added support for Hashed Time Lock Contract (HTLC)
    - Added support for Split contract
- Added support for Group Transactions
- Added support for leases
# 1.1.6
## Changed
- IMPORTANT - This version modifies one of the mnemonic words. Please let your users to know that if they have the word "setupIfNeeded" in their mmnemonic, they should change it to "setup". Everything else remains the same.
# 1.1.5
## Added
- signing and verifying signatures for arbitrary bytes

# 1.1.4
## Fixed
- Compatibility with 2.9 Jackson
