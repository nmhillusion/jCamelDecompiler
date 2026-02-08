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


    public ProgressStatusUpdateHandler(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onUpdateProgressValue(int newPercentValue) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(newPercentValue);
        });
    }
}
