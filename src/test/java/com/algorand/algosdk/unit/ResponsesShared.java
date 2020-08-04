package com.algorand.algosdk.unit;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.unit.utils.ClientMocker;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.google.common.io.Files;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.io.IOException;

import static com.algorand.algosdk.unit.utils.TestingUtils.verifyResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponsesShared {
    public File bodyFile;
    public Response response;

    public AlgodClient algod = new AlgodClient("localhost", 123, "");
    public IndexerClient indexer = new IndexerClient("localhost", 123);

    public ResponsesShared() {
        try {
            ClientMocker.infect(algod);
            ClientMocker.infect(indexer);
        } catch (IllegalAccessException e) {
            Assertions.fail("Failed to infect the client.");
        }
    }


    @Given("mock http responses in {string} loaded from {string}")
    public void mockResponse(String file, String dir) throws Exception {
        mockResponseWithStatus(file, dir, 200);
    }

    @Given("mock http responses in {string} loaded from {string} with status {int}.")
    public void mockResponseWithStatus(String file, String dir, Integer status) throws Exception {
        this.bodyFile = new File("src/test/resources/" + dir + "/" + file);
        assertThat(this.bodyFile).exists();

        // Content type based on file extension.
        String contentType;
        switch (Files.getFileExtension(file)) {
            case "base64":
                contentType = "application/msgpack";
                break;
            case "json":
            default:
                contentType = "application/json";
                break;
        }
        ClientMocker.oneResponse(status, contentType, bodyFile);
    }

    @When("we make any {string} call to {string}.")
    public void we_make_any_call_to(String client, String endpoint) throws Exception {
        switch(client) {
            case "indexer": {
                switch(endpoint) {
                    case "lookupAccountByID":
                        response = indexer.lookupAccountByID(new Address()).execute();
                        break;
                    case "lookupApplicationByID":
                        response = indexer.lookupApplicationByID(10L).execute();
                        break;
                    case "searchForApplications":
                        response = indexer.searchForApplications().execute();
                        break;
                    case "lookupAssetBalances":
                        response = indexer.lookupAssetBalances(10L).execute();
                        break;
                    case "lookupAssetByID":
                        response = indexer.lookupAssetByID(10L).execute();
                        break;
                    case "searchForAssets":
                        response = indexer.searchForAssets().execute();
                        break;
                    case "searchForAccounts":
                        response = indexer.searchForAccounts().execute();
                        break;
                    case "lookupAccountTransactions":
                        response = indexer.lookupAccountTransactions(new Address()).execute();
                        break;
                    case "lookupAssetTransactions":
                        response = indexer.lookupAssetTransactions(10L).execute();
                        break;
                    case "searchForTransactions":
                    case "any": // error case, everything uses the same error message
                        response = indexer.searchForTransactions().execute();
                        break;
                    default:
                        Assertions.fail("Unsupported indexer endpoint: " + endpoint);
                }
                break;
            }
            case "algod": {
                switch(endpoint) {
                    case "GetStatus":
                        response = algod.GetStatus().execute();
                        break;
                    case "WaitForBlock":
                        response = algod.WaitForBlock(10L).execute();
                        break;
                    case "TealCompile":
                        response = algod.TealCompile().source("awesome teal program".getBytes()).execute();
                        break;
                    case "RawTransaction":
                        response = algod.RawTransaction().rawtxn("transaction".getBytes()).execute();
                        break;
                    case "GetSupply":
                        response = algod.GetSupply().execute();
                        break;
                    case "GetApplicationByID":
                        response = algod.GetApplicationByID(10L).execute();
                        break;
                    case "GetAssetByID":
                        response = algod.GetAssetByID(10L).execute();
                        break;
                    case "TransactionParams":
                    case "any": // error case, everything uses the same error message
                        response = algod.TransactionParams().execute();
                        break;
                    case "PendingTransactionInformation":
                        response = algod.PendingTransactionInformation("transaction").execute();
                        break;
                    case "GetPendingTransactions":
                        response = algod.GetPendingTransactions().execute();
                        break;
                    case "GetPendingTransactionsByAddress":
                        response = algod.GetPendingTransactionsByAddress(new Address()).execute();
                        break;
                    default:
                        Assertions.fail("Unsupported algod endpoint: " + endpoint);
                }
                break;
            }
        }
    }

    @Then("the parsed response should equal the mock response.")
    public void the_parsed_response_should_equal() throws IOException {
        verifyResponse(response, bodyFile);
    }

}
