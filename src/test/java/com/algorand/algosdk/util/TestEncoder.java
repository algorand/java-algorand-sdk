package com.algorand.algosdk.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEncoder {
    @Test
    public void Base32() {
        String txId = "3DV5VXT3QFRPVRTZTWP7PSGJN37BXJNXQWAFFW34OJWIZ2UOINQQ";
        byte[] decoded = Encoder.decodeFromBase32StripPad(txId);
        String reEncoded = Encoder.encodeToBase32StripPad(decoded);
        assertThat(reEncoded).isEqualTo(txId);
    }
}
