package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.algorand.algosdk.v2.client.connect.QueryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AssetsResponse;


/*
	Search for assets. /assets 
 */
public class SearchForAssets extends Query {
	private long assetId;
	private String creator;
	private long limit;
	private String name;
	private String next;
	private String unit;

	private boolean assetIdIsSet;
	private boolean creatorIsSet;
	private boolean limitIsSet;
	private boolean nameIsSet;
	private boolean nextIsSet;
	private boolean unitIsSet;

	public SearchForAssets(Client client) {
		super(client);
	}
	public SearchForAssets setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}
	public SearchForAssets setCreator(String creator) {
		this.creator = creator;
		this.creatorIsSet = true;
		return this;
	}
	public SearchForAssets setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}
	public SearchForAssets setName(String name) {
		this.name = name;
		this.nameIsSet = true;
		return this;
	}
	public SearchForAssets setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}
	public SearchForAssets setUnit(String unit) {
		this.unit = unit;
		this.unitIsSet = true;
		return this;
	}

	public AssetsResponse lookup() {
		String response;
		try {
			response = request("get");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		AssetsResponse resp;
		try {
			resp = mapper.readValue(response, AssetsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return resp;
	}
	public QueryData getRequestString() {
		QueryData qd = new QueryData();
		if (this.assetIdIsSet) {
			qd.addQuery("assetId", String.valueOf(assetId));
		}
		if (this.creatorIsSet) {
			qd.addQuery("creator", String.valueOf(creator));
		}
		if (this.limitIsSet) {
			qd.addQuery("limit", String.valueOf(limit));
		}
		if (this.nameIsSet) {
			qd.addQuery("name", String.valueOf(name));
		}
		if (this.nextIsSet) {
			qd.addQuery("next", String.valueOf(next));
		}
		if (this.unitIsSet) {
			qd.addQuery("unit", String.valueOf(unit));
		}
		qd.addPathSegment(String.valueOf("assets"));

		return qd;
	}
}