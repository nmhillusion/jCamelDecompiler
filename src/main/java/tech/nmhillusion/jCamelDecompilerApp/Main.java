package tech.nmhillusion.jCamelDecompilerApp;

import tech.nmhillusion.jCamelDecompilerApp.execution.CliCommandExecution;
import tech.nmhillusion.jCamelDecompilerApp.execution.GuiAppExecution;
import tech.nmhillusion.jCamelDecompilerApp.helper.PathHelper;
import tech.nmhillusion.n2mix.helper.YamlReader;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.helper.log.MixLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-26
 */

public class Main {
    private static final MixLogger logger = LogHelper.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            logger.info("CLI COMMAND MODE");

            CliCommandExecution
                    .executeCLICommand(args);
            System.exit(0);
        } else {
            logger.info("GUI MODE");

            GuiAppExecution.execute();
            System.exit(0);
        }
    }

    public static <T> T getAppInfoProperty(String configKey, Class<T> class2Cast) throws IOException {
        final Path appInfoPath = PathHelper.getPathOfResource("config/app-info.yml");
        try (final InputStream fis = Files.newInputStream(appInfoPath)) {
            return new YamlReader(fis).getProperty(configKey, class2Cast);
        }
    }
}