package com.algorand.algosdk.v2.client.common;

public class HttpMethod{

	public static final String DELETE = "delete"; 
	public static final String GET = "get";
	public static final String HEAD = "head";
	public static final String OPTIONS = "options";
	public static final String PATCH = "patch";
	public static final String POST = "post";
	public static final String PUT = "put";
	public static final String TRACE = "trace"; 
	
	private String method;
	
	public HttpMethod(String method) {
		this.method = method;
	}
	
	public String method() {
		return method;
	}
}
