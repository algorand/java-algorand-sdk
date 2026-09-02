package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Digester;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * A post-quantum (PQ) account address, derived from a post-quantum public key
 * together with the canonical salt used to derive it.
 * <p>
 * The address is SHA-512/256("PQA" + scheme + salt + publicKey), where the
 * canonical salt is the lowest byte value (0-255) whose resulting 32-byte digest
 * does not decode to an ed25519 curve point. The default derivation searches for
 * that canonical salt; {@link #derive(byte[], int, byte[])} derives an address
 * for an explicit, possibly non-canonical salt.
 */
public final class PQAddress implements Serializable {
    private static final byte[] PQ_ADDRESS_PREFIX = "PQA".getBytes(StandardCharsets.UTF_8);

    // ed25519 field parameters: p = 2^255 - 19, d = -121665/121666 mod p
    private static final BigInteger ED25519_P = BigInteger.valueOf(2).pow(255).subtract(BigInteger.valueOf(19));
    private static final BigInteger ED25519_D =
            BigInteger.valueOf(-121665).multiply(BigInteger.valueOf(121666).modInverse(ED25519_P)).mod(ED25519_P);
    private static final BigInteger SQRT_EXP = ED25519_P.subtract(BigInteger.valueOf(5)).shiftRight(3); // (p - 5) / 8

    private final Address address;
    private final int salt;

    private PQAddress(Address address, int salt) {
        this.address = address;
        this.salt = salt;
    }

    /**
     * Derive a post-quantum account address and its canonical salt.
     * @param scheme 2-byte scheme identifier (e.g. {@link PQSignature#falcon1024Scheme()})
     * @param publicKey the scheme's public key
     * @return the derived address with its canonical salt
     * @throws IllegalArgumentException if the scheme is not exactly 2 bytes
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public static PQAddress derive(byte[] scheme, byte[] publicKey) throws NoSuchAlgorithmException {
        checkScheme(scheme);
        Objects.requireNonNull(publicKey, "publicKey must not be null");

        // Rejection-sample the lowest salt that yields an off-curve (non-ed25519)
        // address. The probability of exhausting the range is ~2^-256.
        for (int salt = 0; salt <= 0xff; salt++) {
            byte[] candidate = digest(scheme, salt, publicKey);
            if (!isEd25519Point(candidate)) {
                return new PQAddress(new Address(candidate), salt);
            }
        }
        throw new IllegalStateException("no canonical salt exists for this public key and scheme");
    }

    /**
     * Derive the post-quantum account address for an explicit scheme, salt and
     * public key: SHA-512/256("PQA" + scheme + salt + publicKey).
     * <p>
     * Unlike {@link #derive(byte[], byte[])} this does not search for the
     * canonical salt: it uses exactly the salt given, which is what consensus
     * does when it resolves the authorizer of a post-quantum signature. A public
     * key therefore has one address per off-curve salt, and the SDK can address
     * any of them, not only the canonical one.
     * @param scheme 2-byte scheme identifier (e.g. {@link PQSignature#falcon1024Scheme()})
     * @param salt the salt to derive with (0-255)
     * @param publicKey the scheme's public key
     * @return the derived address
     * @throws IllegalArgumentException if the scheme is not exactly 2 bytes or the salt is out of range
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public static Address derive(byte[] scheme, int salt, byte[] publicKey) throws NoSuchAlgorithmException {
        checkScheme(scheme);
        checkSalt(salt);
        Objects.requireNonNull(publicKey, "publicKey must not be null");
        return new Address(digest(scheme, salt, publicKey));
    }

    /**
     * Derive the account address that a post-quantum signature authorizes.
     * <p>
     * Unlike an ed25519 signature, a post-quantum signature carries the scheme,
     * salt and public key of the signing account, so the address it authorizes is
     * fully determined by the signature and does not have to be supplied out of
     * band. The salt carried by the signature is used as-is -- the network
     * resolves the authorizer the same way -- so a signature made for a
     * non-canonical (but still off-curve) salt resolves to its own address rather
     * than being rejected.
     * @param pqsig the post-quantum signature to derive the address from
     * @return the address of the account the signature authorizes
     * @throws IllegalArgumentException if the signature's scheme is not exactly 2 bytes
     *         or its salt is out of range
     * @throws NoSuchAlgorithmException if SHA-512/256 is unavailable
     */
    public static Address fromSignature(PQSignature pqsig) throws NoSuchAlgorithmException {
        Objects.requireNonNull(pqsig, "pqsig must not be null");
        return derive(pqsig.scheme, pqsig.salt, pqsig.publicKey);
    }

    private static void checkScheme(byte[] scheme) {
        if (scheme == null || scheme.length != PQSignature.SCHEME_LEN_BYTES) {
            throw new IllegalArgumentException("post-quantum scheme must be " + PQSignature.SCHEME_LEN_BYTES +
                    " bytes, got " + (scheme == null ? "null" : scheme.length));
        }
    }

    private static void checkSalt(int salt) {
        if (salt < 0 || salt > 0xff) {
            throw new IllegalArgumentException("post-quantum salt must be 0-255, got " + salt);
        }
    }

    private static byte[] digest(byte[] scheme, int salt, byte[] publicKey) throws NoSuchAlgorithmException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            buf.write(PQ_ADDRESS_PREFIX);
            buf.write(scheme);
            buf.write(salt);
            buf.write(publicKey);
        } catch (IOException e) {
            throw new RuntimeException("unexpected behavior", e);
        }
        return Digester.digest(buf.toByteArray());
    }

    /**
     * Check whether 32 bytes decode to any point on the ed25519 curve.
     * <p>
     * This is the "broad" predicate used for post-quantum address derivation: it
     * returns true if the value could be interpreted as a curve point by any
     * ed25519 implementation, including small-order points, non-canonical
     * encodings, and points outside the prime-order subgroup. It deliberately
     * recognizes more encodings as curve points than libsodium's
     * crypto_core_ed25519_is_valid_point, so address derivation rejects more
     * candidate salts (matching go-algorand's crypto.IsEdwards25519Point).
     * @param encoded 32-byte value to test
     * @return true if the value decodes to an ed25519 point
     */
    public static boolean isEd25519Point(byte[] encoded) {
        if (encoded == null || encoded.length != Address.LEN_BYTES) {
            return false;
        }
        BigInteger p = ED25519_P;
        // the low 255 bits are y, interpreted little-endian; the top bit encodes
        // the sign of x, ignored here
        byte[] bigEndian = new byte[encoded.length];
        for (int i = 0; i < encoded.length; i++) {
            bigEndian[i] = encoded[encoded.length - 1 - i];
        }
        bigEndian[0] &= 0x7f;
        BigInteger y = new BigInteger(1, bigEndian).mod(p);
        BigInteger u = y.multiply(y).subtract(BigInteger.ONE).mod(p);
        BigInteger v = ED25519_D.multiply(y).multiply(y).add(BigInteger.ONE).mod(p);
        // a point exists iff x^2 = u / v has a solution (u / v is a square)
        BigInteger x = u.multiply(v.pow(3)).mod(p)
                .multiply(u.multiply(v.pow(7)).mod(p).modPow(SQRT_EXP, p)).mod(p);
        BigInteger vxx = v.multiply(x).multiply(x).mod(p);
        return vxx.equals(u) || vxx.equals(u.negate().mod(p));
    }

    /**
     * @return the derived address
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * @return the canonical salt used to derive the address (0-255)
     */
    public int getSalt() {
        return this.salt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PQAddress)) return false;
        PQAddress actual = (PQAddress) obj;
        return this.salt == actual.salt && this.address.equals(actual.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address.toString(), salt);
    }

    @Override
    public String toString() {
        return this.address.toString();
    }
}
