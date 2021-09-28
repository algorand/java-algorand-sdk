package com.algorand.algosdk.util.abi.types;

import com.algorand.algosdk.algod.client.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TupleT extends Type {
    public final List<Type> childTypes;

    public TupleT(List<Type> childTypes) {
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
        if (!(o instanceof TupleT)) return false;
        return this.childTypes.equals(((TupleT) o).childTypes);
    }

    @Override
    public int byteLen() throws IllegalAccessException, IllegalArgumentException {
        int size = 0;
        for (int i = 0; i < this.childTypes.size(); i++) {
            if (this.childTypes.get(i) instanceof BoolT) {
                int after = Type.findBoolLR(this.childTypes.toArray(new Type[0]), i, 1);
                i += after;
                int boolNumber = after + 1;
                size += boolNumber / 8;
                size += (boolNumber % 8 != 0) ? 1 : 0;
            } else
                size += this.childTypes.get(i).byteLen();
        }
        return size;
    }
}