package com.algorand.algosdk.util.abi;

import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

public class Type {
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

        } else if (str.startsWith("uint")) {

        } else if (str.equals("byte")) {
            return new Byte();
        } else if (str.startsWith("ufixed")) {

        } else if (str.equals("bool")) {
            return new Bool();
        } else if (str.equals("address")) {
            return new Address();
        } else if (str.equals("string")) {
            return new StringABI();
        } else if (str.length() >= 2 && str.charAt(0) == '(' && str.endsWith(")")) {

        } else {
            throw new IllegalAccessException("Cannot infer type from the string: " + str);
        }
        return new Type();
    }
}

class Uint extends Type {
    public final int bitSize;

    Uint(int size) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "uint initialize failure: bitSize should be in [8, 512] and bitSize moe 8 = 0"
            );
        this.bitSize = size;
    }

    @Override
    public String string() { return "uint" + this.bitSize; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Uint)) return false;
        return this.bitSize == ((Uint) o).bitSize;
    }

    @Override
    public int hashCode() { return Objects.hash(bitSize); }
}

class Byte extends Type {
    Byte() {}

    @Override
    public String string() { return "byte"; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Byte; }
}

class Ufixed extends Type {
    public final int bitSize;
    public final int precision;

    Ufixed(int size, int precision) {
        if (size < 8 || size > 512 || size % 8 != 0)
            throw new IllegalArgumentException(
                    "ufixed initialize failure: bitSize should be in [8, 512] and bitSize moe 8 = 0"
            );
        if (precision < 1 || precision > 160)
            throw new IllegalArgumentException("ufixed initialize failure: precision should be in [1, 160]");
        this.bitSize = size;
        this.precision = precision;
    }

    @Override
    public String string() { return "ufixed" + this.bitSize + "x" + this.precision; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ufixed)) return false;
        return bitSize == ((Ufixed) o).bitSize && precision == ((Ufixed) o).precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSize, precision);
    }
}

class Bool extends Type {
    Bool() {}

    @Override
    public String string() { return "bool"; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Bool; }
}

class ArrayStatic extends Type {
    public final Type elemType;
    public final int length;

    ArrayStatic(Type elemType, int length) {
        if (length < 1)
            throw new IllegalArgumentException("static-array initialize failure: array length should be at least 1");
        this.elemType = elemType;
        this.length = length;
    }

    @Override
    public String string() throws IllegalAccessException { return this.elemType.string() + "[" + this.length + "]"; }

    @Override
    public boolean isDynamic() throws IllegalAccessException { return this.elemType.isDynamic(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayStatic)) return false;
        return length == ((ArrayStatic) o).length && this.elemType.equals(((ArrayStatic) o).elemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemType, length);
    }
}

class Address extends Type {
    Address() {}

    @Override
    public String string() { return "address"; }

    @Override
    public boolean isDynamic() { return false; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Address; }
}

class ArrayDynamic extends Type {
    public final Type elemType;

    ArrayDynamic(Type elemType) { this.elemType = elemType; }

    @Override
    public String string() throws IllegalAccessException { return this.elemType.string() + "[]"; }

    @Override
    public boolean isDynamic() { return true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayDynamic)) return false;
        return this.elemType.equals(((ArrayDynamic) o).elemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemType);
    }
}

class StringABI extends Type {
    StringABI() {}

    @Override
    public String string() { return "string"; }

    @Override
    public boolean isDynamic() { return true; }

    @Override
    public boolean equals(Object obj) { return obj instanceof StringABI; }
}

class TupleABI extends Type {
    public final List<Type> childTypes;

    TupleABI(List<Type> childTypes) { this.childTypes = childTypes; }

    @Override
    public String string() throws IllegalAccessException {
        List<String> childStrs = new ArrayList<>();
        for (Type t : this.childTypes)
            childStrs.add(t.string());
        return  "(" + String.join(",", childStrs) + ")";
    }

    @Override
    public boolean isDynamic() throws IllegalAccessException {
        for (Type t : this.childTypes) {
            if (t.isDynamic())
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleABI)) return false;
        return this.childTypes.equals(((TupleABI) o).childTypes);
    }
}