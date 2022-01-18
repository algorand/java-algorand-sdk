package com.algorand.algosdk.util;

import java.lang.reflect.Array;
import java.util.List;

public class GenericObjToArray {

    /**
     * Given an array/list-like object, infer an array of objects
     * @param val an array/list-like object
     * @return inferred array of objects
     * @throws IllegalArgumentException if it cannot infer if the object is list or array
     */
    public static Object[] unifyToArrayOfObjects(Object val) {
        if (val.getClass().isArray()) {
            if (val instanceof Object[])
                return (Object[]) val;
            int length = Array.getLength(val);
            Object[] outputArray = new Object[length];
            for (int i = 0; i < length; ++i)
                outputArray[i] = Array.get(val, i);
            return outputArray;
        } else if (val instanceof List<?>)
            return ((List<?>) val).toArray(new Object[0]);
        else
            throw new IllegalArgumentException("cannot infer type for unify array/list-like object to object array");
    }

}
