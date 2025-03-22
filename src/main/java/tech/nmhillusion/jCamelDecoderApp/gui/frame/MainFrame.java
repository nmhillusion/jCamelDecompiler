package tech.nmhillusion.jCamelDecoderApp.gui.frame;

import tech.nmhillusion.jCamelDecoderApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecoderApp.constant.DecoderEngineEnum;
import tech.nmhillusion.jCamelDecoderApp.constant.LogType;
import tech.nmhillusion.jCamelDecoderApp.factory.DecoderEngineFactory;
import tech.nmhillusion.jCamelDecoderApp.gui.CustomFileView;
import tech.nmhillusion.jCamelDecoderApp.helper.ViewHelper;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.jCamelDecoderApp.state.ExecutionState;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.ChainMap;
import tech.nmhillusion.n2mix.util.StringUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2024-11-30
 */
public class MainFrame extends JRootPane {
    private final ExecutionState executionState = new ExecutionState();
    private final Executor DECOMPILE_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final DecoderEngineFactory decoderEngineFactory = DecoderEngineFactory.getInstance();
    private final AtomicReference<ProgressStatusUpdatable> progressStatusUpdatableHandlerRef = new AtomicReference<>();

    public MainFrame() {
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        final GridBagLayout mgr = new GridBagLayout();
        mgr.preferredLayoutSize(this);
        panel.setLayout(mgr);

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        int rowIdx = 0;

        {
            final JLabel titleLabel = new JLabel("jCamelDecoderApp");
            titleLabel.setFont(new Font("Calibri", Font.PLAIN, 25));
            titleLabel.setBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 5, 0
                            , Color.GREEN
                    )
            );

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.CENTER;
            panel.add(titleLabel, gbc);
        }


        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 0.0;
            gbc.weightx = 1.0;
            panel.add(createInputDecodeFolder(), gbc);

            gbc.gridy = rowIdx++;
            panel.add(createOutputDecodeFolder(), gbc);
        }

        {
            createDecoderSelectionPanel(panel, rowIdx++);
        }

        {
            createFilterPanel(panel, rowIdx++);
        }

        {
            final JSeparator seperator_ = new JSeparator(JSeparator.HORIZONTAL);

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(8, 0, 8, 0);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(seperator_, gbc);
        }

        {
            gbc.gridx = 2;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = GridBagConstraints.RELATIVE;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.FIRST_LINE_END;
            panel.add(createDecodeButton(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(8, 0, 8, 0);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.PAGE_END;
            panel.add(createStatusProgressBar(), gbc);
        }
        this.setContentPane(panel);
        panel.updateUI();
        panel.repaint();
    }

    private static void openFileExplorer(Path folderPath) throws IOException {
        File directory = folderPath.toFile();
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(directory);
        } else {
            throw new UnsupportedOperationException("Desktop is not supported on this platform.");
        }
    }

    private JPanel createStatusProgressBar() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();

        final JProgressBar progressBar = new JProgressBar(0, 100);
        {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;

            panel.add(
                    progressBar
                    , gbc
            );
        }

        final JTextArea logView = new JTextArea();
        final JScrollPane logScrollPane = new JScrollPane(logView
                , ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        {
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;

            logView.setAutoscrolls(true);
            logView.setLineWrap(false);

            panel.add(
                    logScrollPane
                    , gbc
            );
        }

        progressStatusUpdatableHandlerRef.set(new ProgressStatusUpdatable() {
            @Override
            public void onUpdateProgressValue(int newPercentValue) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(newPercentValue);
                });
            }

            public void removeFirstLogLine() {
                try {
                    int endOfFirstLine = logView.getLineEndOffset(0);
                    logView.replaceRange("", 0, endOfFirstLine);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onLogMessage(LogType logType, String logMsg) {
                SwingUtilities.invokeLater(() -> {
                    final JScrollBar verticalScrollBar = logScrollPane.getVerticalScrollBar();
                    if (verticalScrollBar.isVisible()) {
                        final double logViewHeight = logScrollPane.getBounds().getHeight();
                        final float lineHeight = ViewHelper.getLineHeightOfTextArea(logView);
                        final int MIN_LOG_ROWS = Math.ceilDiv(Math.round((float) logViewHeight), Math.round(lineHeight)) * 2;
                        LogHelper.getLogger(this).info("current height of scroll log view: "
                                + new ChainMap<>()
                                .chainPut("logViewHeight", logViewHeight)
                                .chainPut("lineHeight", lineHeight)
                                .chainPut("MIN_LOG_ROWS", MIN_LOG_ROWS)
                        );

                        int lineCount = logView.getLineCount();
                        while (MIN_LOG_ROWS < lineCount) {
                            removeFirstLogLine();
                            lineCount = logView.getLineCount();
                        }
                    }

                    logView.append(
                            "{timestamp} - [{logType}] - {logMessage}\n"
                                    .replace("{timestamp}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                                    .replace("{logType}", logType.getValue().toUpperCase())
                                    .replace("{logMessage}", logMsg)
                    );
                });
            }

            @Override
            public void onDone(String notificationContent, Path outputFolder) throws IOException {
                final int resultDialog = JOptionPane.showConfirmDialog(panel
                        , notificationContent
                        , "Decompile"
                        , JOptionPane.YES_NO_OPTION);

                getLogger(this).info("result dialog = {}", resultDialog);

                if (JOptionPane.YES_OPTION == resultDialog) {
                    openFileExplorer(outputFolder);
                }
            }
        });
        panel.setBackground(Color.decode("#dddddd"));

        return panel;
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
            getLogger(this).info("Decode for: {}", executionState);

            DECOMPILE_EXECUTOR.execute(() -> {
                try {
                    var outputFolder = new DecoderEngine(executionState)
                            .execute(
                                    progressStatusUpdatableHandlerRef
                            );

                    doLogMessageUI(LogType.INFO
                            , "Done decompilation to folder {outputFolder}".replace("{outputFolder}",
                                    String.valueOf(outputFolder)
                                            .replace("\\", "/")
                            )
                    );

                    onDoneDecompilation(outputFolder);
                } catch (IOException | InterruptedException | InvocationTargetException ex) {
                    try {
                        doLogMessageUI(LogType.ERROR, "Error when decompile [%s]: %s".formatted(ex.getClass().getSimpleName(), ex.getMessage()));
                    } catch (InterruptedException | InvocationTargetException ignored) {
                    }
                    throw new RuntimeException(ex);
                }
            });
        });

        return decodeButton;
    }

    private void onDoneDecompilation(Path outputFolder) throws IOException {
        final ProgressStatusUpdatable handler = progressStatusUpdatableHandlerRef.get();

        if (null != handler) {
            handler.onDone(
                    "Decompiled done. Open decompiled folder?"
                    , outputFolder
            );
        }
    }

    private void doLogMessageUI(LogType logType, String logMessage) throws InterruptedException, InvocationTargetException {
        final ProgressStatusUpdatable handler = progressStatusUpdatableHandlerRef.get();

        if (null != handler) {
            handler.onLogMessage(logType, logMessage);
        }
    }

    private JPanel createInputDecodeFolder() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final int rowIdx = 0;

        final GridBagConstraints gbc = new GridBagConstraints();
        {
            gbc.gridy = rowIdx;
            gbc.gridheight = 1;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.LINE_START;
        }

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        panel.add(new JLabel("Folder to decode: "), gbc);

        final JTextField inputField = new JTextField();
