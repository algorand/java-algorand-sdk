package com.algorand.algosdk.v2.client.connect;

import java.util.Map.Entry;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Client {
	
	private OkHttpClient client;
	private String host;
	private int port;

	public Client(String host, int port) {
		MediaType.parse("application/json; charset=utf-8");
		this.host = host;
		this.port = port;
		this.client = new OkHttpClient();
	}
	
	public String executeCall(QueryData qData, String getOrPost) throws Exception {
		
		HttpUrl.Builder httpUrlBuilder = (new HttpUrl.Builder()).scheme("http").port(port).host(host);
		for (String ps : qData.pathSegments) {
			httpUrlBuilder.addPathSegment(ps);
		}
		for (Entry<String, String> kvp : qData.queries.entrySet()) {
			httpUrlBuilder.addQueryParameter(kvp.getKey(), kvp.getValue());
		}
		HttpUrl httpUrl = httpUrlBuilder.build();
		Request request = new Request.Builder()
				.url(httpUrl)
				.get()
				.build();
/*		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url(httpUrl)
				.post(body)
				.build();*/
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
