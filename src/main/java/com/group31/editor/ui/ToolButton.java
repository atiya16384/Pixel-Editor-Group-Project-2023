package com.group31.editor.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ToolButton extends JButton {

  private ImageIcon icon;
  private ImageIcon onClick;
  private String labelOnHover;

  public ToolButton(ImageIcon icon, String labelOnHover) {
    super();
    this.icon = icon;
    this.labelOnHover = labelOnHover;
  }

  public void runAction() {}
}
