package com.group31.editor.canvas.error;

public class CanvasLimitation extends Exception {

  public CanvasLimitation() {
    super("You may only have a single Canvas instance at a time");
  }

  public CanvasLimitation(String message) {
    super(message);
  }

  public CanvasLimitation(String message, Throwable cause) {
    super(message, cause);
  }
}
