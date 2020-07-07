package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
        String expected = new String(Files.readAllBytes(body.toPath()));

        assertThat(r).isNotNull();
        if (!r.isSuccessful()) {
            assertThat(r.message()).isEqualTo(expected);
        } else {
            switch (r.getContentType()) {
                case "application/json":
                    verifyJsonResponse(r, expected);
                    break;
                case "application/msgpack":
                    verifyMsgpResponse(r, expected);
                    break;
                default:
                    Assertions.fail("Unknown content type, cannot verify: " + r.getContentType());
            }
        }
    }

    private static void verifyJsonResponse(Response r, String expected) throws IOException {
        String expectedString = expected;
        String actualString = r.toString();

        JsonNode expectedNode = mapper.readTree(expectedString);
        JsonNode actualNode = mapper.readTree(actualString);
        //manually compare to ignore "empty" / "null" / "missing" fields?
        compareNodes("root", expectedNode, actualNode);
        //assertThat(actualNode).isEqualTo(expectedNode);
    }

    private static void compareNodes(String field, JsonNode expected, JsonNode actual) {
        JsonNodeType type = null;

        // If one of the objects is null, get the type from the other one.
        if (expected == null || actual == null) {
            if (expected != null) type = expected.getNodeType();
            if (actual != null) type = actual.getNodeType();

            // If they were both null, just return.
            if (type == null) return;
        }
        // If neither is null, the types should be the same.
        else {
            assertThat(expected.getNodeType())
                    .as("Failed to match node types: %s", field)
                    .isEqualTo(actual.getNodeType());
            type = expected.getNodeType();
        }

        // Compare primitive types or recurse complex types.
        switch (type) {
            // Compare each index of the array.
            // Assume that the arrays are sorted and zip thorugh them.
            case ARRAY:
                {
                   int expectedSize = (expected == null) ? 0 : expected.size();
                   int actualSize = (actual == null) ? 0 : actual.size();
                   assertThat(expectedSize)
                           .as("Failed to match array sizes: %s", field)
                           .isEqualTo(actualSize);

                   if (expectedSize > 0) {
                       Iterator<JsonNode> expectedElements = expected.elements();
                       Iterator<JsonNode> actualElements = actual.elements();
                       int index = 0;

                       while (expectedElements.hasNext() && actualElements.hasNext()) {
                           compareNodes(field + "[" + index + "]", expectedElements.next(), actualElements.next());
                           index++;
                       }
                   }
                }
                break;
            // Compare the objects.
            // Allow for missing fields, pass null recursively and let the specific type decide if that is ok.
            case OBJECT:
                {
                    if (expected == null || actual == null) {
                        Assertions.fail("One of the objects it null and the other isn't empty.");
                    }

                    // Recursively compare objects field by field (allowing for nulls)
                    Set<String> allFields = new HashSet<>();
                    expected.fieldNames().forEachRemaining(allFields::add);
                    actual.fieldNames().forEachRemaining(allFields::add);

                    for (String nextField : allFields) {
                        // This will be null if the nextField doesn't exist. That may be OK
                        JsonNode expectedField = expected.get(nextField);
                        JsonNode actualField = actual.get(nextField);
                        compareNodes(field + "." + nextField, expectedField, actualField);
                    }
                }
                break;
            case NUMBER:
                {
                    int expectedValue = (expected == null) ? 0 : expected.asInt();
                    int actualValue = (actual == null) ? 0 : actual.asInt();
                    assertThat(actualValue)
                            .as("Failed to match field: %s", field)
                            .isEqualTo(expectedValue);
                }
                break;
            case BOOLEAN:
                {
                    boolean expectedValue = (expected == null) ? false : expected.asBoolean();
                    boolean actualValue = (actual == null) ? false : actual.asBoolean();
                    assertThat(actualValue)
                            .as("Failed to match field: %s", field)
                            .isEqualTo(expectedValue);
                }
                break;
            // Compare binary/string the same way
            case STRING:
            case BINARY:
            {
                String expectedValue = (expected == null) ? "" : expected.asText();
                String actualValue = (actual == null) ? "" : actual.asText();
                assertThat(actualValue)
                        .as("Failed to match field: %s", field)
                        .isEqualTo(expectedValue);
            }
            break;
            case MISSING:
            case NULL:
            case POJO:
                Assertions.fail("Unhandled type: " + type);
                break;
        }
    }

    private static void verifyMsgpResponse(Response r, String expected) throws IOException {
        String expectedString = expected;

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

    private static URL verifyAndGetURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Assertions.fail("Malformed URL: " + e.getMessage());
        }
        return null;
    }

    /**
     * Used by path tests to verify that two urls are the same.
     */
    public static void verifyPathUrls(String url1, String url2) {
        URL parsedURL1 = verifyAndGetURL(url1);
        String url1NoProtocol = parsedURL1.getPath();
        if (parsedURL1.getQuery() != null) {
            url1NoProtocol += '?' + parsedURL1.getQuery();
        }

        String[] segments1 = url1NoProtocol.split("[&?]");
        String[] segments2 = url2.split("[&?]");

        Arrays.sort(segments1, Comparator.naturalOrder());
        Arrays.sort(segments2, Comparator.naturalOrder());

        if (segments1.length != segments2.length) {
            Assertions.fail("Different number of parameters, "
                    + url1NoProtocol
                    + " != "
                    + url2);
        }

        int s2 = 0;
        for (String seg1 : segments1) {
            assertThat(seg1).isEqualTo(segments2[s2]).describedAs("Parameter mismatch, "
                    + url1NoProtocol
                    + " != "
                    + url2);
            s2++;
        }
    }

    public static boolean notEmpty(String str) {
        return !str.isEmpty() && !str.equals("none");        
    }

    public static boolean notEmpty(Long val) {
        return val != 0;
    }
}
