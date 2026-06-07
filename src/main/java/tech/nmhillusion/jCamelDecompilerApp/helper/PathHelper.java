package tech.nmhillusion.jCamelDecompilerApp.helper;

import tech.nmhillusion.jCamelDecompilerApp.constant.CommonNameConstant;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays; // Keep this import as it's now used for Arrays.copyOfRange

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-15
 */
public abstract class PathHelper {

    public static Path getPathOfResource(String... resourceNames) {
        Path basePath = getPathOfAppHome();
        Path relativePath;

        if (resourceNames == null || resourceNames.length == 0) {
            relativePath = Paths.get(""); // Represents an empty path
        } else if (resourceNames.length == 1) {
            relativePath = Paths.get(resourceNames[0]);
        } else {
            // Paths.get(String first, String... more) to join multiple segments
            relativePath = Paths.get(resourceNames[0], Arrays.copyOfRange(resourceNames, 1, resourceNames.length));
        }
        return basePath.resolve(relativePath).toAbsolutePath();
    }

    public static Path makeSureExistFilePath(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            return filePath;
        }

        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);
        return filePath;
    }

    public static Path getPathOfAppHome() {
        final String appHomeDir = System.getenv(CommonNameConstant.ENV__APP_HOME.getEName());

        getLogger(PathHelper.class)
                .info("App Home (from ENV) = {}", appHomeDir);

        if (StringValidator.isBlank(appHomeDir)) {
            getLogger(PathHelper.class).warn("Environment variable {} not set. Attempting to infer App Home.", CommonNameConstant.ENV__APP_HOME.getEName());

            Path currentWorkingDir = Paths.get(".").toAbsolutePath().normalize();
            getLogger(PathHelper.class).info("Current Working Directory = {}", currentWorkingDir);

            // Check if the current working directory's name is "bin" (case-insensitive)
            // If so, assume the parent directory is the application home.
            if (currentWorkingDir.getFileName() != null && "bin".equalsIgnoreCase(currentWorkingDir.getFileName().toString())) {
                Path parentDir = currentWorkingDir.getParent();
                if (parentDir != null) {
                    getLogger(PathHelper.class).info("Current directory is 'bin'. Setting App Home to parent directory: {}", parentDir);
                    return parentDir;
                }
            }

            // If not in a 'bin' directory or parent is null, use the current working directory as App Home.
            getLogger(PathHelper.class).info("Setting App Home to current working directory: {}", currentWorkingDir);
            return currentWorkingDir;
        } else {
            return Paths.get(appHomeDir)
                    .toAbsolutePath();
        }
    }

}