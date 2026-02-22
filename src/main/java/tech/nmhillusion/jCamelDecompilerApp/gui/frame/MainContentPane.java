package tech.nmhillusion.jCamelDecompilerApp.gui.frame;

import tech.nmhillusion.jCamelDecompilerApp.Main;
import tech.nmhillusion.jCamelDecompilerApp.actionable.LogUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.constant.ExecutionStatus;
import tech.nmhillusion.jCamelDecompilerApp.constant.LogType;
import tech.nmhillusion.jCamelDecompilerApp.engine.DecompilerEngine;
import tech.nmhillusion.jCamelDecompilerApp.gui.CustomFileView;
import tech.nmhillusion.jCamelDecompilerApp.gui.component.ExplainHowToFilterPane;
import tech.nmhillusion.jCamelDecompilerApp.gui.handler.LogUpdateHandler;
import tech.nmhillusion.jCamelDecompilerApp.gui.handler.ProgressStatusUpdateHandler;
import tech.nmhillusion.jCamelDecompilerApp.helper.ViewHelper;
import tech.nmhillusion.jCamelDecompilerApp.loader.DecompilerLoader;
import tech.nmhillusion.jCamelDecompilerApp.loader.ExecutionStateLoader;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompileResultModel;
import tech.nmhillusion.jCamelDecompilerApp.model.DecompilerEngineModel;
import tech.nmhillusion.jCamelDecompilerApp.state.ExecutionState;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

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
import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
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
    private final ExecutorService DECOMPILE_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicReference<ProgressStatusUpdatable> progressStatusUpdatableHandlerRef = new AtomicReference<>();
    private final AtomicReference<LogUpdatable> logUpdatableHandlerRef = new AtomicReference<>();
    private final DecompilerLoader decompilerLoader = DecompilerLoader.getInstance();
    private final ExecutionStateLoader executionStateLoader = ExecutionStateLoader.getInstance();
    private final JTextField fieldInputDecompileFolder = new JTextField();
    private final JTextField fieldOutputDecompileFolder = new JTextField();
    private final JTextField fieldFilteredFilePath = new JTextField();


    public MainContentPane(JFrame mainFrame) throws IOException {
        this.mainFrame = mainFrame;
        loadState();

        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

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
            final JLabel titleLabel = new JLabel("jCamelDecompiler");
            titleLabel.setFont(new Font("Calibri", Font.PLAIN, 25));
            titleLabel.setBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 1, 0
                            , Color.decode("0xdddddd")
                    )
            );

            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(5, 5, 15, 5);
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
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            panel.add(createDecompilerSelectionPanel(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 1;
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.LINE_START;
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
            gbc.gridheight = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.weighty = 0.0;
            gbc.weightx = 1.0;
            panel.add(createDecompileButton(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.insets = new Insets(8, 0, 8, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
            panel.add(createStatusProgressBar(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(8, 0, 8, 0);
            gbc.fill = GridBagConstraints.BOTH;
            panel.add(createLogView(), gbc);
        }

        {
            gbc.gridx = 0;
            gbc.gridy = rowIdx++;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(buildAppCreditPanel(), gbc);
        }

        this.setContentPane(panel);
        panel.updateUI();
        panel.repaint();
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
            getLogger(this).error("Error when load state: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
            getLogger(this).error(ex);
        }
    }

    private JPanel createStatusProgressBar() {
        final JPanel panel = new JPanel(new BorderLayout());

        final JProgressBar progressBar = new JProgressBar(0, 100);
        final JLabel progressStatusLabel = new JLabel("Ready");

        progressStatusUpdatableHandlerRef.set(
                new ProgressStatusUpdateHandler(
                        progressBar
                        , progressStatusLabel
                )
        );

        progressStatusUpdatableHandlerRef.get()
                .resetProcessState();

        panel.add(progressStatusLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createLogView() {
        final JTextPane logView = new JTextPane();
        logView.setEditable(false);
        final JScrollPane logScrollPane = new JScrollPane(logView
                , ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        logView.setAutoscrolls(true);

        logUpdatableHandlerRef.set(
                new LogUpdateHandler(
                        logView
                        , logScrollPane
                )
        );

        return logScrollPane;
    }

    private JLabel buildAppCreditPanel() {
        try {
            final String appAuthor = Main.getAppInfoProperty(
                    "info.author", String.class
            );

            final String appVersion = Main.getAppInfoProperty(
                    "info.version", String.class
            );

            final JLabel resultPanel = new JLabel(
                    MessageFormat.format(
                            "@{0} v{1}"
                            , appAuthor
                            , appVersion
                    )
                    , SwingConstants.RIGHT);

            resultPanel.setForeground(Color.decode("#888888"));
            resultPanel.setFont(
                    new Font("Calibri", Font.ITALIC, 12)
            );
            resultPanel.setBorder(
                    BorderFactory.createEmptyBorder(
                            3, 3, 3, 3
                    )
            );

            return resultPanel;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private JPanel createDecompileButton() throws IOException {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        {
            /// Mark: decompile button
            final JButton decompileButton = new JButton("Decompile");

            decompileButton.setBackground(Color.CYAN);
            decompileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            decompileButton.setIcon(new ImageIcon(
                    ViewHelper.getIconForButton("icon/exec-icon.png", 15, 15)
            ));

            decompileButton.addActionListener(e -> {
                getLogger(this).info("Decompile for: {}", executionState);

                DECOMPILE_EXECUTOR.execute(() -> {
                    try {
                        final long startDecompileTime = System.currentTimeMillis();

                        executionState
                                .setClassesFolderPath(
                                        Path.of(fieldInputDecompileFolder.getText())
                                )
                                .setOutputFolder(
                                        Path.of(fieldOutputDecompileFolder.getText())
                                )
                                .setFilteredFilePath(
                                        Path.of(fieldFilteredFilePath.getText())
                                )
                                .setExecutionStatus(ExecutionStatus.PREPARE)
                        ;


                        executionStateLoader
                                .saveState(executionState);

                        progressStatusUpdatableHandlerRef.get()
                                .startProgress();

                        var decompileResult = new DecompilerEngine(executionState)
                                .execute(
                                        logUpdatableHandlerRef
                                        , progressStatusUpdatableHandlerRef
                                );

                        doLogMessageUI(LogType.WARN
                                , "Done decompilation to folder {decompileResult}".replace("{decompileResult}",
                                        String.valueOf(decompileResult.getOutputFolder())
                                                .replace("\\", "/")
                                )
                        );

                        onDoneDecompilation(decompileResult, startDecompileTime);
                    } catch (Throwable ex) {
                        try {
                            doLogMessageUI(LogType.ERROR, "Error when decompile [%s]: %s".formatted(ex.getClass().getSimpleName(), ex.getMessage()));
                        } catch (InterruptedException | InvocationTargetException ignored) {
                        }
                        throw new RuntimeException(ex);
                    }
                });
            });

            executionState.addListener(() -> {
                decompileButton.setEnabled(ExecutionStatus.PROCESSING != executionState.getExecutionStatus());
            });

            panel.add(decompileButton);
        }

        {
            /// Mark: stop button
            final JButton stopButton = new JButton("Stop");
            stopButton.setForeground(Color.RED);
            stopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stopButton.setIcon(new ImageIcon(
                    ViewHelper.getIconForButton("icon/stop-icon.png", 15, 15)
            ));
            stopButton.setEnabled(false);

            stopButton.addActionListener(e -> {
                getLogger(this).info("Stop decompile");

                executionState.setExecutionStatus(ExecutionStatus.CANCELLED);
                DECOMPILE_EXECUTOR.shutdown();

                try {
                    Thread.sleep(Duration.ofSeconds(3));
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                if (!DECOMPILE_EXECUTOR.isShutdown() || !DECOMPILE_EXECUTOR.isTerminated()) {
                    DECOMPILE_EXECUTOR.shutdownNow();
                }
            });

            executionState.addListener(() -> {
                stopButton.setEnabled(ExecutionStatus.PROCESSING == executionState.getExecutionStatus());
            });

            panel.add(stopButton);
        }

        return panel;
    }

    private void onDoneDecompilation(DecompileResultModel decompileResult, long startDecompileTime) throws IOException {
        final LogUpdatable logHandler = logUpdatableHandlerRef.get();
        final ProgressStatusUpdatable progressStatusUpdatable = progressStatusUpdatableHandlerRef.get();

        if (null != logHandler) {
            logHandler.onDone(
                    "Decompiled done. Open decompiled folder?"
                    , decompileResult
                    , startDecompileTime
            );
        }

        if (null != progressStatusUpdatable) {
            progressStatusUpdatable.resetProcessState();
        }
    }

    private void doLogMessageUI(LogType logType, String logMessage) throws InterruptedException, InvocationTargetException {
        final LogUpdatable handler = logUpdatableHandlerRef.get();

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

        fieldInputDecompileFolder.setSize(new Dimension(200, 20));
        fieldInputDecompileFolder.setMinimumSize(new Dimension(200, 20));

        fieldInputDecompileFolder.setText(
                StringUtil.trimWithNull(executionState.getClassesFolderPath())
        );
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
        panel.add(fieldInputDecompileFolder, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                getLogger(this).info("Selected filePath for classes folder: {}"
                        , fileChooser.getSelectedFile()
                );

                executionState.setClassesFolderPath(
                        fileChooser.getSelectedFile().toPath()
                );

                fieldInputDecompileFolder.setText(
                        String.valueOf(executionState.getClassesFolderPath())
                );
            }
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

        fieldOutputDecompileFolder.setSize(new Dimension(200, 20));
        fieldOutputDecompileFolder.setMinimumSize(new Dimension(200, 20));

        fieldOutputDecompileFolder.setText(
                StringUtil.trimWithNull(executionState.getOutputFolder())
        );
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
        panel.add(fieldOutputDecompileFolder, gbc);

        final JButton browseButton = new JButton("Browse");
        browseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                getLogger(this).info("Selected filePath for output folder: {}"
                        , fileChooser.getSelectedFile()
                );

                executionState.setOutputFolder(
                        fileChooser.getSelectedFile().toPath()
                );

                fieldOutputDecompileFolder.setText(
                        String.valueOf(executionState.getOutputFolder())
                );
            }
        });

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        return panel;
    }

    private JPanel createDecompilerSelectionPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
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

                if (StringValidator.isBlank(executionState.getDecompilerEngineId())) {
                    inited = true;
                    comboBox.setSelectedIndex(0);
                    executionState.setDecompilerEngineId(decompilerEngine.getEngineId());
                } else {
                    if (decompilerEngine.getEngineId().equals(executionState.getDecompilerEngineId())) {
                        inited = true;
                        comboBox.setSelectedItem(decompilerEngine);
                    }
                }
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

        return panel;
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
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        final JCheckBox isFilterCheckBox = new JCheckBox("Is Filtered by File List");
        final JPanel centralPanel = new JPanel(new GridBagLayout());
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
            fieldFilteredFilePath.setPreferredSize(new Dimension(200, 20));
            fieldFilteredFilePath.setSize(new Dimension(200, 20));
            fieldFilteredFilePath.setMaximumSize(new Dimension(200, 20));
            fieldFilteredFilePath.setToolTipText("A text file contains list of paths to filter, separated by new line");

            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.weightx = 1.0;
            gbc.gridx = 1;
            centralPanel.add(fieldFilteredFilePath, gbc);

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

                    fieldFilteredFilePath.setText(
                            StringUtil.trimWithNull(executionState.getFilteredFilePath())
                    );
                } catch (Exception ex) {
                    getLogger(this).error("Error when read content of filtered files");
                    getLogger(this).error(ex);
                }
            }
        });

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        /// Mark: Load from State
        isFilterCheckBox.setSelected(
                executionState.getIsOnlyFilteredFiles()
        );
        fieldFilteredFilePath.setText(
                StringUtil.trimWithNull(executionState.getFilteredFilePath())
        );


        this.doUpdateDisplayOfFilteredPanel(executionState.getIsOnlyFilteredFiles(), centralPanel, browseButton);

        return panel;
    }

    private void doUpdateDisplayOfFilteredPanel(boolean isFiltered, JPanel centralPanel, JButton browseButton) {
        centralPanel.setVisible(isFiltered);
        browseButton.setVisible(isFiltered);
    }
}
