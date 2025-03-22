package tech.nmhillusion.jCamelDecoderApp.actionable;

import tech.nmhillusion.jCamelDecoderApp.constant.LogType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-03-22
 */
public interface ProgressStatusUpdatable {
    void onUpdateProgressValue(int newValue) throws InterruptedException, InvocationTargetException;

    void onLogMessage(LogType logType, String logMsg) throws InterruptedException, InvocationTargetException;

    void onDone(String notificationContent, Path outputFolder) throws IOException;
}
