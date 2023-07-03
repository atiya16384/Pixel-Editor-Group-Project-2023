package com.group31.editor.canvas.error;

public class InUseCanvasException extends Exception {

  public InUseCanvasException() {
    super();
  }

  public InUseCanvasException(String message) {
    super(message);
  }

  public InUseCanvasException(String message, Throwable cause) {
    super(message, cause);
  }
}
