package com.algorand.algosdk.util;

import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public static byte[] readResource(String file) throws IOException {
        InputStream fis = (new ResourceUtils()).getClass().getClassLoader().getResourceAsStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        return data;
    }

    public static byte[] loadResource(String file) {
        try {
            return ResourceUtils.readResource(file);
        } catch (IOException e) {
            Assertions.fail("Unable to read file ('" + file + "') required by test: " + e.getMessage(), e);
            throw new RuntimeException("Unknown error.");
        }
    }

    public static TEALProgram loadTEALProgramFromFile(String file, AlgodClient aclv2) throws Exception {
        byte[] compiled;

        if (file.endsWith(".teal")) {
            if (aclv2 == null)
                throw new IllegalArgumentException("...");
            compiled = Encoder.decodeFromBase64(aclv2.TealCompile().source(loadResource(file)).execute().body().result);
        } else
            compiled = loadResource(file);

        return new TEALProgram(compiled);
    }
}
