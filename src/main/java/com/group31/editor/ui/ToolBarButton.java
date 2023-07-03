package com.group31.editor.ui;

import com.group31.editor.ui.guide.Guidable;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ToolBarButton extends JButton implements Guidable {

  private final int key;
  private final String guideText;

  public ToolBarButton(int key, String toolName, ImageIcon icon) {
    this(key, toolName, icon, "");
  }

  public ToolBarButton(int key, String toolName, ImageIcon icon, String guideText) {
    super(icon);
    this.key = key;
    this.guideText = guideText;
    setOpaque(true);
    setBorderPainted(false);
    setBackground(null);
    setToolTipText(toolName);
  }

  @Override
  public String getGuideText() {
    return guideText;
  }

  public void setIsSelected(boolean isSelected) {
    if (isSelected) setBackground(new Color(130, 137, 141)); else setBackground(null);
  }

  public int getKey() {
    return key;
  }
}
