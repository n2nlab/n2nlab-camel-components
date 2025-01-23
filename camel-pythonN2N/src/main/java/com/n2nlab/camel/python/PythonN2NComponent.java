package com.n2nlab.camel.python;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.spi.annotations.Component;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * PythonN2N component for executing Python scripts within Camel routes.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
@Component("pythonN2N")
public class PythonN2NComponent extends DefaultComponent {

    public PythonN2NComponent() {
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        PythonN2NEndpoint endpoint = new PythonN2NEndpoint(uri, this);
        endpoint.setName(remaining);

        // Get the Python script from parameters
        String pythonScript = (String) parameters.get("pythonScript");
        if (pythonScript != null) {
            // Decode the Python script
            pythonScript = URLDecoder.decode(pythonScript, StandardCharsets.UTF_8);
            parameters.put("pythonScript", pythonScript);
        }

        // Configure the endpoint based on the parameters
        setProperties(endpoint, parameters);

        return endpoint;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    @Override
    public boolean useRawUri() {
        // Allow URIs to contain encoded characters
        return true;
    }
}