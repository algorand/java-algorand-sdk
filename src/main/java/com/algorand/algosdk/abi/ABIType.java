package com.algorand.algosdk.abi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ABIType {
    public static final int ABI_DYNAMIC_HEAD_BYTE_LEN = 2;
    private static final Pattern staticArrayPattern = Pattern.compile("^(?<elemT>[a-z\\d\\[\\](),]+)\\[(?<len>0|[1-9][\\d]*)]$");
    private static final Pattern ufixedPattern = Pattern.compile("^ufixed(?<size>[1-9][\\d]*)x(?<precision>[1-9][\\d]*)$");

    /**
     * Check if this ABI type is a dynamic type.
     * @return boolean decision if the ABI type is dynamic.
     */
    public abstract boolean isDynamic();

    public abstract boolean equals(Object obj);

    /**
     * Precompute the byte size of the static ABI typed value
     * @return the byte size of the ABI value
     * @throws IllegalArgumentException if the ABI type is dynamic typed
     */
    public abstract int byteLen();

    /**
     * Encode JAVA values with ABI rules based on the ABI type schemes
     * @param obj JAVA values
     * @return byte[] of ABI encoding
     * @throws IllegalArgumentException if encoder cannot infer the type from obj
     */
    public abstract byte[] encode(Object obj);

    /**
     * Decode ABI encoded byte array to JAVA values from ABI type schemes
     * @param encoded byte array of ABI encoding
     * @return JAVA object of corresponding values
     * @throws IllegalArgumentException if encoded byte array cannot match with ABI encoding rules
     */
    public abstract Object decode(byte[] encoded);

    /**
     * Deserialize ABI type scheme from string
     * @param str string representation of ABI type schemes
     * @return ABI type scheme object
     * @throws IllegalArgumentException if ABI type serialization strings are corrupted
     */
    public static ABIType valueOf(String str) {
        if (str.endsWith("[]")) {
            ABIType elemType = ABIType.valueOf(str.substring(0, str.length() - 2));
            return new TypeArrayDynamic(elemType);
        } else if (str.endsWith("]")) {
            Matcher m = staticArrayPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("static array type ill format: " + str);
            ABIType elemT = ABIType.valueOf(m.group("elemT"));
            int length = Integer.parseInt(m.group("len"));
            return new TypeArrayStatic(elemT, length);
        } else if (str.startsWith("uint")) {
            int size = Integer.parseInt(str.substring(4));
            return new TypeUint(size);
        } else if (str.equals("byte")) {
            return new TypeByte();
        } else if (str.startsWith("ufixed")) {
            Matcher m = ufixedPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("ufixed type ill format: " + str);
            int size = Integer.parseInt(m.group("size"));
            int precision = Integer.parseInt(m.group("precision"));
            return new TypeUfixed(size, precision);
        } else if (str.equals("bool")) {
            return new TypeBool();
        } else if (str.equals("address")) {
            return new TypeAddress();
        } else if (str.equals("string")) {
            return new TypeString();
        } else if (str.length() >= 2 && str.charAt(0) == '(' && str.endsWith(")")) {
            List<String> tupleContent = parseTupleContent(str.substring(1, str.length() - 1));
            List<ABIType> tupleTypes = new ArrayList<>();
            for (String subStr : tupleContent)
                tupleTypes.add(ABIType.valueOf(subStr));
            return new TypeTuple(tupleTypes);
        } else {
            throw new IllegalArgumentException("Cannot infer type from the string: " + str);
        }
    }

    private static class Segment {
        public int L, R;

        Segment(int left, int right) {
            this.L = left;
            this.R = right;
        }
    }

    public static List<String> parseTupleContent(String str) {
        if (str.length() == 0)
            return new ArrayList<>();

        if (str.startsWith(",") || str.endsWith(","))
            throw new IllegalArgumentException("parsing error: tuple content should not start or end with comma");

        if (str.contains(",,"))
            throw new IllegalArgumentException("parsing error: tuple content should not have consecutive commas");

        ArrayDeque<Integer> parenStack = new ArrayDeque<>();
        List<Segment> parenSegments = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(')
                parenStack.push(i);
            else if (str.charAt(i) == ')') {
                if (parenStack.isEmpty())
                    throw new IllegalArgumentException("parsing error: tuple parentheses are not balanced: " + str);
                int leftParenIndex = parenStack.pop();
                if (parenStack.isEmpty()) {
                    // iterate through the byte str, include all the bytes after closing round bracket, for array indicator
                    // increase the index until it meets comma, or end of string
                    int forwardIndex = i + 1;
                    while (forwardIndex < str.length() && str.charAt(forwardIndex) != ',')
                        forwardIndex++;
                    i = forwardIndex - 1;
                    parenSegments.add(new Segment(leftParenIndex, i));
                }
            }
        }
        if (!parenStack.isEmpty())
            throw new IllegalArgumentException("parsing error: tuple parentheses are not balanced: " + str);

        String strCopied = str;
        for (int i = parenSegments.size() - 1; i >= 0; i--)
            strCopied = strCopied.substring(0, parenSegments.get(i).L) + strCopied.substring(parenSegments.get(i).R + 1);

        String[] tupleSeg = strCopied.split(",", -1);
        int parenSegCount = 0;
        for (int i = 0; i < tupleSeg.length; i++) {
            if (tupleSeg[i].isEmpty()) {
                tupleSeg[i] = str.substring(parenSegments.get(parenSegCount).L, parenSegments.get(parenSegCount).R + 1);
                parenSegCount++;
            }
        }

        return Arrays.asList(tupleSeg);
    }

    protected static int findBoolLR(ABIType[] typeArray, int index, int delta) {
        int until = 0;
        while (true) {
            int currentIndex = index + delta * until;
            if (typeArray[currentIndex] instanceof TypeBool) {
                if (currentIndex != typeArray.length - 1 && delta > 0)
                    until++;
                else if (currentIndex != 0 && delta < 0)
                    until++;
                else
                    break;
            } else {
                until--;
                break;
            }
        }
        return until;
    }

    /**
     * Take the first 2 bytes in the byte array
     * (consider the byte array to be an encoding for ABI dynamic typed value)
     * @param encoded an ABI encoding byte array
     * @return the first 2 bytes of the ABI encoding byte array
     * @throws IllegalArgumentException if the encoded byte array has length < 2
     */
    protected static byte[] getLengthEncoded(byte[] encoded) {
        if (encoded.length < ABI_DYNAMIC_HEAD_BYTE_LEN)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedLength = new byte[ABI_DYNAMIC_HEAD_BYTE_LEN];
        System.arraycopy(encoded, 0, encodedLength, 0, ABI_DYNAMIC_HEAD_BYTE_LEN);
        return encodedLength;
    }

    /**
     * Take the bytes after the first 2 bytes in the byte array
     * (consider the byte array to be an encoding for ABI dynamic typed value)
     * @param encoded an ABI encoding byte array
     * @return the tailing bytes after the first 2 bytes of the ABI encoding byte array
     * @throws IllegalArgumentException if the encoded byte array has length < 2
     */
    protected static byte[] getContentEncoded(byte[] encoded) {
        if (encoded.length < ABI_DYNAMIC_HEAD_BYTE_LEN)
            throw new IllegalArgumentException("encode byte size too small, less than 2 bytes");
        byte[] encodedString = new byte[encoded.length - ABI_DYNAMIC_HEAD_BYTE_LEN];
        System.arraycopy(encoded, ABI_DYNAMIC_HEAD_BYTE_LEN, encodedString, 0, encoded.length - ABI_DYNAMIC_HEAD_BYTE_LEN);
        return encodedString;
    }

    /**
     * Cast a dynamic/static array to ABI tuple type
     * @param size length of the ABI array
     * @param t ABI type of the element of the ABI array
     * @return a type-cast from ABI array to an ABI tuple type
     */
    protected static TypeTuple castToTupleType(int size, ABIType t) {
        List<ABIType> tupleTypes = new ArrayList<>();
        for (int i = 0; i < size; i++)
            tupleTypes.add(t);
        return new TypeTuple(tupleTypes);
    }
}
