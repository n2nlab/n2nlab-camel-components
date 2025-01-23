package com.n2nlab.camel.python;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.Metadata;

/**
 * PythonN2N endpoint for executing Python scripts.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
@UriEndpoint(
        firstVersion = "1.0-SNAPSHOT",
        scheme = "pythonN2N",
        title = "PythonN2N Component",
        syntax = "pythonN2N:name",
        category = Category.TRANSFORMATION,
        producerOnly = true
)
public class PythonN2NEndpoint extends DefaultEndpoint {

    @Metadata(description = "The endpoint configuration")
    private final PythonN2NConfiguration configuration;

    public PythonN2NEndpoint(String uri, PythonN2NComponent component) {
        super(uri, component);
        this.configuration = new PythonN2NConfiguration();
    }

    @Override
    public Producer createProducer() throws Exception {
        return new PythonN2NProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported for PythonN2N endpoint");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public PythonN2NConfiguration getConfiguration() {
        return configuration;
    }

    // Delegate methods to configuration
    public void setName(String name) {
        getConfiguration().setName(name);
    }

    public String getPythonScript() {
        return getConfiguration().getPythonScript();
    }

    public void setPythonScript(String pythonScript) {
        getConfiguration().setPythonScript(pythonScript);
    }

    public String getScriptTemplate() {
        return getConfiguration().getScriptTemplate();
    }

    public void setScriptTemplate(String scriptTemplate) {
        getConfiguration().setScriptTemplate(scriptTemplate);
    }

    public boolean isPreloadPythonModules() {
        return getConfiguration().isPreloadPythonModules();
    }

    public void setPreloadPythonModules(boolean preloadPythonModules) {
        getConfiguration().setPreloadPythonModules(preloadPythonModules);
    }

    public String getRequiredModules() {
        return getConfiguration().getRequiredModules();
    }

    public void setRequiredModules(String requiredModules) {
        getConfiguration().setRequiredModules(requiredModules);
    }

    public String getPythonPath() {
        return getConfiguration().getPythonPath();
    }

    public void setPythonPath(String pythonPath) {
        getConfiguration().setPythonPath(pythonPath);
    }

    public boolean isDebug() {
        return getConfiguration().isDebug();
    }

    public void setDebug(boolean debug) {
        getConfiguration().setDebug(debug);
    }

    public int getTimeout() {
        return getConfiguration().getTimeout();
    }

    public void setTimeout(int timeout) {
        getConfiguration().setTimeout(timeout);
    }

    public boolean isKeepTempFiles() {
        return getConfiguration().isKeepTempFiles();
    }

    public void setKeepTempFiles(boolean keepTempFiles) {
        getConfiguration().setKeepTempFiles(keepTempFiles);
    }

    public String getEncoding() {
        return getConfiguration().getEncoding();
    }

    public void setEncoding(String encoding) {
        getConfiguration().setEncoding(encoding);
    }

    public boolean isReturnFullOutput() {
        return getConfiguration().isReturnFullOutput();
    }

    public void setReturnFullOutput(boolean returnFullOutput) {
        getConfiguration().setReturnFullOutput(returnFullOutput);
    }
}