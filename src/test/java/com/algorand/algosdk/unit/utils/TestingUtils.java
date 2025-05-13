package com.algorand.algosdk.unit.utils;

import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.assertj.core.api.Assertions;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestingUtils {
    static ObjectMapper mapper = new ObjectMapper();
    static ObjectMapper msgpMapper;

    static {
        // MessagePack
        msgpMapper = new ObjectMapper(new MessagePackFactory());
        msgpMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        msgpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        // There's some odd bug in Jackson < 2.8.? where null values are not excluded. See:
        // https://github.com/FasterXML/jackson-databind/issues/1351. So we will
        // also annotate all fields manually
        msgpMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

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
            if (r.getContentType().contains("application/json")) {
                verifyJsonResponse(r, expected);
            }
            else if (r.getContentType().contains("application/msgpack")) {
                    verifyMsgpResponse(r, expected);
            }
            else {
                Assertions.fail("Unknown content type, cannot verify: " + r.getContentType());
            }
        }
    }

    private static void verifyJsonResponse(Response r, String expected) throws IOException {
        String expectedString = expected;
        String actualString = r.toString();

        JsonNode expectedNode = mapper.readTree(expectedString);
        JsonNode actualNode = mapper.readTree(actualString);

        compareNodes("root", expectedNode, actualNode);
    }


    private static void verifyMsgpResponse(Response r, String expected) throws IOException {
        String actualString = r.toString();
        byte[] expectedBytes = Encoder.decodeFromBase64(expected);

        JsonNode actualNode = mapper.readTree(actualString);
        JsonNode expectedNode = msgpMapper.readTree(expectedBytes);

        compareNodes("root", expectedNode, actualNode);
    }

    /**
     * This sort isn't perfect because:
     * 1. it's possible to have optional keys, like 'auth-addr' which are alphabetically first.
     * 2. it's technically possible (not in practice currently) for there to be non-unique fields,
     *    these would require a secondary sort.
     * TODO: If this ever becomes required one solution would be to check the current key and select
     *       a known unique key. i.e. global-state -> sort by 'k', accounts -> sort by address, etc.
     *       Alternatively the sort function could accept both lists and have a secondary sort.
     */
    private static List<JsonNode> sort(JsonNode array, String field) {
        if (!array.isArray()){
            Assertions.fail("bad input.");
        }

        List<JsonNode> list = new ArrayList<>();
        Iterator<JsonNode> elements = array.elements();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        try {
            //list.sort(Comparator.comparing(o -> o.get(field).asText()));
            list.sort((o1, o2) -> {
                if (field == null) {
                    return o1.asText().compareTo(o2.asText());
                }
                if (o1.has(field) && o2.has(field)) {
                    return o1.get(field).asText().compareTo(o2.get(field).asText());
                }
                return 0;
            });
        } catch (Exception e) {
            System.out.println("Exception?!");
        }

        return list;
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
            // Allow a BINARY/STRING mismatch
            if (expected.getNodeType() != actual.getNodeType()) {
                if ((expected.getNodeType() == JsonNodeType.BINARY || expected.getNodeType() == JsonNodeType.STRING) &&
                    (actual.getNodeType() == JsonNodeType.BINARY || actual.getNodeType() == JsonNodeType.STRING)) {
                    // allow this mismatch
                } else {
                    assertThat(expected.getNodeType())
                            .as("Failed to match node types: %s", field)
                            .isEqualTo(actual.getNodeType());
                }
            }

            type = expected.getNodeType();
        }

        // Compare primitive types or recurse complex types.
        switch (type) {
            // Compare each index of the array. Sort them then zip through.
            case ARRAY:
                {
                   int expectedSize = (expected == null) ? 0 : expected.size();
                   int actualSize = (actual == null) ? 0 : actual.size();
                   assertThat(expectedSize)
                           .as("Failed to match array sizes: %s", field)
                           .isEqualTo(actualSize);

                   if (expectedSize > 0) {
                       String firstField = null;
                       JsonNode n = expected.elements().next();

                       // If it is an array of objects, sort by the first field.
                       // It's possible the array is not an object. By leaving firstField as null the nodes are
                       // converted to text and compared as strings.
                       if (n.isObject()) {
                           firstField = n.fieldNames().next();
                       }

                       List<JsonNode> expectedList = sort(expected, firstField);
                       List<JsonNode> actualList = sort(actual, firstField);

                       Iterator<JsonNode> expectedElements = expectedList.iterator();
                       Iterator<JsonNode> actualElements = actualList.iterator();

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
            // Allow comparing binary/string together, depending on the encoding it may be required.
            case STRING:
            case BINARY:
                {
                    String expectedValue = (expected == null) ? "" : expected.asText();
                    String actualValue = (actual == null) ? "" : actual.asText();

                    if (!expectedValue.equals(actualValue)) {
                        String decodedActual = new String(Encoder.decodeFromBase64(actualValue));
                        String decodedExpected = new String(Encoder.decodeFromBase64(expectedValue));
                        if (decodedExpected.equals(actualValue)) {
                            // good, continue
                        } else if (decodedActual.equals(expectedValue)) {
                            // good, continue
                        } else {
                            Assertions.fail("Unable to find a match between ['%s'] and ['%s']", expectedValue, actualValue);
                        }
                    }
                }
                break;
            case NULL:
                // This is fine.
                break;
            case MISSING:
            case POJO:
                Assertions.fail("Unhandled type: " + type);
                break;
        }
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
