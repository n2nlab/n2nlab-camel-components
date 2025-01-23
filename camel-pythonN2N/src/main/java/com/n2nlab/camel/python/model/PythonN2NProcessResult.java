package com.n2nlab.camel.python.model;

/**
 * Data model class for Python process execution results.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public record PythonN2NProcessResult(
        int exitCode,
        String stdout,
        String stderr,
        long executionTime
) {
    /**
     * Check if the process execution was successful.
     *
     * @return true if exit code is 0, false otherwise
     */
    public boolean isSuccess() {
        return exitCode == 0;
    }

    /**
     * Create a result instance for successful execution.
     *
     * @param stdout Standard output
     * @param executionTime Execution time in milliseconds
     * @return A successful process result
     */
    public static PythonN2NProcessResult success(String stdout, long executionTime) {
        return new PythonN2NProcessResult(0, stdout, "", executionTime);
    }

    /**
     * Create a result instance for failed execution.
     *
     * @param exitCode Process exit code
     * @param stderr Standard error output
     * @param executionTime Execution time in milliseconds
     * @return A failed process result
     */
    public static PythonN2NProcessResult failure(int exitCode, String stderr, long executionTime) {
        return new PythonN2NProcessResult(exitCode, "", stderr, executionTime);
    }
}