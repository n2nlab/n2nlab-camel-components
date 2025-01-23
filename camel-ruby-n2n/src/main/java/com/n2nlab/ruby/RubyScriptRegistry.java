package com.n2nlab.ruby;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Registry for storing and managing Ruby scripts
 */
public class RubyScriptRegistry {
    private static final Map<String, String> scripts = new ConcurrentHashMap<>();

    /**
     * Register a script with a unique identifier
     *
     * @param scriptId Unique identifier for the script
     * @param script The Ruby script content
     * @return The script ID for reference
     */
    public static String registerScript(String scriptId, String script) {
        scripts.put(scriptId, script);
        return scriptId;
    }

    /**
     * Retrieve a script by its ID
     *
     * @param scriptId The script identifier
     * @return The script content or null if not found
     */
    public static String getScript(String scriptId) {
        return scripts.get(scriptId);
    }

    /**
     * Clear all registered scripts
     */
    public static void clearScripts() {
        scripts.clear();
    }

    /**
     * Remove a specific script
     *
     * @param scriptId The script identifier to remove
     */
    public static void removeScript(String scriptId) {
        scripts.remove(scriptId);
    }
}