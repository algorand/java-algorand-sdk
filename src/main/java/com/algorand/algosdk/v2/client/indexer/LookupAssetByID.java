package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AssetResponse;


/*
	Lookup asset information. /assets/{asset-id} 
 */
public class LookupAssetByID extends Query {
	private long assetId;

	private boolean assetIdIsSet;

	public LookupAssetByID(Client client) {
		super(client);
	}
	public LookupAssetByID setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}

	public AssetResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		AssetResponse resp;
		try {
			resp = mapper.readValue(response, AssetResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	protected QueryData getRequestString() {
		QueryData qd = new QueryData();
		if  (!this.assetIdIsSet) {
			throw new RuntimeException("assetId is not set, and it is a required parameter.");
		}
		qd.addPathSegment(String.valueOf("assets"));
		qd.addPathSegment(String.valueOf(assetId));

		return qd;
	}
}