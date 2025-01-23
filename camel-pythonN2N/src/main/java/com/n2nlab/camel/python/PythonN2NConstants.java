package com.n2nlab.camel.python;

/**
 * Constants used throughout the PythonN2N component.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public final class PythonN2NConstants {

    private PythonN2NConstants() {
        // Prevent instantiation
    }

    // URI Parameters
    public static final String PYTHON_SCRIPT = "pythonScript";
    public static final String SCRIPT_TEMPLATE = "scriptTemplate";
    public static final String PRELOAD_MODULES = "preloadPythonModules";
    public static final String REQUIRED_MODULES = "requiredModules";
    public static final String PYTHON_PATH = "pythonPath";
    public static final String DEBUG_MODE = "debug";
    public static final String TIMEOUT = "timeout";
    public static final String KEEP_TEMP_FILES = "keepTempFiles";
    public static final String ENCODING = "encoding";
    public static final String RETURN_FULL_OUTPUT = "returnFullOutput";

    // Default Values
    public static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final boolean DEFAULT_DEBUG = false;
    public static final boolean DEFAULT_KEEP_TEMP_FILES = false;
    public static final boolean DEFAULT_PRELOAD_MODULES = false;
    public static final boolean DEFAULT_RETURN_FULL_OUTPUT = false;

    // Environment Variables
    public static final String ENV_PYTHON_HOME = "PYTHON_HOME";
    public static final String ENV_PYTHONPATH = "PYTHONPATH";
    public static final String ENV_PYTHON_VERBOSE = "PYTHONVERBOSE";

    // File Extensions
    public static final String PYTHON_FILE_EXTENSION = ".py";
    public static final String JSON_FILE_EXTENSION = ".json";

    // Temporary File Prefixes
    public static final String TEMP_SCRIPT_PREFIX = "script_";
    public static final String TEMP_DATA_PREFIX = "data_";
    public static final String TEMP_OUTPUT_PREFIX = "output_";

    // Directory Names
    public static final String TEMP_DIR_NAME = "pythonN2N";

    // Error Messages
    public static final String ERR_PYTHON_NOT_FOUND = "Python not found. Please install Python or set PYTHON_HOME environment variable.";
    public static final String ERR_SCRIPT_TIMEOUT = "Python script execution timed out after %d ms";
    public static final String ERR_SCRIPT_EXECUTION = "Python script failed with exit code: %d";
    public static final String ERR_MISSING_MODULES = "Required Python modules are missing: %s";
}