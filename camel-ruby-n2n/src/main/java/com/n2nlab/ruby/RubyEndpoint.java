package com.n2nlab.ruby;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Ruby endpoint for executing Ruby scripts in Camel routes.
 * Supports both inline scripts and external script files.
 */
@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "ruby",
        title = "Ruby Script",
        syntax = "ruby:scriptName",
        category = {Category.TRANSFORMATION, Category.SCRIPT}
)
public class RubyEndpoint extends DefaultEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(RubyEndpoint.class);

    @UriPath
    @Metadata(required = true)
    private String scriptName;

    @UriParam(description = "Inline Ruby script content")
    private String rubyScript;

    @UriParam(description = "Base64 encoded Ruby script content")
    private String encodedScript;

    @UriParam(description = "Path to external Ruby script file")
    private String scriptPath;

    @UriParam(defaultValue = "false",
            description = "Whether to cache the compiled script (improves performance for repeated executions)")
    private boolean cacheScript = false;

    @UriParam(defaultValue = "true",
            description = "Whether to convert null results to empty strings")
    private boolean allowNullBody = true;

    @UriParam(defaultValue = "UTF-8",
            description = "Character encoding when reading script files")
    private String encoding = "UTF-8";

    public RubyEndpoint(String uri, RubyComponent component) {
        super(uri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new RubyProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new RuntimeCamelException("Ruby endpoint doesn't support consumers");
    }

    /**
     * Gets the effective Ruby script by checking encoded, inline, or file-based scripts
     */
    public String getEffectiveScript() {
        if (encodedScript != null) {
            try {
                return new String(Base64.getDecoder().decode(encodedScript), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                throw new RuntimeCamelException("Failed to decode Base64 script", e);
            }
        }
        return rubyScript;
    }

    /**
     * Validates the endpoint configuration before use.
     */
    public void validateConfiguration() {
        int scriptSources = 0;
        if (rubyScript != null) scriptSources++;
        if (encodedScript != null) scriptSources++;
        if (scriptPath != null) scriptSources++;

        if (scriptSources == 0) {
            throw new RuntimeCamelException(
                    "One of 'rubyScript', 'encodedScript', or 'scriptPath' must be specified on " + getEndpointUri());
        }
        if (scriptSources > 1) {
            throw new RuntimeCamelException(
                    "Only one of 'rubyScript', 'encodedScript', or 'scriptPath' can be specified on " + getEndpointUri());
        }
    }

    // Getters and Setters
    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getRubyScript() {
        return rubyScript;
    }

    public void setRubyScript(String rubyScript) {
        this.rubyScript = rubyScript;
    }

    public String getEncodedScript() {
        return encodedScript;
    }

    public void setEncodedScript(String encodedScript) {
        this.encodedScript = encodedScript;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public boolean isCacheScript() {
        return cacheScript;
    }

    public void setCacheScript(boolean cacheScript) {
        this.cacheScript = cacheScript;
    }

    public boolean isAllowNullBody() {
        return allowNullBody;
    }

    public void setAllowNullBody(boolean allowNullBody) {
        this.allowNullBody = allowNullBody;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}