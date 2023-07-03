package com.group31.editor.data;

public record FileHistoryEntry(
  byte[] store,
  FileHistory.Actions action,
  java.util.Date time,
  Integer layer
)
  implements java.io.Serializable {
  public java.awt.image.BufferedImage getStore() throws java.io.IOException {
    return ImageTools.toBufferedImage(store);
  }
  @Override
  public String toString() {
    return String.format("FileHistoryEntry{action=%s, time=%s}", action, time);
  }
}
