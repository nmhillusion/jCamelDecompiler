package tech.nmhillusion.jCamelDecompilerApp.loader;

import tech.nmhillusion.jCamelDecompilerApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecompilerApp.helper.PathHelper;
import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionState;
import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionStateSerializable;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.io.*;
import java.nio.file.Files;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-04-06
 */
public class ExecutionStateLoader {
    private final static ExecutionStateLoader INSTANCE = new ExecutionStateLoader();

    private ExecutionStateLoader() {
    }

    public static ExecutionStateLoader getInstance() {
        return INSTANCE;
    }

    public void saveState(ExecutionState executionState) throws IOException {
        try (final OutputStream fos = Files.newOutputStream(PathHelper.makeSureExistFilePath(
                PathsConstant.EXECUTION_STATE_PATH.getAbsolutePath()
        ));
             final ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(
                    ExecutionStateSerializable.from(executionState)
            );
            oos.flush();
        }
    }

    public ExecutionState loadState() throws IOException, ClassNotFoundException {
        try (final InputStream fis = Files.newInputStream(PathHelper.makeSureExistFilePath(
                PathsConstant.EXECUTION_STATE_PATH.getAbsolutePath()
        ));
             final ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            final ExecutionStateSerializable executionStateSerializable = CastUtil.safeCast(ois.readObject(), ExecutionStateSerializable.class);
            return ExecutionState.from(executionStateSerializable);
        }
    }

}
