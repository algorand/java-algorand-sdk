package com.algorand.algosdk.v2.client.common;

public abstract class Query {

	private Client client;
	private HttpMethod httpMethod;
	protected QueryData qd;
	
	protected Query(Client client, HttpMethod httpMethod) {
		this.client = client;
		this.httpMethod = httpMethod;
		this.qd = new QueryData();
	}

	protected abstract QueryData getRequestString();

	protected <T>Response<T> baseExecute() throws Exception {

		QueryData qData = this.getRequestString();
		com.squareup.okhttp.Response resp = this.client.executeCall(qData, httpMethod);
		if (resp.isSuccessful()) {
			return new Response<T>(resp.code(), null, resp.body().string());
		} else {
			return new Response<T>(resp.code(), resp.body().string(), null);
		}
	}

	public String getRequestUrl(int port, String host) {
		if (qd.pathSegments.size() == 0) {
			throw new RuntimeException("getRequestUrl can be called only after calling execute()");
		}
		return Client.getHttpUrl(this.qd, port, host).toString();
	}
	
	protected void addQuery(String key, String value) {
		qd.addQuery(key, value);
	}
	
	protected void addPathSegment(String segment) {
		qd.addPathSegment(segment);
	}
	
	protected void addToBody(byte[] content) {
		qd.addToBody(content);
	}
	
	public abstract Response<?> execute() throws Exception;
}
