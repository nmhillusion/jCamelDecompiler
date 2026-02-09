package tech.nmhillusion.jCamelDecompilerApp.model;

import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionState;
import tech.nmhillusion.n2mix.type.Stringeable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-02-09
 */
public class DecompileResultModel extends Stringeable {
    private final List<Path> successFiles = new ArrayList<>();
    private final List<Path> failureFiles = new ArrayList<>();
    private Path outputFolder;
    private ExecutionState executionState;

    public List<Path> getSuccessFiles() {
        return Collections.unmodifiableList(successFiles);
    }

    public List<Path> getFailureFiles() {
        return Collections.unmodifiableList(failureFiles);
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public DecompileResultModel setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

    public DecompileResultModel setExecutionState(ExecutionState executionState) {
        this.executionState = executionState;
        return this;
    }

    public DecompileResultModel addSuccessFile(Path successFile) {
        this.successFiles.add(successFile);
        return this;
    }

    public DecompileResultModel addFailureFile(Path failureFile) {
        this.failureFiles.add(failureFile);
        return this;
    }
}
