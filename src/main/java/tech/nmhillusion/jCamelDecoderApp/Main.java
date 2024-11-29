package tech.nmhillusion.jCamelDecoderApp;

import tech.nmhillusion.jCamelDecoderApp.gui.CustomFileView;
import tech.nmhillusion.n2mix.helper.log.LogHelper;

import javax.swing.*;
import java.awt.*;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-26
 */

public class Main {
    public static void main(String[] args) {
        LogHelper.getLogger(Main.class).info("Starting jCamelDecoderApp");

        setLookAndFeelUI();
        SwingUtilities.invokeLater(Main::testGUI);
    }

    private static void setLookAndFeelUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testGUI() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setTitle("jCamelDecoderApp");

        frame.setContentPane(
                getContentPaneUI()
        );

        frame.pack();
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private static Container getContentPaneUI() {
        final JRootPane jRootPane = new JRootPane();
        jRootPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        final GridBagLayout mgr = new GridBagLayout();
        panel.setLayout(mgr);

        final GridBagConstraints gbc = new GridBagConstraints();
        int rowIdx = 0;

        {
            final JLabel titleLabel = new JLabel("jCamelDecoderApp");
            titleLabel.setFont(new Font("Calibri", Font.PLAIN, 24));

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.CENTER;
            panel.add(titleLabel, gbc);
        }

        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        {
            centerPanel.add(new JLabel("Enter the text to decode"));

            final JTextField inputField = new JTextField();
            inputField.setPreferredSize(new Dimension(200, 20));
            inputField.setSize(new Dimension(200, 20));
            inputField.setMaximumSize(new Dimension(200, 20));
            inputField.setText("Hello World");
            inputField.setEnabled(false);
            centerPanel.add(inputField);

            final JButton browseButton = new JButton("Browse");
            browseButton.addActionListener(e -> {
                //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
                // to see how IntelliJ IDEA suggests fixing it.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileView(new CustomFileView());
                fileChooser.showOpenDialog(null);
            });
            centerPanel.add(browseButton);
        }
        gbc.gridx = 0;
        gbc.gridy = rowIdx++;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(centerPanel, gbc);

        {
            final JPanel decoderPanel = new JPanel();
            decoderPanel.setLayout(new FlowLayout());

            decoderPanel.add(new JLabel("Decoder:"));

            final JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("JavaP Decoder");
            comboBox.addItem("JD");
            comboBox.addItem("Proryon Decoder");
            decoderPanel.add(comboBox);

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(decoderPanel, gbc);
        }

        {
            final JPanel filterPanel = new JPanel();
            filterPanel.setLayout(new FlowLayout());

            final JCheckBox isFilterCheckBox = new JCheckBox("Is Filtered by File List");
            filterPanel.add(isFilterCheckBox);

            filterPanel.add(new JLabel("Filter:"));

            final JTextField filterField = new JTextField();
            filterField.setPreferredSize(new Dimension(200, 20));
            filterField.setSize(new Dimension(200, 20));
            filterField.setMaximumSize(new Dimension(200, 20));
            filterPanel.add(filterField);

            final JButton browseButton = new JButton("Browse");
            browseButton.addActionListener(e -> {
                /// Mark: Open file dialog
            });
            filterPanel.add(browseButton);

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(filterPanel, gbc);
        }

        final JButton decodeButton = new JButton("Decode");
        final Dimension decodeButtonSize = new Dimension(100, 30);

        decodeButton.setSize(decodeButtonSize);
        decodeButton.setPreferredSize(decodeButtonSize);
        decodeButton.addActionListener(e -> {
//            String text = inputField.getText();
//            System.out.println("Decoded text: " + text);
        });
        gbc.gridx = 2;
        gbc.gridy = rowIdx++;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(decodeButton, gbc);

        jRootPane.setContentPane(panel);
        return jRootPane;
    }
}