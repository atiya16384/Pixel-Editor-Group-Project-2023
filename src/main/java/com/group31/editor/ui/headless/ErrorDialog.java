package com.group31.editor.ui.headless;

import io.sentry.protocol.SentryId;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ErrorDialog extends JDialog {

  private JPanel panel = new JPanel();
  private JLabel errorLabel = new JLabel("An error has occurred.");
  private JButton exitButton = new JButton("Exit");
  private JButton reportButton = new JButton("Report Error");
  private JButton continueButton = new JButton("Continue Anyway");

  @Deprecated
  public ErrorDialog(SentryId sentryId) {
    setTitle("Error");

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(errorLabel);

    exitButton.addActionListener(event -> {
      dispose();
      System.exit(0);
    });
    panel.add(exitButton);

    reportButton.addActionListener(event -> {
      javax.swing.JFrame deadFrame = null;
      FeedbackDialog feedbackDialog = new FeedbackDialog(deadFrame, sentryId);
      feedbackDialog.pack();
      feedbackDialog.setVisible(true);
    });
    panel.add(reportButton);

    continueButton.addActionListener(event -> {
      dispose();
    });
    panel.add(continueButton);

    add(panel);
    pack();
    setVisible(true);
  }
}
