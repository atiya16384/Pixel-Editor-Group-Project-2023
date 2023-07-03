/**
 * com.group31.editor.Editor
 *
 * The entrypoint to the com.group31.editor package
 *
 * Written by [Mumtaz-Garcia, Jacob | McHale, Thomas | Mahboob, Atiya  | Jennings, Brendan | Amir, Aliff | Killmaier, Luca ]
 * at Lancaster University, 2022-2023.
 */
package com.group31.editor;

import com.formdev.flatlaf.FlatDarkLaf;
import com.group31.editor.canvas.*;
import com.group31.editor.canvas.action.*;
import com.group31.editor.canvas.error.InUseCanvasException;
import com.group31.editor.data.*;
import com.group31.editor.ui.*;
import com.group31.editor.ui.headless.InitialProjectSelection;
import com.group31.editor.ui.headless.PopUpMessage;
import com.group31.editor.util.*; 

import io.sentry.Sentry;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.UIManager;

public class Editor {

  private static ProjectHandler ph = ProjectHandler.getInstance();
  private static JWindow splash;
  private static final JFrame df = null;
  private static boolean fallbackState = false;

  public static void main(String[] args) {
    Thread.currentThread().setName("Core");
    Logger.log("Starting Editor...");
    
    createSplash(); // set up splash screen while firing up
    try {
      Logger.log("Initialising UIFrame LookAndFeel...", Logger.LOG_TYPE.INFO);
      UIManager.setLookAndFeel(new FlatDarkLaf());
    } catch (Exception e) {
      fallbackState = true;
    }

    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialising project",
      "core"
    );
    InitialProjectSelection projectSelector = new InitialProjectSelection(df);
    projectSelector.run();
    if (ph.getCurrent() == null ) {
      System.exit(0);
    }
    Logger.log("Project selection completed.", Logger.LOG_TYPE.PROJECT);
    new SentryReporting(projectSelector.getValue()); // set up Sentry
    // Build a new Canvas
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Building canvas",
      "core"
    );
    try {
      Canvas.getInstance().newCanvas(ph.getCurrent().dimension);
    } catch (InUseCanvasException err) {
      Sentry.captureException(err);
      throw new RuntimeException(err);
    }

    // Setup UIFrame
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialising UI",
      "core"
    );
    UIFrame frame = UIFrame.getInstance();
    Logger.log("Firing up the frame 🚀", Logger.LOG_TYPE.INFO, Logger.COLOUR_SET.SUCCESS);

    // Start frame and attach action handler
    try {
      frame.start();
      while (!frame.isReady())
        Thread.sleep(100);
      if (fallbackState) {
        Logger.log("Using default UI as fallback", Logger.LOG_TYPE.ERROR, Logger.COLOUR_SET.WARNING);
        frame.getJFrame().setDefaultLookAndFeelDecorated(true);
      }
    } catch (InterruptedException err) {
      new PopUpMessage(
        err,
        "Failed to start the UI management thread, this issue has been reported.",
        Sentry.captureException(err)
      ).openGetResult();
      System.exit(1);
    }

    if (!projectSelector.selectionLoad()) {
      new PopUpMessage(
        "Failed to load project",
        "An unrecoverable error occured at initial project loading, press OK to exit and try again.",
        "popup/icons8-high-risk-500.svg"
      ).openGetResult();
      System.exit(14);
    }

    new CanvasActionHandler(frame);
    HistoryManager.getInstance().start();

    // Set frame constraints
    var frameWidth = (int)Math.max(Math.round(ph.getCurrent().dimension.getWidth()*2), 300);
    var frameHeight = (int)Math.max(Math.round(ph.getCurrent().dimension.getHeight()*2), 300);
    frame.getJFrame().setSize(new Dimension(frameWidth,frameHeight));
    Logger.log("Running", Logger.LOG_TYPE.INFO, Logger.COLOUR_SET.SUCCESS);
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "PE is running",
      "core"
    );
    clearSplash();
  }

  private static void createSplash() {
    splash = new JWindow();
    try {
      BufferedImage img = javax.imageio.ImageIO.read(
        Editor.class.getResource("/images/splash.png")
      );
      Image img2 = img.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
      splash.getContentPane().add(new JLabel(new ImageIcon(img2)));
      splash.setBounds(500, 150, 300, 200);
      splash.setLocationRelativeTo(null);
      splash.setVisible(true);
      splash.toFront();
    } catch (Exception e) {
      Sentry.captureException(e);
    }
  }

  private static void clearSplash() {
    if (splash == null) throw new Error("Splash not generated.");
    splash.setVisible(false);
    splash.dispose();
  }
}
