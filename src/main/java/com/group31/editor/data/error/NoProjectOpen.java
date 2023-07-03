package com.group31.editor.data.error;

public class NoProjectOpen extends RuntimeException {

  public NoProjectOpen() {
    super("There is no open project in the FileHandler");
  }

  public NoProjectOpen(String message) {
    super(message);
  }

  public NoProjectOpen(String message, Throwable cause) {
    super(message, cause);
  }
}
