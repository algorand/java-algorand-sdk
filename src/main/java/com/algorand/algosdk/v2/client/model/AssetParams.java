package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	AssetParams specifies the parameters for an asset. (apar) when part of an 
	AssetConfig transaction. Definition: data/transactions/asset.go : AssetParams 
 */
public class AssetParams {

	/*
		(c) Address of account used to clawback holdings of this asset. If empty, 
		clawback is not permitted. 
	 */
	private String clawback;
	private boolean clawbackIsSet;
	@JsonProperty("clawback")
	public void setClawback(String clawback){
		this.clawback = clawback;
		clawbackIsSet = true;
	}
	@JsonProperty("clawback")
	public String getClawback(){
		return clawbackIsSet ? clawback : null;
	}
	/*
		Check if has a value for clawback 
	 */	@JsonIgnore
	public boolean hasClawback(){
		return clawbackIsSet;
	}

	/*
		The address that created this asset. This is the address where the parameters 
		for this asset can be found, and also the address where unwanted asset units can 
		be sent in the worst case. 
	 */
	private String creator;
	private boolean creatorIsSet;
	@JsonProperty("creator")
	public void setCreator(String creator){
		this.creator = creator;
		creatorIsSet = true;
	}
	@JsonProperty("creator")
	public String getCreator(){
		return creatorIsSet ? creator : null;
	}
	/*
		Check if has a value for creator 
	 */	@JsonIgnore
	public boolean hasCreator(){
		return creatorIsSet;
	}

	/*
		(dc) The number of digits to use after the decimal point when displaying this 
		asset. If 0, the asset is not divisible. If 1, the base unit of the asset is in 
		tenths. If 2, the base unit of the asset is in hundredths, and so on. This value 
		must be between 0 and 19 (inclusive). 
	 */
	private long decimals;
	private boolean decimalsIsSet;
	@JsonProperty("decimals")
	public void setDecimals(long decimals){
		this.decimals = decimals;
		decimalsIsSet = true;
	}
	@JsonProperty("decimals")
	public Long getDecimals(){
		return decimalsIsSet ? decimals : null;
	}
	/*
		Check if has a value for decimals 
	 */	@JsonIgnore
	public boolean hasDecimals(){
		return decimalsIsSet;
	}

	/*
		(df) Whether holdings of this asset are frozen by default. 
	 */
	private boolean defaultFrozen;
	private boolean defaultFrozenIsSet;
	@JsonProperty("default-frozen")
	public void setDefaultFrozen(boolean defaultFrozen){
		this.defaultFrozen = defaultFrozen;
		defaultFrozenIsSet = true;
	}
	@JsonProperty("default-frozen")
	public Boolean getDefaultFrozen(){
		return defaultFrozenIsSet ? defaultFrozen : null;
	}
	/*
		Check if has a value for defaultFrozen 
	 */	@JsonIgnore
	public boolean hasDefaultFrozen(){
		return defaultFrozenIsSet;
	}

	/*
		(f) Address of account used to freeze holdings of this asset. If empty, freezing 
		is not permitted. 
	 */
	private String freeze;
	private boolean freezeIsSet;
	@JsonProperty("freeze")
	public void setFreeze(String freeze){
		this.freeze = freeze;
		freezeIsSet = true;
	}
	@JsonProperty("freeze")
	public String getFreeze(){
		return freezeIsSet ? freeze : null;
	}
	/*
		Check if has a value for freeze 
	 */	@JsonIgnore
	public boolean hasFreeze(){
		return freezeIsSet;
	}

	/*
		(m) Address of account used to manage the keys of this asset and to destroy it. 
	 */
	private String manager;
	private boolean managerIsSet;
	@JsonProperty("manager")
	public void setManager(String manager){
		this.manager = manager;
		managerIsSet = true;
	}
	@JsonProperty("manager")
	public String getManager(){
		return managerIsSet ? manager : null;
	}
	/*
		Check if has a value for manager 
	 */	@JsonIgnore
	public boolean hasManager(){
		return managerIsSet;
	}

