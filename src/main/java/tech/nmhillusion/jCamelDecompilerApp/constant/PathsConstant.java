package tech.nmhillusion.jCamelDecompilerApp.constant;/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-23
 */

import tech.nmhillusion.jCamelDecompilerApp.helper.PathHelper;

import java.nio.file.Path;

/**
 * date: 2025-03-23
 * <p>
 * created-by: nmhillusion
 */
public enum PathsConstant {
    LIBRARY_PATH(
            PathHelper.getPathOfResource("decompiler")
    ),
    DECOMPILERS_CONFIG_PATH(
            Path.of(String.valueOf(LIBRARY_PATH.getAbsolutePath()), "decompilers.config.yml")
    );

    private final Path absolutePath;

    PathsConstant(Path absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Path getAbsolutePath() {
        return absolutePath;
    }
}
