package com.algorand.algosdk.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CryptoProvider {

    /**
     * Setup Bouncy Castle 1.61 as the default crypto provider. Required for Ed25519-related operations.
     */
    public static void setupIfNeeded() {
        // add bouncy castle provider for crypto
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
