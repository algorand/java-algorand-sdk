package com.algorand.algosdk.v2.client.indexer;

import java.io.IOException;

import com.algorand.algosdk.v2.client.connect.Client;
import com.algorand.algosdk.v2.client.connect.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.algorand.algosdk.v2.client.model.AssetsResponse;


/*
	Search for assets. 
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
			response = request();
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
	protected String getRequestString() {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append("assets");
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
		if (this.creatorIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("creator=");
			sb.append(creator);
			added = true;
		}
		if (this.limitIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("limit=");
			sb.append(limit);
			added = true;
		}
		if (this.nameIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("name=");
			sb.append(name);
			added = true;
		}
		if (this.nextIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("next=");
			sb.append(next);
			added = true;
		}
		if (this.unitIsSet) {
			if (added) {
				sb.append("&");
			}
			sb.append("unit=");
			sb.append(unit);
			added = true;
		}

		return sb.toString();
	}
}