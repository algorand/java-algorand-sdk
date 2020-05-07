package com.algorand.algosdk.unit.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;

public class TestIndexerClient extends IndexerClient {

	private String filename;
	
	public TestIndexerClient(String host, int port, String token) {
		super(host, port, token);
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}
	
	public <T>Response<T> testExecute (Class<?> valueType) throws Exception {
		String responseStr = readFile(filename);
		Response<T> resp = new Response<T>(200, null, responseStr);
		resp.setValueType(valueType);
		return resp;
	}
	
	public static String readFile(String filename) throws IOException  {
		
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File (filename));
		} catch (FileNotFoundException e) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(fileReader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}

}
