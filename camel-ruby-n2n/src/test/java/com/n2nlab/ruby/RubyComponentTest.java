package com.n2nlab.ruby;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RubyComponentTest extends CamelTestSupport {


    private static final String MULTILINE_SCRIPT = """
        # Get the input body and normalize whitespace
        input = $body.to_s.gsub(/\\s+/, ' ').strip
        original_length = input.length

        # Add processing metadata
        set_header('ProcessedBy', 'RubyScript')
        set_header('ProcessingTime', Time.now.to_s)

        # Transform the data
        transformed = input.upcase
        transformed_length = transformed.length

        # Add metadata
        set_exchange_property('originalLength', original_length)
        set_exchange_property('transformedLength', transformed_length)

        # Set status based on length
        set_header('Status', transformed_length > 100 ? 'LARGE_MESSAGE' : 'NORMAL')

        # Return transformed data
        transformed
        """;

    @Test
    public void testMultiLineScriptEncoded() throws Exception {
        Exchange exchange = template.request("direct:multiline-encoded", ex -> {
            ex.getMessage().setBody("test message");
        });

        // Check body transformation
        assertEquals("TEST MESSAGE", exchange.getMessage().getBody(String.class));

        // Check headers were set
        assertEquals("RubyScript", exchange.getMessage().getHeader("ProcessedBy"));
        assertTrue(exchange.getMessage().getHeaders().containsKey("ProcessingTime"));
        assertEquals("NORMAL", exchange.getMessage().getHeader("Status"));

        // Check properties were set
        assertEquals(12, exchange.getProperty("originalLength", Integer.class));
        assertEquals(12, exchange.getProperty("transformedLength", Integer.class));
    }

    @Test
    public void testSimpleRubyScript() throws Exception {
        String result = template.requestBody("direct:simple", "Hello", String.class);
        assertEquals("HELLO", result);
    }

    @Test
    public void testMultiLineScript() throws Exception {
        Exchange exchange = template.request("direct:multiline", ex -> {
            ex.getMessage().setBody("test message");
        });

        // Check body transformation
        assertEquals("TEST MESSAGE", exchange.getMessage().getBody(String.class));

        // Check headers were set
        assertEquals("RubyScript", exchange.getMessage().getHeader("ProcessedBy"));
        assertTrue(exchange.getMessage().getHeaders().containsKey("ProcessingTime"));
        assertEquals("NORMAL", exchange.getMessage().getHeader("Status"));

        // Check properties were set
        assertEquals(12L, exchange.getProperty("originalLength"));
        assertEquals(12L, exchange.getProperty("transformedLength"));
    }

    @Test
    public void testHeaderManipulation() throws Exception {
        Exchange exchange = template.request("direct:headers", ex -> {
            ex.getMessage().setBody("test");
            ex.getMessage().setHeader("InputHeader", "test-value");
        });

        assertEquals("test-value", exchange.getMessage().getHeader("OutputHeader"));
    }



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Simple string manipulation route
                from("direct:simple")
                        .to("ruby:simple?rubyScript=$body.to_s.upcase");

                String encodedScript = RubyScriptUtils.encodeScript(MULTILINE_SCRIPT);

                from("direct:multiline-encoded")
                        .to("ruby:transform?encodedScript=" + encodedScript);

                // Multi-line script from file
                from("direct:multiline")
                        .to("ruby:transform?scriptPath=src/test/resources/scripts/transform_multiline.rb");

                // Header manipulation using helper method
                from("direct:headers")
                        .to("ruby:headers?rubyScript=set_header('OutputHeader', get_header('InputHeader'))");

                // Property manipulation using helper method - fixed string concatenation
                from("direct:properties")
                        .to("ruby:properties?rubyScript=val = get_exchange_property('InputProperty'); set_exchange_property('OutputProperty', val + '-modified')");
            }
        };
    }
}