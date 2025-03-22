package tech.nmhillusion.jCamelDecoderApp.state;

import tech.nmhillusion.jCamelDecoderApp.constant.DecoderEngineEnum;
import tech.nmhillusion.n2mix.type.Stringeable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-02-12
 */
public class ExecutionState extends Stringeable {
    private Path decodeFolderPath;
    private Path outputFolder;
    private DecoderEngineEnum decoderEngineType;
    private boolean isOnlyFilteredFiles;
    private Path filteredFilePath;

    public Path getDecodeFolderPath() {
        return decodeFolderPath;
    }

    public ExecutionState setDecodeFolderPath(Path decodeFolderPath) {
        this.decodeFolderPath = decodeFolderPath;
        return this;
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public ExecutionState setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public DecoderEngineEnum getDecoderEngineType() {
        return decoderEngineType;
    }

    public ExecutionState setDecoderEngineType(DecoderEngineEnum decoderEngineType) {
        this.decoderEngineType = decoderEngineType;
        return this;
    }

    public boolean getIsOnlyFilteredFiles() {
        return isOnlyFilteredFiles;
    }

    public ExecutionState setIsOnlyFilteredFiles(boolean onlyFilteredFiles) {
        isOnlyFilteredFiles = onlyFilteredFiles;
        return this;
    }

    public Path getFilteredFilePath() {
        return filteredFilePath;
    }

    public ExecutionState setFilteredFilePath(Path filteredFilePath) {
        this.filteredFilePath = filteredFilePath;
        return this;
    }

    public List<Path> parseFilteredFilePaths() throws IOException {
        if (isOnlyFilteredFiles && null != filteredFilePath) {
            return Stream.of(
                            new String(
                                    Files.readAllBytes(filteredFilePath)
                            )
                    )
                    .map(String::trim)
                    .filter(String::isBlank)
                    .map(Path::of)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}
