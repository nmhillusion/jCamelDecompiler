package tech.nmhillusion.jCamelDecoderApp;

import tech.nmhillusion.jCamelDecoderApp.gui.frame.MainFrame;

import javax.swing.*;

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
        SwingUtilities.invokeLater(Main::testGUI);
    }

    private static void setLookAndFeelUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            getLogger(Main.class).error(e);
        }
    }

    private static void testGUI() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setTitle("jCamelDecoderApp");

        frame.setContentPane(
                new MainFrame()
        );

//        frame.pack();
        frame.revalidate();
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }
}