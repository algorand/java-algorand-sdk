package com.algorand.algosdk.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.Assertions;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestingUtils {
	static ObjectMapper mapper = new ObjectMapper();

	public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
		for (T each : enumeration.getEnumConstants()) {
			if (each.name().compareToIgnoreCase(search) == 0) {
				return each;
			}
		}

		Assertions.fail("Unable to find (" + search + ") in enum " + enumeration.getName());
		return null;
	}

	/**
	 * Used by response tests to compare a response to the input file.
	 */
	public static void verifyResponse(Response r, File body) throws IOException {
		assertThat(r).isNotNull();
		assertThat(r.isSuccessful()).isTrue();

		switch (r.getContentType()) {
			case "application/json":
				verifyJsonResponse(r, body);
				break;
			case "application/msgpack":
				verifyMsgpResponse(r, body);
				break;
			default:
				Assertions.fail("Unknown content type, cannot verify: " + r.getContentType());
		}
	}

	private static void verifyJsonResponse(Response r, File body) throws IOException {
		String expectedString = new String(Files.readAllBytes(body.toPath()));
		String actualString = r.toString();

		JsonNode expectedNode = mapper.readTree(expectedString);
		JsonNode actualNode = mapper.readTree(actualString);
		assertThat(expectedNode).isEqualTo(actualNode);
	}

	private static void verifyMsgpResponse(Response r, File body) throws IOException {
		String expectedString = new String(Files.readAllBytes(body.toPath()));

	    /*
		// Convert the POJO back into messagepack, this is the most valid approach.

	    // These are different and I'm not sure why.
	    // There may be an issue with the source 'msgpacktool' displays it incorrectly as well.
		String actualString = r.toString();

		Object o = r.body();
		String encoded = Encoder.encodeToBase64(Encoder.encodeToMsgPack(o));

		assertThat(encoded).isEqualTo(expectedString);
	     */

		// Get both as maps and compare the maps.
		// Somehow the "type" field is turned into a String in the actual value, but is byte[] in the expected.
		// Is the source wrong?
        /*
		Map<String,Object> exp = Encoder.decodeFromMsgPack(expectedString, Map.class);
		Map<String,Object> act = Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(r.body()), Map.class);
		assertThat(act).isEqualTo(exp);
         */

		// This isn't totally valid because it is comparing the two objects after being serialized by the same
		// serializer...

		// Manually decode the thing into a POJO and compare the POJOs.
		// This requires reflection to extract the POJO type.
		Field f = FieldUtils.getField(r.getClass(), "valueType", true);
		Class value = null;
		try {
			value = (Class) f.get(r);
		} catch (IllegalAccessException e) {
		    Assertions.fail("No good.");
		}

		Object act = r.body();
		Object exp = Encoder.decodeFromMsgPack(expectedString, value);

		// This didn't seem to work for deeply nested objects
		//assertThat(act).isEqualTo(exp);

		// This did
		assertThat(Encoder.encodeToJson(act)).isEqualTo(Encoder.encodeToJson(exp));
	}

	/**
	 * Used by path tests to verify that two urls are the same.
	 */
	public static void verifyPathUrls(String url1, String url2, String skip) {
		url1 = url1.replace(skip, "");
		url2 = url2.replace(skip, "");
		
		String[] segments1 = url1.split("[&?]");
		String[] segments2 = url2.split("[&?]");

		//assertThat(segments1).containsExactlyInAnyOrder(segments2);
		///*
		Arrays.sort(segments1, Comparator.naturalOrder());
		Arrays.sort(segments2, Comparator.naturalOrder());


		if (segments1.length != segments2.length) {
			Assertions.fail("wrong lenght");
		}
		
		int s2 = 0;
		for (String seg1 : segments1) {
			assertThat(seg1).isEqualTo(segments2[s2]);
			s2++;
		}
		//return true;
		 //*/
	}
	
	public static boolean notEmpty(String str) {
		return !str.isEmpty() && !str.equals("none");		
	}
	
	public static boolean notEmpty(Long val) {
		return val != 0;
	}
}
