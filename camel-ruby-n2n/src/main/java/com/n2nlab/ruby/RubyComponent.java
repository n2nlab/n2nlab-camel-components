package com.n2nlab.ruby;

import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Ruby component for Apache Camel.
 * Enables execution of Ruby scripts within Camel routes with full access to the Exchange.
 *
 * @author n2nlab
 * @version 1.0
 */
public class RubyComponent extends DefaultComponent {
    private static final Logger LOG = LoggerFactory.getLogger(RubyComponent.class);

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        if (remaining == null || remaining.trim().length() == 0) {
            throw new RuntimeCamelException("Ruby script name must be specified in the endpoint URI");
        }

        LOG.debug("Creating Ruby endpoint: {}", uri);

        // Create and configure the endpoint
        RubyEndpoint endpoint = new RubyEndpoint(uri, this);
        endpoint.setScriptName(remaining);

        try {
            setProperties(endpoint, parameters);
        } catch (Exception e) {
            throw new RuntimeCamelException("Failed to configure Ruby endpoint: " + e.getMessage(), e);
        }

        // Validate endpoint configuration
        endpoint.validateConfiguration();

        return endpoint;
    }
}