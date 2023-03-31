package com.algorand.examples;

import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.AssetResponse;
import com.algorand.algosdk.v2.client.model.AssetsResponse;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;

public class IndexerExamples {

    public static void main(String[] args) throws Exception {
        // example: INDEXER_CREATE_CLIENT
        String indexerHost = "http://localhost";
        int indexerPort = 8980;
        String indexerToken = "a".repeat(64);
        IndexerClient indexerClient = new IndexerClient(indexerHost, indexerPort, indexerToken);
        // example: INDEXER_CREATE_CLIENT

        // Create asset for use with Indexer
        indexerClient = ExampleUtils.getIndexerClient();
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        Account acct = accts.get(0);
        System.out.println("Creating Asset");
        Long existingAssetId = ASAExamples.createAsset(algodClient, acct);

        // example: INDEXER_LOOKUP_ASSET
        Long asaId = existingAssetId;
        Response<AssetResponse> assetResponse = indexerClient.lookupAssetByID(asaId).execute();
        Asset assetInfo = assetResponse.body().asset;
        System.out.printf("Name for %d: %s\n", asaId, assetInfo.params.name);
        // example: INDEXER_LOOKUP_ASSET

        // example: INDEXER_SEARCH_MIN_AMOUNT
        Response<TransactionsResponse> transactionSearchResult = indexerClient.searchForTransactions()
                .minRound(0l).maxRound(1000l).execute();

        if (!transactionSearchResult.isSuccessful()) {
          System.out.println(transactionSearchResult.message());
          System.exit(1);
        }

        TransactionsResponse txResp = transactionSearchResult.body();
        System.out.printf("Found %d transactions that match criteria\n", txResp.transactions.size());
        // example: INDEXER_SEARCH_MIN_AMOUNT

        // example: INDEXER_PAGINATE_RESULTS
        String nextToken = "";
        boolean hasResults = true;
        // Start with empty nextToken and while there are
        // results in the transaction results, query again with the next page
        while (hasResults) {
            Response<TransactionsResponse> searchResults = indexerClient.searchForTransactions().minRound(1000l)
                    .maxRound(1500l).currencyGreaterThan(10l).next(nextToken).execute();
            TransactionsResponse txnRes = searchResults.body();
            //
            // ... do something with transaction results
            //
            hasResults = txnRes.transactions.size() > 0;
            nextToken = txnRes.nextToken;
        }
        // example: INDEXER_PAGINATE_RESULTS

        // example: INDEXER_PREFIX_SEARCH
        byte[] prefix = new String("showing prefix").getBytes();
        Response<TransactionsResponse> prefixResults = indexerClient.searchForTransactions().notePrefix(prefix)
                .execute();
        // ...
        // example: INDEXER_PREFIX_SEARCH

        System.out.println("Deleting Asset: " + existingAssetId);
        ASAExamples.deleteAsset(algodClient, acct, existingAssetId);
    }

}
