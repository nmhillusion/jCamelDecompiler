package tech.nmhillusion.jCamelDecoderApp.gui.frame;

import tech.nmhillusion.jCamelDecoderApp.actionable.ProgressStatusUpdatable;
import tech.nmhillusion.jCamelDecoderApp.constant.LogType;
import tech.nmhillusion.jCamelDecoderApp.helper.ViewHelper;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.ChainMap;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-23
 */
public class ProgressStatusUpdateHandler implements ProgressStatusUpdatable {
    private final JProgressBar progressBar;
    private final JTextArea logView;
    private final JScrollPane logScrollPane;


    public ProgressStatusUpdateHandler(JProgressBar progressBar, JTextArea logView, JScrollPane logScrollPane) {
        this.progressBar = progressBar;
        this.logView = logView;
        this.logScrollPane = logScrollPane;
    }

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
        final int resultDialog = JOptionPane.showConfirmDialog(
                logView
                , notificationContent
                , "Decompile"
                , JOptionPane.YES_NO_OPTION);

        getLogger(this).info("result dialog = {}", resultDialog);

        if (JOptionPane.YES_OPTION == resultDialog) {
            ViewHelper.openFileExplorer(outputFolder);
        }
    }
}
