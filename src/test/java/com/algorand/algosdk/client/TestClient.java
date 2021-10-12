package com.algorand.algosdk.client;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BlockResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestClient {
    @ParameterizedTest
    @CsvSource({
            "localhost, 1234, http://localhost:1234, false",
            "http://localhost, 1234, http://localhost:1234, false",
            "https://localhost, 1234, https://localhost:1234, false",
            "http://localhost:99, 1234, http://localhost:99, true",
            "https://localhost:850, 1234, https://localhost:850, true",
    })
    public void urlTest(String host, int port, String expectedPrefix, boolean expectError) {
        try {
            AlgodClient client = new AlgodClient(host, port, "secure");
            String url = client.TransactionParams().getRequestUrl();
            Assertions.assertThat(url).startsWith(expectedPrefix);
        } catch (Exception e) {
            Assertions.assertThat(expectError).isTrue();
        }
    }

    @Test
    public void testEvalDelta() throws Exception {
        String host = "https://academy-algod.dev.aws.algodev.network";
        int port = 443;
        String token = "2f3203f21e738a1de6110eba6984f9d03e5a95d7a577b34616854064cf2c0e7b";
        AlgodClient client = new AlgodClient(host, port, token);

        long round = 17162527;

        Response<BlockResponse> response = client.GetBlock(round).execute();
        // System.out.println(Encoder.encodeToBase64(response.getRawResponse()));
        Throwable throwable = Assertions.catchThrowable(() -> response.toString());
        Assertions.assertThat(throwable).isNull();
    }
}
