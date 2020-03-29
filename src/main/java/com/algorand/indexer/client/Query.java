package com.algorand.indexer.client;

import com.algorand.indexer.client.Client;

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
