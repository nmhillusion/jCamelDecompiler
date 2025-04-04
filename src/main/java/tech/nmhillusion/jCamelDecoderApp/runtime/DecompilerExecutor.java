package tech.nmhillusion.jCamelDecoderApp.runtime;

import tech.nmhillusion.jCamelDecoderApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecoderApp.helper.PathHelper;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
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
public class DecompilerExecutor {
    private static final Map<String, DecompilerExecutor> executorFactory = new TreeMap<>();
    private final DecoderEngineModel decoderEngineModel;
    private final String decompilerCmd;

    public DecompilerExecutor(DecoderEngineModel decoderEngineModel) {
        this.decoderEngineModel = decoderEngineModel;

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
    }

    protected String[] getCmdArrayOfDecoder(DecompileFileModel decompileFileModel) {
        return new String[]{
                "cmd.exe", "/c", String.valueOf(getExecutedScriptPath()),
                getDecompilerCmd(),
                String.valueOf(decompileFileModel.getClassFilePath()),
                String.valueOf(decompileFileModel.getOutputFilePath())
        };
    }

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
        return PathHelper.getPathOfResource(
                "scripts/{execScriptFilename}"
                        .replace("{execScriptFilename}", decoderEngineModel.getExecScriptFilename())
        );
    }

    public DecoderEngineModel getDecoderEngineModel() {
        return decoderEngineModel;
    }
}
