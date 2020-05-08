package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.v2.client.common.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public class TestingUtils {
	static ObjectMapper mapper = new ObjectMapper();
	public static void verifyResponse(Response r, File body) throws IOException {
		assertThat(r).isNotNull();
		assertThat(r.isSuccessful()).isTrue();

		String expectedString = new String(Files.readAllBytes(body.toPath()));
		String actualString = r.toString();

		JsonNode expectedNode = mapper.readTree(expectedString);
		JsonNode actualNode = mapper.readTree(actualString);

		assertThat(expectedNode).isEqualTo(actualNode);
	}

	public static boolean comparePathUrls(String url1, String url2, String skip) {
		url1 = url1.replace(skip, "");
		url2 = url2.replace(skip, "");
		
		String[] segments1 = url1.split("[&?]");
		String[] segments2 = url2.split("[&?]");
		
		Arrays.sort(segments1, Comparator.naturalOrder());
		Arrays.sort(segments2, Comparator.naturalOrder());
		
		if (segments1.length != segments2.length) {
			return false;
		}
		
		int s2 = 0;
		for (String seg1 : segments1) {
			if (!seg1.equals(segments2[s2])) {
				return false;
			}
			s2++;
		}
		return true;
	}
	
	public static boolean notEmpty(String str) {
		return !str.isEmpty() && !str.equals("none");		
	}
	
	public static boolean notEmpty(Long val) {
		return val != 0;
	}
}
