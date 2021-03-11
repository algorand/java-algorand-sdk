package com.algorand.sdkutils.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<String> getPathParts() {
        return Stream.of(this.path.split("/"))
                .filter(part -> !part.equals(""))
                .collect(Collectors.toList());
    }

    public List<TypeDef> getAllParams() {
        // Make it easier to loop thorugh parameters without caring what they are.
        List<TypeDef> parameters = new ArrayList<>();
        parameters.addAll(this.queryParameters);
        parameters.addAll(this.pathParameters);
        parameters.addAll(this.bodyParameters);
        return parameters;
    }

    public Set<String> getUniqueTypes() {
        // Make it easier to get the types
        Set<String> types = new HashSet<>();
        for (TypeDef typeDef: this.getAllParams()) {
            types.add(typeDef.rawTypeName);
            if (typeDef.isOfType("array")) {
                types.add("array");
            }
            if (typeDef.isOfType("enum")) {
                types.add("enum");
            }
        }
        if (this.returnType != "String") {
            types.add(this.returnType);
        }
        return types;
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
