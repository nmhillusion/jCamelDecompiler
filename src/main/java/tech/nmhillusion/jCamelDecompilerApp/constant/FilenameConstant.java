package tech.nmhillusion.jCamelDecompilerApp.constant;

/**
 * date: 2025-04-06
 * <p>
 * created-by: nmhillusion
 */
public enum FilenameConstant {
    FOLDER__LIBRARY("decompiler"),
    FOLDER__STATE("state"),
    FOLDER__SCRIPTS("scripts"),
    /// //////////
    FILE__DECOMPILERS_CONFIG("decompilers.config.yml"),
    FILE__EXECUTION_STATE("executionState.ser");

    private final String filename;

    FilenameConstant(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
