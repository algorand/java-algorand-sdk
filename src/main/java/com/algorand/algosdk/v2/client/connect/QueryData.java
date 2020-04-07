package com.algorand.algosdk.v2.client.connect;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryData {
	
	public HashMap<String, String> queries;
	public ArrayList<String> pathSegments;
	
	public QueryData () {
		queries = new HashMap<String, String>();
		pathSegments = new ArrayList<String>();
	}
	public void addPathSegment(String segment) {
		pathSegments.add(segment);
	}

	public void addQuery(String key, String value) {
		queries.put(key, value);
	}
}
