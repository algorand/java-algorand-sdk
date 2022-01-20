package com.algorand.algosdk.util;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;

import java.util.ArrayList;
import java.util.List;

public class SplitAndProcessABIArgs {
    public int methodArgIndex = 0;
    public List<String> argTypes = new ArrayList<>();

    public SplitAndProcessABIArgs(Method method) {
        for (Method.Arg argT : method.args) {
            if (Method.TxArgTypes.contains(argT.type))
                continue;
            if (Method.RefArgTypes.contains(argT.type)) {
                if (argT.type.equals("account"))
                    argTypes.add("address");
                else
                    argTypes.add("uint64");
            } else {
                argTypes.add(argT.type);
            }
        }
    }

    public List<Object> splitAndProcessABIArgs(String str) {
        if (str.isEmpty()) return new ArrayList<>();

        String[] argTokens = str.split(",");
        List<Object> res = new ArrayList<>();

        for (String argToken : argTokens) {
            ABIType currT = ABIType.valueOf(this.argTypes.get(methodArgIndex));
            byte[] abiEncoded = Encoder.decodeFromBase64(argToken);
            res.add(currT.decode(abiEncoded));
            methodArgIndex++;
        }

        return res;
    }
}
