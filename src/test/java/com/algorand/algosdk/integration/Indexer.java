package com.algorand.algosdk.integration;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.indexer.LookupBlock;
import com.algorand.algosdk.v2.client.model.AccountResponse;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.AssetHolding;
import com.algorand.algosdk.v2.client.model.Block;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Indexer {
    Map<Integer, Client> indexerClients = new HashMap<>();

    Response<Block> blockResponse;
    Response<AccountResponse> accountResponse;

    @Given("indexer client {int} at {string} port {int} with token {string}")
    public void indexer_client_at_port_with_token(Integer index, String uri, Integer port, String token) {
        indexerClients.put(index, new Client(uri, port));
    }

    @When("I request block {long} with indexer {int}")
    public void i_request_block_with_indexer(Long block, Integer indexer) throws Exception {
        blockResponse = new LookupBlock(indexerClients.get(indexer), block).execute();
    }

    @Then("The block was confirmed at {long}, contains {int} transactions, has the previous block hash {string}")
    public void the_block_was_confirmed_at_contains_transactions_has_the_previous_block_hash(Long unixTimestamp, Integer numTransactions, String previousBlockHash) {
        Block block = blockResponse.body();

        assertThat(block.timestamp).isEqualTo(unixTimestamp);
        assertThat(block.transactions).hasSize(numTransactions);
        assertThat(block.previousBlockHash).isEqualTo(previousBlockHash);
    }

    @When("I lookup account {string} with {int}")
    public void i_lookup_account_with(String account, Integer indexer) throws Exception {
        accountResponse = new LookupAccountByID(indexerClients.get(indexer), account).execute();
    }

    @Then("The account has {int} assets, asset {long} has a frozen status of {string} and amount {long}.")
    public void the_account_has_num_assets_asset_has_a_frozen_status_of_and_amount(Integer numAssets, Long assetId, String frozenStatus, Long amount) {
        AccountResponse response = accountResponse.body();
        assertThat(response.account.assets).hasSize(numAssets);
        Optional<AssetHolding> asset = response.account.assets.stream()
                .filter(assetHolding -> assetHolding.assetId == assetId)
                .findFirst();
        assertThat(asset).isPresent();
        assertThat(asset.get().isFrozen).isEqualTo(Boolean.parseBoolean(frozenStatus));
        assertThat(asset.get().amount).isEqualTo(amount);
    }

    @Then("The account has {long} μalgos and {int} assets")
    public void the_account_has_μalgos_and_assets(Long amount, Integer numAssets) {
        AccountResponse response = accountResponse.body();
        assertThat(response.account.assets).hasSize(numAssets);
        assertThat(response.account.amount).isEqualTo(amount);
    }

    @Then("The account created {int} assets, asset {long} is named {string} with a total amount of {long} {string}")
    public void the_account_created_assets_asset_is_named_with_a_total_amount_of(Integer numAssetsCreated, Long assetId, String assetName, Long assetTotal, String assetUnit) {
        AccountResponse response = accountResponse.body();
        assertThat(response.account.createdAssets).hasSize(numAssetsCreated);
        Optional<Asset> asset = response.account.createdAssets.stream()
                .filter(a -> a.index == assetId)
                .findFirst();
        assertThat(asset).isPresent();
        assertThat(asset.get().params.name).isEqualTo(assetName);
        assertThat(asset.get().params.unitName).isEqualTo(assetUnit);
        assertThat(asset.get().params.total).isEqualTo(assetTotal);
    }
}
