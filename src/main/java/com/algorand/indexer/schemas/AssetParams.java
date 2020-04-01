package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	AssetParams specifies the parameters for an asset. (apar) when part of an 
	AssetConfig transaction. Definition: data/transactions/asset.go : AssetParams 
 */
public class AssetParams {

	/*
		(r) Address of account holding reserve (non-minted) units of this asset. 
	 */
	@JsonProperty("reserve")
	public String reserve;

	/*
		The address that created this asset. This is the address where the parameters 
		for this asset can be found, and also the address where unwanted asset units can 
		be sent in the worst case. 
	 */
	@JsonProperty("creator")
	public String creator;

	/*
		(un) Name of a unit of this asset, as supplied by the creator. 
	 */
	@JsonProperty("unit-name")
	public String unitName;

	/*
		(am) A commitment to some unspecified asset metadata. The format of this 
		metadata is up to the application. 
	 */
	@JsonProperty("metadata-hash")
	public String metadataHash;

	/*
		(t) The total number of units of this asset. 
	 */
	@JsonProperty("total")
	public long total;

	/*
		(au) URL where more information about the asset can be retrieved. 
	 */
	@JsonProperty("url")
	public String url;

	/*
		(f) Address of account used to freeze holdings of this asset. If empty, freezing 
		is not permitted. 
	 */
	@JsonProperty("freeze")
	public String freeze;

	/*
		(dc) The number of digits to use after the decimal point when displaying this 
		asset. If 0, the asset is not divisible. If 1, the base unit of the asset is in 
		tenths. If 2, the base unit of the asset is in hundredths, and so on. This value 
		must be between 0 and 19 (inclusive). 
	 */
	@JsonProperty("decimals")
	public long decimals;

	/*
		(m) Address of account used to manage the keys of this asset and to destroy it. 
	 */
	@JsonProperty("manager")
	public String manager;

	/*
		(c) Address of account used to clawback holdings of this asset. If empty, 
		clawback is not permitted. 
	 */
	@JsonProperty("clawback")
	public String clawback;

	/*
		(df) Whether holdings of this asset are frozen by default. 
	 */
	@JsonProperty("default-frozen")
	public boolean defaultFrozen;

	/*
		(an) Name of this asset, as supplied by the creator. 
	 */
	@JsonProperty("name")
	public String name;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetParams other = (AssetParams) o;
		if (!Objects.deepEquals(this.reserve, other.reserve)) return false;
		if (!Objects.deepEquals(this.creator, other.creator)) return false;
		if (!Objects.deepEquals(this.unitName, other.unitName)) return false;
		if (!Objects.deepEquals(this.metadataHash, other.metadataHash)) return false;
		if (!Objects.deepEquals(this.total, other.total)) return false;
		if (!Objects.deepEquals(this.url, other.url)) return false;
		if (!Objects.deepEquals(this.freeze, other.freeze)) return false;
		if (!Objects.deepEquals(this.decimals, other.decimals)) return false;
		if (!Objects.deepEquals(this.manager, other.manager)) return false;
		if (!Objects.deepEquals(this.clawback, other.clawback)) return false;
		if (!Objects.deepEquals(this.defaultFrozen, other.defaultFrozen)) return false;
		if (!Objects.deepEquals(this.name, other.name)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}