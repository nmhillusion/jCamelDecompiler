package tech.nmhillusion.jCamelDecoderApp.helper;

import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-15
 */
public abstract class PathHelper {

    public static Path getCurrentPath() {
        return Paths.get("").toAbsolutePath();
    }

    public static Path getPathOfResource(String resourceName) {
        try {
            final String appHomeDir = System.getenv("APP_HOME");
            LogHelper.getLogger(PathHelper.class)
                    .info("App Home = {}", appHomeDir);

            if (StringValidator.isBlank(appHomeDir)) {
                final URI resourceUri = ClassLoader.getSystemResource(resourceName).toURI();
                getLogger(PathHelper.class).info("resourceUri = {}", resourceUri);
                return Paths.get(resourceUri)
                        .toAbsolutePath();
            } else {
                return Paths.get(appHomeDir, resourceName)
                        .toAbsolutePath();
            }
        } catch (URISyntaxException e) {
            getLogger(PathHelper.class).error("Cannot find resource: %s".formatted(resourceName), e);
            throw new RuntimeException("Cannot find resource: %s".formatted(resourceName), e);
        }
    }

}
