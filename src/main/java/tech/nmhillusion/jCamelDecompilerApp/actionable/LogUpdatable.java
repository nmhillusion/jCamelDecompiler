package tech.nmhillusion.jCamelDecompilerApp.actionable;

import tech.nmhillusion.jCamelDecompilerApp.constant.LogType;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompileResultModel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-02-08
 */
public interface LogUpdatable {
    void onLogMessage(LogType logType, String logMsg) throws InterruptedException, InvocationTargetException;

    void onDone(String notificationContent, DecompileResultModel decompileResult, long startDecompileTime) throws IOException;
}
