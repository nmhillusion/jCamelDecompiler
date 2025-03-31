package tech.nmhillusion.jCamelDecoderApp.runtime;

import tech.nmhillusion.jCamelDecoderApp.constant.DecoderEngineEnum;
import tech.nmhillusion.jCamelDecoderApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecoderApp.runtime.executor.CfrDecompilerExecutor;
import tech.nmhillusion.jCamelDecoderApp.runtime.executor.FernFlowerDecompilerExecutor;
import tech.nmhillusion.jCamelDecoderApp.runtime.executor.ProcyonDecompilerExecutor;
import tech.nmhillusion.n2mix.type.ChainMap;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-15
 */
public abstract class BaseDecompilerExecutor {
    private static final Map<DecoderEngineEnum, BaseDecompilerExecutor> executorFactory = new TreeMap<>();
    private final DecoderEngineModel decoderEngineModel;
    private final String decompilerCmd;
    private final Path executedScriptPath;

    public BaseDecompilerExecutor(DecoderEngineModel decoderEngineModel) {
        this.decoderEngineModel = decoderEngineModel;
        this.executedScriptPath = PathsConstant.BASE_DECOMPILE_SCRIPT_PATH.getAbsolutePath();

        final String libraryPath = StringUtil.trimWithNull(PathsConstant.LIBRARY_PATH.getAbsolutePath());
        {
            final String compilerOptions = decoderEngineModel.getCompilerOptions();

            final String execFile = StringUtil.trimWithNull(
                    Path.of(libraryPath, decoderEngineModel.getLibFilename())
            );

            this.decompilerCmd = StringValidator.isBlank(compilerOptions) ?
                    execFile
                    : String.format("%s %s", execFile, compilerOptions);
        }

        getLogger(this).info("Execution properties: {}", new ChainMap<String, String>()
                .chainPut("executedPath", String.valueOf(executedScriptPath)
                        .replace("\\", "/")
                )
                .chainPut("decompilerCmd", decompilerCmd)
        );
    }

    public static BaseDecompilerExecutor getInstance(DecoderEngineModel decoderEngineModel) {
        return executorFactory.computeIfAbsent(decoderEngineModel.getEngineId(), mId -> switch (mId) {
            case CFR -> new CfrDecompilerExecutor(decoderEngineModel);
            case Procyon -> new ProcyonDecompilerExecutor(decoderEngineModel);
            case FERN_FLOWER -> new FernFlowerDecompilerExecutor(decoderEngineModel);
            default ->
                    throw new IllegalArgumentException("Cannot find DecoderEngine executor with id: %s".formatted(mId));
        });
    }

    protected abstract String[] getCmdArrayOfDecoder(DecompileFileModel decompileFileModel);

    public int execScriptFile(DecompileFileModel decompileFileModel, ThrowableVoidFunction<String> logFunction) throws Throwable {
        final String[] cmdArray = getCmdArrayOfDecoder(decompileFileModel);

        logFunction.throwableVoidApply("Command executing: %s".formatted(
                        Stream.of(cmdArray)
                                .map(it -> it.replace("\\", "/"))
                                .collect(Collectors.joining(" "))
                )
        );

        final Process process_ = Runtime.getRuntime()
                .exec(cmdArray);

        final int exitCode = process_.waitFor();

        getLogger(this).info("Exit code: {}", exitCode);

        return exitCode;
    }

    public String getDecompilerCmd() {
        return decompilerCmd;
    }

    public Path getExecutedScriptPath() {
        return executedScriptPath;
    }

    public DecoderEngineModel getDecoderEngineModel() {
        return decoderEngineModel;
    }
}
