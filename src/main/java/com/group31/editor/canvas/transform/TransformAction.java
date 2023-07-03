package com.group31.editor.canvas.transform;

import com.group31.editor.canvas.action.CanvasActionEvent;

import java.awt.Graphics2D;
import java.awt.Point;

public interface TransformAction {
  void onCanvasAction(CanvasActionEvent canvasActionEvent);
  void drawOverlay(Graphics2D g2, Point offset);
}
