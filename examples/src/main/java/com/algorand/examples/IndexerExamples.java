package com.algorand.examples;

import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.AssetResponse;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;

public class IndexerExamples {

    public static void main(String[] args) throws Exception {
        // example: INDEXER_CREATE_CLIENT
        String indexerHost = "http://localhost";
        int indexerPort = 8980;
        String indexerToken = "a".repeat(64);
        IndexerClient indexerClient = new IndexerClient(indexerHost, indexerPort, indexerToken);
        // example: INDEXER_CREATE_CLIENT

        // example: INDEXER_LOOKUP_ASSET
        Long asaId = 25l;
        Response<AssetResponse> assetResponse = indexerClient.lookupAssetByID(asaId).execute();
        Asset assetInfo = assetResponse.body().asset;
        System.out.printf("Name for %d: %s\n", asaId, assetInfo.params.name);
        // example: INDEXER_LOOKUP_ASSET

        // example: INDEXER_SEARCH_MIN_AMOUNT
        Response<TransactionsResponse> transactionSearchResult = indexerClient.searchForTransactions()
                .minRound(10l).maxRound(500l).currencyGreaterThan(10l).execute();
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
    }

}
