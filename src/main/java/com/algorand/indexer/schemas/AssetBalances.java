package com.algorand.indexer.schemas;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class AssetBalances{

	private MiniAssetHolding[] balances;
	private long currentRound;
	private String nextToken;

	private AssetBalances() {}
	
	public AssetBalances(String json) {
		AssetBalances abs = parse(json);
		this.balances = abs.balances;
		this.currentRound = abs.currentRound;
		this.nextToken = abs.nextToken;
	}

	private void setBalances(MiniAssetHolding[] balances) {
		this.balances = balances;
	}
	private void setCurrentRound(long currentRound) {
		this.currentRound = currentRound;
	}
	private void setNextToken(String next) {
		this.nextToken = next;
	}

	public MiniAssetHolding[] getBalances() {
		return balances;
	}

	public long getCurrentRound() {
		return currentRound;
	}

	public String getNext() {
		return this.nextToken;
	}

	private static AssetBalances parse(String json) {

		AssetBalances abs = new AssetBalances();

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

		// balances
		JsonNode balanceNode = root.path("balances");
		if (balanceNode.isArray()) {
			ArrayNode aNode = (ArrayNode)balanceNode;
			MiniAssetHolding[] balances = new MiniAssetHolding[aNode.size()];
			for (int i = 0; i < aNode.size(); i++) {
				balances[i] = new MiniAssetHolding(aNode.get(i));
			}
			abs.setBalances(balances);
		}

		//round
		JsonNode roundNode = root.findPath("current-round");
		abs.setCurrentRound(roundNode.asLong());

		//nextToken
		JsonNode nextTokenNode = root.findPath("next-token");
		abs.setNextToken(nextTokenNode.asText());
		return abs;
	}
}
