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
import org.apache.commons.codec.binary.Base64;

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


    private static boolean isAsciiPrintable(final byte symbol) {
        char symbolChar = (char) symbol;
        // linebreak existence check in program byte
        boolean isBreakLine = symbolChar == '\n';
        // printable ascii between range 32 (space) and 126 (tilde ~)
        boolean isStdPrintable = symbolChar >= ' ' && symbolChar <= '~';
        return isBreakLine || isStdPrintable;
    }

    private static boolean isAsciiPrintable(final byte[] program) {
        for (byte b : program) {
            if (!isAsciiPrintable(b))
                return false;
        }
        return true;
    }

    /**
     * Performs heuristic program validation:
     * check if passed in bytes are Algorand address or is B64 encoded, rather than Teal bytes
     * @param program
     */
    private static void sanityCheckProgram(final byte[] program) {
        if (program == null || program.length == 0)
            throw new IllegalArgumentException("empty program");
        // in any case, if a slice of "program-bytes" is full of ASCII printable,
        // then the slice of bytes can't be Teal program bytes.
        // need to check what possible kind of bytes are passed in.
        if (isAsciiPrintable(program)) {
            // maybe the bytes passed in are representing an Algorand address
            boolean isAddress = false;
            try {
                new Address(new String(program));
                isAddress = true;
            } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
                // if exception is IllegalArgException, it means bytes are not Algorand address
                if (e instanceof NoSuchAlgorithmException)
                    throw new IllegalArgumentException("cannot check if program bytes are Algorand address" + e);
            }
            if (isAddress)
                throw new IllegalArgumentException("requesting program bytes, get Algorand address");

            // or maybe these bytes are some B64 encoded bytes representation
            if (Base64.isBase64(program))
                throw new IllegalArgumentException("program should not be b64 encoded");

            // can't further analyze, but it is more than just B64 encoding at this point
            throw new IllegalArgumentException(
                    "program bytes are all ASCII printable characters, not looking like Teal byte code"
            );
        }
    }

    @JsonCreator
    public LogicsigSignature(
        @JsonProperty("l") byte[] logic,
        @JsonProperty("arg") List<byte[]> args,
        @JsonProperty("sig") byte[] sig,
        @JsonProperty("msig") MultisigSignature msig
    ) {
        this.logic = Objects.requireNonNull(logic, "program must not be null");
        this.args = args;

        sanityCheckProgram(this.logic);

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
     * NOTE: THIS RETURNS AN ESCROW ACCOUNT OF A LOGIC-SIG (FROM LOGIC ITSELF),
     *       IT WILL NOT RETURN THE DELEGATED ADDRESS OF THE LOGIC-SIG.
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
     * @param singleSigner only used in the case of a delegated LogicSig whose delegating account
     *                     is backed by a single private key
     * @return boolean
     */
    public boolean verify(Address singleSigner) throws NoSuchAlgorithmException {
        if (this.logic == null) {
            return false;
        }

        if (this.sig != null && this.msig != null) {
            return false;
        }

        sanityCheckProgram(this.logic);

        PublicKey pk;
        try {
            pk = singleSigner.toVerifyKey();
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

        if (this.msig != null)
            return this.msig.verify(this.bytesToSign());

        return true;
    }

    private static boolean nullCheck(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        else return o1 != null && o2 != null;
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
            return this.msig == null || this.msig.equals(actual.msig);
        } else {
            return false;
        }
    }
}
