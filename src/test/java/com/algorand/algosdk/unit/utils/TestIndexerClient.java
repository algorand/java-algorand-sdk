package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.squareup.okhttp.*;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestIndexerClient extends IndexerClient {
	public TestIndexerClient() {
		super("localhost", 1234, "");
		this.client = Mockito.mock(OkHttpClient.class);
	}

	/**
	 * Add a response to the client.
	 */
	public void addResponss(int code, String contentType, File bodyFile) throws IOException {
	    // Read file
		byte[] bodyBytes = Files.readAllBytes(bodyFile.toPath());

		// Return a mock "Call" which returns a mock "Response" loaded with the mock body data and return code.
		Mockito.when(this.client.newCall(Mockito.any())).thenAnswer(i -> {
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
