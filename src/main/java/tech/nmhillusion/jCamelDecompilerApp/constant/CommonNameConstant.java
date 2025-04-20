package tech.nmhillusion.jCamelDecompilerApp.constant;

/**
 * date: 2025-04-06
 * <p>
 * created-by: nmhillusion
 */
public enum CommonNameConstant {
    FOLDER__LIBRARY("decompiler"),
    FOLDER__STATE("state"),
    FOLDER__SCRIPTS("scripts"),
    /// //////////
    FILE__DECOMPILERS_CONFIG("decompilers.config.yml"),
    FILE__EXECUTION_STATE("executionState.ser"),
    FILE__DECOMPLIED_LIST_OUTPUT("decompiled-file-list.txt"),
    /// //////////
    ENV__APP_HOME("APP_HOME");

    private final String ename;

    CommonNameConstant(String ename) {
        this.ename = ename;
    }

    public String getEName() {
        return ename;
    }
}
