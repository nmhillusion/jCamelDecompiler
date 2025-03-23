package tech.nmhillusion.jCamelDecoderApp.engine;

import tech.nmhillusion.jCamelDecoderApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecoderApp.constant.LogType;
import tech.nmhillusion.jCamelDecoderApp.factory.DecoderEngineFactory;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecoderApp.runtime.BaseDecompilerExecutor;
import tech.nmhillusion.jCamelDecoderApp.state.ExecutionState;
import tech.nmhillusion.n2mix.helper.storage.FileHelper;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-03-15
 */
public class DecoderEngine {
    private final ExecutionState executionState;
    private final DecoderEngineModel decoderEngineModel;

    public DecoderEngine(ExecutionState executionState) {
        this.executionState = executionState;
        this.decoderEngineModel = DecoderEngineFactory.getInstance().getEngine(executionState.getDecoderEngineType());
    }

    private List<Path> traversalPaths(Path rootPath) throws IOException {
        final List<Path> dicPaths = new ArrayList<>();
        try (final Stream<Path> rootItemPathStream = Files.list(rootPath)) {
            final List<Path> rootItemPaths = rootItemPathStream.toList();

            for (var itemPath : rootItemPaths) {
                final boolean isDirectory = Files.isDirectory(itemPath);

                if (isDirectory) {
                    dicPaths.addAll(
                            traversalPaths(itemPath)
                    );
                } else {
                    dicPaths.add(itemPath);
                }
            }
        }

        return dicPaths;
    }

    private Path mkdirForDirectory(Path absolutePathOfItem, Path classesFolder, Path outputFolerPath) throws IOException {
        final Path relativePathOfItem = classesFolder.relativize(absolutePathOfItem.getParent());

        if (StringValidator.isBlank(relativePathOfItem.toString())) {
            getLogger(this).info("sub-path is empty --> ignored");
            return relativePathOfItem;
        }

        final Path outItemPath = Paths.get(outputFolerPath.toString(), relativePathOfItem.toString());
        if (Files.exists(outItemPath)) {
            getLogger(this).info("Directory already existed: {}",
                    String.valueOf(outItemPath)
                            .replace("\\", "/")
            );
            return relativePathOfItem;
        }

        return Files.createDirectories(outItemPath);
    }

    private void doLogMessage(ProgressStatusUpdatable progressStatusUpdatableHandler, LogType logType, String logMessage) throws InterruptedException, InvocationTargetException {
        getLogger(this).info(
                logMessage
        );
        if (null != progressStatusUpdatableHandler) {
            progressStatusUpdatableHandler.onLogMessage(logType, logMessage);
        }
    }

