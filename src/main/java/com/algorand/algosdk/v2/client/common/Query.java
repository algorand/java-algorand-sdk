package com.algorand.algosdk.v2.client.common;

public abstract class Query {

	private Client client;
	private String getOrPost;
	protected Query(Client client, String getOrPost) {
		this.client = client;
		this.getOrPost = getOrPost;
	}
		
	
	public abstract QueryData getRequestString();

	public <T>Response<T> baseExecute() throws Exception {

		QueryData qData = this.getRequestString();
		com.squareup.okhttp.Response resp = this.client.executeCall(qData, getOrPost);
		if (resp.isSuccessful()) {
			return new Response<T>(resp.code(), null, resp.body().string());
		} else {
			return new Response<T>(resp.code(), resp.body().string(), null);
		}
	}


	public abstract Response<?> execute() throws Exception;
}
