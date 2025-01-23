package com.n2nlab.camel.python.exception;

/**
 * Base exception class for PythonN2N component.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public class PythonN2NException extends RuntimeException {

    public PythonN2NException(String message) {
        super(message);
    }

    public PythonN2NException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when Python script execution fails.
 */
class PythonScriptExecutionException extends PythonN2NException {
    private final int exitCode;
    private final String stdout;
    private final String stderr;

    public PythonScriptExecutionException(String message, int exitCode, String stdout, String stderr) {
        super(message);
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}

/**
 * Exception thrown when Python script execution times out.
 */
class PythonScriptTimeoutException extends PythonN2NException {
    private final long timeout;

    public PythonScriptTimeoutException(String message, long timeout) {
        super(message);
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }
}

/**
 * Exception thrown when required Python modules are missing.
 */
class PythonModuleNotFoundException extends PythonN2NException {
    private final String moduleName;

    public PythonModuleNotFoundException(String message, String moduleName) {
        super(message);
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
}

/**
 * Exception thrown when Python installation is not found.
 */
class PythonNotFoundException extends PythonN2NException {
    public PythonNotFoundException(String message) {
        super(message);
    }
}