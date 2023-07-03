package com.group31.editor.data;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.ui.ChangesMenu;
import com.group31.editor.ui.event.LayerManager;
import com.group31.editor.ui.headless.PopUpMessage;
import com.group31.editor.util.Logger;
import com.group31.editor.util.SentryReporting;
import io.sentry.Sentry;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

/**
 * HistoryManager
 *
 * Why build it like this?
 *
 * Benefits:
 *  - Significantly lower memory usage compared to GIMP, Photoshop, MSPaint etc.
 *  - Change compiling
 *
 * Disadvantages:
 *  - More complex to implement (but I've done it now, so what's the problem eh?)
 *  - Higher CPU usage (only) during change tracking (impact significantly reduced and mitigated by running change tracking on a seperate thread)
 *  - Can only redo one change (didn't want to implement a redo stack, but I could if I wanted to)
 *
 * HistoryManager (API) <--> ProjectHandler (API) --> FileSchema (Protected) --> FileHistory (Protected) <-- FileHistoryEntry (Protected LL Record)
 *
 * @author jennin16
 * @see com.group31.editor.data.ProjectHandler
 * @see com.group31.editor.data.FileHistory
 */
public class HistoryManager extends Thread {

  private static volatile HistoryManager instance;
  private FileSchema project = ProjectHandler.getInstance().getCurrent();
  private Integer elementCap = 100; // TODO: not implemented
  private volatile LinkedList<HistoryCache> history = new LinkedList<HistoryCache>();
  private volatile BufferedImage redoCache = null;
  private volatile BufferedImage redoChangeCache = null;
  private boolean skipRedoCache = false;

  public record HistoryCache(BufferedImage store, FileHistory.Actions changeType) {}

