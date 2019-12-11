package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Digester;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * MultisigAddress is a convenience class for handling multisignature public identities.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MultisigAddress implements Serializable {
    public final int version;
    public final int threshold;
    public final List<Ed25519PublicKey> publicKeys = new ArrayList<>();

    private static final byte[] PREFIX = "MultisigAddr".getBytes(StandardCharsets.UTF_8);

    public MultisigAddress(
            int version,
            int threshold,
            List<Ed25519PublicKey> publicKeys
    ) {
        this.version = version;
        this.threshold = threshold;
        this.publicKeys.addAll(publicKeys);

        if (this.version != 1) {
            throw new IllegalArgumentException("Unknown msig version");
        }

        if (
            this.threshold == 0 ||
            this.publicKeys.size() == 0 ||
            this.threshold > this.publicKeys.size()
        ) {
            throw new IllegalArgumentException("Invalid threshold");
        }
    }

    // building an address object helps us generate string representations
    public Address toAddress() throws NoSuchAlgorithmException {
        int numPkBytes = Ed25519PublicKey.KEY_LEN_BYTES * this.publicKeys.size();
        byte[] hashable = new byte[PREFIX.length + 2 + numPkBytes];
        System.arraycopy(PREFIX, 0, hashable, 0, PREFIX.length);
        hashable[PREFIX.length] = (byte)this.version;
        hashable[PREFIX.length+1] = (byte)this.threshold;
        for (int i = 0; i < this.publicKeys.size(); i++) {
            System.arraycopy(
                    this.publicKeys.get(i).getBytes(),
                    0,
                    hashable,
                    PREFIX.length+2+i*Ed25519PublicKey.KEY_LEN_BYTES,
                    Ed25519PublicKey.KEY_LEN_BYTES
            );
        }
        return new Address(Digester.digest(hashable));
    }

    @Override
    public String toString() {
        try {
            return this.toAddress().toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
