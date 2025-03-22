package tech.nmhillusion.jCamelDecoderApp.gui.frame;

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

    private Path mkdirForDirectory(Path absolutePathOfItem, Path decodedFolder, Path outPath) throws IOException {
        final Path relativePathOfItem = decodedFolder.relativize(absolutePathOfItem.getParent());

        if (StringValidator.isBlank(relativePathOfItem.toString())) {
            getLogger(this).info("sub-path is empty --> ignored");
            return relativePathOfItem;
        }

        final Path outItemPath = Paths.get(outPath.toString(), relativePathOfItem.toString());
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

    public Path execute(AtomicReference<ProgressStatusUpdatable> progressStatusUpdatableHandlerRef) throws IOException, InterruptedException, InvocationTargetException {
        final ProgressStatusUpdatable progressStatusUpdatableHandler = progressStatusUpdatableHandlerRef.get();

        doLogMessage(
                progressStatusUpdatableHandler
                , LogType.INFO
                , "do runtime decoding on state: {state}".replace("{state}", StringUtil.trimWithNull(executionState))
        );

        final Path outputFolder = executionState.getOutputFolder();

        final List<Path> allPaths = traversalPaths(executionState.getDecodeFolderPath());
        final List<DecompileFileModel> decompileFileList = new ArrayList<>();

        for (var path_ : allPaths) {
//            LogHelper.getLogger(this).info("item path: {}", path_.toString().replace("\\", "/"));

            if (Files.isDirectory(path_)) {
                mkdirForDirectory(path_, executionState.getDecodeFolderPath(), outputFolder);
            } else {
                mkdirForDirectory(path_, executionState.getDecodeFolderPath(), outputFolder);

                if (FileHelper.getFileExtensionFromName(String.valueOf(path_.getFileName()))
                        .orElse("")
                        .endsWith("class")
                ) {
                    decompileFileList
                            .add(new DecompileFileModel()
                                    .setClassFilePath(path_)
                                    .setOutputFilePath(getOutputFilePath(path_, outputFolder))
                            );
                }
            }
        }

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

        for (int fileIdx = 0; fileIdx < decompileFileCount; fileIdx++) {
            var decompileItem = decompileFileList.get(fileIdx);

            doLogMessage(
                    progressStatusUpdatableHandler
                    , LogType.INFO
                    , "Decompiling for {filePath}"
                            .replace("{filePath}", String.valueOf(decompileItem.getClassFilePath())
                                    .replace("\\", "/"))
            );

            final int exitCode = baseDecompilerExecutor.execScriptFile(
                    decompileItem
            );

            doLogMessage(
                    progressStatusUpdatableHandler
                    , LogType.INFO
                    , "Decompiled result: {exitCode} for {filePath}".replace("{exitCode}", String.valueOf(exitCode))
                            .replace("{filePath}", String.valueOf(decompileItem.getClassFilePath())
                                    .replace("\\", "/"))
            );

            if (null != progressStatusUpdatableHandler) {
                progressStatusUpdatableHandler.onUpdateProgressValue(
                        Math.floorDivExact((fileIdx + 1) * 100, decompileFileCount)
                );
            }
        }

        return outputFolder;
    }

    private Path getOutputFilePath(Path itemAbsolutePath, Path outputDirPath) {
        final Path relativeItemPath = executionState.getDecodeFolderPath()
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
