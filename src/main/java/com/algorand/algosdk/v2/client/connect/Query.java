package com.algorand.algosdk.v2.client.connect;

public abstract class Query {

	private Client client;	
	public Query(Client client) {
		this.client = client;
	}
		
	abstract public QueryData getRequestString();
	
	protected String request(String getOrPost) throws Exception {

		QueryData qData = this.getRequestString();
		String responseString = this.client.executeCall(qData, getOrPost);
		return responseString;
	}
}
