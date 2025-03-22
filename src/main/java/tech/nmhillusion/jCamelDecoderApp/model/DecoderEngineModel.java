package tech.nmhillusion.jCamelDecoderApp.model;

import tech.nmhillusion.jCamelDecoderApp.constant.DecoderEngineEnum;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-02-12
 */
public class DecoderEngineModel {
    private DecoderEngineEnum engineId;
    private String engineName;
    private boolean isRelativeJarFile;
    private String decompilerCmdPath;
    private String compilerOptions;

    public DecoderEngineEnum getEngineId() {
        return engineId;
    }

    public DecoderEngineModel setEngineId(DecoderEngineEnum engineId) {
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

    public String getDecompilerCmdPath() {
        return decompilerCmdPath;
    }

    public DecoderEngineModel setDecompilerCmdPath(String decompilerCmdPath) {
        this.decompilerCmdPath = decompilerCmdPath;
        return this;
    }

    public String getCompilerOptions() {
        return compilerOptions;
    }

    public DecoderEngineModel setCompilerOptions(String compilerOptions) {
        this.compilerOptions = compilerOptions;
        return this;
    }

    public boolean getIsRelativeJarFile() {
        return isRelativeJarFile;
    }

    public DecoderEngineModel setIsRelativeJarFile(boolean relativeJarFile) {
        isRelativeJarFile = relativeJarFile;
        return this;
    }

    @Override
    public String toString() {
        return engineName;
    }
}
