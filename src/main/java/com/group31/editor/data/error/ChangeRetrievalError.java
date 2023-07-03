package com.group31.editor.data.error;

public class ChangeRetrievalError extends ArrayIndexOutOfBoundsException {

  public ChangeRetrievalError() {
    super("There was an issue attempting to retrieve a change.");
  }

  public ChangeRetrievalError(String message) {
    super(message);
  }
}
