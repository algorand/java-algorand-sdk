package com.algorand.sdkutils.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructDef {

    public String name;
    public String aliasOf;
    public String doc;
    public List<TypeDef> properties = new ArrayList<>();
    public Set<String> requiredProperties = new HashSet<>();
    public Set<String> mutuallyExclusiveProperties = new HashSet<>();

    public StructDef(String name, String aliasOf) {
        this.name = name;
        this.aliasOf = aliasOf;
    }

    public StructDef(String name, String doc, List<TypeDef> properties, Set<String> requiredProperties, Set<String> mutuallyExclusiveProperties) {
        this.name = name;
        this.doc = doc;
        if (properties != null) {
            this.properties = properties;
        }
        if (requiredProperties != null) {
            this.requiredProperties = requiredProperties;
        }
        if (mutuallyExclusiveProperties != null) {
            this.mutuallyExclusiveProperties = mutuallyExclusiveProperties;
        }
    }

    public Set<String> getUniqueTypes() {
        Set<String> types = new HashSet<>();
        for (TypeDef typeDef : this.properties) {
            types.add(typeDef.rawTypeName);
            if (typeDef.isOfType("array")) {
                types.add("array");
            }
        }
        return types;
    }

    @Override
    public String toString() {
        return "name: '" + name + "', " +
                "doc: '" + doc + "', " +
                "required: ['" + String.join("', '", requiredProperties) + "'], " +
                "mutuallyExclusive: ['" + String.join("', '", mutuallyExclusiveProperties) + "']";
    }

    public String getName() {
        return name;
    }

    public String getAliasOf() {
        return aliasOf;
    }

    public String getDoc() {
        return doc;
    }

    public Set<String> getRequiredProperties() {
        return requiredProperties;
    }

    public Set<String> getMutuallyExclusiveProperties() {
        return mutuallyExclusiveProperties;
    }
}
