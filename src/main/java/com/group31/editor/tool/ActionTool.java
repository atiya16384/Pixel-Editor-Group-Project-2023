package com.group31.editor.tool;

public abstract class ActionTool extends Tool {

  ActionTool(String name, String iconName) {
    super(name, iconName);
  }

  public abstract void onAction();
}
