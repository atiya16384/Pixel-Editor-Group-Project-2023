package com.group31.editor.ui;

import com.group31.editor.tool.util.Colour;
import javax.swing.*;

public class ColourButton extends JButton {

  public ColourButton(String s, Colour initialColour) {
    super(s);
    this.setFocusable(false);
    this.setBounds(0, 0, 25, 25);
    this.setBorderPainted(false);
    this.setBackground(initialColour);
  }
}
