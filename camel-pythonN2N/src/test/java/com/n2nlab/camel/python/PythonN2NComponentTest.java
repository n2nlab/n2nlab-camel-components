package com.n2nlab.camel.python;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PythonN2NComponent}.
 */
public class PythonN2NComponentTest extends CamelTestSupport {

    @TempDir
    Path tempDir;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                context.addComponent("pythonN2N", new PythonN2NComponent());

                // Add test routes
                from("direct:pythonTest")
                        .toF("pythonN2N:test?pythonScript=%s",
                                URLEncoder.encode("result = body * 2", StandardCharsets.UTF_8));

                from("direct:pythonString")
                        .toF("pythonN2N:test?pythonScript=%s",
                                URLEncoder.encode("result = 'Hello, ' + str(body)", StandardCharsets.UTF_8));

                from("direct:pythonList")
                        .toF("pythonN2N:test?pythonScript=%s",
                                URLEncoder.encode("numbers = body\nresult = sum(numbers)", StandardCharsets.UTF_8));

                from("direct:pythonTimeout")
                        .toF("pythonN2N:test?pythonScript=%s&timeout=1000",
                                URLEncoder.encode("import time\ntime.sleep(2)\nresult = 'Done'", StandardCharsets.UTF_8));
            }
        };
    }

    @Test
    @DisplayName("Should execute simple Python script successfully")
    public void testSimplePythonScript() throws Exception {
        // When
        Object result = template.requestBody("direct:pythonTest", 5, Object.class);

        // Then
        assertEquals(10, result);
    }

    @Test
    @DisplayName("Should handle Python script with string manipulation")
    public void testStringManipulation() throws Exception {
        // When
        Object result = template.requestBody("direct:pythonString", "World", Object.class);

        // Then
        assertEquals("Hello, World", result);
    }

    @Test
    @DisplayName("Should handle list processing")
    public void testListProcessing() throws Exception {
        // Given
        int[] numbers = {1, 2, 3, 4, 5};

        // When
        Object result = template.requestBody("direct:pythonList", numbers, Object.class);

        // Then
        assertEquals(15, result);
    }

    @Test
    @DisplayName("Should handle script timeout")
    public void testScriptTimeout() {
        // When/Then
        assertThrows(Exception.class, () -> {
            template.requestBody("direct:pythonTimeout", null, Object.class);
        });
    }
}