package tech.nmhillusion.jCamelDecompilerApp.gui.frame;

import tech.nmhillusion.jCamelDecompilerApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.constant.LogType;
import tech.nmhillusion.jCamelDecompilerApp.engine.DecompilerEngine;
import tech.nmhillusion.jCamelDecompilerApp.gui.CustomFileView;
import tech.nmhillusion.jCamelDecompilerApp.gui.component.ExplainHowToFilterPane;
import tech.nmhillusion.jCamelDecompilerApp.gui.handler.ProgressStatusUpdateHandler;
import tech.nmhillusion.jCamelDecompilerApp.loader.DecompilerLoader;
import tech.nmhillusion.jCamelDecompilerApp.loader.ExecutionStateLoader;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompilerEngineModel;
import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionState;
import tech.nmhillusion.n2mix.util.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2024-11-30
 */
public class MainContentPane extends JRootPane {
    private final JFrame mainFrame;
    private final ExecutionState executionState = new ExecutionState();
    private final Executor DECOMPILE_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicReference<ProgressStatusUpdatable> progressStatusUpdatableHandlerRef = new AtomicReference<>();
    private final DecompilerLoader decompilerLoader = DecompilerLoader.getInstance();
    private final ExecutionStateLoader executionStateLoader = ExecutionStateLoader.getInstance();

