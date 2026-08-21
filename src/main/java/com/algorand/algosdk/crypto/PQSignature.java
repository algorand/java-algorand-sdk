package com.algorand.algosdk.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A serializable post-quantum signature, attached to a transaction or a delegated
 * logic signature. Mirrors the PQSig struct from go-algorand.
 * <p>
 * The SDK does not bundle a post-quantum signature implementation: signatures are
 * produced by a caller-supplied {@link com.algorand.algosdk.signer.RawSigner}
 * callback (see {@link com.algorand.algosdk.signer.Falcon1024AlgorandSigner}),
 * and consensus performs the cryptographic verification.
 */
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PQSignature implements Serializable {
    /**
     * Length in bytes of a post-quantum scheme identifier.
     */
    public static final int SCHEME_LEN_BYTES = 2;

    // Not exposed directly: a public static final byte[] is not actually
    // immutable, and every address derived afterwards would follow a mutation.
    private static final byte[] FALCON_1024_SCHEME = "f1".getBytes(StandardCharsets.UTF_8);

    /**
     * @return the 2-byte scheme identifier for Falcon-1024 ("f1")
     */
    public static byte[] falcon1024Scheme() {
        return Arrays.copyOf(FALCON_1024_SCHEME, FALCON_1024_SCHEME.length);
    }

    /**
     * The 2-byte identifier of the post-quantum signature scheme (e.g. "f1" for Falcon-1024).
     */
    @JsonProperty("sch")
    public byte[] scheme;

    /**
     * The 1-byte salt used when deriving the post-quantum account address from the
     * public key. Omitted from the encoding when zero.
     */
    @JsonProperty("slt")
    public int salt;

    /**
     * The post-quantum public key.
     */
    @JsonProperty("pk")
    public byte[] publicKey;

    /**
     * The post-quantum signature over the transaction or program.
     */
    @JsonProperty("sig")
    public byte[] signature;

    /**
     * @param scheme the 2-byte scheme identifier, or null for a blank signature
     * @param salt the salt of the signing account's address (0-255)
     * @param publicKey the post-quantum public key
     * @param signature the post-quantum signature
     * @throws IllegalArgumentException if a non-null scheme is not exactly 2 bytes,
     *         or the salt is outside 0-255
     */
    public PQSignature(
            @JsonProperty("sch") byte[] scheme,
            @JsonProperty("slt") int salt,
            @JsonProperty("pk") byte[] publicKey,
            @JsonProperty("sig") byte[] signature
    ) {
        // A blank signature decodes with every field absent, so only a present
        // scheme is length-checked.
        if (scheme != null && scheme.length != SCHEME_LEN_BYTES) {
            throw new IllegalArgumentException("post-quantum scheme must be " + SCHEME_LEN_BYTES +
                    " bytes, got " + scheme.length);
        }
        if (salt < 0 || salt > 0xff) {
            throw new IllegalArgumentException("post-quantum salt must be 0-255, got " + salt);
        }
        // Copy so that a caller's array -- or the shared scheme constant -- cannot
        // be mutated into this signature after the fact.
        this.scheme = copyOrNull(scheme);
        this.salt = salt;
        this.publicKey = copyOrNull(publicKey);
        this.signature = copyOrNull(signature);
    }

    /**
     * Build a signature straight from decoded wire fields, without validating
     * them.
     * <p>
     * Decoding deliberately does not reject a scheme or salt no address can be
     * derived from, so a malformed or future-scheme signature can still be
     * decoded and inspected; the check happens where an address is actually
     * derived, in {@link PQAddress}. py-algorand-sdk behaves the same way (see
     * its test_decoding_tolerates_an_underivable_pq_address); js-algorand-sdk
     * instead refuses such a blob outright.
     */
    @JsonCreator
    static PQSignature fromWireFields(
            @JsonProperty("sch") byte[] scheme,
            @JsonProperty("slt") int salt,
            @JsonProperty("pk") byte[] publicKey,
            @JsonProperty("sig") byte[] signature
    ) {
        PQSignature pqsig = new PQSignature();
        pqsig.scheme = copyOrNull(scheme);
        pqsig.salt = salt;
        pqsig.publicKey = copyOrNull(publicKey);
        pqsig.signature = copyOrNull(signature);
        return pqsig;
    }

    private static byte[] copyOrNull(byte[] bytes) {
        return bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Uninitialized object used for serializer to ignore default values.
     */
    public PQSignature() {
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PQSignature)) return false;
        PQSignature actual = (PQSignature) obj;
        return Arrays.equals(this.scheme, actual.scheme)
                && this.salt == actual.salt
                && Arrays.equals(this.publicKey, actual.publicKey)
                && Arrays.equals(this.signature, actual.signature);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(scheme);
        result = 31 * result + salt;
        result = 31 * result + Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
}
