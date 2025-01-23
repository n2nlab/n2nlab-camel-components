package com.n2nlab.camel.python;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PythonN2NEndpoint}.
 */
public class PythonN2NEndpointTest extends CamelTestSupport {

    @Test
    @DisplayName("Should create endpoint with default configuration")
    public void testDefaultConfiguration() throws Exception {
        // When
        PythonN2NEndpoint endpoint = context.getEndpoint("pythonN2N:test", PythonN2NEndpoint.class);

        // Then
        assertNotNull(endpoint);
        assertEquals("test", endpoint.getConfiguration().getName());
        assertEquals(PythonN2NConstants.DEFAULT_TIMEOUT, endpoint.getTimeout());
        assertEquals(PythonN2NConstants.DEFAULT_ENCODING, endpoint.getEncoding());
        assertFalse(endpoint.isDebug());
        assertFalse(endpoint.isKeepTempFiles());
        assertFalse(endpoint.isPreloadPythonModules());
    }

    @Test
    @DisplayName("Should create endpoint with custom configuration")
    public void testCustomConfiguration() throws Exception {
        // When
        PythonN2NEndpoint endpoint = context.getEndpoint(
                "pythonN2N:test?" +
                        "timeout=5000&" +
                        "debug=true&" +
                        "keepTempFiles=true&" +
                        "encoding=UTF-16&" +
                        "pythonPath=/custom/python&" +
                        "requiredModules=numpy,pandas",
                PythonN2NEndpoint.class
        );

        // Then
        assertNotNull(endpoint);
        assertEquals(5000, endpoint.getTimeout());
        assertTrue(endpoint.isDebug());
        assertTrue(endpoint.isKeepTempFiles());
        assertEquals("UTF-16", endpoint.getEncoding());
        assertEquals("/custom/python", endpoint.getPythonPath());
        assertEquals("numpy,pandas", endpoint.getRequiredModules());
    }

    @Test
    @DisplayName("Should not support consumer")
    public void testConsumerNotSupported() {
        // When/Then
        PythonN2NEndpoint endpoint = context.getEndpoint("pythonN2N:test", PythonN2NEndpoint.class);
        assertThrows(UnsupportedOperationException.class, () -> {
            endpoint.createConsumer(null);
        });
    }
}