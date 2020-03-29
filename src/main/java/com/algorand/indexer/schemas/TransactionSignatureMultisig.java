package com.algorand.indexer.schemas;

import com.algorand.indexer.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class TransactionSignatureMultisig {
	public long threshold;
	public long version;
	public TransactionSignatureMultisigSubsignature[] subsignature;
	
	public TransactionSignatureMultisig(String json) {
		JsonNode node = Utils.getRoot(json);
		this.threshold = Utils.getLong("threshold", node);
		this.version = Utils.getLong("version", node);
		
		ArrayNode an = Utils.getArrayNode("subsignature", node);
		this.subsignature = new TransactionSignatureMultisigSubsignature[an.size()];
		for (int i = 0; i < an.size(); i++) {
			this.subsignature[i] = new TransactionSignatureMultisigSubsignature(an.get(i));
		}
	}
}
