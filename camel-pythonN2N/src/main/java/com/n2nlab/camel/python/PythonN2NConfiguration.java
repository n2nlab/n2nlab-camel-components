package com.n2nlab.camel.python;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.spi.UriPath;

/**
 * Configuration class for PythonN2N endpoint.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
@UriParams
public class PythonN2NConfiguration {

    @UriPath
    @Metadata(required = true)
    private String name;

    @UriParam
    private String pythonScript;

    @UriParam
    private String scriptTemplate;

    @UriParam(defaultValue = "false")
    private boolean preloadPythonModules = PythonN2NConstants.DEFAULT_PRELOAD_MODULES;

    @UriParam
    private String requiredModules;

    @UriParam
    private String pythonPath;

    @UriParam(defaultValue = "false")
    private boolean debug = PythonN2NConstants.DEFAULT_DEBUG;

    @UriParam(defaultValue = "30000")
    private int timeout = PythonN2NConstants.DEFAULT_TIMEOUT;

    @UriParam(defaultValue = "false")
    private boolean keepTempFiles = PythonN2NConstants.DEFAULT_KEEP_TEMP_FILES;

    @UriParam(defaultValue = "UTF-8")
    private String encoding = PythonN2NConstants.DEFAULT_ENCODING;

    @UriParam(defaultValue = "false")
    private boolean returnFullOutput = PythonN2NConstants.DEFAULT_RETURN_FULL_OUTPUT;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPythonScript() {
        return pythonScript;
    }

    public void setPythonScript(String pythonScript) {
        this.pythonScript = pythonScript;
    }

    public String getScriptTemplate() {
        return scriptTemplate;
    }

    public void setScriptTemplate(String scriptTemplate) {
        this.scriptTemplate = scriptTemplate;
    }

    public boolean isPreloadPythonModules() {
        return preloadPythonModules;
    }

    public void setPreloadPythonModules(boolean preloadPythonModules) {
        this.preloadPythonModules = preloadPythonModules;
    }

    public String getRequiredModules() {
        return requiredModules;
    }

    public void setRequiredModules(String requiredModules) {
        this.requiredModules = requiredModules;
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isKeepTempFiles() {
        return keepTempFiles;
    }

    public void setKeepTempFiles(boolean keepTempFiles) {
        this.keepTempFiles = keepTempFiles;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isReturnFullOutput() {
        return returnFullOutput;
    }

    public void setReturnFullOutput(boolean returnFullOutput) {
        this.returnFullOutput = returnFullOutput;
    }
}