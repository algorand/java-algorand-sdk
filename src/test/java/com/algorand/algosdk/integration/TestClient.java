package com.algorand.algosdk.integration;

import com.algorand.algosdk.v2.client.algod.RawTransaction;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

public class TestClient {
    @ParameterizedTest
    @CsvSource({
            "http://localhost:60000, 60000, false",
    })
    public void rawTxnTest(String host, int port,boolean expectError) {
        try {
            AlgodClient client = new AlgodClient(host, port, "secure");
            byte[] msg = new byte[]{123};
            RawTransaction rawtxn = client.RawTransaction();
            rawtxn.rawtxn(msg);
            Response r = rawtxn.execute();
            Map<String,String> headers = rawtxn.getRequestHeaders();
            Assertions.assertThat(headers).containsKey("Content-Type");
            Assertions.assertThat(headers.get("Content-Type").equals("application/x-binary"));

        } catch (Exception e) {
            Assertions.assertThat(expectError).isTrue();
        }
    }
}
