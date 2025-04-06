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
            PathHelper.getPathOfResource(FilenameConstant.FOLDER__LIBRARY.getFilename())
            , true
    ),
    DECOMPILERS_CONFIG_PATH(
            Path.of(String.valueOf(LIBRARY_PATH.getAbsolutePath())
                    , FilenameConstant.FILE__DECOMPILERS_CONFIG.getFilename()
            )
            , true
    ),
    /// ///////////////
    EXECUTION_STATE_PATH(
            PathHelper.getPathOfResource(FilenameConstant.FOLDER__STATE.getFilename()
                    , FilenameConstant.FILE__EXECUTION_STATE.getFilename()
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
