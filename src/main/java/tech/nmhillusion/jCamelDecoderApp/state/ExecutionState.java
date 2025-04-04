package tech.nmhillusion.jCamelDecoderApp.state;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.nio.file.Path;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-02-12
 */
public class ExecutionState extends Stringeable {
    private Path classesFolderPath;
    private Path outputFolder;
    private String decoderEngineId;
    private boolean isOnlyFilteredFiles;
    private Path filteredFilePath;

    public Path getClassesFolderPath() {
        return classesFolderPath;
    }

    public ExecutionState setClassesFolderPath(Path classesFolderPath) {
        this.classesFolderPath = classesFolderPath;
        return this;
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public ExecutionState setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public String getDecoderEngineId() {
        return decoderEngineId;
    }

    public ExecutionState setDecoderEngineId(String decoderEngineId) {
        this.decoderEngineId = decoderEngineId;
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
}
