package tech.nmhillusion.jCamelDecoderApp.gui.frame;

import tech.nmhillusion.jCamelDecoderApp.gui.CustomFileView;

import javax.swing.*;
import java.awt.*;

/**
 * created by: chubb
 * <p>
 * created date: 2024-11-30
 */
public class MainFrame extends JRootPane {

    public MainFrame() {
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

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


        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(createInputDecodeFolder(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(createDecoderSelectionPanel(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(createFilterPanel(), gbc);
        }

        {
            gbc.gridx = 2;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(createDecodeButton(), gbc);
        }

        this.setContentPane(panel);
    }

    private JButton createDecodeButton() {
        final JButton decodeButton = new JButton("Decode");
        final Dimension decodeButtonSize = new Dimension(100, 30);

        decodeButton.setBackground(Color.CYAN);
        decodeButton.setSize(decodeButtonSize);
        decodeButton.setPreferredSize(decodeButtonSize);
        decodeButton.addActionListener(e -> {
//            String text = inputField.getText();
//            System.out.println("Decoded text: " + text);
        });

        return decodeButton;
    }

    private JPanel createInputDecodeFolder() {
        final JPanel panel_ = new JPanel();
        panel_.setLayout(new FlowLayout());
        panel_.add(new JLabel("Folder to decode"));

        final JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 20));
        inputField.setSize(new Dimension(200, 20));
        inputField.setMaximumSize(new Dimension(200, 20));
        inputField.setEnabled(false);
        panel_.add(inputField);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            fileChooser.showOpenDialog(null);
        });
        panel_.add(browseButton);

        return panel_;
    }

    private JPanel createDecoderSelectionPanel() {
        final JPanel panel_ = new JPanel();
        panel_.setLayout(new FlowLayout());

        panel_.add(new JLabel("Decoder:"));

        final JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("JavaP Decoder");
        comboBox.addItem("JD");
        comboBox.addItem("Proryon Decoder");
        panel_.add(comboBox);

        return panel_;
    }

    private JPanel createFilterPanel() {
        final JPanel panel_ = new JPanel();
        panel_.setLayout(new FlowLayout());

        final JCheckBox isFilterCheckBox = new JCheckBox("Is Filtered by File List");
        panel_.add(isFilterCheckBox);

        panel_.add(new JLabel("Filter:"));

        final JTextField filterField = new JTextField();
        filterField.setPreferredSize(new Dimension(200, 20));
        filterField.setSize(new Dimension(200, 20));
        filterField.setMaximumSize(new Dimension(200, 20));
        panel_.add(filterField);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            /// Mark: Open file dialog
        });
        panel_.add(browseButton);

        return panel_;
    }
}
