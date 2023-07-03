package com.group31.editor.tool;

import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.ui.options.OptionsPanel;

public abstract class CanvasUtilityTool extends Tool {

  protected CanvasUtilityTool(String name, String iconName, OptionsPanel optionsPanel) {
    super(name, iconName, optionsPanel);
  }
  protected CanvasUtilityTool(String name, String iconName) {
    this(name, iconName, null);
  }

  public abstract void onCanvasAction(CanvasActionEvent canvasActionEvent);
}