	/*
		(am) A commitment to some unspecified asset metadata. The format of this 
		metadata is up to the application. 
	 */
	private String metadataHash;
	private boolean metadataHashIsSet;
	@JsonProperty("metadata-hash")
	public void setMetadataHash(String metadataHash){
		this.metadataHash = metadataHash;
		metadataHashIsSet = true;
	}
	@JsonProperty("metadata-hash")
	public String getMetadataHash(){
		return metadataHashIsSet ? metadataHash : null;
	}
	/*
		Check if has a value for metadataHash 
	 */	@JsonIgnore
	public boolean hasMetadataHash(){
		return metadataHashIsSet;
	}

	/*
		(an) Name of this asset, as supplied by the creator. 
	 */
	private String name;
	private boolean nameIsSet;
	@JsonProperty("name")
	public void setName(String name){
		this.name = name;
		nameIsSet = true;
	}
	@JsonProperty("name")
	public String getName(){
		return nameIsSet ? name : null;
	}
	/*
		Check if has a value for name 
	 */	@JsonIgnore
	public boolean hasName(){
		return nameIsSet;
	}

	/*
		(r) Address of account holding reserve (non-minted) units of this asset. 
	 */
	private String reserve;
	private boolean reserveIsSet;
	@JsonProperty("reserve")
	public void setReserve(String reserve){
		this.reserve = reserve;
		reserveIsSet = true;
	}
	@JsonProperty("reserve")
	public String getReserve(){
		return reserveIsSet ? reserve : null;
	}
	/*
		Check if has a value for reserve 
	 */	@JsonIgnore
	public boolean hasReserve(){
		return reserveIsSet;
	}

	/*
		(t) The total number of units of this asset. 
	 */
	private long total;
	private boolean totalIsSet;
	@JsonProperty("total")
	public void setTotal(long total){
		this.total = total;
		totalIsSet = true;
	}
	@JsonProperty("total")
	public Long getTotal(){
		return totalIsSet ? total : null;
	}
	/*
		Check if has a value for total 
	 */	@JsonIgnore
	public boolean hasTotal(){
		return totalIsSet;
	}

	/*
		(un) Name of a unit of this asset, as supplied by the creator. 
	 */
	private String unitName;
	private boolean unitNameIsSet;
	@JsonProperty("unit-name")
	public void setUnitName(String unitName){
		this.unitName = unitName;
		unitNameIsSet = true;
	}
	@JsonProperty("unit-name")
	public String getUnitName(){
		return unitNameIsSet ? unitName : null;
	}
	/*
		Check if has a value for unitName 
	 */	@JsonIgnore
	public boolean hasUnitName(){
		return unitNameIsSet;
	}

	/*
		(au) URL where more information about the asset can be retrieved. 
	 */
	private String url;
	private boolean urlIsSet;
	@JsonProperty("url")
	public void setUrl(String url){
		this.url = url;
		urlIsSet = true;
	}
	@JsonProperty("url")
	public String getUrl(){
		return urlIsSet ? url : null;
	}
	/*
		Check if has a value for url 
	 */	@JsonIgnore
	public boolean hasUrl(){
		return urlIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		AssetParams other = (AssetParams) o;
		if (!Objects.deepEquals(this.clawback, other.clawback)) return false;
		if (!Objects.deepEquals(this.creator, other.creator)) return false;
		if (!Objects.deepEquals(this.decimals, other.decimals)) return false;
		if (!Objects.deepEquals(this.defaultFrozen, other.defaultFrozen)) return false;
		if (!Objects.deepEquals(this.freeze, other.freeze)) return false;
		if (!Objects.deepEquals(this.manager, other.manager)) return false;
		if (!Objects.deepEquals(this.metadataHash, other.metadataHash)) return false;
		if (!Objects.deepEquals(this.name, other.name)) return false;
		if (!Objects.deepEquals(this.reserve, other.reserve)) return false;
		if (!Objects.deepEquals(this.total, other.total)) return false;
		if (!Objects.deepEquals(this.unitName, other.unitName)) return false;
		if (!Objects.deepEquals(this.url, other.url)) return false;

		return true;
	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper(); 
		String jsonStr;
		try {
			jsonStr = om.setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonStr;
	}
}
