package tech.nmhillusion.jCamelDecompilerApp;

import tech.nmhillusion.jCamelDecompilerApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecompilerApp.gui.frame.MainContentPane;
import tech.nmhillusion.jCamelDecompilerApp.helper.PathHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-26
 */

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        getLogger(Main.class).info("Starting jCamelDecompilerApp");

        try {
            setLookAndFeelUI();
            throwIfUnavailableRequiredPaths();
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(
                    null
                    , "Error when init program [%s]: %s".formatted(ex.getClass().getSimpleName(), ex.getMessage())
                    , "Error"
                    , JOptionPane.ERROR_MESSAGE
            );
            exitAppOnError(ex);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                makeGUI();
            } catch (IOException ex) {
                exitAppOnError(ex);
            }
        });
    }

    private static void throwIfUnavailableRequiredPaths() {
        final List<PathsConstant> requiredPaths = Arrays.stream(PathsConstant.values())
                .filter(PathsConstant::getRequired)
                .toList();
        for (PathsConstant requiredPath : requiredPaths) {
            if (Files.notExists(requiredPath.getAbsolutePath())) {
                throw new IllegalStateException(String.format("Required path is not available: %s", requiredPath.getAbsolutePath()));
            }
        }
    }

    private static void setLookAndFeelUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            getLogger(Main.class).error(e);
        }
    }

    private static void makeGUI() throws IOException {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setTitle("jCamelDecompiler");
        frame.setLocationByPlatform(true);
        setIconForApp(frame);

        frame.setContentPane(
                new MainContentPane(frame)
        );

//        frame.pack();
        frame.revalidate();
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private static void setIconForApp(JFrame frame) throws IOException {
        try (final InputStream icStream = Files.newInputStream(PathHelper.getPathOfResource("icon/app-icon.png"))) {
            if (null == icStream) {
                throw new IOException("App icon not found");
            }

            frame.setIconImage(
                    ImageIO.read(icStream)
            );
        }
    }

    private static void exitAppOnError(Throwable ex) {
        getLogger(Main.class).error(ex);
        System.exit(-1);
    }
}