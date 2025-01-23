package com.n2nlab.camel.python;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n2nlab.camel.python.model.PythonN2NExchangeData;
import com.n2nlab.camel.python.model.PythonN2NProcessResult;
import com.n2nlab.camel.python.model.PythonN2NResult;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The PythonN2N producer.
 *
 * @author Mahmoud Ahmed at N2NLab
 * @version 1.0
 * @since 2025-01-22
 */
public class PythonN2NProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(PythonN2NProducer.class);

    private final PythonN2NEndpoint endpoint;
    private final ObjectMapper objectMapper;
    private final AtomicInteger scriptCounter;
    private final String pythonExecutable;
    private final Path tempDir;
    private ExecutorService executorService;

    public PythonN2NProducer(PythonN2NEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.objectMapper = new ObjectMapper();
        this.scriptCounter = new AtomicInteger();
        this.pythonExecutable = determinePythonExecutable();
        this.tempDir = Paths.get(System.getProperty("java.io.tmpdir"), PythonN2NConstants.TEMP_DIR_NAME);
    }

    private String determinePythonExecutable() {
        String customPath = endpoint.getPythonPath();
        if (customPath != null && !customPath.isEmpty()) {
            return customPath;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return System.getenv().getOrDefault(PythonN2NConstants.ENV_PYTHON_HOME,
                    isPythonAvailable("python3.exe") ? "python3.exe" : "python.exe");
        }
        return System.getenv().getOrDefault(PythonN2NConstants.ENV_PYTHON_HOME,
                isPythonAvailable("python3") ? "python3" : "python");
    }

    private boolean isPythonAvailable(String command) {
        try {
            Process process = new ProcessBuilder(command, "--version")
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        Files.createDirectories(tempDir);
        executorService = Executors.newCachedThreadPool();

        if (endpoint.isPreloadPythonModules()) {
            verifyPythonModules();
        }

        verifyPythonInstallation();
    }

    private void verifyPythonInstallation() throws Exception {
        Process process = new ProcessBuilder(pythonExecutable, "--version")
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        if (process.waitFor() != 0) {
            throw new IllegalStateException(PythonN2NConstants.ERR_PYTHON_NOT_FOUND);
        }
    }

    private void verifyPythonModules() throws Exception {
        String requiredModules = endpoint.getRequiredModules();
        if (requiredModules != null && !requiredModules.isEmpty()) {
            String verificationScript = createModuleVerificationScript(requiredModules);
            Path scriptPath = tempDir.resolve("verify_modules.py");
            Files.writeString(scriptPath, verificationScript);

            Process process = new ProcessBuilder(pythonExecutable, scriptPath.toString())
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();

            if (process.waitFor() != 0) {
                throw new IllegalStateException(
                        String.format(PythonN2NConstants.ERR_MISSING_MODULES, requiredModules));
            }
        }
    }

    private String createModuleVerificationScript(String modules) {
        StringBuilder script = new StringBuilder("import sys\ntry:\n");
        for (String module : modules.split(",")) {
            script.append("    import ").append(module.trim()).append("\n");
        }
        script.append("    print('Modules verified successfully')\n")
                .append("except ImportError as e:\n")
                .append("    print(f'Missing module: {str(e)}', file=sys.stderr)\n")
                .append("    sys.exit(1)\n");
        return script.toString();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        int scriptId = scriptCounter.incrementAndGet();
        Path scriptPath = tempDir.resolve(PythonN2NConstants.TEMP_SCRIPT_PREFIX + scriptId + PythonN2NConstants.PYTHON_FILE_EXTENSION);
        Path dataPath = tempDir.resolve(PythonN2NConstants.TEMP_DATA_PREFIX + scriptId + PythonN2NConstants.JSON_FILE_EXTENSION);
        Path outputPath = tempDir.resolve(PythonN2NConstants.TEMP_OUTPUT_PREFIX + scriptId + PythonN2NConstants.JSON_FILE_EXTENSION);

        try {
            // Write exchange data to JSON file
            objectMapper.writeValue(dataPath.toFile(), new PythonN2NExchangeData(exchange));

            // Create and write Python script
            String finalScript = buildFinalScript(dataPath, outputPath);
            Files.writeString(scriptPath, finalScript, Charset.forName(endpoint.getEncoding()));

            // Execute Python script
            ProcessBuilder processBuilder = createProcessBuilder(scriptPath);
            PythonN2NProcessResult processResult = executeScript(processBuilder);

            if (processResult.isSuccess()) {
                handleSuccess(exchange, outputPath, processResult);
            } else {
                handleError(processResult);
            }
        } finally {
            if (!endpoint.isKeepTempFiles()) {
                cleanup(scriptPath, dataPath, outputPath);
            } else if (endpoint.isDebug()) {
                LOG.info("Debug mode: Keeping temporary files at: {}", scriptPath.getParent());
            }
        }
    }

    private ProcessBuilder createProcessBuilder(Path scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath.toString());
        configureEnvironment(processBuilder);
        return processBuilder;
    }

    private void configureEnvironment(ProcessBuilder processBuilder) {
        if (endpoint.isDebug()) {
            processBuilder.environment().put(PythonN2NConstants.ENV_PYTHON_VERBOSE, "1");
        }

        String pythonPath = endpoint.getPythonPath();
        if (pythonPath != null && !pythonPath.isEmpty()) {
            processBuilder.environment().put(PythonN2NConstants.ENV_PYTHONPATH, pythonPath);
        }
    }

    private PythonN2NProcessResult executeScript(ProcessBuilder processBuilder) throws Exception {
        Future<PythonN2NProcessResult> future = executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            Process process = processBuilder.start();

            String stdout = new String(process.getInputStream().readAllBytes());
            String stderr = new String(process.getErrorStream().readAllBytes());
            int exitCode = process.waitFor();
            long executionTime = System.currentTimeMillis() - startTime;

            return new PythonN2NProcessResult(exitCode, stdout, stderr, executionTime);
        });

        try {
            return future.get(endpoint.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException(String.format(PythonN2NConstants.ERR_SCRIPT_TIMEOUT, endpoint.getTimeout()));
        }
    }

    private String buildFinalScript(Path dataPath, Path outputPath) {
        StringBuilder scriptBuilder = new StringBuilder();

        if (endpoint.getRequiredModules() != null) {
            for (String module : endpoint.getRequiredModules().split(",")) {
                scriptBuilder.append("import ").append(module.trim()).append("\n");
            }
        }

        if (endpoint.getScriptTemplate() != null) {
            scriptBuilder.append(endpoint.getScriptTemplate()).append("\n");
        }

        scriptBuilder.append(String.format("""
            import json
            import os
            
            # Load exchange data
            with open(r'%s', 'r', encoding='%s') as f:
                data = json.load(f)
            
            # Set variables
            body = data['body']
            headers = data['headers']
            properties = data['properties']
            exchange_id = data['exchangeId']
            
            # Initialize debug info
            runtime_info = {}
            stdout_capture = []
            
            # User script starts here
            %s
            # User script ends here
            
            # Save result
            with open(r'%s', 'w', encoding='%s') as f:
                json.dump({
                    'result': result,
                    'debugInfo': {
                        'stdout': stdout_capture,
                        'runtimeInfo': runtime_info
                    }
                }, f)
            """,
                dataPath.toString().replace("\\", "\\\\"),
                endpoint.getEncoding(),
                endpoint.getPythonScript(),
                outputPath.toString().replace("\\", "\\\\"),
                endpoint.getEncoding()
        ));

        return scriptBuilder.toString();
    }

    private void handleSuccess(Exchange exchange, Path outputPath, PythonN2NProcessResult processResult)
            throws IOException {
        String jsonResult = Files.readString(outputPath, Charset.forName(endpoint.getEncoding()));

        if (endpoint.isReturnFullOutput()) {
            PythonN2NResult fullResult = objectMapper.readValue(jsonResult, PythonN2NResult.class);
            exchange.getMessage().setBody(fullResult);

            if (endpoint.isDebug()) {
                exchange.setProperty("pythonStdout", processResult.stdout());
                exchange.setProperty("pythonStderr", processResult.stderr());
                exchange.setProperty("pythonExecutionTime", processResult.executionTime());
            }
        } else {
            PythonN2NResult result = objectMapper.readValue(jsonResult, PythonN2NResult.class);
            exchange.getMessage().setBody(result.getResult());
        }
    }

    private void handleError(PythonN2NProcessResult result) {
        String errorMsg = String.format(PythonN2NConstants.ERR_SCRIPT_EXECUTION + "\nStderr: %s\nStdout: %s",
                result.exitCode(), result.stderr(), result.stdout());
        throw new RuntimeException(errorMsg);
    }

    private void cleanup(Path... paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (Exception e) {
                LOG.warn("Error cleaning up file {}: {}", path, e.getMessage());
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}