package com.n2nlab.camel.python.model;

import java.util.Map;

/**
 * Data model class for Python script execution results.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public class PythonN2NResult {
    private Object result;
    private Map<String, Object> debugInfo;

    // Default constructor for Jackson
    public PythonN2NResult() {
    }

    public PythonN2NResult(Object result, Map<String, Object> debugInfo) {
        this.result = result;
        this.debugInfo = debugInfo;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Map<String, Object> getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(Map<String, Object> debugInfo) {
        this.debugInfo = debugInfo;
    }
}