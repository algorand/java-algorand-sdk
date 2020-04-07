package com.algorand.indexer;

import org.junit.jupiter.api.Test;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.indexer.SearchForTransactions;
import com.algorand.algosdk.v2.client.model.AssetBalancesResponse;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;

class RoundTripper {

	@Test
	void Transaction() {
		Client client = new Client("localhost", 8980);
		SearchForTransactions sft = new SearchForTransactions(client);
		sft.setRound(5904023);
		TransactionsResponse ans = sft.lookup();
		System.out.println(ans.toString());
		
		LookupAssetBalances lab = new LookupAssetBalances(client);
		lab.setAssetId(33);
		AssetBalancesResponse ans2 = lab.lookup();
		System.out.println(ans2.toString());
	}

}
