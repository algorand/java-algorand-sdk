package com.algorand.algosdk.v2.client.connect;

public abstract class Query {

	private Client client;	
	public Query(Client client) {
		this.client = client;
	}
		
	abstract protected String getRequestString();
	
	protected String request() throws Exception {

		String requestString = this.getRequestString();
		String responseString = this.client.executeCall(requestString);
		return responseString;
	}
}
