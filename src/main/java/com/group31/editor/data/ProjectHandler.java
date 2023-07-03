package com.group31.editor.data;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.data.error.NoProjectOpen;
import com.group31.editor.ui.UIFrame;
import com.group31.editor.util.*;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectHandler {

  private FileSchema currentProject;
  private static volatile ProjectHandler instance;

  public ProjectHandler() {}

  public ProjectHandler(FileSchema currentProject) {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialised new ProjectHandler",
      "ProjectHandler"
    );
    if (currentProject != null) {
      this.currentProject = currentProject;
      HistoryManager.getInstance().loadProject();
    }
  }

  /**
   * FileHandler.saveCurrentProject(Canvas)
   *   Saves the current project
   * @param canvas The Canvas element to extract image data from
   * @return Success bool
   * @throws IOException
   */
  public boolean saveCurrentProject(Canvas canvas) throws IOException {
    if (currentProject == null) throw new NoProjectOpen();
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Saving active projcet",
      "ProjectHandler"
    );
    Logger.log("Attempting to save current project", Logger.LOG_TYPE.PROJECT);
    currentProject.saveCanvas();
    Logger.log("Project saved", Logger.LOG_TYPE.PROJECT, Logger.COLOUR_SET.SUCCESS);
    return newProject(currentProject.getProjectLocation(), currentProject);
  }

  /**
   * FileHandler.saveCurrentProject(Canvas, Path)
   *   Saves the current project (save-as)
   * @param canvas The Canvas element to extract image data from
   * @return Success bool
   * @throws IOException
   */
  public boolean saveCurrentProject(Canvas canvas, Path path) throws IOException {
    if (currentProject == null) throw new NoProjectOpen();
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Saving active projcet",
      "ProjectHandler"
    );
    Logger.log("Attempting to save current project", Logger.LOG_TYPE.PROJECT);
    currentProject.saveCanvas();
    try {
      currentProject.updateProjectLocation(path, true);
    } catch (FileNotFoundException e) {
      return false;
    } // This should be thrown
    Logger.log("Project saved", Logger.LOG_TYPE.PROJECT, Logger.COLOUR_SET.SUCCESS);
    return newProject(path, currentProject);
  }

  /**
   * newProject(Path, Canvas, FileSchema)
   *   Serializes project data and exports to file
   * @param path A Path to a location on disk
   * @param canvas An initialised Canvas
   * @param schema Project schema
   * @return Boolean success value
   */
  public boolean newProject(Path path, FileSchema schema) throws IOException {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Creating new project file",
      "ProjectHandler"
    );
    try {
      Logger.log("Writing a project to the disk: " + path, Logger.LOG_TYPE.PROJECT);
      FileOutputStream fileOutput = new FileOutputStream(path.toString());
      ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

      objectOutput.writeObject(schema);
      Logger.log(Logger.FILE_OP.SAVE, path);

      objectOutput.close();
      fileOutput.close();

      return true;
    } catch (FileNotFoundException e) {
      return false;
    }
  }

  /**
   * FileHandler.readProject(Path, Canvas, FileSchema)
   *   Reads a serialized project
   * @author jennin16
   * @param path A Path to a location on disk
   * @throws IOException
   * @throws FileNotFoundException
   * @throws ClassNotFoundException
   * @return The read file schema
   */
  public FileSchema readProject(Path path)
    throws IOException, FileNotFoundException, ClassNotFoundException {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.QUERY,
      "Retrieving a project",
      "data/ProjectHandler"
    );
    FileSchema schema;
    if (!Files.exists(path)) throw new FileNotFoundException();

    FileInputStream fileInput = new FileInputStream(path.toFile());
    ObjectInputStream objectInput = new ObjectInputStream(fileInput);
    schema = (FileSchema) objectInput.readObject();
    objectInput.close();
    fileInput.close();

    currentProject = schema;
    HistoryManager.getInstance().loadProject();
    if (UIFrame.isGenerated())
      if (UIFrame.getInstance().isAlive())
        UIFrame
          .getInstance()
          .getJFrame()
          .setTitle("Pixel Editor - " + currentProject.getName());
    return schema;
  }

  /**
   * FileHandler.getCurrent()
   * @author jennin16
   * @return Current FileSchema object
   */
  public FileSchema getCurrent() {
    return currentProject;
  }

  /**
   * FileHandler.getCanvasSize()
   * @author jennin16
   * @return Canvas Dimension object
   */
  public Dimension getCanvasSize() {
    return currentProject.dimension;
  }

  /**
   * FileHandler.getInstance()
   * Locates an existing instance of ProjectHandler
   * @return
   */
  public static ProjectHandler getInstance() {
    return getInstance(null);
  }

  /**
   * FileHandler.getInstance(FileSchema)
   * Locates an existing instance of ProjectHandler or instantiates a new one and sets the instance
   * @param currentProject
   * @return The package-wide instance of ProjectHandler
   */
  public static ProjectHandler getInstance(FileSchema currentProject) {
    if (instance == null) {
      synchronized (ProjectHandler.class) {
        if (instance == null) {
          instance = new ProjectHandler(currentProject);
        }
      }
    }
    return instance;
  }

  public void loadSchema(FileSchema schema) {
    if (schema == null) throw new RuntimeException("Cannot load blank schema.");
    if (schema.dimension == null) throw new RuntimeException("Schema is corrupt.");
    currentProject = schema;
    HistoryManager.getInstance().loadProject();
  }
}
