package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Client;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;

public class ClientMocker {
    private static OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);

    public static void infect(Client client) throws IllegalAccessException {
        // Infect the client with a mock, assign it to the private field.
        FieldUtils.writeField(client, "client", mockClient, true);
    }

    /**
     * Orchestrates a mocked response to be returned by the low level client the next time 'execute()' is called.
     */
    public static void oneResponse(int code, String contentType, File bodyFile) throws Exception {
        Mockito.reset(mockClient);

        byte[] bytes = Files.readAllBytes(bodyFile.toPath());
        if (bodyFile.getName().endsWith("base64")) {
            bytes = Encoder.decodeFromBase64(new String(bytes));
        }

        // Give the lambda a final variable.
        final byte[] bodyBytes = bytes;

        // Return a mock "Call" which returns a mock "Response" loaded with the mock body data and return code.
        Mockito.when(mockClient.newCall(Mockito.any())).thenAnswer(i -> {
            Request r = i.getArgument(0);

            ResponseBody body = ResponseBody.create(
                    MediaType.parse(contentType),
                    bodyBytes);

            Response mockResponse = (new Response.Builder())
                    .code(code)
                    .body(body)
                    .request(r)
                    .protocol(Protocol.HTTP_2)
                    .build();

            Call c = Mockito.mock(Call.class);
            Mockito.when(c.execute()).thenReturn(mockResponse);

            return c;
        });
    }
}
