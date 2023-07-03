package com.group31.editor.ui.headless;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileDialog extends JFileChooser {

  private WindowType action;

  public enum WindowType {
    OPEN,
    SAVE,
  }

  public FileDialog(String title) {
    super(title);
  }

  public FileDialog(String title, int selection) {
    super(title);
    setFileSelectionMode(selection);
  }

  public FileDialog(String title, WindowType type) {
    super(title);
    this.action = type;
  }

  public FileDialog(String title, WindowType type, int selection) {
    super(title);
    this.action = type;
    setFileSelectionMode(selection);
  }

  public FileDialog(String title, FileNameExtensionFilter filter) {
    super(title);
    setFileFilter(filter);
  }

  public FileDialog(String title, FileNameExtensionFilter filter, int selection) {
    super(title);
    setFileFilter(filter);
    setFileSelectionMode(selection);
  }

  public FileDialog(String title, FileNameExtensionFilter filter, WindowType type) {
    super(title);
    setFileFilter(filter);
    this.action = type;
  }

  /**
   * FileDialog(String, FileNameExtensionFilter, WindowType, int)
   * This constructor will create a new FileDialog object with the given title, file filter, window type, and selection mode.
   * Multiple constructors are provided to allow for more flexibility in the creation of the FileDialog object.
   * @author jennin16
   * @param title The title of the window
   * @param filter A file filter for the window (optional)
   * @param type SAVE or OPEN dialog enum (optional)
   * @param selection A window dialog option (optional)
   */
  public FileDialog(
    String title,
    FileNameExtensionFilter filter,
    WindowType type,
    int selection
  ) {
    super(title);
    setFileFilter(filter);
    this.action = type;
    setFileSelectionMode(selection);
  }

  /**
   * getResult()
   *
   * This method will return the path of the file selected by the user.
   * @author jennin16
   * @return Path pointing to selection, could be a file or directory
   */
  public Path getResult() {
    setCurrentDirectory(new java.io.File("user.home"));
    setAcceptAllFileFilterUsed(false); // disable the "All files" option.
    switch (action) {
      case OPEN:
        if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) return Paths.get(
          getSelectedFile().getAbsolutePath()
        );
      case SAVE:
        if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) return Paths.get(
          getSelectedFile().getAbsolutePath()
        );
    }
    return null;
  }
}
