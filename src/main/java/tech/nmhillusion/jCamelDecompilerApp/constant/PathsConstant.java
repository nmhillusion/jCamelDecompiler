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
            PathHelper.getPathOfResource(CommonNameConstant.FOLDER__LIBRARY.getEName())
            , true
    ),
    DECOMPILERS_CONFIG_PATH(
            Path.of(String.valueOf(LIBRARY_PATH.getAbsolutePath())
                    , CommonNameConstant.FILE__DECOMPILERS_CONFIG.getEName()
            )
            , true
    ),
    /// ///////////////
    EXECUTION_STATE_PATH(
            PathHelper.getPathOfResource(CommonNameConstant.FOLDER__STATE.getEName()
                    , CommonNameConstant.FILE__EXECUTION_STATE.getEName()
            )
            , false
    );

    private final Path absolutePath;
    private final boolean required;

    PathsConstant(Path absolutePath, boolean required) {
        this.absolutePath = absolutePath;
        this.required = required;
    }

    public Path getAbsolutePath() {
        return absolutePath;
    }

    public boolean getRequired() {
        return required;
    }
}
