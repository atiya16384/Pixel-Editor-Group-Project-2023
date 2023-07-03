package com.group31.editor.ui.headless;

import com.group31.editor.util.Logger;
import io.sentry.Sentry;
import io.sentry.UserFeedback;
import io.sentry.protocol.SentryId;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public class FeedbackDialog extends JDialog {

  // private JPanel panel = new JPanel();
  // private JTextField nameField = new JTextField();
  // private JTextField emailField = new JTextField();
  // private JTextArea commentsArea = new JTextArea();
  // private JButton submitButton = new JButton("Submit");
  private JPanel panel = new JPanel();
  private JLabel nameLabel = new JLabel();
  private JTextField nameField = new JTextField();
  private JLabel emailLabel = new JLabel();
  private JTextField emailField = new JTextField();
  private JLabel commentLabel = new JLabel();
  private JScrollPane commentScrollPane = new JScrollPane();
  private JTextPane commentField = new JTextPane();
  private JPanel buttonBar = new JPanel();
  private JButton submitButton = new JButton();
  private JButton exitButton = new JButton();

  public FeedbackDialog(java.awt.Frame parent, SentryId sentryId) {
    super(parent, "Submit Feedback", true);
    setTitle("Submit Feedback");
    setResizable(false);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    Border padding = BorderFactory.createEmptyBorder(0, 10, 0, 10);
    panel.setBorder(padding);

    panel.setLayout(
      new MigLayout(
        "hidemode 3",
        // columns
        "[139,fill]" + "[321,fill]",
        // rows
        "[]" + "[]" + "[146]"
      )
    );

    //---- nameLabel ----
    nameLabel.setText("Name");
    panel.add(nameLabel, "cell 0 0,alignx left,growx 0");
    panel.add(nameField, "cell 1 0");

    //---- emailLabel ----
    emailLabel.setText("Email");
    panel.add(emailLabel, "cell 0 1,alignx left,growx 0");
    panel.add(emailField, "cell 1 1");

    //---- commentLabel ----
    commentLabel.setText("Feedback");
    panel.add(commentLabel, "cell 0 2,align left top,grow 0 0");

    //======== commentScrollPane ========
    commentScrollPane.setViewportView(commentField);
    panel.add(commentScrollPane, "cell 1 2,hmin 150");

    buttonBar.setLayout(
      new MigLayout(
        "insets dialog,alignx right",
        // columns
        "[button,fill]",
        // rows
        "[]"
      )
    );

    //---- submitButton ----
    submitButton.setText("Submit Feedback");
    buttonBar.add(submitButton, "cell 0 0");

    //---- exitButton ----
    exitButton.setText("Cancel");
    buttonBar.add(exitButton, "cell 1 0");

    contentPane.add(panel, BorderLayout.CENTER);
    contentPane.add(buttonBar, BorderLayout.SOUTH);

    submitButton.addActionListener(event -> {
      // Get the values from the input fields
      String name = nameField.getText();
      String email = emailField.getText();
      String comments = commentField.getText();

      UserFeedback userFeedback = new UserFeedback(sentryId);
      userFeedback.setName(name);
      userFeedback.setEmail(email);
      userFeedback.setComments(comments);
      Sentry.captureUserFeedback(userFeedback);

      dispose();
    });
    exitButton.addActionListener(event -> {
      dispose();
    });

    pack();
    setVisible(true);
    try {
      setLocationRelativeTo(getOwner());
    } catch (Exception e) {
      Logger.log("Failed to set FeedbackDialog parent Dialog/Frame");
    }
  }
}
