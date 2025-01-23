package com.n2nlab.ruby;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.RuntimeCamelException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.io.TempDir;

public class RubyComponentTest extends CamelTestSupport {

    private ObjectMapper objectMapper;
    @TempDir
    File tempDir;

    private static final String AGE_PROCESSOR_ID = "ageProcessor";
    private static final String MULTILINE_TRANSFORM_ID = "multilineTransform";

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

    String rubyScript = """
                require 'json'
                require 'time'
                
                # Get input data
                user_data = {
                  'age' => $body,
                  'timestamp' => Time.now.strftime('%Y-%m-%d %H:%M:%S')
                }
                
                # Calculate category
                categories = {
                  (0..12) => 'Child',
                  (13..19) => 'Teenager',
                  (20..29) => 'Young Adult',
                  (30..49) => 'Adult',
                  (50..200) => 'Senior'
                }
                category = categories.find { |range, _| range.include?(user_data['age']) }&.last || 'Unknown'
                
                # Generate message
                special_messages = {
                  'Child' => "🧒 Special activities for children under 13! Age: #{user_data['age']}",
                  'Teenager' => "👦 Teen program available! Age: #{user_data['age']}",
                  'Young Adult' => "👨 Career guidance available! Age: #{user_data['age']}",
                  'Adult' => "👨 Professional network events! Age: #{user_data['age']}",
                  'Senior' => "🧓 Senior wellness program! Age: #{user_data['age']}"
                }
                message = special_messages.fetch(category, "Welcome! Age: #{user_data['age']}")
                
                # Build programs list
                programs = []
                if user_data['age'] >= 0  # Only add programs for valid ages
                  programs << 'Sports' if user_data['age'] < 30
                  programs << 'Health' if user_data['age'] > 40
                  programs << 'Education' if user_data['age'] < 25
                  programs << 'Investment' if user_data['age'] >= 25 && user_data['age'] <= 60
                end
                
                # Create response
                result = {
                  'message' => message,
                  'details' => {
                    'age' => user_data['age'],
                    'category' => category,
                    'timestamp' => user_data['timestamp'],
                    'eligibleForDiscount' => user_data['age'] >= 0 && (user_data['age'] < 13 || user_data['age'] > 60),
                    'recommendedPrograms' => programs
                  }
                }.to_json
                result
                """;

    @BeforeEach
    public void initializeTest() {
        objectMapper = new ObjectMapper();
        RubyScriptRegistry.registerScript(AGE_PROCESSOR_ID, rubyScript);
        RubyScriptRegistry.registerScript(MULTILINE_TRANSFORM_ID, MULTILINE_SCRIPT);
    }

    @AfterEach
    public void cleanup() {
        RubyScriptRegistry.clearScripts();
    }

    @Test
    public void testNegativeAge() throws Exception {
        String jsonResult = template.requestBody("direct:rubyScript", -5, String.class);
        JsonNode result = objectMapper.readTree(jsonResult);

        JsonNode details = result.get("details");
        assertEquals("Unknown", details.get("category").asText());
        assertFalse(details.get("eligibleForDiscount").asBoolean());
        assertTrue(details.get("recommendedPrograms").isEmpty());
    }

    @Test
    public void testZeroAge() throws Exception {
        String jsonResult = template.requestBody("direct:rubyScript", 0, String.class);
        JsonNode result = objectMapper.readTree(jsonResult);

        JsonNode details = result.get("details");
        assertEquals("Child", details.get("category").asText());
        assertTrue(details.get("eligibleForDiscount").asBoolean());
    }

    @Test
    public void testNonNumericAge() {
        assertThrows(RuntimeCamelException.class, () -> {
            template.requestBody("direct:rubyScript", "not-a-number", String.class);
        });
    }

