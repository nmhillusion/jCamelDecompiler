package tech.nmhillusion.jCamelDecompilerApp.gui.handler;

import tech.nmhillusion.jCamelDecompilerApp.actionable.LogUpdatable;
import tech.nmhillusion.jCamelDecompilerApp.constant.LogType;
import tech.nmhillusion.jCamelDecompilerApp.helper.ViewHelper;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.ChainMap;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-02-08
 */
public class LogUpdateHandler implements LogUpdatable {
    private final JTextPane logView;
    private final JScrollPane logScrollPane;
    private final StyleContext sc = StyleContext.getDefaultStyleContext();
    private final Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
    private final Map<LogType, Style> logTypeStyles = new TreeMap<>();

    public LogUpdateHandler(JTextPane logView, JScrollPane logScrollPane) {
        this.logView = logView;
        this.logScrollPane = logScrollPane;
    }

    private void removeFirstLineOfLogView() {
        StyledDocument doc = logView.getStyledDocument();
        try {
            final int endOfFirstLine = logView.getText().indexOf('\n');
            if (endOfFirstLine > 0) {
                doc.remove(0, endOfFirstLine);
            }
        } catch (BadLocationException ex) {
            getLogger(this).error("Error when remove first line");
            getLogger(this).error(ex);
        }
    }

    @Override
    public void onLogMessage(LogType logType, String logMsg) {
        SwingUtilities.invokeLater(() -> {
            try {
                final JScrollBar verticalScrollBar = logScrollPane.getVerticalScrollBar();
                if (verticalScrollBar.isVisible()) {

                    final double logViewHeight = logScrollPane.getBounds().getHeight();
                    final float lineHeight = ViewHelper.getLineHeightOfTextArea(logView);
                    final int MIN_LOG_ROWS = Math.ceilDiv(Math.round((float) logViewHeight), Math.round(lineHeight)) * 3;
                    getLogger(this).info("current height of scroll log view: "
                            + new ChainMap<>()
                            .chainPut("logViewHeight", logViewHeight)
                            .chainPut("lineHeight", lineHeight)
                            .chainPut("MIN_LOG_ROWS", MIN_LOG_ROWS)
                    );

                    int lineCount = ViewHelper.getLineCount(logView);
                    while (MIN_LOG_ROWS < lineCount) {
                        removeFirstLineOfLogView();
                        lineCount = ViewHelper.getLineCount(logView);
                    }
                }

                final StyledDocument doc = logView.getStyledDocument();
                doc.insertString(
                        doc.getLength()
                        , "{timestamp} - [{logType}] - {logMessage}\n"
                                .replace("{timestamp}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                                .replace("{logType}", logType.getValue().toUpperCase())
                                .replace("{logMessage}", logMsg)
                        , getLogMessageStyle(doc, logType)
                );

                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                logView.setCaretPosition(doc.getLength());
            } catch (Throwable ex) {
                LogHelper.getLogger(this).error("Error when log message");
                LogHelper.getLogger(this).error(ex);
            }
        });
    }

    private Style getLogMessageStyle(StyledDocument doc, LogType logType) {
        return logTypeStyles.computeIfAbsent(logType, logType1 -> {
            final Style style = doc.addStyle(logType.getValue() + "Style", defaultStyle);

            final Color logColor = switch (logType) {
                case ERROR -> Color.RED;
                case WARN -> Color.decode("0xb84300");
                case INFO -> Color.BLUE;
                case DEBUG -> Color.DARK_GRAY;
                default -> Color.BLACK;
            };

            StyleConstants.setFontFamily(style, "Monospaced");
            StyleConstants.setForeground(style, logColor);

            return style;
        });
    }

    @Override
    public void onDone(String notificationContent, Path outputFolder) throws IOException {
        final int resultDialog = JOptionPane.showConfirmDialog(
                logScrollPane
                , notificationContent
                , "Decompile"
                , JOptionPane.YES_NO_OPTION);

        getLogger(this).info("result dialog = {}", resultDialog);

        if (JOptionPane.YES_OPTION == resultDialog) {
            ViewHelper.openFileExplorer(outputFolder);
        }
    }
}
