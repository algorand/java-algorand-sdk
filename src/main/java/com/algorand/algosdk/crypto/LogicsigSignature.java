package com.algorand.algosdk.crypto;

import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.util.Digester;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Serializable logicsig class.
 * LogicsigSignature is constructed from a program and optional arguments.
 * Signature sig and MultisigSignature msig property are available for modification by it's clients.
 */
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class LogicsigSignature {
    @JsonIgnore
    private static final byte[] LOGIC_PREFIX = ("Program").getBytes(StandardCharsets.UTF_8);
    @JsonIgnore
    private static final String SIGN_ALGO = "EdDSA";

    @JsonProperty("l")
    public final byte[] logic;
    @JsonProperty("arg")
    public final List<byte[]> args;
    @JsonProperty("sig")
    public Signature sig;
    @JsonProperty("msig")
    public MultisigSignature msig;

    @JsonCreator
    public LogicsigSignature(
        @JsonProperty("l") byte[] logic,
        @JsonProperty("arg") List<byte[]> args,
        @JsonProperty("sig") byte[] sig,
        @JsonProperty("msig") MultisigSignature msig
    ) {
        this.logic = Objects.requireNonNull(logic, "program must not be null");
        this.args = args;
        boolean verified = false;
        try {
            verified = Logic.checkProgram(this.logic, this.args);
        } catch (IOException ex) {
            throw new IllegalArgumentException("invalid program", ex);
        }

        assert verified == true;

        if (sig != null) this.sig = new Signature(sig);
        this.msig = msig;
    }

    /**
     * Unsigned logicsig object.
     * @param logicsig
     */
    public LogicsigSignature(byte[] logicsig) {
        this(logicsig, null);
    }

    /**
     * Unsigned logicsig object, and its arguments.
     * @param logicsig
     * @param args
     */
    public LogicsigSignature(byte[] logicsig, List<byte[]> args) {
        this(logicsig, args, null, null);
    }

    /**
     * Uninitialized object used for serializer to ignore default values.
     */
    public LogicsigSignature() {
        this.logic = null;
        this.args = null;
    }

    /**
     * Calculate escrow address from logic sig program
     * @return Address
     * @throws NoSuchAlgorithmException
     */
    public Address toAddress() throws NoSuchAlgorithmException {
        byte[] prefixedEncoded = this.bytesToSign();
        return new Address(Digester.digest(prefixedEncoded));
    }

    /**
     * Return prefixed program as byte array that is ready to sign
     * @return byte[]
     */
    public byte[] bytesToSign() {
        byte[] prefixedEncoded = new byte[this.logic.length + LOGIC_PREFIX.length];
        System.arraycopy(LOGIC_PREFIX, 0, prefixedEncoded, 0, LOGIC_PREFIX.length);
        System.arraycopy(this.logic, 0, prefixedEncoded, LOGIC_PREFIX.length, this.logic.length);
        return prefixedEncoded;
    }

    /**
     * Perform signature verification against the sender address
     * @param address Address to verify
     * @return boolean
     */
    public boolean verify(Address address) {
        if (this.logic == null) {
            return false;
        }

        if (this.sig != null && this.msig != null) {
            return false;
        }

        try {
            Logic.checkProgram(this.logic, this.args);
        } catch (Exception ex) {
            return false;
        }

        if (this.sig == null && this.msig == null) {
            try {
                return address.equals(this.toAddress());
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }

        PublicKey pk;
        try {
            pk = address.toVerifyKey();
        } catch (Exception ex) {
            return false;
        }

        if (this.sig != null) {
            try {
                java.security.Signature sig = java.security.Signature.getInstance(SIGN_ALGO);
                sig.initVerify(pk);
                sig.update(this.bytesToSign());
                return sig.verify(this.sig.getBytes());
            } catch (Exception ex) {
                return false;
            }
        }

        return this.msig.verify(this.bytesToSign());
    }

    private static boolean nullCheck(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null && o2 != null) return false;
        if (o1 != null && o2 == null) return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LogicsigSignature) {
            LogicsigSignature actual = (LogicsigSignature) obj;

            if (!LogicsigSignature.nullCheck(this.logic, actual.logic)) return false;
            if (!Arrays.equals(this.logic, actual.logic)) return false;

            if (!LogicsigSignature.nullCheck(this.args, actual.args)) return false;
            if (this.args != null) {
                if (this.args.size() != actual.args.size()) return false;
                for (int i = 0; i < this.args.size(); i++) {
                    if (!Arrays.equals(this.args.get(i), actual.args.get(i))) return false;
                }
            }

            if (!LogicsigSignature.nullCheck(this.sig, actual.sig)) return false;
            if (this.sig != null && !this.sig.equals(actual.sig)) return false;

            if (!LogicsigSignature.nullCheck(this.msig, actual.msig)) return false;
            if (this.msig != null && !this.msig.equals(actual.msig)) return false;

            return true;
        } else {
            return false;
        }
    }
}
