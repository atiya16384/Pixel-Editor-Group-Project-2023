package com.group31.editor.util;

import java.nio.file.Path;

public interface Logger {
  /**
   * LOG_TYPE
   *   Used to set log title and automatic styling
   */
  public static enum LOG_TYPE {
    WARN,
    ERROR,
    INFO,
    DEBUG,
    FILE,
    CANVAS,
    PROJECT,
  }

  /**
   * COLOUR_SET
   *   Used to set the colour of the log message
   */
  public static enum COLOUR_SET {
    SUCCESS,
    WARNING,
    ERROR,
    SELECT,
    INFO,
    DEFAULT,
  }

  public static enum FILE_OP {
    SAVE,
    OVERWRITE,
    UPDATE,
    DELETE,
    READ,
  }

  /**
   * Logger.clean()
   *   Ends the styling, starts a new line
   * @author jennin16
   */
  private static void clean() {
    System.out.printf("\u001B[0m");
  }

  /**
   * Logger.print(COLOUR_SET, LOG_TYPE, String)
   *   Prints a log message to the screen
   * @author jennin16
   * @param col
   * @param type
   * @param msg
   */
  private static void print(COLOUR_SET col, LOG_TYPE type, String msg) {
    System.out.printf(
      "%s[%s]\u001B[0m [%s] %s\n",
      getEsc(col),
      getTitle(type),
      Thread.currentThread().getName(),
      msg
    );
  }

  private static void print(COLOUR_SET col, String type, String msg) {
    System.out.printf(
      "%s[%s]\u001B[0m [%s] %s\n",
      getEsc(col),
      type,
      Thread.currentThread().getName(),
      msg
    );
  }

  /**
   * Logger.log(FILE_OP, Path)
   *   Logs a file operation message
   * @author jennin16
   * @param operation A enum for operation ran
   * @param path Path to edited file
   */
  public static void log(FILE_OP operation, Path path) {
    switch (operation) {
      case SAVE -> System.out.printf(
        "%s[FILE (+)]\u001B[0m Created %s\n",
        getEsc(COLOUR_SET.INFO),
        path.toString()
      );
      case OVERWRITE -> System.out.printf(
        "%s[FILE (!)]\u001B[0m The path %s overwritten on disk.\n",
        getEsc(COLOUR_SET.WARNING),
        path.toString()
      );
      case UPDATE -> System.out.printf(
        "%s[FILE (~)]\u001B[0m Modified %s\n",
        getEsc(COLOUR_SET.INFO),
        path.toString()
      );
      case DELETE -> System.out.printf(
        "%s[FILE (-)]\u001B[0m The path %s was deleted from the disk.\n",
        getEsc(COLOUR_SET.WARNING),
        path.toString()
      );
      case READ -> System.out.printf(
        "%s[FILE (>)]\u001B[0m Read %s\n",
        getEsc(COLOUR_SET.INFO),
        path.toString()
      );
    }
    clean();
  }

  /**
   * Logger.log(String)
   * @author jennin16
   *   Logs a simple string to stdout
   */
  public static void log(String msg) {
    checkMsg(msg);
    System.out.printf(msg + '\n');
    clean();
  }

  /**
   * Logger.log(String, LOG_TYPE)
   *   Logs a simple string to the stdout with default colour settings
   * @param msg Message to show in the console
   * @param type A log type enum (sets the default colour)
   */
  public static void log(String msg, LOG_TYPE type) {
    checkMsg(msg);
    switch (type) {
      case WARN -> print(COLOUR_SET.WARNING, type, msg);
      case ERROR -> print(COLOUR_SET.ERROR, type, msg);
      case INFO -> print(COLOUR_SET.INFO, type, msg);
      case DEBUG -> print(COLOUR_SET.DEFAULT, type, msg);
      case FILE -> print(COLOUR_SET.SELECT, type, msg);
      case CANVAS -> print(COLOUR_SET.INFO, type, msg);
      case PROJECT -> print(COLOUR_SET.SELECT, type, msg);
      default -> print(COLOUR_SET.DEFAULT, type, msg);
    }
    clean();
  }

  /**
   * Logger.log(String, LOG_TYPE)
   *   Logs a simple string to the stdout with custom colour settings
   * @param msg Message to show in the console
   * @param type A log type enum
   * @param col The colour to use
   */
  public static void log(String msg, LOG_TYPE type, COLOUR_SET col) {
    print(col, type, msg);
  }

  public static void log(String msg, String type, COLOUR_SET col) {
    print(col, type, msg);
  }

  /**
   * Logger.getEsc(COLOUR_SET)
   * @author jennin16
   * @param col
   * @return Escape code for given COLOUR_SET
   */
  private static String getEsc(COLOUR_SET col) {
    if (col == null) throw new IllegalArgumentException();
    return switch (col) {
      case SUCCESS -> "\u001B[32m";
      case WARNING -> "\u001B[33m";
      case ERROR -> "\u001B[31m";
      case SELECT -> "\u001b[42m\u001B[31m";
      case INFO -> "\u001B[34m";
      case DEFAULT -> "\u001B[0m";
      default -> "\u001B[0m";
    };
  }

  /**
   * Logger.getTitle(LOG_TYPE)
   * @author jennin16
   * @param type
   * @return The log type string
   */
  private static String getTitle(LOG_TYPE type) {
    if (type == null) throw new IllegalArgumentException();
    return switch (type) {
      case WARN -> "Warning";
      case ERROR -> "ERROR";
      case DEBUG -> "DEBUG";
      case FILE -> "I/O";
      case CANVAS -> "Canvas";
      case PROJECT -> "Project";
      case INFO -> "Info.";
      default -> "";
    };
  }

  /**
   * Logger.checkMsg(String)
   *   Makes sure a valid message is given
   * @param msg
   */
  private static void checkMsg(String msg) {
    if (msg == null || msg.length() == 0) throw new IllegalArgumentException(
      "No String given"
    );
  }
}
