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
	public Long assetId() {
		return this.assetId;
	}
	private String creator;
	public String creator() {
		return this.creator;
	}
	private Long limit;
	public Long limit() {
		return this.limit;
	}
	private String name;
	public String name() {
		return this.name;
	}
	private String next;
	public String next() {
		return this.next;
	}
	private String unit;
	public String unit() {
		return this.unit;
	}

	public SearchForAssets(Client client) {
		super(client, "get");
	}

	/**
	 * Asset ID 
	 */
	public SearchForAssets assetId(Long assetId) {
		this.assetId = assetId;
		addQuery("asset-id", String.valueOf(assetId));
		return this;
	}

	/**
	 * Filter just assets with the given creator address. 
	 */
	public SearchForAssets creator(String creator) {
		this.creator = creator;
		addQuery("creator", String.valueOf(creator));
		return this;
	}

	/**
	 * Maximum number of results to return. 
	 */
	public SearchForAssets limit(Long limit) {
		this.limit = limit;
		addQuery("limit", String.valueOf(limit));
		return this;
	}

	/**
	 * Filter just assets with the given name. 
	 */
	public SearchForAssets name(String name) {
		this.name = name;
		addQuery("name", String.valueOf(name));
		return this;
	}

	/**
	 * The next page of results. Use the next token provided by the previous results. 
	 */
	public SearchForAssets next(String next) {
		this.next = next;
		addQuery("next", String.valueOf(next));
		return this;
	}

	/**
	 * Filter just assets with the given unit. 
	 */
	public SearchForAssets unit(String unit) {
		this.unit = unit;
		addQuery("unit", String.valueOf(unit));
		return this;
	}

	@Override
	public Response<AssetsResponse> execute() throws Exception {
		Response<AssetsResponse> resp = baseExecute();
		resp.setValueType(AssetsResponse.class);
		return resp;
	}
	protected QueryData getRequestString() {
		addPathSegment(String.valueOf("assets"));

		return qd;
	}
}