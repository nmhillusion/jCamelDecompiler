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
public class FernFlowerDecompilerExecutor extends BaseDecompilerExecutor {

    public FernFlowerDecompilerExecutor(DecoderEngineModel decoderEngineModel) {
        super(decoderEngineModel);
    }

    @Override
    protected String[] getCmdArrayOfDecoder(DecompileFileModel decompileFileModel) {
        return new String[]{
                "cmd.exe", "/c", String.valueOf(getExecutedScriptPath()),
                getDecompilerCmd(),
                String.valueOf(decompileFileModel.getClassFilePath()),
                String.valueOf(decompileFileModel.getOutputFilePath().getParent())
        };
    }

    @Override
    public Path getExecutedScriptPath() {
        return PathsConstant.FERNFLOWER_DECOMPILE_SCRIPT_PATH.getAbsolutePath();
    }
}
