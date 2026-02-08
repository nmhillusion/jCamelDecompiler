package tech.nmhillusion.jCamelDecompilerApp.actionable;

import java.lang.reflect.InvocationTargetException;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-22
 */
public interface ProgressStatusUpdatable {
    void onUpdateProgressValue(int newValue) throws InterruptedException, InvocationTargetException;
}
