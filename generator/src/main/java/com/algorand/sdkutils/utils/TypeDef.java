package com.algorand.sdkutils.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            // TODO: Remove fields from here (requires refactoring all generators which use them)
            String javaTypeName,
            String rawTypeName,
            String type,
            // TODO: Until here
            String propertyName,
            // TODO: Remove this one too
            String goPropertyName,
            String doc,
            boolean required,
            // TODO: After removing the above, remove openApi prefix from these.
            String openApiRefType,
            String openApiType,
            String openApiArrayType,
            String openApiFormat,
            String openApiAlgorandFormat,
            String openApiAlgorandGoFormat) {
        this.javaTypeName = javaTypeName;
        this.rawTypeName = rawTypeName;
        this.type = type;
        this.propertyName = propertyName;
        this.goPropertyName = goPropertyName;
        this.doc = doc;
        this.openApiRefType = openApiRefType;
        this.openApiType = openApiType;
        this.openApiArrayType = openApiArrayType;
        this.openApiFormat = openApiFormat;
        this.openApiAlgorandFormat = openApiAlgorandFormat;
        this.openApiAlgorandGoFormat = openApiAlgorandGoFormat;
        this.enumValues = null;
        this.required = required;
    }
    public boolean isOfType(String typeArg) {
        if (this.type == null) {
            return false;
        }
        //return this.type.contains(type);
        boolean match = Arrays.stream(type.split(","))
                .anyMatch(t -> typeArg.equals(t));
        return match;
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
    public final String openApiRefType;
    public String openApiType;
    public final String openApiArrayType;
    public String openApiFormat;
    public String openApiAlgorandFormat;
    public String openApiAlgorandGoFormat;

    // This field is private because it is sometimes (but usually not) a CSV and it's too easy to accidentally
    // use it the wrong way. Use 'isOfType' to compare against specific types.
    // For example, "getterSetter,array" if it's an array of special types.
    private String type;

    /*
    public String getJavaTypeName() {
        return javaTypeName;
    }

    public String getRawTypeName() {
        return rawTypeName;
    }

    public String getGoPropertyName() {
        return goPropertyName;
    }
    */

    public String getPropertyName() {
        return propertyName;
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
        return openApiType;
    }

    public String getFormat() {
        return openApiFormat;
    }

    public String getArrayType() {
        return openApiArrayType;
    }

    public String getRefType() {
        if ("TealKeyValueStore".equals(openApiRefType)) {
            System.out.println("break");
        }
        return openApiRefType;
    }

    public String getAlgorandFormat() {
        return openApiAlgorandFormat;
    }

    public String getAlgorandGoFormat() {
        return openApiAlgorandGoFormat;
    }
}
