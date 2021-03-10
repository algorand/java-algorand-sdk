package com.algorand.algosdk.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEncoder {
    @ParameterizedTest
    @ValueSource(strings = {
            "3DV5VXT3QFRPVRTZTWP7PSGJN37BXJNXQWAFFW34OJWIZ2UOINQQ",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HURQ",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HU======",
            "B3R46T7Z3JYHSZUMDKVQQKBYLJKWLJ36FQ5OJ3FFSWFXNXJ5HU"
    })
    public void Base32EncodeDecode(String input) {
        byte[] decoded = Encoder.decodeFromBase32StripPad(input);
        String reEncoded = Encoder.encodeToBase32StripPad(decoded);
        assertThat(reEncoded).isEqualTo(StringUtils.stripEnd(input, "="));
    }
}
