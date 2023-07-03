package com.group31.editor.ui.event;

import com.group31.editor.canvas.transform.ClipboardTransformAction;
import com.group31.editor.canvas.Canvas;

import com.group31.editor.canvas.error.InUseCanvasException;
import com.group31.editor.data.*;
import com.group31.editor.data.filetypes.BMPHandler;
import com.group31.editor.data.filetypes.JPEGHandler;
import com.group31.editor.data.filetypes.PNGHandler;
import com.group31.editor.ui.headless.FeedbackDialog;
import com.group31.editor.ui.headless.FileDialog;
import com.group31.editor.ui.headless.GridLines;
import com.group31.editor.ui.headless.JPEGOptions;
import com.group31.editor.ui.headless.NewProjectDialog;
import com.group31.editor.ui.headless.PopUpMessage;
import com.group31.editor.util.Logger;
import com.group31.editor.util.SentryReporting;


import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.protocol.SentryId;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

public enum MenuEvent {
  // TODO make all these transactions
  NEW_PROJECT(
    new AbstractAction("New Project") {
      @Override
      public void actionPerformed(ActionEvent e) {
        Logger.log("Setting up a new project...", Logger.LOG_TYPE.PROJECT);
        FileSchema temp = projectSetup();
        if (temp == null) System.exit(0);
        ph.loadSchema(temp);
        canvas.cleanCanvas();
        try {
          canvas.newCanvas(ph.getCurrent().dimension);
        } catch (InUseCanvasException err) {
          Sentry.captureException(err);
          Logger.log("Failed to load canvas", Logger.LOG_TYPE.ERROR);
          new PopUpMessage(
            "Failed to load project",
            "An unrecoverable error occured at initial project loading.",
            "popup/icons8-high-risk-500.svg"
          ).openGetResult();
          System.exit(14);
        }
      }
    }
  ),
  SAVE_PROJECT(
    new AbstractAction("Save Project") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "SAVE_PROJECT event",
          "ui/event"
        );
        
        try {
          ProjectHandler.getInstance().getCurrent().getProjectLocation();
        } catch (Exception err) {
          new PopUpMessage(
            "No project location",
            "You have not saved this project before, please use Save Project As",
            "popup/icons8-high-risk-500.svg"
          ).openGetResult();
          return;
        }
          
