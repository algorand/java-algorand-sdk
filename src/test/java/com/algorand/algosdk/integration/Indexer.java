package com.algorand.algosdk.integration;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.indexer.*;
import com.algorand.algosdk.v2.client.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Indexer {
    Map<Integer, Client> indexerClients = new HashMap<>();

    Response<Block> blockResponse;
    Response<AccountResponse> accountResponse;
    Response<AccountsResponse> accountsResponse;
    Response<AssetResponse> assetResponse;
    Response<AssetBalancesResponse> assetBalancesResponse;
    Response<TransactionsResponse> transactionsResponse;
    Response<AssetsResponse> assetsResponse;

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }


    @Given("indexer client {int} at {string} port {int} with token {string}")
    public void indexer_client_at_port_with_token(Integer index, String uri, Integer port, String token) {
        indexerClients.put(index, new Client(uri, port));
    }

    @When("I use {int} to lookup block {long}")
    public void i_request_block_with_indexer(Integer indexer, Long block) throws Exception {
        blockResponse = indexerClients.get(indexer).lookupBlock(block).execute();
    }

    @Then("The block was confirmed at {long}, contains {int} transactions, has the previous block hash {string}")
    public void the_block_was_confirmed_at_contains_transactions_has_the_previous_block_hash(Long unixTimestamp, Integer numTransactions, String previousBlockHash) {
        Block block = blockResponse.body();

        assertThat(block.timestamp).isEqualTo(unixTimestamp);
        assertThat(block.transactions).hasSize(numTransactions);
        assertThat(block.previousBlockHash).isEqualTo(Encoder.decodeFromBase64(previousBlockHash));
    }

    @When("I use {int} to lookup account {string} at round {long}")
    public void i_lookup_account_with(Integer indexer, String account, Long round) throws Exception {
        LookupAccountByID query = indexerClients.get(indexer).lookupAccountByID(new Address(account));
        if (round != 0) {
            query.round(round);
        }
        accountResponse = query.execute();
    }

    @Then("The account has {int} assets, the first is asset {long} has a frozen status of {string} and amount {biginteger}.")
    public void the_account_has_num_assets_asset_has_a_frozen_status_of_and_amount(Integer numAssets, Long assetId, String frozenStatus, BigInteger amount) {
        AccountResponse response = accountResponse.body();

        assertThat(response.account.assets).hasSize(numAssets);
        AssetHolding holding = response.account.assets.get(0);
        assertThat(holding.assetId).isEqualTo(assetId);
        assertThat(holding.isFrozen).isEqualTo(Boolean.parseBoolean(frozenStatus));
        assertThat(holding.amount).isEqualTo(amount);
    }

    @Then("The account has {long} μalgos and {int} assets, {long} has {biginteger}")
    public void the_account_has_μalgos_and_assets(Long amount, Integer numAssets, Long assetId, BigInteger assetAmount) {
        AccountResponse response = accountResponse.body();
        assertThat(response.account.assets).hasSize(numAssets);
        assertThat(response.account.amount).as("μalgos").isEqualTo(amount);

        if (assetId != 0) {
            response.account.assets.forEach(a -> {
                if (a.assetId == assetId) {
                    assertThat(a.amount).as("assets").isEqualTo(assetAmount);
                }
            });
        }
    }

    @Then("The account created {int} assets, the first is asset {long} is named {string} with a total amount of {biginteger} {string}")
    public void the_account_created_assets_the_first_is_asset_is_named_with_a_total_amount_of(Integer numAssetsCreated, Long assetId, String assetName, BigInteger assetTotal, String assetUnit) {
        AccountResponse response = accountResponse.body();
        assertThat(response.account.createdAssets).hasSize(numAssetsCreated);
        Asset asset = response.account.createdAssets.get(0);
        assertThat(asset.params.name).isEqualTo(assetName);
        assertThat(asset.params.unitName).isEqualTo(assetUnit);
        assertThat(asset.params.total).isEqualTo(assetTotal);
    }

    @When("I use {int} to lookup asset {long}")
    public void i_lookup_asset_with(Integer indexer, Long assetId) throws Exception {
        assetResponse = indexerClients.get(indexer).lookupAssetByID(assetId).execute();
    }

    @Then("The asset found has: {string}, {string}, {string}, {long}, {string}, {biginteger}, {string}")
    public void the_asset_found_has(String name, String units, String creatorAddress, Long decimals, String defaultFrozen, BigInteger total, String clawbackAddress) {
        AssetResponse response = assetResponse.body();
        AssetParams params = response.asset.params;

        assertThat(params.name).isEqualTo(name);
        assertThat(params.unitName).isEqualTo(units);
        assertThat(params.creator).isEqualTo(creatorAddress);
        assertThat(params.decimals).isEqualTo(decimals);
        assertThat(params.defaultFrozen).isEqualTo(Boolean.parseBoolean(defaultFrozen));
        assertThat(params.total).isEqualTo(total);
        assertThat(params.clawback).isEqualTo(clawbackAddress);
    }

    @When("I use {int} to lookup asset balances for {long} with {long}, {long}, {long} and token {string}")
    public void i_lookup_asset_balances_for_with_with(Integer indexer, Long assetId, Long currencyGT, Long currencyLT, Long limit, String next) throws Exception {
        LookupAssetBalances query = indexerClients.get(indexer).lookupAssetBalances(assetId);
        if (currencyGT != 0) {
            query.currencyGreaterThan(currencyGT);
        }
        if (currencyLT != 0) {
            query.currencyLessThan(currencyLT);
        }
        if (limit != 0) {
            query.limit(limit);
        }
        if (StringUtils.isNotEmpty(next)) {
            query.next(next);
        }
        assetBalancesResponse = query.execute();
    }

    @Then("There are {int} with the asset, the first is {string} has {string} and {biginteger}")
    public void there_are_with_the_asset_the_first_is_has_and(Integer numResults, String account, String frozenState, BigInteger amount) {
        AssetBalancesResponse response = assetBalancesResponse.body();

        assertThat(response.balances).hasSize(numResults);

        MiniAssetHolding holding = response.balances.get(0);

        assertThat(holding.isFrozen).isEqualTo(Boolean.parseBoolean(frozenState));
        assertThat(holding.amount).isEqualTo(amount);
    }

    @When("I get the next page using {int} to lookup asset balances for {long} with {long}, {long}, {long}")
    public void i_get_the_next_page_using_to_search_for_asset_balances_with(Integer indexer, Long assetId, Long currencyGT, Long currencyLT, Long limit) throws Exception {
        String token = assetBalancesResponse.body().nextToken;
        i_lookup_asset_balances_for_with_with(indexer, assetId, currencyGT, currencyLT, limit, token);
    }

    @When("I use {int} to search for an account with {long}, {long}, {long}, {long} and token {string}")
    public void searchForAnAccount(Integer indexer, Long assetId, Long limit, Long gt, Long lt, String token) throws Exception {
        SearchForAccounts query = indexerClients.get(indexer).searchForAccounts();
        if (assetId != 0) {
            query.assetId(assetId);
        }
        if (limit != 0) {
            query.limit(limit);
        }
        if (gt != 0) {
            query.currencyGreaterThan(gt);
        }
        if (lt != 0) {
            query.currencyLessThan(lt);
        }
        if (StringUtils.isNotEmpty(token)) {
            query.next(token);
        }
        accountsResponse = query.execute();
    }

    @Then("There are {int}, the first has {long}, {long}, {long}, {long}, {string}, {long}, {string}, {string}")
    public void there_are_the_first_has(Integer numAccounts, Long pendingRewards, Long rewardsBase, Long rewards, Long amountWithoutPendingRewards, String address, Long amount, String status, String type) {
        AccountsResponse response = accountsResponse.body();

        assertThat(response.accounts).hasSize(numAccounts);

        Account account = response.accounts.get(0);
        assertThat(account.address.toString()).isEqualTo(address);
        assertThat(account.pendingRewards).isEqualTo(pendingRewards);
        assertThat(account.rewardBase).isEqualTo(rewardsBase);
        assertThat(account.rewards).isEqualTo(rewards);
        assertThat(account.amountWithoutPendingRewards).isEqualTo(amountWithoutPendingRewards);
        assertThat(account.amount).isEqualTo(amount);
        assertThat(account.status).isEqualTo(status);
        assertThat(account.sigType).isEqualTo(searchEnum(Enums.SigType.class, type));
    }

    @Then("The first account is online and has {string}, {long}, {long}, {long}, {string}, {string}")
    public void the_first_account_is_online_and_has(String address, Long keyDilution, Long firstValid, Long lastValid, String voteKey, String selectionKey) {
        Account account = accountsResponse.body().accounts.get(0);
        assertThat(account.address.toString()).isEqualTo(address);

        AccountParticipation participation = account.participation;
        assertThat(participation).isNotNull();
        assertThat(participation.voteKeyDilution).isEqualTo(keyDilution);
        assertThat(participation.voteFirstValid).isEqualTo(firstValid);
        assertThat(participation.voteLastValid).isEqualTo(lastValid);
        assertThat(participation.voteParticipationKey.getBytes()).isEqualTo(Encoder.decodeFromBase64(voteKey));
        assertThat(participation.selectionParticipationKey.getBytes()).isEqualTo(Encoder.decodeFromBase64(selectionKey));
    }

    @Then("I get the next page using {int} to search for an account with {long}, {long}, {long} and {long}")
    public void i_get_the_next_page_using_to_search_for_an_account_with_and(Integer indexer, Long assetId, Long limit, Long gt, Long lt) throws Exception {
        AccountsResponse response = accountsResponse.body();
        searchForAnAccount(indexer, assetId, limit, gt, lt, response.nextToken);
    }

    @When("I use {int} to search for transactions with {long}, {string}, {string}, {string}, {string}, {long}, {long}, {long}, {long}, {string}, {string}, {long}, {long}, {string}, {string}, {string} and token {string}")
    public void i_use_to_search_for_transactions_with_and_token(Integer indexer, Long limit, String notePrefix,
                                                                String txType, String sigType, String txId, Long round,
                                                                Long minRound, Long maxRound, Long assetId,
                                                                String beforeTime, String afterTime, Long currencyGT,
                                                                Long currencyLT, String address, String addressRole,
                                                                String excludeCloseTo, String token) throws Exception {
        SearchForTransactions query = indexerClients.get(indexer).searchForTransactions();

        if (limit != 0) {
            query.limit(limit);
        }
        if (StringUtils.isNotEmpty(notePrefix)) {
            query.notePrefix(Encoder.decodeFromBase64(notePrefix));
        }
        if (StringUtils.isNotEmpty(txType)) {
            query.txType(searchEnum(Enums.TxType.class, txType));
        }
        if (StringUtils.isNotEmpty(sigType)) {
            query.sigType(searchEnum(Enums.SigType.class, sigType));
        }
        if (StringUtils.isNotEmpty(txId)) {
            query.txid(txId);
        }
        if (round != 0) {
            query.round(round);
        }
        if (minRound != 0) {
            query.minRound(minRound);
        }
        if (maxRound != 0) {
            query.maxRound(maxRound);
        }
        if (assetId != 0) {
            query.assetId(assetId);
        }
        if (StringUtils.isNotEmpty(beforeTime)) {
            query.beforeTime(Date.from(Instant.parse(beforeTime)));
        }
        if (StringUtils.isNotEmpty(afterTime)) {
            query.afterTime(Date.from(Instant.parse(afterTime)));
        }
        if (currencyGT != 0) {
            query.currencyGreaterThan(currencyGT);
        }
        if (currencyLT != 0) {
            query.currencyLessThan(currencyLT);
        }
        if (StringUtils.isNotEmpty(address)) {
            query.address(new Address(address));
        }
        if (StringUtils.isNotEmpty(addressRole)) {
            query.addressRole(Enums.AddressRole.valueOf(addressRole.toUpperCase()));
        }
        if (StringUtils.isNotEmpty(excludeCloseTo)) {
            query.excludeCloseTo(Boolean.parseBoolean(excludeCloseTo));
        }
        if (StringUtils.isNotEmpty(token)) {
            query.next(token);
        }

        transactionsResponse = query.execute();
    }

    @Then("there are {int} transactions in the response, the first is {string}.")
    public void there_are_transactions_in_the_response_the_first_is(Integer num, String txid) throws NoSuchAlgorithmException {
        TransactionsResponse transactions = transactionsResponse.body();

        assertThat(transactions.transactions).hasSize(num);

        // Don't check the txid if there weren't supposed to be any results.
        if (num == 0) return;

        assertThat(transactions.transactions)
                .first()
                .hasFieldOrPropertyWithValue("id", txid);
    }

    @And("Every transaction has tx-type {string}")
    public void every_transaction_has_txtype(String type) {
        TransactionsResponse transactions = transactionsResponse.body();

        transactions.transactions.forEach(tx -> {
            assertThat(tx.txType).isEqualTo(Enums.TxType.valueOf(type.toUpperCase()));
        });
    }

    @And("Every transaction has sig-type {string}")
    public void every_transaction_has_sigtype(String type) {
        TransactionsResponse transactions = transactionsResponse.body();

        transactions.transactions.forEach(tx -> {
                    switch (type) {
                        case "sig":
                            assertThat(tx.signature.sig).isNotEmpty();
                            break;
                        case "msig":
                            assertThat(tx.signature.multisig).isNotNull();
                            break;
                        case "lsig":
                            assertThat(tx.signature.logicsig).isNotNull();
                            break;
                    }
            ;
                }
        );
    }

    @And("Every transaction has round {long}")
    public void every_transaction_has_round(Long round) {
        TransactionsResponse transactions = transactionsResponse.body();

        // Nothing to check for these results.
        if (transactions.transactions.size() == 0) return;

        assertThat(transactions.transactions)
                .extracting("confirmedRound")
                .containsOnly(round);
    }

    @And("Every transaction has round >= {long}")
    public void every_transaction_has_round_greater_than(Long minRound) {
        transactionsResponse.body().transactions.forEach(tx -> assertThat(tx.confirmedRound).isGreaterThanOrEqualTo(minRound));
    }

    @And("Every transaction has round <= {long}")
    public void every_transaction_has_round_less_than(Long maxRound) {
        transactionsResponse.body().transactions.forEach(tx -> assertThat(tx.confirmedRound).isLessThanOrEqualTo(maxRound));
    }

    @And("Every transaction works with asset-id {long}")
    public void every_transaction_works_with_asset_id(Long assetId) {
        transactionsResponse.body().transactions.forEach(tx -> {
            if (tx.createdAssetIndex != null) {
                assertThat(tx.createdAssetIndex).isEqualTo(assetId);
            }
            if (tx.assetTransferTransaction != null) {
                assertThat(tx.assetTransferTransaction.assetId).isEqualTo(assetId);
            }
        });
    }

    @And("Every transaction is newer than {string}")
    public void every_transaction_is_newer_than(String dateString) {
        Instant i = Instant.parse(dateString);
        transactionsResponse.body().transactions.forEach(tx -> {
            //assertThat(tx.c)
            System.out.println("TODO");
        });
    }

    @And("Every transaction is older than {string}")
    public void every_transaction_is_older_than(String dateString) {
        Instant i = Instant.parse(dateString);
        transactionsResponse.body().transactions.forEach(tx -> {
            //assertThat(tx.c)
            System.out.println("TODO");
        });
    }

    @And("Every transaction moves between {biginteger} and {biginteger} currency")
    public void every_transaction_moves_between_and_currency(BigInteger min, BigInteger max) {
        // Normalize in case max isn't set.
        BigInteger maxUnsignedLong = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2));
        final BigInteger normalizedMax = (max.longValue() == 0) ? maxUnsignedLong : max;

        transactionsResponse.body().transactions.forEach(tx -> {
            switch (tx.txType) {
                case PAY:
                    assertThat(BigInteger.valueOf(tx.paymentTransaction.amount)).isBetween(min, normalizedMax);
                    break;
                case AXFER:
                    assertThat(tx.assetTransferTransaction.amount).isBetween(min, normalizedMax);
                    break;
                default:
                    Assertions.fail("Only transactions that move currency should match this.");
            }
        });
    }

    @When("I use {int} to search for all {string} transactions")
    public void i_use_to_search_for_all_transactions(Integer indexer, String account) throws Exception {
        LookupAccountTransactions query = indexerClients.get(indexer).lookupAccountTransactions(new Address(account));
        transactionsResponse = query.execute();
    }

    @When("I use {int} to search for all {long} asset transactions")
    public void i_use_to_search_for_all_asset_transactions(Integer indexer, Long assetId) throws Exception {
        LookupAssetTransactions query = indexerClients.get(indexer).lookupAssetTransactions(assetId);
        transactionsResponse = query.execute();
    }

    @And("I get the next page using {int} to search for transactions with {long} and {long}")
    public void i_get_the_next_page_using_to_search_for_transactions_with_and(Integer indexer, Long limit, Long maxRound) throws Exception {
        String next = transactionsResponse.body().nextToken;

        // Reuse the all-args wrapper, injecting the next token
        i_use_to_search_for_transactions_with_and_token(indexer, limit, "", "", "", "",
                0L, 0L, maxRound, 0L, "", "", 0L, 0L,
                "", "", "", next);
    }

    @When("I use {int} to search for assets with {long}, {long}, {string}, {string}, {string}, and token {string}")
    public void i_use_to_search_for_assets_with_and_token(Integer indexer, Long limit, Long assetId, String creator, String name, String unit, String token) throws Exception {
        SearchForAssets query = indexerClients.get(indexer).searchForAssets();

        if (limit != 0) {
            query.limit(limit);
        }
        if (assetId != 0) {
            query.assetId(assetId);
        }
        if (StringUtils.isNotEmpty(creator)) {
            query.creator(creator);
        }
        if (StringUtils.isNotEmpty(name)) {
            query.name(name);
        }
        if (StringUtils.isNotEmpty(unit)) {
            query.unit(unit);
        }
        if (StringUtils.isNotEmpty(token)) {
            query.next(token);
        }
        assetsResponse = query.execute();
    }

    @Then("there are {int} assets in the response, the first is {long}.")
    public void there_are_assets_in_the_response_the_first_is(Integer num, Long assetId) {
        AssetsResponse response = assetsResponse.body();
        assertThat(response.assets).hasSize(num);
        response.assets.forEach(a -> assertThat(a.index).isEqualTo(assetId));
    }


}
