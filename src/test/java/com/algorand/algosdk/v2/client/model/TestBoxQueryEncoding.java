package com.algorand.algosdk.v2.client.model;

import com.algorand.algosdk.transaction.AppBoxReference;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestBoxQueryEncoding {

    private static class Example {
        final String source;
        final String expectedEncoding;

        public Example(String source, String expectedEncoding) {
            this.source = source;
            this.expectedEncoding = "b64:" + expectedEncoding;
        }
    }

    private final Example e = new Example("tkÿÿ", "dGvDv8O/");

    @Test
    public void testEncodeBytes() {
        Assert.assertEquals(
                e.expectedEncoding,
                BoxQueryEncoding.encodeBytes(e.source.getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testEncodeBox() {
        Box b = new Box();
        b.name(Encoder.encodeToBase64(e.source.getBytes(StandardCharsets.UTF_8)));

        Assert.assertEquals(
                e.expectedEncoding,
                BoxQueryEncoding.encodeBox(b)
        );
    }

    @Test
    public void testEncodeBoxReference() {
        Transaction.BoxReference br = new Transaction.BoxReference(0, e.source.getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(
                e.expectedEncoding,
                BoxQueryEncoding.encodeBoxReference(br)
        );
    }

    @Test
    public void testEncodeAppBoxReference() {
        AppBoxReference abr = new AppBoxReference(0, e.source.getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(
                e.expectedEncoding,
                BoxQueryEncoding.encodeAppBoxReference(abr)
        );
    }
}
