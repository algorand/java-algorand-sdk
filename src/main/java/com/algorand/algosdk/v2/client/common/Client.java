package com.algorand.algosdk.v2.client.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Client {

	protected OkHttpClient client;
	protected String host;
	protected int port;
	protected String token;
	protected String tokenKey;

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public static HttpUrl getHttpUrl(QueryData qData, int port, String host) {
		HttpUrl.Builder httpUrlBuilder = null;
		HttpUrl parsedHttpUrl = HttpUrl.parse(host);

		if (parsedHttpUrl != null) {
			httpUrlBuilder = parsedHttpUrl.newBuilder();

			// Don't allow shenanigans, they aren't always intentional.
			if (HttpUrl.defaultPort(parsedHttpUrl.scheme()) != parsedHttpUrl.port() && parsedHttpUrl.port() != port) {
				throw new RuntimeException("Different ports were specified in the host URI and the port");
			}
		} else {
			httpUrlBuilder = new HttpUrl.Builder().scheme("http").host(host);
		}

		// Set the port with URI and host formats.
		httpUrlBuilder.port(port);

		for (String ps : qData.pathSegments) {
			httpUrlBuilder.addPathSegment(ps);
		}
		for (Entry<String, String> kvp : qData.queries.entrySet()) {
			try {
				// Try using a different URLEncoder because OkHttp does not encode ':' characters.
				String encodedKey = URLEncoder.encode(kvp.getKey(), StandardCharsets.UTF_8.toString());
				String encodedValue = URLEncoder.encode(kvp.getValue(), StandardCharsets.UTF_8.toString());
				httpUrlBuilder.addEncodedQueryParameter(encodedKey, encodedValue);
			} catch (UnsupportedEncodingException e) {
				httpUrlBuilder.addQueryParameter(kvp.getKey(), kvp.getValue());
			}
		}
		HttpUrl httpUrl = httpUrlBuilder.build();
		return httpUrl;
	}

	public Client(String host, int port, String token, String tokenKey) {
		MediaType.parse("application/json; charset=utf-8");
		this.host = host;
		this.port = port;
		this.token = token;
		this.client = new OkHttpClient();
		this.tokenKey = tokenKey;
	}

	public Response executeCall(QueryData qData, HttpMethod httpMethod) throws Exception {

		HttpUrl httpUrl = getHttpUrl(qData, port, host);
		Builder reqBuilder = new Request.Builder().url(httpUrl);
		if (token != null) {
			reqBuilder.addHeader(tokenKey, token);
		}
		RequestBody rb = RequestBody.create(
				MediaType.parse("Binary data"),
				qData.bodySegments.isEmpty() ? new byte[0] : qData.bodySegments.get(0));

		switch (httpMethod.method()) {
		case HttpMethod.GET:
			reqBuilder.get();
			break;
		case HttpMethod.POST:
			reqBuilder.post(rb);
			break;
		}

		Request request = reqBuilder.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (Exception e) {
			throw e;
		}
		return response;
	}
}
