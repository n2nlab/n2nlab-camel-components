package com.n2nlab.ruby;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class RubyScriptUtils {

    /**
     * Encodes a Ruby script to Base64 and URL-encodes it for use in URIs
     *
     * @param script The Ruby script to encode
     * @return URL-safe Base64 encoded script
     * @throws RuntimeException if encoding fails
     */
    public static String encodeScript(String script) {
        try {
            // First Base64 encode
            String base64 = Base64.getEncoder().encodeToString(
                    script.getBytes(StandardCharsets.UTF_8));

            // Then URL encode
            return URLEncoder.encode(base64, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode script: " + e.getMessage(), e);
        }
    }

    /**
     * Decodes a URL-encoded Base64 Ruby script
     *
     * @param encoded The URL-encoded Base64 script
     * @return The decoded Ruby script
     * @throws RuntimeException if decoding fails
     */
    public static String decodeScript(String encoded) {
        try {
            // First URL decode
            String urlDecoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());

            // Remove any whitespace
            String cleaned = urlDecoded.trim().replaceAll("\\s+", "");

            // Add padding if necessary
            int padding = cleaned.length() % 4;
            if (padding > 0) {
                cleaned = cleaned + "=".repeat(4 - padding);
            }

            // Then Base64 decode
            byte[] decoded = Base64.getDecoder().decode(cleaned);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to decode script: " + e.getMessage(), e);
        }
    }
}