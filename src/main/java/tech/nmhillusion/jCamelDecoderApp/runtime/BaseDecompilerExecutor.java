package tech.nmhillusion.jCamelDecoderApp.runtime;

import tech.nmhillusion.jCamelDecoderApp.helper.PathHelper;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.model.DecompileFileModel;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.ChainMap;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-03-15
 */
public class BaseDecompilerExecutor {
    private final DecoderEngineModel decoderEngineModel;
    private final String decompilerCmd;
    private final Path executedScriptPath;

    public BaseDecompilerExecutor(DecoderEngineModel decoderEngineModel) {
        this.decoderEngineModel = decoderEngineModel;
        final Path currentPath = PathHelper.getCurrentPath();

        this.executedScriptPath = Path.of(String.valueOf(currentPath), "scripts", "base_decompile.bat");

        {
            final String compilerOptions = decoderEngineModel.getCompilerOptions();

            final String execFile = decoderEngineModel.getIsRelativeJarFile() ?
                    StringUtil.trimWithNull(
                            Path.of(String.valueOf(currentPath), decoderEngineModel.getDecompilerCmdPath())
                    )
                    : decoderEngineModel.getDecompilerCmdPath();

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

    public int execScriptFile(DecompileFileModel decompileFileModel) throws IOException, InterruptedException {
        final String[] cmdArray = {
                "cmd.exe", "/c", String.valueOf(executedScriptPath),
                decompilerCmd,
                String.valueOf(decompileFileModel.getClassFilePath()),
                String.valueOf(decompileFileModel.getOutputFilePath())
        };

        LogHelper.getLogger(this).info("Command executing: {}",
                Stream.of(cmdArray)
                        .map(it -> it.replace("\\", "/"))
                        .collect(Collectors.joining(" "))
        );

        final Process process_ = Runtime.getRuntime()
                .exec(cmdArray);

        final int exitCode = process_.waitFor();

        getLogger(this).info("Exit code: {}", exitCode);

        return exitCode;
    }
}
