package com.algorand.indexer.schemas;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	Fields for asset allocation, re-configuration, and destruction. A zero value for 
	asset-id indicates asset creation. A zero value for the params indicates asset 
	destruction. Definition: data/transactions/asset.go : AssetConfigTxnFields 
 */
public class TransactionAssetConfig {

	@JsonProperty("params")
	public AssetParams params;

	/*
		(xaid) ID of the asset being configured or empty if creating. 
	 */
	@JsonProperty("asset-id")
	public long assetId;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		TransactionAssetConfig other = (TransactionAssetConfig) o;
		if (!Objects.deepEquals(this.params, other.params)) return false;
		if (!Objects.deepEquals(this.assetId, other.assetId)) return false;

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
