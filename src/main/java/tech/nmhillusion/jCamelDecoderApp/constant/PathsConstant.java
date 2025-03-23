package tech.nmhillusion.jCamelDecoderApp.constant;/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-23
 */

import tech.nmhillusion.jCamelDecoderApp.helper.PathHelper;

import java.nio.file.Path;

/**
 * date: 2025-03-23
 * <p>
 * created-by: nmhillusion
 */
public enum PathsConstant {
    LIBRARY_PATH(
            PathHelper.getPathOfResource("decoder")
    ),
    DECOMPILER_CONFIG_PATH(
            Path.of(String.valueOf(LIBRARY_PATH.getAbsolutePath()), "decompiler.config.yml")
    ),
    BASE_DECOMPILE_SCRIPT_PATH(
            PathHelper.getPathOfResource("scripts/base_decompile.bat")
    );

    private final Path absolutePath;

    PathsConstant(Path absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Path getAbsolutePath() {
        return absolutePath;
    }
}