    public Path execute(AtomicReference<ProgressStatusUpdatable> progressStatusUpdatableHandlerRef) throws Throwable {
        final ProgressStatusUpdatable progressStatusUpdatableHandler = progressStatusUpdatableHandlerRef.get();

        doLogMessage(
                progressStatusUpdatableHandler
                , LogType.INFO
                , "do runtime decoding on state: {state}".replace("{state}", StringUtil.trimWithNull(executionState))
        );

        final Path outputFolder = executionState.getOutputFolder();

        final List<Path> allPaths = traversalPaths(executionState.getClassesFolderPath());
        final List<DecompileFileModel> decompileFileOriginalList = new ArrayList<>();

        for (var path_ : allPaths) {
//            LogHelper.getLogger(this).info("item path: {}", path_.toString().replace("\\", "/"));

            if (Files.isRegularFile(path_)) {
                if (FileHelper.getFileExtensionFromName(String.valueOf(path_.getFileName()))
                        .orElse("")
                        .endsWith("class")
                ) {
                    decompileFileOriginalList
                            .add(new DecompileFileModel()
                                    .setClassFilePath(path_)
                                    .setOutputFilePath(getOutputFilePath(path_, outputFolder))
                            );
                }
            }
        }

        final List<DecompileFileModel> decompileFileList = doFilterFileByFiltedList(
                decompileFileOriginalList
        );

        final BaseDecompilerExecutor baseDecompilerExecutor = new BaseDecompilerExecutor(
                decoderEngineModel
        );

        final int decompileFileCount = decompileFileList.size();
        doLogMessage(
                progressStatusUpdatableHandler
                , LogType.INFO
                , "There are {fileCount} class files to decompile in total.".replace("{fileCount}",
                        String.valueOf(decompileFileCount))
        );

        doLogMessage(progressStatusUpdatableHandler
                , LogType.WARN
                , "Cleaning output folder"
        );
        this.deleteFolderRecursive(executionState.getOutputFolder());

        for (int fileIdx = 0; fileIdx < decompileFileCount; fileIdx++) {
            var decompileItem = decompileFileList.get(fileIdx);

            final String currentExecClassFilePath = String.valueOf(
                            executionState.getClassesFolderPath()
                                    .relativize(decompileItem.getClassFilePath())
                    )
                    .replace("\\", "/");
            doLogMessage(
                    progressStatusUpdatableHandler
                    , LogType.INFO
                    , "Decompiling for {filePath}"
                            .replace("{filePath}"
                                    , currentExecClassFilePath
                            )
            );

            mkdirForDirectory(
                    decompileItem.getClassFilePath()
                    , executionState.getClassesFolderPath()
                    , outputFolder
            );

            /// Mark: EXECUTING DECOMPILATION
            final int exitCode = baseDecompilerExecutor.execScriptFile(
                    decompileItem
                    , msg -> doLogMessage(
                            progressStatusUpdatableHandler
                            , LogType.DEBUG
                            , msg
                    )
            );

            doLogMessage(
                    progressStatusUpdatableHandler
                    , LogType.INFO
                    , "Decompiled result: {exitCode} for {filePath}"
                            .replace("{exitCode}", String.valueOf(exitCode))
                            .replace("{filePath}", currentExecClassFilePath)
            );

            if (null != progressStatusUpdatableHandler) {
                progressStatusUpdatableHandler.onUpdateProgressValue(
                        Math.floorDivExact((fileIdx + 1) * 100, decompileFileCount)
                );
            }
        }

        return outputFolder;
    }

    private void deleteFolderRecursive(Path dirPath) throws IOException {
        if (Files.isDirectory(dirPath)) {
            try (final Stream<Path> itemList = Files.list(dirPath)) {
                itemList.forEach(itemPath -> {
                    try {
                        deleteFolderRecursive(itemPath);
                    } catch (Throwable ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }

        Files.deleteIfExists(dirPath);
    }

    private List<DecompileFileModel> doFilterFileByFiltedList(List<DecompileFileModel> decompileFileOriginalList) throws IOException {
        if (!executionState.getIsOnlyFilteredFiles()) {
            return decompileFileOriginalList;
        }

        if (null == executionState.getFilteredFilePath()) {
            throw new IllegalArgumentException("Filtered file path is null");
        }

        final List<String> allLines = Files.readAllLines(executionState.getFilteredFilePath())
                .stream()
                .filter(Predicate.not(String::isBlank))
                .map(StringUtil::trimWithNull)
                .map(it -> it.replaceFirst("\\.(class|java)$", ""))
                .toList();

        return decompileFileOriginalList
                .stream()
                .filter(decompileFileModel -> {
                    final Path classFilePath = decompileFileModel.getClassFilePath();
                    final String pathWithoutExt = StringUtil.trimWithNull(classFilePath).replaceFirst("\\.(class|java)$", "");
                    return (allLines.stream().anyMatch(pathWithoutExt::endsWith));
                })
                .toList();
    }

    private Path getOutputFilePath(Path itemAbsolutePath, Path outputDirPath) {
        final Path relativeItemPath = executionState.getClassesFolderPath()
                .relativize(itemAbsolutePath);

        final String javaFileName = String.valueOf(relativeItemPath.getFileName())
                .replaceFirst("\\.class$", ".java");

        return Paths.get(
                String.valueOf(outputDirPath),
                String.valueOf(
                        relativeItemPath.getParent()
                ),
                javaFileName
        );
    }
}
