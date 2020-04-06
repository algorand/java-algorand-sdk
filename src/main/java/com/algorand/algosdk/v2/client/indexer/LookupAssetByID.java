package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AssetResponse;


/*
	Lookup asset information. 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("assets");
		sb.append("/");
		sb.append(assetId);
		sb.append("?");

		boolean added = false;

		if (this.assetIdIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("assetId=");
			sb.append(assetId);
			added = true;
		}

		return sb.toString();
	}
}