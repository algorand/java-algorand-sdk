package com.algorand.algosdk.client;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import org.assertj.core.api.Assertions;
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
}
