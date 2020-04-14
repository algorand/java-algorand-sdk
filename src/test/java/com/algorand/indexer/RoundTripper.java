package com.algorand.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import com.algorand.algosdk.v2.client.common.Client;
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
//		System.out.println(ans.toString());
		
		LookupAssetBalances lab = new LookupAssetBalances(client);
		lab.setAssetId(33);
		AssetBalancesResponse ans2 = lab.lookup();
//		System.out.println(ans2.toString());
		
		
		SearchForTransactions sft1 = new SearchForTransactions(client);
		TransactionsResponse trsp = sft1.setLimit(3).setAssetId(333).lookup();
		System.out.println(trsp.toString());

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("curl", "-s", "localhost:8980/blocks/333");

        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

		
		
	}
}
