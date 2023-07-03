package com.group31.editor.ui;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.data.ProjectHandler;
import com.group31.editor.tool.CanvasUtilityTool;
import com.group31.editor.ui.headless.PopUpMessage;
import com.group31.editor.ui.options.EmptyOptionsPanel;
import com.group31.editor.ui.options.OptionsPanel;
import com.group31.editor.util.Logger;
import com.group31.editor.util.SentryReporting;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import io.sentry.Sentry;

public class UIFrame extends Thread {

  private static volatile UIFrame instance;
  private final Canvas canvas = Canvas.getInstance();

  private Boolean ready = false;
  private JFrame frame;

  private CanvasUtilityTool selectedEditorTool/* = (CanvasUtilityTool) ToolBar.tools[0]*/;
  private OptionsPanel optionsPanel = new EmptyOptionsPanel();
  private BottomPanel bottomPanel;

  public UIFrame() {
    if (instance != null) throw new RuntimeException("UIFrame already initialised");
    setDefaultUncaughtExceptionHandler(
      new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
          // new ErrorDialog(Sentry.captureException(e));
          new PopUpMessage(
            e,
            "Something went wrong, and we're not quite sure what!",
            Sentry.captureException(e)
          )
            .openGetResult();
          Logger.log(
            String.format(
              "Running with an uncaught Exception: %s\n Stack trace: ",
              e.getMessage()
            ),
            Logger.LOG_TYPE.ERROR,
            Logger.COLOUR_SET.WARNING
          );
          e.printStackTrace();
        }
      }
    );
  }

  /**
   * UIFrame().getAwtFrame()
   * @return Returns the base java.awt.Frame object
   */
  public Frame getAwtFrame() {
    return frame;
  }

  /**
   * UIFrame().getCanvas()
   * @return Returns the canvas
   */
  @Deprecated
  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * UIFrame().getJFrame()
   * @return Returns the JFrame
   */
  public JFrame getJFrame() {
    return frame;
  }

  @Override
  public void run() {
    Thread.currentThread().setName("UIFrame");
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialising UIFrame",
      "UIFrame"
    );
    Logger.log("Initialising UIFrame Thread...", Logger.LOG_TYPE.INFO);
    frame = new JFrame();
    frame.setTitle("Pixel Editor");
    frame.setLayout(new BorderLayout());

    // Set up Canvas scrollpane and add
    JScrollPane scrollableCanvas = new JScrollPane(canvas);
    scrollableCanvas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollableCanvas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    frame.add(scrollableCanvas, BorderLayout.CENTER);

    // Window decorations and state
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setMinimumSize(new Dimension(600, 300));
    {
      Dimension dim = ProjectHandler.getInstance().getCanvasSize();
      frame.setSize(new Dimension((int)(dim.getWidth()+500), (int)(dim.getHeight()+300)));
    }

    Logger.log("Initialising UIFrame components...", Logger.LOG_TYPE.INFO);
    this.initComponents();
    frame.setVisible(true);
    frame.setState(Frame.NORMAL);
    frame.toFront();
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Thread listening",
      "UIFrame"
    );
    Logger.log("UIFrame ready", Logger.LOG_TYPE.INFO, Logger.COLOUR_SET.SUCCESS);
    this.ready = true;
  }

  private void initComponents() {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialising UI components",
      "UIFrame"
    );
    new ToolBar(this);
    new MenuBar(this);
    bottomPanel = new BottomPanel(this, canvas.getWidth(), canvas.getHeight());
    optionsPanel.addToFrame(this);
  }

  public BottomPanel getBottomPanel() {
    return bottomPanel;
  }

  public CanvasUtilityTool getSelectedEditorTool() {
    return selectedEditorTool;
  }

  public void setSelectedEditorTool(CanvasUtilityTool selectedEditorTool) {
    if (this.selectedEditorTool != null) {
      SentryReporting.toolBreadcrumb(
              this.selectedEditorTool.getName(),
              selectedEditorTool.getName()
      );
    }
    if (this.optionsPanel != null) {
      this.optionsPanel.removeFromFrame();
      this.optionsPanel = null;
    }
    this.selectedEditorTool = selectedEditorTool;
    this.optionsPanel = selectedEditorTool.getOptionsPanel();
    if (this.optionsPanel != null) {
      this.optionsPanel.addToFrame(this);
    }
//    this.getJFrame().pack();
    this.getJFrame().repaint();
    this.getJFrame().revalidate();
  }

  public Boolean isReady() {
    return ready;
  }

  public static UIFrame getInstance() {
    if (instance == null) {
      synchronized (UIFrame.class) {
        if (instance == null) {
          instance = new UIFrame();
        }
      }
    }
    return instance;
  }

  public static boolean isGenerated() {
    return instance == null;
  }
}
