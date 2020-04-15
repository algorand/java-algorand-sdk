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
	private Long assetId;
	private String creator;
	private Long limit;
	private String name;
	private String next;
	private String unit;


	public SearchForAssets(Client client) {
		super(client, "get");
	}

	/**
	 * Asset ID 
	 */
	public SearchForAssets setAssetId(Long assetId) {
		this.assetId = assetId;
		return this;
	}

	/**
	 * Filter just assets with the given creator address. 
	 */
	public SearchForAssets setCreator(String creator) {
		this.creator = creator;
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForAssets setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * Filter just assets with the given name. 
	 */
	public SearchForAssets setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForAssets setNext(String next) {
		this.next = next;
		return this;
	}

	/**
	 * Filter just assets with the given unit. 
	 */
	public SearchForAssets setUnit(String unit) {
		this.unit = unit;
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
		if (this.assetId != null) {
			qd.addQuery("assetId", String.valueOf(assetId));
		}
		if (this.creator != null) {
			qd.addQuery("creator", String.valueOf(creator));
		}
		if (this.limit != null) {
			qd.addQuery("limit", String.valueOf(limit));
		}
		if (this.name != null) {
			qd.addQuery("name", String.valueOf(name));
		}
		if (this.next != null) {
			qd.addQuery("next", String.valueOf(next));
		}
		if (this.unit != null) {
			qd.addQuery("unit", String.valueOf(unit));
		}
		qd.addPathSegment(String.valueOf("assets"));

		return qd;
	}
}