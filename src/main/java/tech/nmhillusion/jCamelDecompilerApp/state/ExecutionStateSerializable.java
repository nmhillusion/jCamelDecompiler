package tech.nmhillusion.jCamelDecompilerApp.state;

import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.StringUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-02-12
 */
public class ExecutionStateSerializable extends Stringeable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String classesFolderPath;
    private String outputFolder;
    private String decompilerEngineId;
    private boolean isOnlyFilteredFiles;
    private String filteredFilePath;

    public static ExecutionStateSerializable from(ExecutionState executionState) {
        final ExecutionStateSerializable executionStateSerializable = new ExecutionStateSerializable();
        executionStateSerializable.setClassesFolderPath(StringUtil.trimWithNull(executionState.getClassesFolderPath()));
        executionStateSerializable.setOutputFolder(StringUtil.trimWithNull(executionState.getOutputFolder()));
        executionStateSerializable.setDecompilerEngineId(executionState.getDecompilerEngineId());
        executionStateSerializable.setIsOnlyFilteredFiles(executionState.getIsOnlyFilteredFiles());
        executionStateSerializable.setFilteredFilePath(StringUtil.trimWithNull(executionState.getFilteredFilePath()));
        return executionStateSerializable;
    }

    public String getClassesFolderPath() {
        return classesFolderPath;
    }

    public ExecutionStateSerializable setClassesFolderPath(String classesFolderPath) {
        this.classesFolderPath = classesFolderPath;
        return this;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public ExecutionStateSerializable setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public String getDecompilerEngineId() {
        return decompilerEngineId;
    }

    public ExecutionStateSerializable setDecompilerEngineId(String decompilerEngineId) {
        this.decompilerEngineId = decompilerEngineId;
        return this;
    }

    public boolean getIsOnlyFilteredFiles() {
        return isOnlyFilteredFiles;
    }

    public ExecutionStateSerializable setIsOnlyFilteredFiles(boolean onlyFilteredFiles) {
        isOnlyFilteredFiles = onlyFilteredFiles;
        return this;
    }

    public String getFilteredFilePath() {
        return filteredFilePath;
    }

    public ExecutionStateSerializable setFilteredFilePath(String filteredFilePath) {
        this.filteredFilePath = filteredFilePath;
        return this;
    }
}
