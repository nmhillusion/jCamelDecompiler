package tech.nmhillusion.jCamelDecoderApp;

import tech.nmhillusion.jCamelDecoderApp.gui.frame.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-26
 */

public class Main {
    public static void main(String[] args) {
        getLogger(Main.class).info("Starting jCamelDecoderApp");

        setLookAndFeelUI();
        SwingUtilities.invokeLater(() -> {
            try {
                makeGUI();
            } catch (IOException e) {
                getLogger(Main.class).error(e);
                throw new RuntimeException(e);
            }
        });
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
        frame.setTitle("jCamelDecoderApp");
        setIconForApp(frame);

        frame.setContentPane(
                new MainFrame()
        );

//        frame.pack();
        frame.revalidate();
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private static void setIconForApp(JFrame frame) throws IOException {
        try (final InputStream icStream = ClassLoader.getSystemResourceAsStream("icon/app.png")) {
            if (null == icStream) {
                throw new IOException("App icon not found");
            }

            frame.setIconImage(
                    ImageIO.read(icStream)
            );
        }
    }
}