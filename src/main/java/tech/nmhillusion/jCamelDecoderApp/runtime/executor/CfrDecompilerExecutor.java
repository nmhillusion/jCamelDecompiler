package tech.nmhillusion.jCamelDecoderApp.runtime.executor;

import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecoderApp.runtime.BaseDecompilerExecutor;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-31
 */
public class CfrDecompilerExecutor extends BaseDecompilerExecutor {

    public CfrDecompilerExecutor(DecoderEngineModel decoderEngineModel) {
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
}
