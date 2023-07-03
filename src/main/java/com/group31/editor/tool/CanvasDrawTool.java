package com.group31.editor.tool;

import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.ui.options.OptionsPanel;

import java.awt.*;
public abstract class CanvasDrawTool extends CanvasUtilityTool {

  protected CanvasDrawTool(String name, String iconName, OptionsPanel optionsPanel) {
    super(name, iconName, optionsPanel);
  }

  public void drawOverlay(Graphics2D g2, Point offset) {}

  public abstract void onCanvasAction(CanvasActionEvent canvasActionEvent);
}
