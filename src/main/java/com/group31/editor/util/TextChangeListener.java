package com.group31.editor.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface TextChangeListener extends DocumentListener {
  void update(DocumentEvent e);

  @Override
  default void insertUpdate(DocumentEvent e) {
    update(e);
  }

  @Override
  default void removeUpdate(DocumentEvent e) {
    update(e);
  }

  @Override
  default void changedUpdate(DocumentEvent e) {
    update(e);
  }
}
