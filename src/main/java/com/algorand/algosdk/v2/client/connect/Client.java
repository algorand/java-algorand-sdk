package com.algorand.algosdk.v2.client.connect;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Client {
	
	private OkHttpClient client;
	private String url;
	private MediaType mediaType;

	public Client(String url) {
		this.mediaType = MediaType.parse("application/json; charset=utf-8");
		this.url = url;
		this.client = new OkHttpClient();
	}
	
	public String executeCall(String requestString) throws Exception {
		
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();	        
		} catch (Exception e) {
			throw e;
		}
		String responseString = response.body().string();
		return responseString;
        // String contentType = response.headers().get("Content-Type");

	}
}
