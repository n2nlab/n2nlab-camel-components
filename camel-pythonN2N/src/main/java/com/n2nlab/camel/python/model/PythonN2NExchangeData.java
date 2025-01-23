package com.n2nlab.camel.python.model;

import org.apache.camel.Exchange;
import java.util.Map;

/**
 * Data model class for passing exchange data to Python script.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public class PythonN2NExchangeData {
    private Object body;
    private Map<String, Object> headers;
    private Map<String, Object> properties;
    private String exchangeId;

    public PythonN2NExchangeData(Exchange exchange) {
        this.body = exchange.getMessage().getBody();
        this.headers = exchange.getMessage().getHeaders();
        this.properties = exchange.getProperties();
        this.exchangeId = exchange.getExchangeId();
    }

    // Default constructor for Jackson
    public PythonN2NExchangeData() {
    }

    // Getters and setters
    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }
}