package com.algorand.sdkutils.utils;

import java.util.List;

/**
 * TypeDef hold together information about a type
 * e.g. For enum type, typeName will be enum class name
 */
public class TypeDef {
    /**
     * @param javaTypeName is the generated code type: e.g. List<abc>, or MyEnumClassName
     * @param rawTypeName is the type name from the spec file
     * @param type is a loosely defined tag used by generator e.g. enum, array, etc.
     * @param propertyName is the class/struct member name
     * @param goPropertyName, is when provided with x-go-name
     * @param doc is the comments associated with the parameter
     * @param required indicates if the field is a required field
     */
    public TypeDef(
            String javaTypeName,
            String rawTypeName,
            String type, 
            String propertyName,
            String goPropertyName,
            String doc,
            boolean required) {
        this.javaTypeName = javaTypeName;
        this.rawTypeName = rawTypeName;
        this.type = type;
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
                "propertyName: '" + propertyName + "', " +
                "goPropertyName: '" + goPropertyName + "', " +
                "doc: '" + doc + "', " +
                "enumValues: " + enums + ", " +
                "required: '" + required + "'";
    }

    public String javaTypeName;
    public String rawTypeName;
    public String propertyName;
    public String goPropertyName;
    public String doc;
    public List<String> enumValues;
    public boolean required;

    // This field is private because it is sometimes (but usually not) a CSV and it's too easy to accidentally
    // use it the wrong way. Use 'isOfType' to compare against specific types.
    private String type;

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public String getRawTypeName() {
        return rawTypeName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getGoPropertyName() {
        return goPropertyName;
    }

    public String getDoc() {
        return doc;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public boolean isRequired() {
        return required;
    }

    public String getType() {
        return type;
    }
}
