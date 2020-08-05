package com.algorand.algosdk.util;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public static byte[] readResource(String file) throws IOException {
        InputStream fis = (new ResourceUtils()).getClass().getClassLoader().getResourceAsStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        return data;
    }
}
