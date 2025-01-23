package com.n2nlab.ruby;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RubyScriptUtils {

    /**
     * Encodes a Ruby script to Base64 for use in endpoint URIs
     *
     * @param script The Ruby script to encode
     * @return Base64 encoded script
     */
    public static String encodeScript(String script) {
        return Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a Base64 encoded Ruby script
     *
     * @param encoded The Base64 encoded script
     * @return The decoded Ruby script
     */
    public static String decodeScript(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }
}