  public HistoryManager() {
    Logger.log("HistoryManager initialising...", Logger.LOG_TYPE.INFO);
    if (instance != null) throw new RuntimeException(
      "Use getInstance() method to get the single instance of this class."
    );
    setDefaultUncaughtExceptionHandler(
      new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
          new PopUpMessage(
            e,
            "HistoryManager has crashed and been automatically restarted",
            Sentry.captureException(e)
          )
            .openGetResult();
          Logger.log(
            String.format(
              "HistoryManager hit a snag and is being restarted: %s\n Stack trace: ",
              e.getMessage()
            ),
            Logger.LOG_TYPE.ERROR,
            Logger.COLOUR_SET.WARNING
          );
          e.printStackTrace();
          restartManagerThread();
        }
      }
    );
  }

  @Override
  public void run() {
    if (Thread.currentThread().getName() == "Core" || Thread.currentThread().getName() == "main" ) throw new RuntimeException("run() should not be called directly, use start() instead");
    // HistoryManager thread tracks changes throughout the project, runs on seperate thread so change tracking doesn't slow canvas updates
    Thread.currentThread().setName("HistoryManager");
    Logger.log(
      "HistoryManager is awake",
      Logger.LOG_TYPE.INFO,
      Logger.COLOUR_SET.SUCCESS
    );
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.INFO,
      "Initialising HistoryManager",
      "HistoryManager"
    );
    loadProject(); // ensure project loaded before starting
    synchronized (history) {
      try {
        while (true) {
          Logger.log("HistoryManager is watching for changes", Logger.LOG_TYPE.INFO);
          SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.INFO,
            "Entered listening state",
            "HistoryManager"
          );
          history.wait(); // wait for changes to be announced
          Logger.log(
            "HistoryManager is processing changes...",
            Logger.LOG_TYPE.INFO,
            Logger.COLOUR_SET.SELECT
          );
          for (int i = 0; i < history.size(); i++) { // process all pending changes
            HistoryCache entry = history.removeFirst();
            try {
              project.getHistory().addNewChange(entry.store(), entry.changeType()); // add change to project history
            } catch (IOException err) {
              Logger.log(err.getMessage(), Logger.LOG_TYPE.ERROR);
            }
          }
          LayerManager.setChange();
          ChangesMenu.triggerUpdate();
          Logger.log(
            "HistoryManager processed all pending changes",
            Logger.LOG_TYPE.INFO,
            Logger.COLOUR_SET.SUCCESS
          );
        }
      } catch (InterruptedException err) {
        Logger.log(err.getMessage(), Logger.LOG_TYPE.ERROR);
      }
    }
    Logger.log("HistoryManager is shutting down", Logger.LOG_TYPE.INFO);
  }

  /**
   * HistoryManager.loadProject()
   *  Loads the current project from ProjectHandler into HistoryManager
   * @author jennin16
   */
  public void loadProject() {
    project = ProjectHandler.getInstance().getCurrent();
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.INFO,
      "Loaded new project",
      "HistoryManager"
    );
  }

  public String[] getProjectChanges(Integer tailSeek) {
    return project.getHistory().getChangeStrings(tailSeek);
  }

  /**
   * HistoryManager.redoAvailable()
   *  Checks if a redo is available
   * @author jennin16
   * @returns A boolean
   */
  public boolean redoAvailable() { return redoCache != null; }

  /**
   * HistoryManager.redo()
   *  Restores the last change to the canvas
   * @author jennin16
   * @since 0.2.345-CANARY
   * @returns Success boolean
   */
  public boolean redo() {
    if (redoCache == null) return false;
    skipRedoCache = false;
    try {
      Canvas.getInstance().cleanCanvas();
      Canvas.getInstance().loadCanvas(redoCache);
      redoCache = null;
      return true;
    } catch (com.group31.editor.canvas.error.InUseCanvasException err) {
      Logger.log(err.getMessage(), Logger.LOG_TYPE.ERROR);
      SentryReporting.leaveABreadcrumb(
        SentryReporting.BREADCRUMB_TYPE.ERROR,
        "Failed to restore changes",
        "HistoryManager"
      );
      return false;
    }
  }

  /**
   * HistoryManager.undo()
   *  Restores the last change to the canvas
   * @author jennin16
   * @since 0.2.345-CANARY
   * @returns Success boolean
   * @see boolean#restoreChanges(Integer)
   */
  public boolean undo() { return restoreChanges(1); }

  /**
   * HistoryManager.restoreChanges(Integer)
   *  Restores x amount of changes to the canvas by compiling them
   * @author jennin16
   * @since 0.2.345-CANARY
   * @param tailSeek
   * @returns Success boolean
   */
  public boolean restoreChanges(Integer tailSeek) {
    // runs on main thread since restore change is a direct action, we do not expect any other actions to appear during an undo
    BufferedImage bi; // image to load to canvas
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.INFO,
      "Attempting to restore changes",
      "HistoryManager"
    );
    redoCache = Canvas.copyBI(Canvas.getInstance().getCanvas()); // store a snapshot of the canvas to redo the undo change
    try {
      if (redoChangeCache != null && !skipRedoCache) { // Check if last action was an undo
        bi = redoChangeCache;
        skipRedoCache = true;
      } else if (tailSeek == 1) { // else restore one change
        FileHistoryEntry fs = project.getHistory().pop();
        Canvas.getInstance().changeLayer(fs.layer());
        bi =
          ImageTools.biCombine(
            Canvas.getInstance().getCanvas(),
            fs.getStore()
          );
        redoChangeCache = bi;
      } else { // or compile changes together to restore
        bi = Canvas.getInstance().getCanvas();
        for (int i = 0; i < tailSeek; i++) bi =
          ImageTools.biCombine(bi, project.getHistory().pop().getStore());
      }
      Canvas.getInstance().cleanCanvas();
      Canvas.getInstance().loadCanvas(bi); // load whatever changes we decide on
      ChangesMenu.triggerUpdate();
    } catch (IOException err) {
      Logger.log(err.getMessage(), Logger.LOG_TYPE.ERROR);
      new PopUpMessage(
        err,
        "IOException occured during the restoration of changes",
        Sentry.captureException(err)
      )
        .openGetResult();
      SentryReporting.leaveABreadcrumb(
        SentryReporting.BREADCRUMB_TYPE.ERROR,
        "Failed to restore changes",
        "HistoryManager"
      );
      return false;
    } catch (com.group31.editor.canvas.error.InUseCanvasException err) {
      Logger.log(err.getMessage(), Logger.LOG_TYPE.ERROR);
      SentryReporting.leaveABreadcrumb(
        SentryReporting.BREADCRUMB_TYPE.ERROR,
        "Failed to restore changes",
        "HistoryManager"
      );
      return false;
    } catch (com.group31.editor.data.error.ChangeRetrievalError err) { // not fatal
      return false;
    }
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.INFO,
      "Restored changes",
      "HistoryManager"
    );
    return true;
  }

  /**
   * HistoryManager.biCombine(BufferedImage, Actions)
   *  Records a change to the project history.
   * @author jennin16
   * @since 0.2.345-CANARY
   * @param bi BufferedImage containing the change mask to record
   * @param changeType Actions enum containing the type of change
   */
  public void recordChange(BufferedImage bi, FileHistory.Actions changeType) {
    redoCache = null;
    redoChangeCache = null;
    synchronized (history) {
      history.add(new HistoryCache(bi, changeType));
      history.notifyAll();
      Logger.log("Notified manager of pending changes", Logger.LOG_TYPE.INFO);
    }
  }

  /**
   * HistoryManager.setCap(Integer)
   *  Sets the maximum amount of changes that can be stored
   * @author jennin16
   * @since 0.2.345-CANARY
   * @deprecated
   * @param cap
   */
  public void setCap(Integer cap) { this.elementCap = cap; }

  /*
   * getInstance - Share a singe instance of a class with easy location @gaborbata 2014
   */
  public static HistoryManager getInstance() {
    if (instance == null) {
      synchronized (HistoryManager.class) {
        if (instance == null) {
          instance = new HistoryManager();
        }
      }
    }
    return instance;
  }

  protected static void restartManagerThread() {
    instance = null;
    HistoryManager.getInstance().start();
  }

  public int sizeof() {
    return project.getHistory().size();
  }
}
