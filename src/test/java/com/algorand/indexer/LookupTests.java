package com.algorand.indexer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.algorand.algosdk.v2.client.indexer.LookupAssetBalances;
import com.algorand.algosdk.v2.client.model.*;

class TestClient extends Client {

	public TestClient() {
		super("http://www.abc.com", 0);
	}

	@Override
	public String executeCall(QueryData qData, String getOrPost) throws Exception {
		
		return "{\n" + 
				"  \"balances\": [\n" + 
				"    {\n" + 
				"      \"amount\": 1,\n" + 
				"      \"address\": \"string\",\n" + 
				"      \"is-frozen\": true\n" + 
				"    }\n" + 
				"  ],\n" + 
				"  \"current-round\": 0,\n" + 
				"  \"next-token\": \"string\"\n" + 
				"}";
	}
}
public class LookupTests {

	
	
	@Test
	public void testLookupAssetBalances() {
		TestClient client = new TestClient();
		LookupAssetBalances lookup = new LookupAssetBalances(client);
		AssetBalancesResponse assetBalances = lookup.setLimit(3)
		.setRound(2)
		.setAssetId(3)
		.lookup();

		for (MiniAssetHolding ah : assetBalances.getBalances()) {
			assertEquals(1, ah.getAmount().longValue());
		}
	}
}
