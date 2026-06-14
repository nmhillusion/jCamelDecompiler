package tech.nmhillusion.jCamelDecompilerApp.execution;

import tech.nmhillusion.jCamelDecompilerApp.actionable.LogUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.constant.ExecutionStatus;
import tech.nmhillusion.jCamelDecompilerApp.constant.LogType;
import tech.nmhillusion.jCamelDecompilerApp.engine.DecompilerEngine;
import tech.nmhillusion.jCamelDecompilerApp.loader.DecompilerLoader;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompileResultModel;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompilerEngineModel;
import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionState;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.helper.log.MixLogger;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-06-08
 */
final public class CliCommandExecution {
    private static final MixLogger logger = LogHelper.getLogger(CliCommandExecution.class);

    public static void executeCLICommand(String[] args) throws IOException {
        String inputPathStr = null;
        String outputPathStr = null;
        String engineId = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input" -> {
                    if (i + 1 < args.length) inputPathStr = args[++i];
                    break;
                }
                case "--output" -> {
                    if (i + 1 < args.length) outputPathStr = args[++i];
                    break;
                }
                case "--engine" -> {
                    if (i + 1 < args.length) engineId = args[++i];
                    break;
                }
                default -> {
                    // Ignore unknown flags
                }
            }
        }

        if (StringValidator.isBlank(inputPathStr)) {
            logger.error("Missing required --input argument. Usage: java -jar jCamelDecompiler.jar --input <folder-or-file> --output <folder> [--engine <engine-id>]");
            System.exit(1);
        }

        if (StringValidator.isBlank(outputPathStr)) {
            logger.error("Missing required --output argument. Usage: java -jar jCamelDecompiler.jar --input <folder-or-file> --output <folder> [--engine <engine-id>]");
            System.exit(1);
        }

        final File inputFile = new File(inputPathStr);
        if (!inputFile.exists()) {
            logger.error("Input path does not exist: " + inputPathStr);
            System.exit(1);
        }

        final Path inputPath = Paths.get(inputPathStr);
        final Path outputPath = Paths.get(outputPathStr);

        // Determine the base classes folder path for DecompilerEngine
        final Path classesFolderPath;
        if (Files.isDirectory(inputPath)) {
            classesFolderPath = inputPath;
        } else {
            logger.error("Input must be a directory.");
            System.exit(1);
            return; // unreachable, but good practice
        }

        // Set default engineId if not provided
        if (StringValidator.isBlank(engineId)) {
            try {
                engineId = DecompilerLoader.getInstance().loadEngines().getFirst().getEngineId();
            } catch (Exception e) {
                logger.error("No decompiler engines found or failed to load default engine: " + e.getMessage());
                System.exit(1);
            }
        } else {
            final DecompilerEngineModel decompilerEngineModel = DecompilerLoader.getInstance().loadEngine(engineId);
            if (decompilerEngineModel == null) {
                logger.error("Invalid decompiler engine ID: " + engineId);
                System.exit(1);
            }
        }
        logger.info("Using decompiler engine ID: {}", engineId);

        // Create ExecutionState
        final ExecutionState executionState = new ExecutionState()
                .setClassesFolderPath(classesFolderPath)
                .setOutputFolder(outputPath)
                .setDecompilerEngineId(engineId)
                .setIsOnlyFilteredFiles(false)
                .setFilteredFilePath(null)
                .setExecutionStatus(ExecutionStatus.PREPARE);

        // Implement LogUpdatable for CLI
        final LogUpdatable cliLogUpdatable = new LogUpdatable() {
            @Override
            public void onLogMessage(LogType logType, String logMessage) {
                switch (logType) {
                    case INFO -> logger.info(logMessage);
                    case WARN -> logger.warn(logMessage);
                    case ERROR -> logger.error(logMessage);
                    case DEBUG -> logger.debug(logMessage);
                }
            }

            @Override
            public void onStartProgress() {
                logger.info("Starting decompilation...");
            }

            @Override
            public void onDone(String notificationContent, DecompileResultModel decompileResult, long startDecompileTime) {
                logger.info("Decompilation completed successfully. Message: {}, result: {}, time: {}ms"
                        , notificationContent
                        , decompileResult
                        , System.currentTimeMillis() - startDecompileTime
                );
            }

            @Override
            public void onClearLog() {
                // No-op for CLI
            }
        };

        // Implement ProgressStatusUpdatable for CLI
        final ProgressStatusUpdatable cliProgressStatusUpdatable = new ProgressStatusUpdatable() {
            @Override
            public void onUpdateProgressValue(int newPercent, int currentCompletedCount, int totalCount) {
                logger.info("Progress: {}% ({}/{})", newPercent, currentCompletedCount, totalCount);
            }

            @Override
            public void resetProcessState() {
                // No-op for CLI
            }

            @Override
            public void startProgress() {
                // No-op for CLI
            }

            @Override
            public void cancelProgress() {
                // No-op for CLI
            }
        };

        try {
            // Execute decompilation using DecompilerEngine
            logger.info("Executing decompilation...");
            final DecompilerEngine decompilerEngine = new DecompilerEngine(executionState);
            decompilerEngine.execute(cliLogUpdatable, cliProgressStatusUpdatable);
            logger.info("Decompilation completed successfully.");
        } catch (Throwable e) {
            logger.error("Decompilation failed: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