//        inputField.setPreferredSize(new Dimension(200, 20));
        inputField.setSize(new Dimension(200, 20));
        inputField.setMinimumSize(new Dimension(200, 20));
        inputField.setEnabled(false);

        {
            /// TODO: 2025-03-15 TESTING
            final String classTestPath = "C:\\Users\\nmhil\\OneDrive\\Desktop\\tmp\\test-decoder\\classes";
            inputField.setText(
                    classTestPath
            );
            executionState.setDecodeFolderPath(
                    Paths.get(classTestPath)
            );
        }

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panel.add(inputField, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            final int resultCode = fileChooser.showOpenDialog(null);

            if (JFileChooser.APPROVE_OPTION == resultCode) {
                executionState.setDecodeFolderPath(
                        fileChooser.getSelectedFile().toPath()
                );
            }

            inputField.setText(
                    String.valueOf(executionState.getDecodeFolderPath())
            );
        });

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        return panel;
    }

    private JPanel createOutputDecodeFolder() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final int rowIdx = 0;

        final GridBagConstraints gbc = new GridBagConstraints();
        {
            gbc.gridy = rowIdx;
            gbc.gridheight = 1;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.LINE_START;
        }

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        panel.add(new JLabel("Output folder: "), gbc);

        final JTextField inputField = new JTextField();
        inputField.setSize(new Dimension(200, 20));
        inputField.setMinimumSize(new Dimension(200, 20));
        inputField.setEnabled(false);

        {
            /// TODO: 2025-03-15 TESTING
            final String outJavaTestPath = "C:\\Users\\nmhil\\OneDrive\\Desktop\\tmp\\test-decoder\\outJava";
            inputField.setText(
                    outJavaTestPath
            );
            executionState.setOutputFolder(
                    Paths.get(outJavaTestPath)
            );
        }

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panel.add(inputField, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            final int resultCode = fileChooser.showOpenDialog(null);

            if (JFileChooser.APPROVE_OPTION == resultCode) {
                executionState.setOutputFolder(
                        fileChooser.getSelectedFile().toPath()
                );
            }

            inputField.setText(
                    String.valueOf(executionState.getOutputFolder())
            );
        });

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        return panel;
    }

    private void createDecoderSelectionPanel(JPanel panel, int rowIdx) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = rowIdx;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0;
        panel.add(new JLabel("Decoder:"), gbc);

        final JComboBox<DecoderEngineModel> comboBox = new JComboBox<>();

        boolean inited = false;
        for (final DecoderEngineEnum decoderEngineEnum : DecoderEngineEnum.values()) {
            comboBox.addItem(
                    decoderEngineFactory.getEngine(
                            decoderEngineEnum
                    )
            );

            if (!inited) {
                inited = true;
                comboBox.setSelectedIndex(0);
                executionState.setDecoderEngineType(DecoderEngineEnum.Procyon);
            }
        }

        comboBox.addItemListener(selectedItemEvent -> {
            if (ItemEvent.SELECTED == selectedItemEvent.getStateChange()) {
                final Object selectedItemRaw = comboBox.getSelectedItem();
                if (selectedItemRaw instanceof DecoderEngineModel decoderEngineModel) {
                    final DecoderEngineEnum selectedDecodeEngineId = decoderEngineModel.getEngineId();
                    getLogger(this).info("Selected item event: " + selectedDecodeEngineId);

                    executionState.setDecoderEngineType(
                            selectedDecodeEngineId
                    );
                } else {
                    throw new IllegalArgumentException("Selected item is not a valid DecoderEngine");
                }
            }
        });

        gbc.gridx = 1;
        panel.add(comboBox, gbc);
    }

    private void createFilterPanel(JPanel panel, int rowIdx) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = rowIdx;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_START;

        final JCheckBox isFilterCheckBox = new JCheckBox("Is Filtered by File List");
        final JTextField filterField = new JTextField();
        final JButton browseButton = new JButton("Browse");

        isFilterCheckBox.addItemListener(evt -> {
            final boolean isSelected = isFilterCheckBox.isSelected();
            getLogger(this).info("Changed event of isFiltered comboBox: " + StringUtil.trimWithNull(evt)
                    + " --> checked: " + isSelected
            );

            executionState.setIsOnlyFilteredFiles(
                    isSelected
            );

            this.doUpdateDisplayOfFilteredPanel(isSelected, filterField, browseButton);
        });

        gbc.gridx = 0;
        panel.add(isFilterCheckBox, gbc);

        filterField.setPreferredSize(new Dimension(200, 20));
        filterField.setSize(new Dimension(200, 20));
        filterField.setMaximumSize(new Dimension(200, 20));
        filterField.setToolTipText("A text file contains list of paths to filter, separated by new line");
        filterField.setEnabled(false);

        gbc.gridx = 1;
        panel.add(filterField, gbc);

        browseButton.addActionListener(e -> {
            //-- Mark: Open file dialog
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            final int resultCode = fileChooser.showOpenDialog(null);

            if (JFileChooser.APPROVE_OPTION == resultCode) {
                final File selectedFile = fileChooser.getSelectedFile();
                getLogger(this).info("Selected filePath: {}"
                        , selectedFile
                );

                try {
                    final Path filteredFilePath = selectedFile.toPath();
                    executionState.setFilteredFilePath(filteredFilePath);
                } catch (Exception ex) {
                    getLogger(this).error("Error when read content of filtered files", ex);
                }
            }

            filterField.setText(
                    StringUtil.trimWithNull(executionState.getFilteredFilePath())
            );
        });

        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        this.doUpdateDisplayOfFilteredPanel(false, filterField, browseButton);
    }

    private void doUpdateDisplayOfFilteredPanel(boolean isFiltered, JTextField filterField, JButton browseButton) {
        filterField.setVisible(isFiltered);
        browseButton.setVisible(isFiltered);
    }
}
