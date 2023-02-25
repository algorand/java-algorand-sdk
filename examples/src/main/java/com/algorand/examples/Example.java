package com.algorand.examples;

import com.algorand.algosdk.v2.client.common.AlgodClient;

public class Example {
    public static void main(String[] args) {
        System.out.println("hi");
        AlgodClient c = ExampleUtils.getAlgodClient();
    }

    public static void createClient() {
        // example: ALGOD_CREATE_CLIENT
        String algodHost = "http://localhost";
        int algodPort = 4001;
        String algodToken = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        AlgodClient algodClient = AlgodClient(algodHost, algodPort, algodToken);

        // OR if the API provider requires a specific header key for the token
        String tokenHeader = "X-API-Key";
        AlgodClient otherAlgodClient = AlgodClient(algodHost, algodPort, algodToken, tokenHeader);
        // example: ALGOD_CREATE_CLIENT
    }
}

// Missing ATOMIC_CREATE_TXNS in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:71)
// Missing ATOMIC_GROUP_TXNS in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:154)
// Missing ATOMIC_GROUP_SIGN in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:222)
// Missing ATOMIC_GROUP_ASSEMBLE in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:284)
// Missing ATOMIC_GROUP_SEND in JAVASDK examples (in
// ../docs/get-details/atomic_transfers.md:346)
// Missing MULTISIG_CREATE in JAVASDK examples (in
// ../docs/get-details/transactions/signatures.md:256)
// Missing MULTISIG_SIGN in JAVASDK examples (in
// ../docs/get-details/transactions/signatures.md:258)
// Missing TRANSACTION_FEE_OVERRIDE in JAVASDK examples (in
// ../docs/get-details/transactions/index.md:791)
// Missing CODEC_TRANSACTION_UNSIGNED in JAVASDK examples (in
// ../docs/get-details/transactions/offline_transactions.md:62)
// Missing CODEC_TRANSACTION_SIGNED in JAVASDK examples (in
// ../docs/get-details/transactions/offline_transactions.md:268)
// Missing KMD_CREATE_CLIENT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:115)
// Missing KMD_CREATE_WALLET in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:132)
// Missing KMD_CREATE_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:150)
// Missing KMD_RECOVER_WALLET in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:294)
// Missing KMD_EXPORT_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:488)
// Missing KMD_IMPORT_ACCOUNT in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:690)
// Missing ACCOUNT_GENERATE in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:894)
// Missing MULTISIG_CREATE in JAVASDK examples (in
// ../docs/get-details/accounts/create.md:1030)
// Missing ACCOUNT_REKEY in JAVASDK examples (in
// ../docs/get-details/accounts/rekey.md:364)
// Missing CODEC_ADDRESS in JAVASDK examples (in
// ../docs/get-details/encoding.md:86)
// Missing CODEC_BASE64 in JAVASDK examples (in
// ../docs/get-details/encoding.md:138)
// Missing CODEC_UINT64 in JAVASDK examples (in
// ../docs/get-details/encoding.md:196)
// Missing CODEC_TRANSACTION_UNSIGNED in JAVASDK examples (in
// ../docs/get-details/encoding.md:337)
// Missing CODEC_TRANSACTION_SIGNED in JAVASDK examples (in
// ../docs/get-details/encoding.md:339)
// Missing CODEC_BLOCK in JAVASDK examples (in
// ../docs/get-details/encoding.md:363)
// Missing CONST_MIN_FEE in JAVASDK examples (in
// ../docs/get-details/dapps/smart-contracts/guidelines.md:62)
// Missing SP_MIN_FEE in JAVASDK examples (in
// ../docs/get-details/dapps/smart-contracts/guidelines.md:96)
// Missing ASSET_CREATE in JAVASDK examples (in ../docs/get-details/asa.md:203)
// Missing ASSET_CONFIG in JAVASDK examples (in ../docs/get-details/asa.md:429)
// Missing ASSET_OPTIN in JAVASDK examples (in ../docs/get-details/asa.md:628)
// Missing ASSET_XFER in JAVASDK examples (in ../docs/get-details/asa.md:809)
// Missing ASSET_FREEZE in JAVASDK examples (in ../docs/get-details/asa.md:1007)
// Missing ASSET_CLAWBACK in JAVASDK examples (in
// ../docs/get-details/asa.md:1193)
// Missing ASSET_DELETE in JAVASDK examples (in ../docs/get-details/asa.md:1386)
// Missing ASSET_INFO in JAVASDK examples (in ../docs/get-details/asa.md:1549)
// Missing ATC_CREATE in JAVASDK examples (in ../docs/get-details/atc.md:48)
// Missing ATC_ADD_TRANSACTION in JAVASDK examples (in
// ../docs/get-details/atc.md:142)
// Missing ATC_CONTRACT_INIT in JAVASDK examples (in
// ../docs/get-details/atc.md:307)
// Missing ATC_ADD_METHOD_CALL in JAVASDK examples (in
// ../docs/get-details/atc.md:325)
// Missing ATC_RESULTS in JAVASDK examples (in ../docs/get-details/atc.md:406)
// Missing CREATE_INDEXER_CLIENT in JAVASDK examples (in
// ../docs/get-details/indexer.md:42)
// Missing INDEXER_LOOKUP_ASSET in JAVASDK examples (in
// ../docs/get-details/indexer.md:131)
// Missing INDEXER_SEARCH_MIN_AMOUNT in JAVASDK examples (in
// ../docs/get-details/indexer.md:205)
// Missing INDEXER_PAGINATE_RESULTS in JAVASDK examples (in
// ../docs/get-details/indexer.md:322)
// Missing INDEXER_PREFIX_SEARCH in JAVASDK examples (in
// ../docs/get-details/indexer.md:477)
// Missing TRANSACTION_KEYREG_OFFLINE_CREATE in JAVASDK examples (in
// ../docs/run-a-node/participate/offline.md:41)
// Missing TRANSACTION_KEYREG_ONLINE_CREATE in JAVASDK examples (in
// ../docs/run-a-node/participate/online.md:42)
