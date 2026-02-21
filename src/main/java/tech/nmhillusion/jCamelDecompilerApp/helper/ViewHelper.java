package tech.nmhillusion.jCamelDecompilerApp.helper;

import tech.nmhillusion.n2mix.util.StringUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-22
 */
public abstract class ViewHelper {
    public static float getLineHeightOfTextArea(JTextPane textArea) {
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

    public static int getLineCount(JTextPane logView) {
        return logView.getText().split("\n")
                .length;
    }

    public static JFrame getFrameAncestor(Component component) {
        if (component == null) {
            return null;
        }
        Component ancestor = component.getParent();
        while (ancestor != null && !(ancestor instanceof JFrame)) {
            ancestor = ancestor.getParent();
        }
        return (JFrame) ancestor;
    }

    public static Image getIconForButton(String resourcePath, int width, int height) throws IOException {
        final BufferedImage rawImage = ImageIO.read(
                Files.newInputStream(
                        PathHelper.getPathOfResource(resourcePath)
                )
        );
        return rawImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static boolean isMacOS() {
        String osName = StringUtil.trimWithNull(System.getProperty("os.name")).toLowerCase();
        return osName.contains("mac") || osName.contains("darwin");
    }
}
