package com.group31.editor.tool;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.ui.options.OptionsPanel;
import com.group31.editor.util.SwingUtilities;
import java.awt.Image;
import javax.swing.*;

public class Tool {

  protected final Canvas canvas = Canvas.getInstance();

  private final String name;
  private final ImageIcon icon;
  private final Image cursorImage;
  private final OptionsPanel optionsPanel;

  Tool(String name, String iconName, OptionsPanel optionsPanel) {
    this.name = name;
    this.icon = SwingUtilities.loadStretchIconFromResource("icons/" + iconName + ".png");
    this.cursorImage =
            SwingUtilities.loadImageFromResource("cursorImages/" + iconName + ".png");
    this.optionsPanel = optionsPanel;
  }

  Tool(String name, String iconName) {
    this(name, iconName, null);
  }

  public String getName() {
    return name;
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public Image getCursorImage() {
    return cursorImage;
  }

  public OptionsPanel getOptionsPanel() {
    if (optionsPanel == null) {
      return OptionsPanel.empty;
    }
    return optionsPanel;
  }

  public String getGuideText() {
    return "";
  }

  @Override
  public String toString() {
    return name;
  }
}
