package tech.nmhillusion.jCamelDecoderApp.gui;

import tech.nmhillusion.jCamelDecoderApp.helper.PathHelper;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-29
 */
public class CustomFileView extends FileView {

    private static ImageIcon FOLDER_ICON;
    private static ImageIcon FILE_ICON;

    @Override
    public Icon getIcon(File f) {
        if (f.isDirectory()) {
            return getFolderIcon();
        } else {
            return getFileIcon();
        }
    }

    private Icon getFileIcon() {
        if (null != FILE_ICON) {
            return FILE_ICON;
        }

        try (final InputStream iconStream = Files.newInputStream(PathHelper.getPathOfResource("icon/file.png"))) {
            if (null == iconStream) {
                throw new FileNotFoundException("File not found: icon/file.png");
            }

            final ImageIcon imageIcon = new ImageIcon(iconStream.readAllBytes());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

            FILE_ICON = imageIcon;

            return FILE_ICON;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Icon getFolderIcon() {
        if (null != FOLDER_ICON) {
            return FOLDER_ICON;
        }

        try (final InputStream iconStream = Files.newInputStream(PathHelper.getPathOfResource("icon/folder.png"))) {
            if (null == iconStream) {
                throw new FileNotFoundException("File not found: icon/folder.png");
            }

            final ImageIcon imageIcon = new ImageIcon(iconStream.readAllBytes());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

            FOLDER_ICON = imageIcon;

            return FOLDER_ICON;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
