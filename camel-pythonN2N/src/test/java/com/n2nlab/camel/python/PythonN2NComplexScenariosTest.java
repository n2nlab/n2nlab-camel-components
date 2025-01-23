package com.n2nlab.camel.python;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for complex Python script scenarios.
 */
public class PythonN2NComplexScenariosTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                context.addComponent("pythonN2N", new PythonN2NComponent());

                // Complex data structures route
                from("direct:complex")
                        .toF("pythonN2N:test?pythonScript=%s",
                                URLEncoder.encode("""
                            # Process the complex input
                            numbers = body['numbers']
                            text = body['text']
                            nested = body['nested']
                            
                            # Create complex output
                            result = {
                                'sum': sum(numbers),
                                'modified_text': text.upper(),
                                'nested_value': nested['key'].capitalize()
                            }
                            """, StandardCharsets.UTF_8));

                // Headers and properties route
                from("direct:headers")
                        .toF("pythonN2N:test?pythonScript=%s",
                                URLEncoder.encode("""
                            # Access exchange data
                            header_value = headers.get('test_header')
                            property_value = properties.get('test_property')
                            
                            # Create result using exchange data
                            result = {
                                'header': header_value,
                                'property': property_value,
                                'combined': str(header_value) + str(property_value)
                            }
                            """, StandardCharsets.UTF_8));

                // Script templates route
                from("direct:templates")
                        .toF("pythonN2N:test?pythonScript=%s&scriptTemplate=%s",
                                URLEncoder.encode("result = process_data(body)", StandardCharsets.UTF_8),
                                URLEncoder.encode("""
                            def process_data(input_data):
                                return input_data * 3
                            """, StandardCharsets.UTF_8));

                // Debug info route
                from("direct:debug")
                        .toF("pythonN2N:test?pythonScript=%s&debug=true&returnFullOutput=true",
                                URLEncoder.encode("""
                            import time
                            
                            # Add some runtime info
                            runtime_info['start_time'] = time.time()
                            
                            # Process data
                            result = body * 2
                            
                            # Add more runtime info
                            runtime_info['end_time'] = time.time()
                            
                            # Add some output capture
                            stdout_capture.append('Processing completed')
                            """, StandardCharsets.UTF_8));
            }
        };
    }

    @Test
    @DisplayName("Should handle complex data structures")
    public void testComplexDataStructures() throws Exception {
        // Given
        Map<String, Object> data = new HashMap<>();
        data.put("numbers", Arrays.asList(1, 2, 3, 4, 5));
        data.put("text", "Hello");
        data.put("nested", Map.of("key", "value"));

        // When
        @SuppressWarnings("unchecked")
        Map<String, Object> result = template.requestBody("direct:complex", data, Map.class);

        // Then
        assertNotNull(result);
        assertEquals(15, result.get("sum"));
        assertEquals("HELLO", result.get("modified_text"));
        assertEquals("Value", result.get("nested_value"));
    }

    @Test
    @DisplayName("Should handle exchange headers and properties")
    public void testExchangeHeadersAndProperties() throws Exception {
        // When
        Exchange exchange = template.request("direct:headers", e -> {
            e.getMessage().setBody("test");
            e.getMessage().setHeader("test_header", "Header");
            e.setProperty("test_property", "Property");
        });

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> result = exchange.getMessage().getBody(Map.class);
        assertEquals("Header", result.get("header"));
        assertEquals("Property", result.get("property"));
        assertEquals("HeaderProperty", result.get("combined"));
    }




}