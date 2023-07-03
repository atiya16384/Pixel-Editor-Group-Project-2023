package com.group31.editor.canvas.error;

public class CanvasNotReady extends RuntimeException {

  public CanvasNotReady() {
    super("newCanvas hasn't been called");
  }

  public CanvasNotReady(String message) {
    super(message);
  }

  public CanvasNotReady(String message, Throwable cause) {
    super(message, cause);
  }
}
