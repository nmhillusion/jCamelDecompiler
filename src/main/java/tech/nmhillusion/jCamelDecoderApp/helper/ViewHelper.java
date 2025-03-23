package tech.nmhillusion.jCamelDecoderApp.helper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-03-22
 */
public abstract class ViewHelper {
    public static float getLineHeightOfTextArea(JTextArea textArea) {
        final FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());

        return fontMetrics.getHeight();
    }

    public static void openFileExplorer(Path folderPath) throws IOException {
        File directory = folderPath.toFile();
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(directory);
        } else {
            throw new UnsupportedOperationException("Desktop is not supported on this platform.");
        }
    }
}
