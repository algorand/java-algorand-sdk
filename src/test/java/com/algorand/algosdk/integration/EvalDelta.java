package com.algorand.algosdk.integration;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.model.*;
import io.cucumber.java.en.Then;
import org.assertj.core.api.Assertions;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EvalDelta {
    private final Clients clients;
    private final TransientAccount transientAccount;
    private final Applications applications;

    public EvalDelta(Clients clients, TransientAccount transientAccount, Applications applications) {
        this.clients = clients;
        this.transientAccount = transientAccount;
        this.applications = applications;
    }

    @Then("the unconfirmed pending transaction by ID should have no apply data fields.")
    public void the_unconfirmed_pending_transaction_by_ID_should_have_no_apply_data_fields() throws Exception {
        PendingTransactionResponse r = clients.v2Client
                .PendingTransactionInformation(applications.txId)
                .execute()
                .body();

        // Just in case we missed the boat and the tx is now confirmed.
        if (r.confirmedRound == null) {
            assertThat(r.globalStateDelta).isEmpty();
            assertThat(r.localStateDelta).isEmpty();
        }
    }

    private List<EvalDeltaKeyValue> getAccountDelta(Address addr, List<AccountStateDelta> data) {
        return data.stream()
                .filter(ad -> ad.address.equals(addr))
                .map(ad -> ad.delta)
                .findAny()
                .orElse(null);
    }

    @Then("the confirmed pending transaction by ID should have a {string} state change for {string} to {string}, indexer {int} should also confirm this.")
    public void checkConfirmedTransaction(String stateLocation, String key, String newValue, int indexer) throws Exception {
        PendingTransactionResponse r = clients.v2Client
                .PendingTransactionInformation(applications.txId)
                .execute()
                .body();

        // Try fetching the transaction from indexer a few times to give indexer a chance to process the new block.
        Transaction tx = null;
        for (int i = 0; i < 5 && tx == null; i++) {
            TransactionsResponse indexerResponse = clients.indexerClients.get(3)
                    .searchForTransactions()
                    .txid(applications.txId)
                    .execute()
                    .body();
            if (! indexerResponse.transactions.isEmpty()) {
                tx = indexerResponse.transactions.get(0);
            } else {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            }
        }

        assertThat(tx).as("Indexer was unable to return txid %s.", applications.txId).isNotNull();


        //I JUST REBUILD THE TEST ENVIRONMENT USING MY INDEXER BRANCH IN HOPES THAT THE DELTA VALUES ARE NOW THERE


        // Grab local or global key/value deltas.
        // Algod
        List<EvalDeltaKeyValue> keyValuesAlgod = null;
        // Indexer
        List<EvalDeltaKeyValue> keyValuesIndexer = null;
        switch(stateLocation) {
            case "local":
                Address localAccountKey = r.txn.tx.sender;
                keyValuesAlgod = getAccountDelta(localAccountKey, r.localStateDelta);
                keyValuesIndexer = getAccountDelta(localAccountKey, tx.localStateDelta);
                break;
            case "global":
                keyValuesAlgod = r.globalStateDelta;
                keyValuesIndexer = tx.globalStateDelta;
                break;
            default:
                Assertions.fail("Unknown state location: %s", stateLocation);
        }

        // Algod
        assertThat(keyValuesAlgod).hasSize(1);
        EvalDeltaKeyValue keyValueAlgod = keyValuesAlgod.get(0);
        assertThat(keyValueAlgod.key).isEqualTo(key);

        // Indexer
        assertThat(keyValuesIndexer).hasSize(1);
        EvalDeltaKeyValue keyValueIndexer = keyValuesIndexer.get(0);
        assertThat(keyValueIndexer.key).isEqualTo(key);

        // They should have the same action
        assertThat(keyValueAlgod.value.action).isEqualTo(keyValueIndexer.value.action);

        // And the values should be equal, and should equal the expected value
        switch(keyValueAlgod.value.action.toString()) {
            case "1":
                assertThat(keyValueAlgod.value.bytes).isEqualTo(newValue);
                assertThat(keyValueAlgod.value.bytes).isEqualTo(keyValueIndexer.value.bytes);
                break;
            case "2":
                assertThat(keyValueAlgod.value.uint).isEqualTo(BigInteger.valueOf(Long.parseLong(newValue)));
                assertThat(keyValueAlgod.value.uint).isEqualTo(keyValueIndexer.value.uint);
                break;
            default:
                Assertions.fail("Unknown value action %d.", keyValueAlgod.value.action);
        }
    }
}
