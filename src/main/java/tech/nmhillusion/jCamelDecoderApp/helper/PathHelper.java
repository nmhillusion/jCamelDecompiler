package tech.nmhillusion.jCamelDecoderApp.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-03-15
 */
public abstract class PathHelper {

    public static Path getCurrentPath() {
        return Paths.get("").toAbsolutePath();
    }

}
