package com.algorand.algosdk.util.abi.types;

import com.algorand.algosdk.algod.client.StringUtil;

import java.util.ArrayList;
import java.util.List;

class TupleABI extends Type {
    public final List<Type> childTypes;

    TupleABI(List<Type> childTypes) {
        this.childTypes = childTypes;
    }

    @Override
    public String string() throws IllegalAccessException {
        List<String> childStrs = new ArrayList<>();
        for (Type t : this.childTypes)
            childStrs.add(t.string());
        return "(" + StringUtil.join(childStrs.toArray(new String[0]), ",") + ")";
    }

    @Override
    public boolean isDynamic() throws IllegalAccessException {
        for (Type t : this.childTypes) {
            if (t.isDynamic())
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleABI)) return false;
        return this.childTypes.equals(((TupleABI) o).childTypes);
    }
}