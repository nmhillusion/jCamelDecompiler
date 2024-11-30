package tech.nmhillusion.jCamelDecoderApp.gui;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-29
 */
public class CustomFileView extends FileView {

    private static ImageIcon FOLDER_ICON;

    @Override
    public Icon getIcon(File f) {
        if (null != FOLDER_ICON) {
            return FOLDER_ICON;
        }

        try (final InputStream folderIcon = getClass().getClassLoader().getResourceAsStream("icon/folder.png")) {
            if (null == folderIcon) {
                throw new FileNotFoundException("File not found: icon/folder.png");
            }

            final ImageIcon imageIcon = new ImageIcon(folderIcon.readAllBytes());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

            FOLDER_ICON = imageIcon;

            return FOLDER_ICON;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
