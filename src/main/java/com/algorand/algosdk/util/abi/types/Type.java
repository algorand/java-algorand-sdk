package com.algorand.algosdk.util.abi.types;

import java.util.ArrayList;
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
    public boolean equals(Object obj) { return false; }

    public static Type fromString(String str) throws Exception {
        if (str.endsWith("[]")) {
            Type elemType = Type.fromString(str.substring(0, str.length() - 2));
            return new ArrayDynamic(elemType);
        } else if (str.endsWith("]")) {
            Matcher m = staticArrayPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("static array type ill format: " + str);
            Type elemT = Type.fromString(m.group("elemT"));
            int length = Integer.parseInt(m.group("len"));
            return new ArrayStatic(elemT, length);
        } else if (str.startsWith("uint")) {
            int size = Integer.parseInt(str.substring(4));
            return new Uint(size);
        } else if (str.equals("byte")) {
            return new Byte();
        } else if (str.startsWith("ufixed")) {
            Matcher m = ufixedPattern.matcher(str);
            if (!m.matches())
                throw new IllegalArgumentException("ufixed type ill format: " + str);
            int size = Integer.parseInt(m.group("size"));
            int precision = Integer.parseInt(m.group("precision"));
            return new Ufixed(size, precision);
        } else if (str.equals("bool")) {
            return new Bool();
        } else if (str.equals("address")) {
            return new Address();
        } else if (str.equals("string")) {
            return new StringABI();
        } else if (str.length() >= 2 && str.charAt(0) == '(' && str.endsWith(")")) {
            List<String> tupleContent = parseTupleContent(str.substring(1, str.length() - 1));
            List<Type> tupleTypes = new ArrayList<>();
            for (String subStr : tupleContent)
                tupleTypes.add(Type.fromString(subStr));
            return new TupleABI(tupleTypes);
        } else {
            throw new IllegalAccessException("Cannot infer type from the string: " + str);
        }
    }

    private static List<String> parseTupleContent(String str) throws Exception {
        if (str.length() == 0)
            return new ArrayList<>();

        if (str.startsWith(",") || str.endsWith(","))
            throw new IllegalArgumentException("parsing error: tuple content should not start with comma");

        if (str.contains(",,"))
            throw new IllegalArgumentException("parsing error: tuple content should not have consecutive commas");

        Stack<Integer> parenStack = new Stack<>();

//        List<>


        return new ArrayList<>();
    }
}