package tech.nmhillusion.jCamelDecompilerApp.gui.handler;

import tech.nmhillusion.jCamelDecompilerApp.actionable.ProgressStatusUpdatable;

import javax.swing.*;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-23
 */
public class ProgressStatusUpdateHandler implements ProgressStatusUpdatable {
    private final JProgressBar progressBar;
    private final JLabel progressStatusLabel;

    public ProgressStatusUpdateHandler(JProgressBar progressBar, JLabel progressStatusLabel) {
        this.progressBar = progressBar;
        this.progressStatusLabel = progressStatusLabel;
    }

    @Override
    public void onUpdateProgressValue(int newPercent, int currentCompletedCount, int totalCount) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(newPercent);
            progressStatusLabel.setText("Processing... " + currentCompletedCount + "/" + totalCount);
        });
    }
}
