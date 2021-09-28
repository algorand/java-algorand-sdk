package com.algorand.algosdk.util.abi.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Type {
    private static final Pattern staticArrayPattern = Pattern.compile("^(?<elemT>[a-z\\d\\[\\](),]+)\\[(?<len>[1-9][\\d]*)]$");
    private static final Pattern ufixedPattern = Pattern.compile("^ufixed(?<size>[1-9][\\d]*)x(?<precision>[1-9][\\d]*)$");

    public String string() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access base type method: string");
    }

    public boolean isDynamic() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access base type method: isDynamic");
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public int byteLen() throws IllegalAccessException {
        throw new IllegalAccessException("Should not access base type method: byteLen");
    }

    public static Type fromString(String str) throws IllegalArgumentException {
        if (str.endsWith("[]")) {
            Type elemType = Type.fromString(str.substring(0, str.length() - 2));
            return new ArrayDynamicT(elemType);
        } else if (str.endsWith("]")) {
            Matcher m = staticArrayPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("static array type ill format: " + str);
            Type elemT = Type.fromString(m.group("elemT"));
            int length = Integer.parseInt(m.group("len"));
            return new ArrayStaticT(elemT, length);
        } else if (str.startsWith("uint")) {
            int size = Integer.parseInt(str.substring(4));
            return new UintT(size);
        } else if (str.equals("byte")) {
            return new ByteT();
        } else if (str.startsWith("ufixed")) {
            Matcher m = ufixedPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("ufixed type ill format: " + str);
            int size = Integer.parseInt(m.group("size"));
            int precision = Integer.parseInt(m.group("precision"));
            return new UfixedT(size, precision);
        } else if (str.equals("bool")) {
            return new BoolT();
        } else if (str.equals("address")) {
            return new AddressT();
        } else if (str.equals("string")) {
            return new StringT();
        } else if (str.length() >= 2 && str.charAt(0) == '(' && str.endsWith(")")) {
            List<String> tupleContent = parseTupleContent(str.substring(1, str.length() - 1));
            List<Type> tupleTypes = new ArrayList<>();
            for (String subStr : tupleContent)
                tupleTypes.add(Type.fromString(subStr));
            return new TupleT(tupleTypes);
        } else {
            throw new IllegalArgumentException("Cannot infer type from the string: " + str);
        }
    }

    public static class Segment {
        public int L, R;

        Segment(int left, int right) {
            this.L = left;
            this.R = right;
        }
    }

    private static List<String> parseTupleContent(String str) {
        if (str.length() == 0)
            return new ArrayList<>();

        if (str.startsWith(",") || str.endsWith(","))
            throw new IllegalArgumentException("parsing error: tuple content should not start with comma");

        if (str.contains(",,"))
            throw new IllegalArgumentException("parsing error: tuple content should not have consecutive commas");

        Stack<Integer> parenStack = new Stack<>();
        List<Segment> parenSegments = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(')
                parenStack.push(i);
            else if (str.charAt(i) == ')') {
                if (parenStack.empty())
                    throw new IllegalArgumentException("parsing error: tuple parentheses are not balanced: " + str);
                int leftParenIndex = parenStack.pop();
                if (parenStack.empty())
                    parenSegments.add(new Segment(leftParenIndex, i));
            }
        }
        if (!parenStack.empty())
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

    public static int findBoolLR(Type[] typeArray, int index, int delta) {
        int until = 0;
        while (true) {
            int currentIndex = index + delta * until;
            if (typeArray[currentIndex] instanceof BoolT) {
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
}