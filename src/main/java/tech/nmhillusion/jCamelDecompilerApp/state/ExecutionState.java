package tech.nmhillusion.jCamelDecompilerApp.state;

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
    private String decompilerEngineId;
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

    public String getDecompilerEngineId() {
        return decompilerEngineId;
    }

    public ExecutionState setDecompilerEngineId(String decompilerEngineId) {
        this.decompilerEngineId = decompilerEngineId;
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
