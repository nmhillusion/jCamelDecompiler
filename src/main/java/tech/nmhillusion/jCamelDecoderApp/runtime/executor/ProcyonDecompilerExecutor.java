package tech.nmhillusion.jCamelDecoderApp.runtime.executor;

import tech.nmhillusion.jCamelDecoderApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecoderApp.runtime.BaseDecompilerExecutor;

import java.nio.file.Path;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-31
 */
public class ProcyonDecompilerExecutor extends BaseDecompilerExecutor {
    public ProcyonDecompilerExecutor(DecoderEngineModel decoderEngineModel) {
        super(decoderEngineModel);
    }

    @Override
    protected String[] getCmdArrayOfDecoder(DecompileFileModel decompileFileModel) {
        return new String[]{
                "cmd.exe", "/c", String.valueOf(getExecutedScriptPath()),
                getDecompilerCmd(),
                String.valueOf(decompileFileModel.getClassFilePath()),
                String.valueOf(decompileFileModel.getOutputFilePath())
        };
    }

    @Override
    public Path getExecutedScriptPath() {
        return PathsConstant.BASE_DECOMPILE_SCRIPT_PATH.getAbsolutePath();
    }
}
