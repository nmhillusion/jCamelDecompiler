package tech.nmhillusion.jCamelDecoderApp.model;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-02-12
 */
public class DecoderEngineModel {
    private String engineId;
    private String engineName;
    private String libFilename;
    private String compilerOptions;
    private String execScriptFilename;

    public String getEngineId() {
        return engineId;
    }

    public DecoderEngineModel setEngineId(String engineId) {
        this.engineId = engineId;
        return this;
    }

    public String getEngineName() {
        return engineName;
    }

    public DecoderEngineModel setEngineName(String engineName) {
        this.engineName = engineName;
        return this;
    }

    public String getLibFilename() {
        return libFilename;
    }

    public DecoderEngineModel setLibFilename(String libFilename) {
        this.libFilename = libFilename;
        return this;
    }

    public String getCompilerOptions() {
        return compilerOptions;
    }

    public DecoderEngineModel setCompilerOptions(String compilerOptions) {
        this.compilerOptions = compilerOptions;
        return this;
    }

    public String getExecScriptFilename() {
        return execScriptFilename;
    }

    public DecoderEngineModel setExecScriptFilename(String execScriptFilename) {
        this.execScriptFilename = execScriptFilename;
        return this;
    }

    @Override
    public String toString() {
        return getEngineName();
    }
}
