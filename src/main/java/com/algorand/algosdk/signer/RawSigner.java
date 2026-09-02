package com.algorand.algosdk.signer;

/**
 * A low-level signing callback: signs exact bytes and returns the raw detached
 * signature. Implementations wrap whatever holds the key material (an in-memory
 * key, an HD wallet, a hardware device, a KMS, an external post-quantum
 * implementation, ...).
 * <p>
 * Every signer passes the callback the full domain-separated preimage — the
 * exact bytes the chain verifies the signature against — never a digest of it.
 */
@FunctionalInterface
public interface RawSigner {
    /**
     * Sign the given bytes.
     * @param bytesToSign the exact bytes to sign
     * @return the raw detached signature
     */
    byte[] sign(byte[] bytesToSign) throws Exception;
}
