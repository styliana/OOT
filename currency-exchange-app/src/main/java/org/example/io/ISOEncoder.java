package org.example.io;

import java.nio.charset.StandardCharsets;

public class ISOEncoder implements Encoder {
    @Override
    public String encode(byte[] bytes) {
        // NBP pliki często w ISO-8859-2; jeśli potrzebujesz innego kodowania - zmień
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
