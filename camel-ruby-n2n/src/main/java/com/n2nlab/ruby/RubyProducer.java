package com.n2nlab.ruby;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.apache.camel.support.ExchangeHelper;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;


public class RubyProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(RubyProducer.class);
    private final RubyEndpoint endpoint;
    private ScriptingContainer container;

    private static final String HELPER_FUNCTIONS = """
        def set_body(val)
          $exchange.getMessage().setBody(val)
        end
        
        def set_header(name, val)
          $exchange.getMessage().setHeader(name, val)
        end
        
        def get_header(name)
          $exchange.getMessage().getHeader(name)
        end
        
        def set_exchange_property(name, val)
          $exchange.setProperty(name, val)
        end
        
        def get_exchange_property(name)
          $exchange.getProperty(name)
        end
        """;

    public RubyProducer(RubyEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        container = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.PERSISTENT);
    }

    @Override
    protected void doStop() throws Exception {
        if (container != null) {
            container.terminate();
            container = null;
        }
        super.doStop();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String script;

        // Load script based on configuration
        if (endpoint.getScriptPath() != null) {
            // Load from file
            File scriptFile = new File(endpoint.getScriptPath());
            script = new String(Files.readAllBytes(scriptFile.toPath()), endpoint.getEncoding());
        } else {
            // Get from inline or encoded script
            script = endpoint.getEffectiveScript();
        }

        try {
            // Set up global variables
            container.put("$exchange", exchange);
            container.put("$message", exchange.getMessage());
            container.put("$body", exchange.getMessage().getBody());
            container.put("$headers", exchange.getMessage().getHeaders());
            container.put("$properties", exchange.getProperties());

            // Initialize helper functions
            container.runScriptlet(HELPER_FUNCTIONS);

            // Execute user script
            Object result = container.runScriptlet(script);

            // Update exchange if result is not null
            if (result != null) {
                exchange.getMessage().setBody(result);
            }

        } catch (Exception e) {
            LOG.error("Error executing Ruby script: " + e.getMessage(), e);
            throw e;
        } finally {
            container.clear();
        }
    }
}