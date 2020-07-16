package com.algorand.sdkutils.utils;

import java.util.HashSet;
import java.util.Set;

public class StructDef {

    public String name;
    public String aliasOf;
    public String doc;
    public Set<String> requiredProperties = new HashSet<>();
    public Set<String> mutuallyExclusiveProperties = new HashSet<>();

    public StructDef(String name, String aliasOf) {
        this.name = name;
        this.aliasOf = aliasOf;
    }

    public StructDef(String name, String doc, Set<String> requiredProperties, Set<String> mutuallyExclusiveProperties) {
        this.name = name;
        this.doc = doc;
        if (requiredProperties != null) {
            this.requiredProperties = requiredProperties;
        }
        if (mutuallyExclusiveProperties != null) {
            this.mutuallyExclusiveProperties = mutuallyExclusiveProperties;
        }
    }

    @Override
    public String toString() {
        return "name: '" + name + "', " +
                "doc: '" + doc + "', " +
                "required: ['" + String.join("', '", requiredProperties) + "'], " +
                "mutuallyExclusive: ['" + String.join("', '", mutuallyExclusiveProperties) + "']";
    }
}
