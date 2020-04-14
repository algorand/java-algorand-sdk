package com.algorand.algosdk.v2.client.model;

import java.util.Objects;

import com.algorand.algosdk.v2.client.common.PathResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorModel extends PathResponse {

	private String data;
	private boolean dataIsSet;
	@JsonProperty("data")
	public void setData(String data){
		this.data = data;
		dataIsSet = true;
	}
	@JsonProperty("data")
	public String getData(){
		return dataIsSet ? data : null;
	}
	/**
	 * Check if has a value for data 
	 */	@JsonIgnore
	public boolean hasData(){
		return dataIsSet;
	}

	private String msg;
	private boolean msgIsSet;
	@JsonProperty("msg")
	public void setMsg(String msg){
		this.msg = msg;
		msgIsSet = true;
	}
	@JsonProperty("msg")
	public String getMsg(){
		return msgIsSet ? msg : null;
	}
	/**
	 * Check if has a value for msg 
	 */	@JsonIgnore
	public boolean hasMsg(){
		return msgIsSet;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null) return false;

		ErrorModel other = (ErrorModel) o;
		if (!Objects.deepEquals(this.data, other.data)) return false;
		if (!Objects.deepEquals(this.msg, other.msg)) return false;

		return true;
	}
}
