package com.algorand.indexer.utils;

import java.io.IOException;
import java.math.BigInteger;

import com.algorand.indexer.schemas.MiniAssetHolding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Utils {

	public static JsonNode getRoot(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root;
		try {
			root = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return root;
	}
	
	public static long getLong(String pathName, JsonNode node) {
		return node.findPath(pathName).asLong();
	}
	
	public static String getString(String pathName, JsonNode node) {
		return node.findPath(pathName).asText();
	}
	
	public static BigInteger getBigInteger(String pathName, JsonNode node) {
		return new BigInteger(node.findPath(pathName).asText());
	}
	
	public static boolean getBoolean(String pathName, JsonNode node) {
		return node.findPath(pathName).asBoolean();
	}
	
	public static ArrayNode getArrayNode(String pathName, JsonNode node) {
		return (ArrayNode) node.findPath(pathName);
	}
	
	public static JsonNode getNode(String pathName, JsonNode parent) {
		return parent.findPath(pathName);
	}
}
