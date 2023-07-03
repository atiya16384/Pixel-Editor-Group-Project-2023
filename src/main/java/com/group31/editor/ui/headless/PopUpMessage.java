package com.group31.editor.ui.headless;

import com.group31.editor.libs.SvgImageIcon;
import com.group31.editor.util.Logger;
import com.group31.editor.util.SentryReporting;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import net.miginfocom.swing.*;

public class PopUpMessage
  extends javax.swing.JDialog
  implements java.awt.event.ActionListener {

  private JPanel content = new JPanel();
  private SentryId sentryId = null;

  /**
   * PopUpMessage
   *  A generic pop-up box for all your pop-up-box message needs
   * @param title Title at the top of the JDialog
   * @param message Content to display in JDialorg
   * @param icon Image to show on left side of JDialog
   * @param optionType Buttons to display on bottom right of JDialog
   */
  public PopUpMessage(String message) {
    add(new JLabel(message));
    setModal(true);
    setVisible(true);
  }

  public PopUpMessage(String title, String message) {
    // super(title);
    setTitle(title);
    buildUi(message, "", ButtonSet.DEFAULT_OPTION);
  }

  public PopUpMessage(String title, String message, String icon) {
    // super(title);
    setTitle(title);
    buildUi(message, icon, ButtonSet.DEFAULT_OPTION);
  }

  public PopUpMessage(String title, String message, String icon, ButtonSet optionType) {
    // super(title);
    setTitle(title);
    buildUi(message, icon, optionType);
  }

  public PopUpMessage(Exception err) {
    this(err, "An error occured", Sentry.captureException(err));
  }

  public PopUpMessage(Throwable err) {
    this(err, "An error occured", Sentry.captureException(err));
  }

  public PopUpMessage(Throwable err, SentryId sentryId) {
    this(err, "An error occured", sentryId);
  }

  public PopUpMessage(Throwable err, String message) {
    this(err, message, null);
  }

  public PopUpMessage(Throwable err, String message, SentryId sentryId) {
    this(err, message, "popup/icons8-do-not-disturb-500.svg", sentryId);
  }

  public PopUpMessage(Throwable err, String message, String icon, SentryId sentryId) {
    String errOut = err == null ? "" : err.toString();
    Logger.log(String.format("An error occured: %s", errOut), Logger.LOG_TYPE.ERROR);
    setTitle("An error occured");
    this.sentryId = sentryId;
    buildUi(message, icon, ButtonSet.ERROR_OPTION);
  }

  public static enum ButtonSet {
    YES_NO_OPTION,
    YES_NO_CANCEL_OPTION,
    OK_CANCEL_OPTION,
    ERROR_OPTION,
    DEFAULT_OPTION,
  }

  public static enum Option {
    YES_OPTION,
    OK_OPTION,
    NO_OPTION,
    CANCEL_OPTION,
    EXIT_OPTION,
    REPORT_OPTION,
  }

  private Option selectedOption;

  private void buildUi(String message, String icon, ButtonSet optionType) {
    JLabel image = new JLabel();
    content.setLayout(
      new MigLayout(
        "insets dialog,hidemode 3",
        // columns
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]" +
        "[fill]",
        // rows
        "[]" + "[]" + "[]" + "[]" + "[]" + "[]"
      )
    );

    if (icon != "") image.setIcon(
      SvgImageIcon.createIconFromSvg(icon, new Dimension(100, 100))
    );
    setLayout(new BorderLayout());
    content.add(image, "cell 0 0 3 6,grow");
    content.add(
      new JLabel("<html>" + message + "</html>"),
      "cell 22 0 3 6,alignx trailing,grow 0 100"
    );
    // add(image, BorderLayout.WEST);
    // add(new JLabel("<html>"+message+"</html>"), BorderLayout.EAST);
    add(content, BorderLayout.NORTH);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    JButton defaultButton;
    switch (optionType) {
      case YES_NO_OPTION:
        defaultButton = createButton("Yes", Option.YES_OPTION);
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("No", Option.NO_OPTION));
        break;
      case YES_NO_CANCEL_OPTION:
        defaultButton = createButton("Yes", Option.YES_OPTION);
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("No", Option.NO_OPTION));
        buttonPanel.add(createButton("Cancel", Option.CANCEL_OPTION));
        break;
      case OK_CANCEL_OPTION:
        defaultButton = createButton("OK", Option.OK_OPTION);
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("Cancel", Option.CANCEL_OPTION));
        break;
      case ERROR_OPTION:
        if (sentryId != null) {
          defaultButton = createButton("Report Error", Option.YES_OPTION);
          defaultButton.addActionListener(event -> {
            new FeedbackDialog((javax.swing.JFrame) null, sentryId);
          });
          JButton ignore = createButton("Ignore Error", Option.CANCEL_OPTION);
          ignore.addActionListener(event -> {
            dispose();
          });
          buttonPanel.add(defaultButton);
          buttonPanel.add(ignore);
        } else {
          defaultButton = createButton("Ignore Error", Option.CANCEL_OPTION);
          defaultButton.addActionListener(event -> {
            dispose();
          });
          buttonPanel.add(defaultButton);
        }
        JButton exit = createButton("Exit", Option.EXIT_OPTION);
        exit.addActionListener(event -> {
          dispose();
          System.exit(0);
        });
        buttonPanel.add(exit);
        break;
      default:
        defaultButton = createButton("OK", Option.OK_OPTION);
        buttonPanel.add(defaultButton);
        break;
    }
    switch (optionType) {
      case YES_NO_OPTION:
      case OK_CANCEL_OPTION:
        buttonPanel.setLayout(
          new MigLayout(
            "insets dialog,alignx right",
            // columns
            "[button,fill]" + "[button,fill]",
            // rows
            null
          )
        );
      case ERROR_OPTION:
      case YES_NO_CANCEL_OPTION:
        buttonPanel.setLayout(
          new MigLayout(
            "insets dialog,alignx right",
            // columns
            "[button,fill]" + "[button,fill]" + "[button,fill]",
            // rows
            null
          )
        );
      default:
        buttonPanel.setLayout(
          new MigLayout(
            "insets dialog,alignx right",
            // columns
            "[button,fill]",
            // rows
            null
          )
        );
    }
    add(buttonPanel, BorderLayout.SOUTH);
    setModal(true);
    setSize(600, 250);
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  /**
   * Create a button with the given text and option type.
   * @author jennin16
   * @param text
   * @param buttonType
   * @return JButton
   */
  protected JButton createButton(String text, Option buttonType) {
    ImageIcon icon;
    switch (buttonType) {
      case YES_OPTION:
      case OK_OPTION:
        icon = SvgImageIcon.createIconFromSvg("popup/confirm.svg", new Dimension(10, 10));
        break;
      case REPORT_OPTION:
        icon = SvgImageIcon.createIconFromSvg("popup/error.svg", new Dimension(10, 10));
        break;
      case EXIT_OPTION:
        icon = SvgImageIcon.createIconFromSvg("popup/exit.svg", new Dimension(10, 10));
        break;
      case NO_OPTION:
      case CANCEL_OPTION:
        icon = SvgImageIcon.createIconFromSvg("popup/close.svg", new Dimension(10, 10));
        break;
      default:
        icon = new ImageIcon();
    }
    JButton button = new JButton(text, icon);
    button.setMnemonic(text.charAt(0));
    button.setActionCommand(optToStr(buttonType));
    button.addActionListener(this);
    return button;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    this.selectedOption = strToOpt(event.getActionCommand());
    dispose();
  }

  /**
   * PopUpMessage.optToStr()
   * Convert an Option enum to a string.
   * @author jennin16
   * @param opt
   * @return string
   */
  protected static String optToStr(Option opt) {
    switch (opt) {
      default:
      case YES_OPTION:
      case OK_OPTION:
        return "YES_OPTION";
      case NO_OPTION:
        return "NO_OPTION";
      case CANCEL_OPTION:
        return "CANCEL_OPTION";
      case REPORT_OPTION:
        return "REPORT_OPTION";
      case EXIT_OPTION:
        return "EXIT_OPTION";
    }
  }

  /**
   * PopUpMessage.strToOpt()
   * Convert a string to an Option enum.
   * @author jennin16
   * @param opt
   * @return enum
   */
  protected static Option strToOpt(String opt) {
    switch (opt) {
      case "YES_OPTION":
        return Option.YES_OPTION;
      case "OK_OPTION":
        return Option.OK_OPTION;
      case "NO_OPTION":
        return Option.NO_OPTION;
      case "CANCEL_OPTION":
        return Option.CANCEL_OPTION;
      case "REPORT_OPTION":
        return Option.REPORT_OPTION;
      case "EXIT_OPTION":
        return Option.EXIT_OPTION;
      default:
        return Option.OK_OPTION;
    }
  }

  /**
   * PopUpMessage.openGetResult()
   * Open the dialog and return the selected option.
   * @author jennin16
   * @returns The option selected in the UI
   */
  public Option openGetResult() {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Running a new pop-up",
      "ui/PopUpMessage"
    );
    setVisible(true);
    return getSelectedOption();
  }

  /**
   * PopUpMessage.getSelectedOption()
   * Get the selected option.
   * @author jennin16
   * @returns The option selected in the UI
   */
  public Option getSelectedOption() {
    return this.selectedOption;
  }
}
