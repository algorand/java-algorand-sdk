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
