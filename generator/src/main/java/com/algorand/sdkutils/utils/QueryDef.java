package com.algorand.sdkutils.utils;

import java.util.List;

public class QueryDef {
    final public String name;
    final public String returnType;
    //final public String returnTypeModel;
    final public String path;
    final public String description;
    final public String method;
    final public List<String> contentType;

    public QueryDef(String name, String returnType, String path, String description, String method, List<String> contentType) {
        this.name = name;
        this.returnType = returnType;
        this.path = path;
        this.description = description;
        this.method = method;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return
                "name: " + this.name
                + ", return type: " + this.returnType
                + ", path: " + this.path
                + ", description: " + this.description
                + ", method: " + this.method;
    }
}
