package com.group31.editor.util;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import java.awt.event.ActionEvent;

public class PrefixNavigationFilter extends NavigationFilter {
  private final int prefixLength;
  private final Action deletePrevious;

  public PrefixNavigationFilter(int prefixLength, JTextComponent component) {
    this.prefixLength = prefixLength;
    deletePrevious = component.getActionMap().get("delete-previous");
    component.getActionMap().put("delete-previous", new BackspaceAction());
    component.setCaretPosition(prefixLength);
  }

  @Override
  public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
    fb.setDot(Math.max(dot, prefixLength), bias);
  }

  @Override
  public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
    fb.moveDot(Math.max(dot, prefixLength), bias);
  }

  class BackspaceAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = (JTextComponent) e.getSource();

      if (component.getCaretPosition() > prefixLength) {
        deletePrevious.actionPerformed(null);
      }
    }
  }
}

