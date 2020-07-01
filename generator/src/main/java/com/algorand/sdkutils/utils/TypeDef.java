package com.algorand.sdkutils.utils;

import java.util.List;

/**
 * 
 * TypeDef hold together information about a type
 * javaTypeName is the generated code type: e.g. List<abc>, or MyEnumClassName
 * rawTypeName is the type name from the spec file
 * def is the definition of the type. e.g. the class declaration of the enum class
 * type is a loosely defined tag used by generator e.g. enum, array, etc.
 * propertyName is the class/struct member name
 * goPropertyName, is when provided with x-go-name
 * doc is the comments associated with the parameter
 * required indeicates if the field is a required field
 * e.g. For enum type, typeName will be enum class name, def will be the enum
 */
public class TypeDef {
    public TypeDef(
            String javaTypeName,
            String rawTypeName,
            String def, 
            String type, 
            String propertyName,
            String goPropertyName,
            String doc,
            boolean required) {
        this.javaTypeName = javaTypeName;
        this.rawTypeName = rawTypeName;
        this.def = def;
        this.type = type;
        this.propertyName = propertyName;
        this.goPropertyName = goPropertyName;
        this.doc = doc;
        this.enumValues = null;
        this.required = required;
    }
    public TypeDef(String typeName,
            String rawTypeName,
            String propertyName, 
            String goPropertyName,
            String doc,
            boolean required) {
        this.javaTypeName = typeName;
        this.rawTypeName = rawTypeName;
        this.def = null;
        this.type = null;
        this.propertyName = propertyName;
        this.goPropertyName = goPropertyName;
        this.doc = doc;
        this.enumValues = null;
        this.required = required;
    }
    public boolean isOfType(String type) {
        if (this.type == null) {
            return false;
        }
        return this.type.contains(type);
    }

    @Override
    public String toString() {
        String enums = "[]";
        if (enumValues != null) {
            enums = "[" + String.join(", ", enumValues) + "]";
        }
        return
                "javaTypeName: '" + javaTypeName + "', " +
                "rawTypeName: '" + rawTypeName + "', " +
                "def: '" + def + "', " +
                "propertyName: '" + propertyName + "', " +
                "goPropertyName: '" + goPropertyName + "', " +
                "doc: '" + doc + "', " +
                "enumValues: " + enums + ", " +
                "required: '" + required + "'";
    }

    public String javaTypeName;
    public String rawTypeName;
    public String def;
    public String propertyName;
    public String goPropertyName;
    public String doc;
    public List<String> enumValues;
    public boolean required;

    // This field is private because it is sometimes (but usually not) a CSV and it's too easy to accidentally
    // use it the wrong way. Use 'isOfType' to compare against specific types.
    private String type;
}
