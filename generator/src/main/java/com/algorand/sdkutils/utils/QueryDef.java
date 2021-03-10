package com.algorand.sdkutils.utils;

import java.util.ArrayList;
import java.util.List;

public class QueryDef {
    final public String name;
    final public String returnType;
    //final public String returnTypeModel;
    final public String path;
    final public String description;
    final public String method;
    final public List<String> contentType;
    final public List<TypeDef> queryParameters = new ArrayList<>();
    final public List<TypeDef> pathParameters = new ArrayList<>();
    final public List<TypeDef> bodyParameters = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getPath() {
        return path;
    }


    public String getDescription() {
        return description;
    }

    public String getMethod() {
        return method;
    }

    public List<String> getContentType() {
        return contentType;
    }

    public List<TypeDef> getQueryParameters() {
        return queryParameters;
    }

    public List<TypeDef> getPathParameters() {
        return pathParameters;
    }

    public List<TypeDef> getBodyParameters() {
        return bodyParameters;
    }
}
