package com.algorand.sdkutils.utils;

public class StructDef {

    public String name;
    public String doc;
    
    public StructDef(String name, String doc) {
        this.name = name;
        this.doc = doc;
    }

    @Override
    public String toString() {
        return "name: '" + name + "', " + "doc: '" + doc + "'";
    }
}
