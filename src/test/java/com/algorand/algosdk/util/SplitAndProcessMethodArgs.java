package com.algorand.algosdk.util;

import com.algorand.algosdk.abi.ABIType;
import com.algorand.algosdk.abi.Method;

import java.util.ArrayList;
import java.util.List;

public class SplitAndProcessMethodArgs {
    public int methodArgIndex = 0;
    public List<String> argTypes = new ArrayList<>();

    public SplitAndProcessMethodArgs(Method method) {
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

    public List<Object> splitAndProcessMethodArgs(String str, List<Long> rememberedAppIDs) {
        if (str.isEmpty()) return new ArrayList<>();

        String[] argTokens = str.split(",");
        List<Object> res = new ArrayList<>();

        for (String argToken : argTokens) {
            byte[] abiEncoded;
            ABIType currT;

            if (argToken.contains(":")) {
                String[] parts = argToken.split(":");
                if (parts.length != 2 || !parts[0].equals("ctxAppIdx"))
                    throw new IllegalArgumentException("cannot process argument " + argToken);
                long ctxAppId;
                try {
                    ctxAppId = Long.parseLong(parts[1]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("cannot parse appID " + argToken + ": " + e.getMessage());
                }
                if (ctxAppId >= rememberedAppIDs.size())
                    throw new IllegalArgumentException(
                            "application index out of bound: appID is " + ctxAppId + ", num of appIDs is " + rememberedAppIDs.size()
                    );

                currT = ABIType.valueOf("uint64");
                abiEncoded = currT.encode(rememberedAppIDs.get((int) ctxAppId));
            } else {
                currT = ABIType.valueOf(this.argTypes.get(methodArgIndex));
                abiEncoded = Encoder.decodeFromBase64(argToken);
            }

            res.add(currT.decode(abiEncoded));
            methodArgIndex++;
        }

        return res;
    }
}
