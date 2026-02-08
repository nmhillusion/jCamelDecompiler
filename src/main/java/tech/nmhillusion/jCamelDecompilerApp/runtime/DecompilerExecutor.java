package tech.nmhillusion.jCamelDecompilerApp.runtime;

import tech.nmhillusion.jCamelDecompilerApp.constant.CommonNameConstant;
import tech.nmhillusion.jCamelDecompilerApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecompilerApp.helper.PathHelper;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompileFileModel;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompilerEngineModel;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
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
    private final DecompilerEngineModel decompilerEngineModel;
    private final String decompilerCmd;

    public DecompilerExecutor(DecompilerEngineModel decompilerEngineModel) {
        this.decompilerEngineModel = decompilerEngineModel;

        final String libraryPath = StringUtil.trimWithNull(PathsConstant.LIBRARY_PATH.getAbsolutePath());
        {
            final String compilerOptions = decompilerEngineModel.getCompilerOptions();

            final String execFile = StringUtil.trimWithNull(
                    Path.of(libraryPath, decompilerEngineModel.getLibFilename())
            );

            this.decompilerCmd = StringValidator.isBlank(compilerOptions) ?
                    execFile
                    : String.format("%s %s", execFile, compilerOptions);
        }
    }

    protected String[] getCmdArrayOfDecompiler(DecompileFileModel decompileFileModel) {
        return new String[]{
                "cmd.exe", "/c", String.valueOf(getExecutedScriptPath()),
                getDecompilerCmd(),
                String.valueOf(decompileFileModel.getClassFilePath()),
                String.valueOf(decompileFileModel.getOutputFilePath())
        };
    }

    public int execScriptFile(DecompileFileModel decompileFileModel, ThrowableVoidFunction<String> logFunction) throws Throwable {
        final String[] cmdArray = getCmdArrayOfDecompiler(decompileFileModel);

        logFunction.throwableVoidApply("Command executing: %s".formatted(
                        Stream.of(cmdArray)
                                .map(it -> it.replace("\\", "/"))
                                .collect(Collectors.joining(" "))
                )
        );

        final ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        processBuilder.redirectErrorStream(true);
        final Process process_ = processBuilder.start();

        try (final BufferedReader bufferedReader = process_.inputReader(StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logFunction.throwableVoidApply(line);
            }
        }

        final int exitCode = process_.waitFor();

        getLogger(this).info("Exit code: {}", exitCode);

        return exitCode;
    }

    public String getDecompilerCmd() {
        return decompilerCmd;
    }

    public Path getExecutedScriptPath() {
        return PathHelper.getPathOfResource(
                CommonNameConstant.FOLDER__SCRIPTS.getEName()
                , decompilerEngineModel.getExecScriptFilename()
        );
    }

    public DecompilerEngineModel getDecompilerEngineModel() {
        return decompilerEngineModel;
    }
}
