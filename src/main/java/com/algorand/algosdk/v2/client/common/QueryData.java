package com.algorand.algosdk.v2.client.common;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryData {
	
	public HashMap<String, String> queries;
	public ArrayList<String> pathSegments;
	public ArrayList<byte[]> bodySegments;
	
	public QueryData () {
		queries = new HashMap<String, String>();
		pathSegments = new ArrayList<String>();
		bodySegments = new ArrayList<byte[]>();
	}
	public void resetPathSegments() {
		pathSegments = new ArrayList<String>();
	}
	public void addPathSegment(String segment) {
		pathSegments.add(segment);
	}

	public void addQuery(String key, String value) {
		queries.put(key, value);
	}
	
	public void addToBody(byte[] content) {
		if (!bodySegments.isEmpty()) {
			throw new RuntimeException("Only single body element content is supported.");
		}
		bodySegments.add(content);
	}	
}
