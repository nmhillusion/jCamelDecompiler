package tech.nmhillusion.jCamelDecoderApp.helper;

import javax.swing.*;
import java.awt.*;

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
}