    public MainContentPane(JFrame mainFrame) {
        this.mainFrame = mainFrame;

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
            final JLabel titleLabel = new JLabel("jCamelDecompilerApp");
            titleLabel.setFont(new Font("Calibri", Font.PLAIN, 25));
            titleLabel.setBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 1, 0
                            , Color.decode("0xeedddd")
                    )
            );

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
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
            panel.add(createInputDecompileFolder(), gbc);

            gbc.gridy = rowIdx++;
            panel.add(createOutputDecompileFolder(), gbc);
        }

        {
            createDecompilerSelectionPanel(panel, rowIdx++);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weighty = 0.0;
            gbc.weightx = 1.0;
            panel.add(createFilterPanel(), gbc);
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
            gbc.weighty = 0.0;
            gbc.weightx = 1.0;
            panel.add(createDecompileButton(), gbc);
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
        loadState();
    }

    private void loadState() {
        try {
            final ExecutionState loadedState = executionStateLoader.loadState();
            if (null != loadedState) {
                executionState.setClassesFolderPath(loadedState.getClassesFolderPath());
                executionState.setOutputFolder(loadedState.getOutputFolder());
                executionState.setDecompilerEngineId(loadedState.getDecompilerEngineId());
                executionState.setIsOnlyFilteredFiles(loadedState.getIsOnlyFilteredFiles());
                executionState.setFilteredFilePath(loadedState.getFilteredFilePath());
            }
        } catch (Exception ex) {
            getLogger(this).error("Error when load state", ex);
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

        final JTextPane logView = new JTextPane();
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

            panel.add(
                    logScrollPane
                    , gbc
            );
        }

        progressStatusUpdatableHandlerRef.set(
                new ProgressStatusUpdateHandler(
                        progressBar
                        , logView
                        , logScrollPane
                )
        );
        panel.setBackground(Color.decode("#dddddd"));

        return panel;
    }

    private JButton createDecompileButton() {
        final JButton decompileButton = new JButton("Decompile");
        final Dimension decompileButtonSize = new Dimension(200, 30);

        decompileButton.setBackground(Color.CYAN);
        decompileButton.setSize(decompileButtonSize);
        decompileButton.setPreferredSize(decompileButtonSize);
        decompileButton.addActionListener(e -> {
            getLogger(this).info("Decompile for: {}", executionState);

            DECOMPILE_EXECUTOR.execute(() -> {
                try {
                    executionStateLoader
                            .saveState(executionState);

                    var outputFolder = new DecompilerEngine(executionState)
                            .execute(
                                    progressStatusUpdatableHandlerRef
                            );

                    doLogMessageUI(LogType.WARN
                            , "Done decompilation to folder {outputFolder}".replace("{outputFolder}",
                                    String.valueOf(outputFolder)
                                            .replace("\\", "/")
                            )
                    );

                    onDoneDecompilation(outputFolder);
                } catch (Throwable ex) {
                    try {
                        doLogMessageUI(LogType.ERROR, "Error when decompile [%s]: %s".formatted(ex.getClass().getSimpleName(), ex.getMessage()));
                    } catch (InterruptedException | InvocationTargetException ignored) {
                    }
                    throw new RuntimeException(ex);
                }
            });
        });

        return decompileButton;
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

    private JPanel createInputDecompileFolder() {
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
        panel.add(new JLabel("Folder to decompile: "), gbc);

        final JTextField inputField = new JTextField();
//        inputField.setPreferredSize(new Dimension(200, 20));
        inputField.setSize(new Dimension(200, 20));
        inputField.setMinimumSize(new Dimension(200, 20));
        inputField.setEnabled(false);

//        {
//            /// TODO: 2025-03-15 TESTING
//            final String classTestPath = "C:\\Users\\nmhil\\OneDrive\\Desktop\\tmp\\test-decoder\\classes";
//            inputField.setText(
//                    classTestPath
//            );
//            executionState.setClassesFolderPath(
//                    Paths.get(classTestPath)
//            );
//        }

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panel.add(inputField, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            setCurrentDirectoryOfFileChooser(
                    fileChooser
                    , executionState.getClassesFolderPath()
            );
            final int resultCode = fileChooser.showOpenDialog(null);

            if (JFileChooser.APPROVE_OPTION == resultCode) {
                executionState.setClassesFolderPath(
                        fileChooser.getSelectedFile().toPath()
                );
            }

            inputField.setText(
                    String.valueOf(executionState.getClassesFolderPath())
            );
        });

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        return panel;
    }

    private void setCurrentDirectoryOfFileChooser(JFileChooser fileChooser, Path folderPath) {
        if (null != folderPath) {
            fileChooser.setCurrentDirectory(
                    folderPath.toFile()
            );
        }
    }

    private JPanel createOutputDecompileFolder() {
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

//        {
//            /// TODO: 2025-03-15 TESTING
//            final String outJavaTestPath = "C:\\Users\\nmhil\\OneDrive\\Desktop\\tmp\\test-decoder\\outJava";
//            inputField.setText(
//                    outJavaTestPath
//            );
//            executionState.setOutputFolder(
//                    Paths.get(outJavaTestPath)
//            );
//        }

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panel.add(inputField, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            setCurrentDirectoryOfFileChooser(
                    fileChooser
                    , executionState.getOutputFolder()
            );
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

    private void createDecompilerSelectionPanel(JPanel panel, int rowIdx) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = rowIdx;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0;
        panel.add(new JLabel("Decompiler Engine:"), gbc);

        final JComboBox<DecompilerEngineModel> comboBox = new JComboBox<>();

        boolean inited = false;
        for (final DecompilerEngineModel decompilerEngine : decompilerLoader.loadEngines()) {
            comboBox.addItem(
                    decompilerEngine
            );

            if (!inited) {
                inited = true;
                comboBox.setSelectedIndex(0);
                executionState.setDecompilerEngineId(decompilerEngine.getEngineId());
            }
        }

        comboBox.addItemListener(selectedItemEvent -> {
            if (ItemEvent.SELECTED == selectedItemEvent.getStateChange()) {
                final Object selectedItemRaw = comboBox.getSelectedItem();
                if (selectedItemRaw instanceof DecompilerEngineModel decompilerEngineModel) {
                    final String selectedDecompilerEngineId = decompilerEngineModel.getEngineId();
                    getLogger(this).info("Selected item event: " + selectedDecompilerEngineId);

                    executionState.setDecompilerEngineId(
                            selectedDecompilerEngineId
                    );
                } else {
                    throw new IllegalArgumentException("Selected item is not a valid DecompilerEngine");
                }
            }
        });

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 1;
        panel.add(comboBox, gbc);
    }

    private JPanel createFilterPanel() {
        final GridBagLayout gbl = new GridBagLayout();
        final JPanel panel = new JPanel(gbl);
        final int rowIdx = 0;

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = rowIdx;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.BASELINE;

        final JCheckBox isFilterCheckBox = new JCheckBox("Is Filtered by File List");
        final JPanel centralPanel = new JPanel(new GridBagLayout());
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

            gbc.gridx = 0;
            gbc.weightx = isSelected ? 0.0 : 1.0;
            gbl.setConstraints(isFilterCheckBox, gbc);

            this.doUpdateDisplayOfFilteredPanel(isSelected, centralPanel, browseButton);
        });

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        panel.add(isFilterCheckBox, gbc);

        {
            filterField.setPreferredSize(new Dimension(200, 20));
            filterField.setSize(new Dimension(200, 20));
            filterField.setMaximumSize(new Dimension(200, 20));
            filterField.setToolTipText("A text file contains list of paths to filter, separated by new line");
            filterField.setEnabled(false);

            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.weightx = 1.0;
            gbc.gridx = 1;
            centralPanel.add(filterField, gbc);

            final JLabel explainLink = new JLabel("<html><a href=''>How to filter</a></html>");
            explainLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            explainLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final JDialog d = new JDialog(mainFrame, "How to filter", Dialog.ModalityType.TOOLKIT_MODAL);
                    d.add(new ExplainHowToFilterPane());
                    d.pack();
                    d.setLocationRelativeTo(mainFrame);
                    d.setVisible(true);
                }
            });

            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.weightx = 1.0;
            gbc.gridx = 1;
            centralPanel.add(explainLink, gbc);
        }

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panel.add(centralPanel, gbc);

        browseButton.addActionListener(e -> {
            //-- Mark: Open file dialog
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileView(new CustomFileView());
            setCurrentDirectoryOfFileChooser(
                    fileChooser
                    , executionState.getFilteredFilePath()
            );
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                            "Text files (split by new line)", "txt"
                    )
            );

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

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        this.doUpdateDisplayOfFilteredPanel(false, centralPanel, browseButton);

        return panel;
    }

    private void doUpdateDisplayOfFilteredPanel(boolean isFiltered, JPanel centralPanel, JButton browseButton) {
        centralPanel.setVisible(isFiltered);
        browseButton.setVisible(isFiltered);
    }
}
