package tech.nmhillusion.jCamelDecompilerApp.helper;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-02-28
 */
public abstract class ThreadHelper {
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
