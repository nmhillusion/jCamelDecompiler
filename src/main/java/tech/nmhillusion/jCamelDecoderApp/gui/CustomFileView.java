package tech.nmhillusion.jCamelDecoderApp.gui;

import javax.swing.*;
import javax.swing.filechooser.FileView;
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

    @Override
    public Icon getIcon(File f) {
        try (final InputStream folderIcon = getClass().getClassLoader().getResourceAsStream("icon/folder.png")) {
            if (null == folderIcon) {
                throw new FileNotFoundException("File not found: icon/folder.png");
            }

            return new ImageIcon(folderIcon.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