        try {
          ph.saveCurrentProject(canvas);
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          Logger.log("Failed to write file to disk", Logger.LOG_TYPE.ERROR);
          // TODO some failed to write file box
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  SAVE_PROJECT_AS(
    new AbstractAction("Save Project As") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "SAVE_PROJECT_AS event",
          "ui/event"
        );
        FileDialog fd = new FileDialog(
          "Save Project As...",
          new FileNameExtensionFilter("Editor Project", "epj"),
          FileDialog.WindowType.SAVE,
          FileDialog.FILES_ONLY
        );
        
        try {
          Path str = fd.getResult();
          File file = str.toFile();
          if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("epj")) {
            // filename is OK as-is
          } else {
            file = new File(file.toString() + ".epj");
            file =
              new File(
                file.getParentFile(),
                FilenameUtils.getBaseName(file.getName()) + ".epj"
              );
          }
          ph.saveCurrentProject(canvas, file.toPath());
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          Sentry.captureException(err); // TODO might be redundant in a transaction i will check
          Logger.log("Failed to write file to disk", Logger.LOG_TYPE.ERROR);
          // TODO some failed to write file box
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  OPEN_PROJECT(
    new AbstractAction("Open Project") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "OPEN_PROJECT event",
          "ui/event"
        );
        
        try {
          FileDialog fd = new FileDialog(
            "Open Project...",
            new FileNameExtensionFilter("Editor Project", "epj"),
            FileDialog.WindowType.OPEN,
            FileDialog.FILES_ONLY
          );
          Path pt = fd.getResult();
          FileSchema newProject = ph.readProject(pt);
          canvas.cleanCanvas();
          try {
            canvas.loadCanvas(newProject.getCanvas());
          } catch (InUseCanvasException err) {
            transaction.setThrowable(err);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            Sentry.captureException(err);
            Logger.log(
              "The Canvas was not free before loading data",
              Logger.LOG_TYPE.ERROR
            );
            transaction.finish();
            throw new RuntimeException(err);
          }
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          Sentry.captureException(err);
          Logger.log("Failed to read file", Logger.LOG_TYPE.ERROR);
          // TODO file fail to read error pop-up
        } catch (ClassNotFoundException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.INTERNAL_ERROR);
          Sentry.captureException(err);
          // TODO program severe error pop-up
          Logger.log(
            "A severe error has occured, check the program is installed properly and try again.",
            Logger.LOG_TYPE.ERROR
          );
          transaction.finish();
          throw new RuntimeException(err);
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  OPEN_FILE(
    new AbstractAction("Open File") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction("OPEN_FILE event", "ui/event");
        FileDialog fd = new FileDialog(
          "Open Project...",
          new FileNameExtensionFilter("Image Files (Supported Only)", "bmp", "png", "jpg", "jpeg"),
          FileDialog.WindowType.OPEN,
          FileDialog.FILES_ONLY
        );
        Path target = fd.getResult();
        if (
          FilenameUtils.getExtension(target.toFile().getName()).equalsIgnoreCase("bmp") ||
          FilenameUtils.getExtension(target.toFile().getName()).equalsIgnoreCase("png") ||
          FilenameUtils.getExtension(target.toFile().getName()).equalsIgnoreCase("jpg") ||
          FilenameUtils.getExtension(target.toFile().getName()).equalsIgnoreCase("jpeg")
        ) {
          try {
            BufferedImage file = FileType.importData(target);
            canvas.cleanCanvas();
            try {
              canvas.loadCanvas(file);
              canvas.cleanCache();
            } catch (InUseCanvasException err) {
              transaction.setThrowable(err);
              transaction.setStatus(SpanStatus.INTERNAL_ERROR);
              Sentry.captureException(err);
              Logger.log(
                "The Canvas was not free before loading data",
                Logger.LOG_TYPE.ERROR
              );
              transaction.finish();
              throw new RuntimeException(err);
            }
          } catch (FileNotFoundException err) {
            Logger.log("File not found", Logger.LOG_TYPE.WARN);
            new PopUpMessage(
              "Error",
              "Failed to read the file, it does not exist.",
              "popup/icons8-low-risk-500.svg"
            )
              .openGetResult();
            transaction.finish();
            return;
          } catch (IOException err) {
            transaction.setThrowable(err);
            transaction.setStatus(SpanStatus.DATA_LOSS);
            Sentry.captureException(err);
            Logger.log("Failed to read file", Logger.LOG_TYPE.ERROR);
            new PopUpMessage(
              "Error",
              "Failed to read the file, it may be corupt.",
              "popup/icons8-error-500.svg"
            )
              .openGetResult();
          } finally {
            transaction.finish();
          }
        } else {
          transaction.setThrowable(
            new IllegalArgumentException("Attempted to give unsupported file type")
          );
          transaction.setStatus(SpanStatus.INVALID_ARGUMENT);
          Logger.log("File is not a supported image file", Logger.LOG_TYPE.ERROR);
          new PopUpMessage(
            "Error",
            "Failed to read the file, the file type is not supported by PE.",
            "popup/icons8-low-risk-500.svg"
          )
            .openGetResult();
          transaction.finish();
          return;
        }
      }
    }
  ),
  EXPORT_BMP(
    new AbstractAction("To Bitmap") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "EXPORT_BMP event",
          "ui/event"
        );
        FileDialog fd = new FileDialog(
          "Export To...",
          new FileNameExtensionFilter("Bitmap Image", "bmp"),
          FileDialog.WindowType.SAVE,
          FileDialog.FILES_ONLY
        );
        try {
          Path str = fd.getResult();
          File file = str.toFile();
          if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("bmp")) {
            // filename is OK as-is
          } else {
            file = new File(file.toString() + ".bmp");
            // file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".bmp");
          }
          new BMPHandler().exportData(file.toPath(), canvas.getCanvas());
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          new PopUpMessage(
            err,
            "Failed to write the file to the disk.",
            "popup/icons8-error-500.svg",
            Sentry.captureException(err)
          )
            .openGetResult();
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  EXPORT_PNG(
    new AbstractAction("To PNG") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "EXPORT_PNG event",
          "ui/event"
        );
        FileDialog fd = new FileDialog(
          "Export To...",
          new FileNameExtensionFilter("Portable Network Graphics Image", "png"),
          FileDialog.WindowType.SAVE,
          FileDialog.FILES_ONLY
        );
        try {
          Path str = fd.getResult();
          File file = str.toFile();
          if (
            !(FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("png"))
          ) file = new File(file.toString() + ".png");
          new PNGHandler().exportData(file.toPath(), canvas.getCanvas());
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          // new ErrorDialog(Sentry.captureMessage("Failed to write file to disk"));
          // new PopUpMessage("Error", "Failed to write the file to the disk.", "popup/icons8-error-500.svg").openGetResult();
          new PopUpMessage(
            err,
            "Failed to write the file to the disk.",
            "popup/icons8-error-500.svg",
            Sentry.captureException(err)
          )
            .openGetResult();
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  EXPORT_JPEG(
    new AbstractAction("To JPEG") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "EXPORT_JPEG event",
          "ui/event"
        );
        FileDialog fd = new FileDialog(
          "Export To...",
          new FileNameExtensionFilter("JPEG", "jpg", "jpeg"),
          FileDialog.WindowType.SAVE,
          FileDialog.FILES_ONLY
        );
        try {
          Path str = fd.getResult();
          File file = str.toFile();
          if (
            !(
              FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpg") ||
              FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpeg")
            )
          ) file = new File(file.toString() + ".jpg");
          new JPEGHandler().exportData(file.toPath(), canvas.getCanvas());
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          // new ErrorDialog(Sentry.captureMessage("Failed to write file to disk"));
          // new PopUpMessage("Error", "Failed to write the file to the disk.", "popup/icons8-error-500.svg").openGetResult();
          new PopUpMessage(
            err,
            "Failed to write the file to the disk.",
            "popup/icons8-error-500.svg",
            Sentry.captureException(err)
          )
            .openGetResult();
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  EXPORT_JPEG_QUALITY(
    new AbstractAction("To JPEG (quality)") {
      @Override
      public void actionPerformed(ActionEvent e) {
        ITransaction transaction = Sentry.startTransaction(
          "EXPORT_JPEG event",
          "ui/event"
        );
        // float quality = new JPEGOptions((java.awt.Frame)null).getQuality();
        float quality = new JPEGOptions().getQuality();
        FileDialog fd = new FileDialog(
          "Export To...",
          new FileNameExtensionFilter("JPEG", "jpg", "jpeg"),
          FileDialog.WindowType.SAVE,
          FileDialog.FILES_ONLY
        );
        try {
          Path str = fd.getResult();
          File file = str.toFile();
          if (
            !(
              FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpg") ||
              FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpeg")
            )
          ) file = new File(file.toString() + ".jpg");
          new JPEGHandler()
            .exportData(file.toPath(), canvas.getCanvas(), quality);
        } catch (IOException err) {
          transaction.setThrowable(err);
          transaction.setStatus(SpanStatus.DATA_LOSS);
          // new ErrorDialog(Sentry.captureMessage("Failed to write file to disk"));
          // new PopUpMessage("Error", "Failed to write the file to the disk.", "popup/icons8-error-500.svg").openGetResult();
          new PopUpMessage(
            err,
            "Failed to write the file to the disk.",
            "popup/icons8-error-500.svg",
            Sentry.captureException(err)
          )
            .openGetResult();
        } finally {
          transaction.finish();
        }
      }
    }
  ),
  UNDO( // TODO: this is temporary, replace me!
    new AbstractAction("Undo") {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (HistoryManager.getInstance().undo()) Logger.log(
          "Undo successful",
          Logger.LOG_TYPE.INFO
        ); else Logger.log("Undo failed", Logger.LOG_TYPE.WARN);
      }
    }
  ),
  REDO( // TODO: this is temporary, replace me!
    new AbstractAction("Redo") {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (HistoryManager.getInstance().redoAvailable()) {
          if (HistoryManager.getInstance().redo()) Logger.log(
            "Redo successful",
            Logger.LOG_TYPE.INFO
          ); else Logger.log("Redo failed", Logger.LOG_TYPE.WARN);
        } else {
          new PopUpMessage(
            "No changes to redo",
            "You have no changes to redo",
            "popup/icons8-info-500.svg"
          )
            .openGetResult();
        }
      }
    }
  ),

  COPY(
    new AbstractAction("Copy") {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (canvas.getSelection() != null) {
          canvas.setTransformAction(new ClipboardTransformAction(canvas.getSelection(), false));
        }
      }
    }
  ),

  CUT(
          new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (canvas.getSelection() != null) {
                canvas.setTransformAction(new ClipboardTransformAction(canvas.getSelection(), true));
              }
            }
          }
  ),

  PASTE(
          new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (canvas.getTransformAction() instanceof ClipboardTransformAction cta) {
                cta.enablePasting();
              }
            }
          }
  ),

  ZOOM_IN(
    new AbstractAction("Zoom (+)") {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (canvas.getScale() <= 8) {
          canvas.setScale(canvas.getScale()*2);
        }
      }
    }
  ),

  ZOOM_OUT(
          new AbstractAction("Zoom (-)") {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (canvas.getScale() >= 0.5) {
                canvas.setScale(canvas.getScale()/2);
              }
            }
          }
  ),

  SEND_FEEDBACK(
    new AbstractAction("Send Feedback") {
      @Override
      public void actionPerformed(ActionEvent e) {
        SentryId sentryId = Sentry.captureMessage("Send feedback button pressed");
        javax.swing.JFrame deadFrame = null;
        new FeedbackDialog(deadFrame, sentryId);
      }
    }
  ),

  GRID_LINES(
    new AbstractAction("Toggle Gridlines") {
      @Override
      public void actionPerformed(ActionEvent e) {
        canvas.drawGridlines();
        

    }
  }
  ),
  FILL_BG(
    new AbstractAction("Fill Background") {
      @Override
      public void actionPerformed(ActionEvent e) {
        canvas.fillBg(com.group31.editor.tool.util.Colour.getActiveColour());
      }
    }
  ),
  TOGGLE_TP(
    new AbstractAction("Toggle Transparency Grid") {
      @Override
      public void actionPerformed(ActionEvent e) {
        canvas.toggleTransparency();
      }
    }
  );

  private static final Canvas canvas = Canvas.getInstance();
  private final AbstractAction action;
  private static final ProjectHandler ph = ProjectHandler.getInstance();

  private MenuEvent(AbstractAction action) {
    this.action = action;
  }

  public AbstractAction getAction() {
    return this.action;
  }

  private static FileSchema projectSetup() {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "PE changing project",
      "core"
    );
    JFrame deadFrame = null;
    NewProjectDialog dialog = new NewProjectDialog(deadFrame);
    dialog.setVisible(true);
    dialog.setAutoRequestFocus(true);
    dialog.toFront();
    return dialog.getOutcome();
  }
}