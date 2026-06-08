package tech.nmhillusion.jCamelDecompilerApp.execution;

import tech.nmhillusion.jCamelDecompilerApp.loader.DecompilerLoader;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompilerEngineModel;
import tech.nmhillusion.jCamelDecompilerApp.runtime.DecompilerExecutor;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-06-08
 */
final public class CliCommandExecution {

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
            System.err.println("Missing required --input argument. Usage: java -jar jCamelDecompiler.jar --input <folder-or-file> [--output <folder>] [--engine <engine-id>]");
            System.exit(1);
        }

        final File inputFile = new File(inputPathStr);
        if (!inputFile.exists()) {
            System.err.println("Input path does not exist: " + inputPathStr);
            System.exit(1);
        }

        // Load appropriate engine configuration
        final DecompilerEngineModel engineModel = engineId != null ?
                DecompilerLoader.getInstance().loadEngine(engineId) :
                DecompilerLoader.getInstance().loadEngines().getFirst();

        // Process input - if it's a folder, find all .class files
        final List<Path> filesToProcess = new ArrayList<>();
        final Path inputPath_ = Paths.get(inputPathStr);
        if (inputFile.isDirectory()) {
            // Walk the directory tree to find all .class files
            try (Stream<Path> paths = Files.walk(inputPath_)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".class"))
                        .forEach(filesToProcess::add);
            }

            if (filesToProcess.isEmpty()) {
                System.err.println("No .class files found in directory: " + inputPathStr);
                System.exit(1);
            }
        } else if (inputFile.isFile() && inputPathStr.endsWith(".class")) {
            // Single class file
            filesToProcess.add(inputPath_);
        } else {
            System.err.println("Input file must be a .class file or directory containing .class files");
            System.exit(1);
        }

        // Ensure output directory exists if specified
        Path outputPath = null;
        if (outputPathStr != null) {
            outputPath = Paths.get(outputPathStr);
            Path outputDir = outputPath;
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
        }

        // Process each file
        for (Path filePath : filesToProcess) {
            try {
                // Set up decompile target model
                final DecompileFileModel model = new DecompileFileModel()
                        .setClassFilePath(filePath);

                // Set optional output path - if output directory specified,
                // place decompiled file there with same name but .java extension
                if (outputPath != null) {
                    String fileName = filePath.getFileName().toString();
                    String javaFileName = fileName.replaceFirst("\\.class$", ".java");
                    Path outputFilePath = outputPath.resolve(javaFileName);
                    model.setOutputFilePath(outputFilePath);
                }

                // Execute decompilation
                final DecompilerExecutor executor = new DecompilerExecutor(engineModel);
                executor.execScriptFile(model, System.out::println);
            } catch (Throwable e) {
                System.err.println("Failed to decompile " + filePath + ": " + e.getMessage());
                // Continue with other files rather than failing completely
            }
        }
    }

}
