package com.algorand.algosdk.abi;

import com.algorand.algosdk.algod.client.StringUtil;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.CryptoProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Method {
    @JsonIgnore
    private static final Set<String> TxArgTypes = new HashSet<>(
            Arrays.asList(
                    Transaction.Type.Payment.toValue(),
                    Transaction.Type.KeyRegistration.toValue(),
                    Transaction.Type.AssetConfig.toValue(),
                    Transaction.Type.AssetTransfer.toValue(),
                    Transaction.Type.AssetFreeze.toValue(),
                    Transaction.Type.ApplicationCall.toValue()
            )
    );

    @JsonIgnore
    private static final String HASH_ALG = "SHA-512/256";

    @JsonIgnore
    private static final Pattern validName = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

    @JsonProperty("name")
    public String name;
    @JsonProperty("desc")
    public String desc;
    @JsonProperty("args")
    public List<Arg> args = new ArrayList<>();
    @JsonProperty("returns")
    public Returns returns = new Returns("void", null);
    @JsonIgnore
    private int txnCallCount = 1;

    @JsonCreator
    public Method(
            @JsonProperty("name") String name,
            @JsonProperty("desc") String desc,
            @JsonProperty("args") List<Arg> args,
            @JsonProperty("returns") Returns returns
    ) {
        this.name = Method.nameChecker(name);
        this.desc = desc;
        if (args != null) this.args = args;
        if (returns != null) this.returns = returns;
        for (Arg arg : this.args)
            if (TxArgTypes.contains(arg.type))
                this.txnCallCount++;
    }

    private static String nameChecker(String name) {
        String nameStr = Objects.requireNonNull(name, "name must not be null");
        if (nameStr.isEmpty() || !Method.validName.matcher(nameStr).matches())
            throw new IllegalArgumentException("name must not be an empty string");
        return nameStr;
    }

    public Method(String method) {
        List<String> parsedMethod = Method.methodParse(method);
        this.name = Method.nameChecker(parsedMethod.get(0));

        this.args = new ArrayList<>();
        String argTuple = parsedMethod.get(1);
        List<String> parsedMethodArgs = Type.parseTupleContent(argTuple.substring(1, argTuple.length() - 1));
        for (int i = 0; i < parsedMethodArgs.size(); i++)
            this.args.add(new Method.Arg("arg" + i, parsedMethodArgs.get(i), null));

        this.returns = new Returns(parsedMethod.get(2), null);
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
                if (parenStack.size() > 0)
                    continue;
                List<String> res = new ArrayList<>();
                res.add(method.substring(0, leftParenIndex));
                res.add(method.substring(leftParenIndex, i + 1));
                res.add(method.substring(i + 1));
                return res;
            }
        }

        throw new IllegalArgumentException("method string parentheses unbalanced: " + method);
    }

    @JsonIgnore
    public String getSignature() {
        List<String> argStringList = new ArrayList<>();
        for (Arg value : this.args) argStringList.add(value.type);
        return this.name + "(" + StringUtil.join(argStringList.toArray(new String[0]), ",") + ")" + this.returns.type;
    }

    @JsonIgnore
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

    @JsonIgnore
    public int getTxnCallCount() {
        return this.txnCallCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(name, method.name) && Objects.equals(desc, method.desc) &&
                Objects.equals(args, method.args) && Objects.equals(returns, method.returns);
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
            this.type = type.equals("void") ?
                    type : Type.Of(Objects.requireNonNull(type, "type must not be null")).toString();
            this.desc = desc;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Returns returns = (Returns) o;
            return Objects.equals(type, returns.type) && Objects.equals(desc, returns.desc);
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
            this.name = Method.nameChecker(name);
            this.desc = desc;
            this.type = Type.Of(Objects.requireNonNull(type, "type must not be null")).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Arg arg = (Arg) o;
            return Objects.equals(name, arg.name) && Objects.equals(type, arg.type) && Objects.equals(desc, arg.desc);
        }
    }
}
