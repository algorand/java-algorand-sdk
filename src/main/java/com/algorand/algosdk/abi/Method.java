package com.algorand.algosdk.abi;

import com.algorand.algosdk.algod.client.StringUtil;
import com.algorand.algosdk.util.CryptoProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Method {
    @JsonIgnore
    private static final String HASH_ALG = "SHA-512/256";

    @JsonProperty("name")
    public String name;
    @JsonProperty("desc")
    public String desc;
    @JsonProperty("args")
    public List<Arg> args;
    @JsonProperty("returns")
    public Returns returns;

    @JsonCreator
    public Method(
            @JsonProperty("name") String name,
            @JsonProperty("desc") String desc,
            @JsonProperty("args") List<Arg> args,
            @JsonProperty("returns") Returns returns
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.desc = desc;
        this.args = Objects.requireNonNull(args, "args must not be null");
        this.returns = returns;
    }

    // default values for serializer to ignore
    public Method() {
    }

    public Method(String method) {
        List<String> parsedMethod = Method.methodParse(method);
        this.name = parsedMethod.get(0);

        this.args = new ArrayList<>();
        String argTuple = parsedMethod.get(1);
        List<String> parsedMethodArgs = Type.parseTupleContent(argTuple.substring(1, argTuple.length() - 1));
        for (int i = 0; i < parsedMethodArgs.size(); i++)
            this.args.add(new Method.Arg("arg" + i, parsedMethodArgs.get(i), null));

        this.returns = new Returns(Type.Of(parsedMethod.get(2)).toString(), null);
    }

    private static List<String> methodParse(String method) {
        ArrayDeque<Integer> parenStack = new ArrayDeque<>();

        for (int i = 0; i < method.length(); i++) {
            if (method.charAt(i) == '(')
                parenStack.push(i);
            else if (method.charAt(i) == ')') {
                if (parenStack.isEmpty())
                    break;
                int leftParenIndex = parenStack.pop();
                List<String> res = new ArrayList<>();
                res.add(method.substring(0, leftParenIndex));
                res.add(method.substring(leftParenIndex, i + 1));
                res.add(method.substring(i + 1));
                return res;
            }
        }

        throw new IllegalArgumentException("method string parentheses unbalanced: " + method);
    }

    public String getSignature() {
        List<String> argStringList = new ArrayList<>();
        for (Arg value : this.args) argStringList.add(value.type);
        return this.name + "(" + StringUtil.join(argStringList.toArray(new String[0]), ",") + ")" + this.returns.type;
    }

    public byte[] getSelector() {
        try {
            CryptoProvider.setupIfNeeded();
            MessageDigest digest = MessageDigest.getInstance(Method.HASH_ALG);
            String method = this.getSignature();
            digest.update(method.getBytes(StandardCharsets.UTF_8));
            byte[] d = digest.digest();
            return Arrays.copyOfRange(d, 0, 4);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class Returns {
        @JsonProperty("type")
        public String type;
        @JsonProperty("desc")
        public String desc;

        @JsonCreator
        public Returns(
                @JsonProperty("type") String type,
                @JsonProperty("desc") String desc
        ) {
            this.type = Type.Of(Objects.requireNonNull(type, "type must not be null")).toString();
            this.desc = desc;
        }

        // default values for serializer to ignore
        public Returns() {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class Arg {
        @JsonProperty("name")
        public String name;
        @JsonProperty("type")
        public String type;
        @JsonProperty("desc")
        public String desc;

        @JsonCreator
        public Arg(
                @JsonProperty("name") String name,
                @JsonProperty("type") String type,
                @JsonProperty("desc") String desc
        ) {
            this.name = Objects.requireNonNull(name, "name must not be null");
            this.desc = desc;
            this.type = Type.Of(Objects.requireNonNull(type, "type must not be null")).toString();
        }

        // default values for serializer to ignore
        public Arg() {
        }
    }
}
