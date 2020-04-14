package com.algorand.algosdk.v2.client.indexer;

import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.Query;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.AssetsResponse;


/**
 * Search for assets. /assets 
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
		super(client, "get");
	}

	/**
	 * Asset ID 
	 */
	public SearchForAssets setAssetId(long assetId) {
		this.assetId = assetId;
		this.assetIdIsSet = true;
		return this;
	}

	/**
	 * Filter just assets with the given creator address. 
	 */
	public SearchForAssets setCreator(String creator) {
		this.creator = creator;
		this.creatorIsSet = true;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForAssets setLimit(long limit) {
		this.limit = limit;
		this.limitIsSet = true;
		return this;
	}

	/**
	 * Filter just assets with the given name. 
	 */
	public SearchForAssets setName(String name) {
		this.name = name;
		this.nameIsSet = true;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForAssets setNext(String next) {
		this.next = next;
		this.nextIsSet = true;
		return this;
	}

	/**
	 * Filter just assets with the given unit. 
	 */
	public SearchForAssets setUnit(String unit) {
		this.unit = unit;
		this.unitIsSet = true;
		return this;
	}

	@Override
	public Response<AssetsResponse> execute() throws Exception {
		Response<AssetsResponse> resp = baseExecute();
		resp.setValueType(AssetsResponse.class);
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