    // Script Loading Tests
    @Test
    public void testScriptFromFile() throws Exception {
        // Create a temporary script file
        File scriptFile = new File(tempDir, "test-script.rb");
        Files.write(scriptFile.toPath(), "set_body($body.to_s.reverse)".getBytes(StandardCharsets.UTF_8));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:file-script")
                        .to("ruby:reverse?scriptPath=" + scriptFile.getAbsolutePath());
            }
        });

        String result = template.requestBody("direct:file-script", "hello", String.class);
        assertEquals("olleh", result);
    }

    @Test
    public void testNonExistentScriptFile() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:missing-script")
                        .to("ruby:missing?scriptPath=/non/existent/path.rb");
            }
        });

        assertThrows(RuntimeCamelException.class, () -> {
            template.requestBody("direct:missing-script", "test", String.class);
        });
    }

    // Helper Methods Tests
    @Test
    public void testSetAndGetExchangeProperties() throws Exception {
        String script = """
            set_exchange_property('testKey', 'testValue')
            get_exchange_property('testKey')
            """;
        RubyScriptRegistry.registerScript("properties-test", script);

        Exchange exchange = template.request("direct:properties-test", null);
        assertEquals("testValue", exchange.getProperty("testKey"));
    }

    @Test
    public void testHeaderManipulation() throws Exception {
        String script = """
            set_header('TestHeader', 'HeaderValue')
            modified = get_header('TestHeader').downcase
            set_header('ModifiedHeader', modified)
            """;
        RubyScriptRegistry.registerScript("headers-test", script);

        Exchange exchange = template.request("direct:headers-test", null);
        assertEquals("HeaderValue", exchange.getMessage().getHeader("TestHeader"));
        assertEquals("headervalue", exchange.getMessage().getHeader("ModifiedHeader"));
    }

    // Boundary Tests
    @Test
    public void testAgeAtCategoryBoundaries() throws Exception {
        // Test boundary between Child and Teenager (age 12-13)
        assertCategory(12, "Child");
        assertCategory(13, "Teenager");

        // Test boundary between Teenager and Young Adult (age 19-20)
        assertCategory(19, "Teenager");
        assertCategory(20, "Young Adult");

        // Test boundary between Young Adult and Adult (age 29-30)
        assertCategory(29, "Young Adult");
        assertCategory(30, "Adult");

        // Test boundary between Adult and Senior (age 49-50)
        assertCategory(49, "Adult");
        assertCategory(50, "Senior");
    }

    private void assertCategory(int age, String expectedCategory) throws Exception {
        String jsonResult = template.requestBody("direct:rubyScript", age, String.class);
        JsonNode result = objectMapper.readTree(jsonResult);
        assertEquals(expectedCategory, result.get("details").get("category").asText());
    }

    // Performance Test
    @Test
    public void testMultipleScriptExecutions() throws Exception {
        long startTime = System.currentTimeMillis();
        for (int age = 0; age <= 100; age++) {
            String jsonResult = template.requestBody("direct:rubyScript", age, String.class);
            assertNotNull(jsonResult);
            JsonNode result = objectMapper.readTree(jsonResult);
            assertTrue(result.has("details"));
        }
        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 30000);
    }

    // Script Registry Tests
    @Test
    public void testScriptRegistryOperations() {
        String scriptId = "test-script";
        String scriptContent = "puts 'test'";

        // Test registration
        RubyScriptRegistry.registerScript(scriptId, scriptContent);
        assertEquals(scriptContent, RubyScriptRegistry.getScript(scriptId));

        // Test removal
        RubyScriptRegistry.removeScript(scriptId);
        assertNull(RubyScriptRegistry.getScript(scriptId));

        // Test clear
        RubyScriptRegistry.registerScript(scriptId, scriptContent);
        RubyScriptRegistry.clearScripts();
        assertNull(RubyScriptRegistry.getScript(scriptId));
    }

    @Test
    public void testInvalidScriptId() {
        assertThrows(RuntimeCamelException.class, () -> {
            template.requestBody("direct:invalid-script", "test", String.class);
        });
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:simple")
                        .to("ruby:simple?rubyScript=$body.to_s.upcase");

                // Using script registry for complex scripts
                from("direct:multiline-encoded")
                        .to("ruby:transform?scriptId=" + MULTILINE_TRANSFORM_ID);

                from("direct:rubyScript")
                        .to("ruby:processor?scriptId=" + AGE_PROCESSOR_ID);

                // Header manipulation using helper method
                from("direct:headers")
                        .to("ruby:headers?rubyScript=set_header('OutputHeader', get_header('InputHeader'))");


                // New test routes
                from("direct:properties-test")
                        .to("ruby:properties?scriptId=properties-test");

                from("direct:headers-test")
                        .to("ruby:headers?scriptId=headers-test");

                from("direct:invalid-script")
                        .to("ruby:invalid?scriptId=non-existent-script");
            }
        };
    }
}