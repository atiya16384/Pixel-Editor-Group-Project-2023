package com.group31.editor.canvas.action;

public record CanvasActionEvent(
  CanvasAction action,
  java.awt.Point point
) {}
