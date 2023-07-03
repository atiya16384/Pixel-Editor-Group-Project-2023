package com.group31.editor.data;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.data.error.ChangeRetrievalError;
import com.group31.editor.tool.Tool;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class FileHistory implements java.io.Serializable {

  /*
   * YOU AREN'T LOOKING FOR THIS CLASS
   *
   * Want to access history changes? I'm not what you are looking for!
   * Most of the methods here are protected and built to be serialized, use the HistoryManager to
   * interact with the current project, which will offer features such as undo/redo.
   *
   */
  private LinkedList<FileHistoryEntry> entries = new LinkedList<FileHistoryEntry>();

  /**
   * FileHistory.Actions
   *  Enumerates the possible actions that can be performed on the canvas.
   *
   * PAINT  -> Draws to multiple pixels (e.g. brush, shapes)
   * PENCIL -> Draws to a single pixel (e.g. pencil)
   * ERASE  -> Resets a pixel to a defined default value (e.g. eraser)
   * FILL   -> Sets all pixels within a region to a defined value (e.g. fill background, bucket fill)
   */
  public static enum Actions {
    PAINT,
    FILL,
    PENCIL,
    ERASE,
    CLIPBOARD
  }

  /**
   * FileHistory.addNewChange(BufferedImage, Tool)
   *  Records a new change to the file history.
   * @author jennin16
   * @param snapshot The recorded change
   * @param action The tool or action that was performed/used
   */
  protected void addNewChange(BufferedImage snapshot, Actions action) throws IOException {
    entries.addLast(
      new FileHistoryEntry(
        ImageTools.toByteArray(snapshot),
        action,
        new java.util.Date(),
        Canvas.selectedLayer()
      )
    );
  }
  /**
   * FileHistory.addNewChange(BufferedImage, Tool)
   *  Records a new change to the file history.
   * @author jennin16
   * @param snapshot The recorded change
   * @param tool The tool or action that was performed/used
   */
  protected void addNewChange(BufferedImage snapshot, Tool tool) throws IOException {
    entries.addLast(
      new FileHistoryEntry(
        ImageTools.toByteArray(snapshot),
        toolToAction(tool),
        new java.util.Date(),
        Canvas.selectedLayer()
      )
    );
  }

  /**
   * FileHistory.getChanges(Integer)
   *  Returns the canvas change history as an array of FileHistoryEntry's.
   * @author jennin16
   * @param seek How many changes to return
   * @returns An array of FileHistoryEntry's
   */
  protected FileHistoryEntry[] getChanges(Integer seek) {
    if (seek == null || seek == 0) return toArray(); // default to all changes
    else if (seek > entries.size())
      throw new ChangeRetrievalError("Unable to seek to entry specified");
    
    ArrayList<FileHistoryEntry> returnEntries = new ArrayList<FileHistoryEntry>();
    for (int i = 0; i < seek; i++) returnEntries.add(this.pop());
    return returnEntries.toArray(new FileHistoryEntry[returnEntries.size()]);
  }

  /**
   * FileHistory.pop()
   *  Removes the latest entry from the history and returns it.
   * @author jennin16
   * @returns The latest entry in the history
   */
  protected FileHistoryEntry pop() {
    if (entries.size() == 0) throw new ChangeRetrievalError(
      "There are no changes in the projects history"
    );
    return entries.removeLast();
  }

  /**
   * FileHistory.getChangeStrings(Integer)
   *  Returns the changes as an array of descriptive strings for UI purposes
   * @author jennin16
   * @param seek How many changes to return
   * @returns Array of strings containing change descriptions
   */
  protected String[] getChangeStrings(Integer seek) {
    if (seek != null) if (seek == 0) return null;
    ArrayList<String> returnEntries = new ArrayList<String>();
    Iterator<FileHistoryEntry> iterator = entries.descendingIterator();
    while (iterator.hasNext()) {
      FileHistoryEntry entry = iterator.next();
      returnEntries.add(
        String.format(
          "%s - %d:%d",
          entry.action().toString(),
          entry.time().getHours(),
          entry.time().getMinutes()
        )
      );
      if (seek != null) if (returnEntries.size() == seek) break;
    }
    return returnEntries.toArray(new String[returnEntries.size()]);
  }

  protected Iterator<FileHistoryEntry> exposeIterator() {
    return entries.descendingIterator();
  }

  /**
   * FileHistory.toolToAction(Tool)
   *  Converts a Tool to an Action for use in FileHistoryEntry records.
   * @param tool
   * @return
   */
  public static Actions toolToAction(Tool tool) {
    if (tool == null) return null;
    switch (tool.toString()) {
      case "Brush":
        return Actions.PENCIL;
      case "EraserTool":
        return Actions.ERASE;
      case "BGFillTool":
        return Actions.FILL;
      case "ClipboardTool":
        return Actions.CLIPBOARD;
      default:
        return null;
    }
  }

  protected FileHistoryEntry[] toArray() {
    return entries.toArray(new FileHistoryEntry[entries.size()]); // List.toArray implements Array.copyOf/System.arraycopy -> O(n)
  }

  public int size() {
    return entries.size();
  }

  @Override
  public String toString() {
    return String.format("FileHistory: %d entries", entries.size());
  }
}
