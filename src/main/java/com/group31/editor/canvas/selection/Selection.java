package com.group31.editor.canvas.selection;

import java.awt.*;

public interface Selection {
  Point getStartPoint();
  Point getEndPoint();
  Rectangle getBounds();
  boolean isSelected(int x, int y);
  Point[] getSelectedPixels();
  void moveSelection(int dx, int dy);
  void drawOverlay(Graphics2D g2, Point offset);
}
