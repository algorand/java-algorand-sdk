package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.v2.client.common.Client;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClientMocker {
    /**
     * Orchestrates a mocked response to be returned by the low level client the next time 'execute()' is called.
     */
    public static void addResponse(Client client, int code, String contentType, File bodyFile) throws Exception {
        OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);

        // Infect the client with a mock.
        FieldUtils.writeField(client, "client", mockClient, true);

        // Read file
        byte[] bodyBytes = Files.readAllBytes(bodyFile.toPath());

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
