package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

/**
 * BoxQueryEncoding provides convenience methods to String encode box names for use with Box search APIs (e.g. GetApplicationBoxByName).
 */
public final class BoxQueryEncoding {

    private static final String ENCODING_BASE64_PREFIX = "b64:";

    public static String encodeBytes(byte[] xs) {
        return ENCODING_BASE64_PREFIX + Encoder.encodeToBase64(xs);
    }

    public static String encodeBox(Box b) {
        return encodeBytes(b.name);
    }

    public static String encodeBoxReference(Transaction.BoxReference br) {
        return encodeBytes(br.getName());
    }

    public static String encodeAppBoxReference(AppBoxReference abr) {
        return encodeBytes(abr.getName());
    }
}
