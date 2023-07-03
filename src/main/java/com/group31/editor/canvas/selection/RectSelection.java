package com.group31.editor.canvas.selection;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.util.Util2D;

import java.awt.*;

public class RectSelection implements Selection {

  private final Point startPoint;
  private Point endPoint;

  public RectSelection(Point startPoint) {
    this.startPoint = startPoint;
  }

  @Override
  public Point getStartPoint() {
    return this.startPoint;
  }

  @Override
  public Point getEndPoint() {
    return this.endPoint;
  }

  public void setEndPoint(Point endPoint) {
    this.endPoint = endPoint;
    Canvas.getInstance().fireSelectionListeners();
    Canvas.getInstance().updateCanvas();
  }

  @Override
  public Rectangle getBounds() {
    if (startPoint == null || endPoint == null) return null;
    return new Rectangle(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
  }

  public void moveSelection(int dx, int dy) {
    this.startPoint.translate(dx, dy);
    this.endPoint.translate(dx, dy);
  }

  @Override
  public boolean isSelected(int x, int y) {
    Point min = Util2D.min(startPoint, endPoint);
    Point max = Util2D.max(startPoint, endPoint);
    return x >= min.x && x < max.x && y >= min.y && y < max.y;
  }

  @Override
  public Point[] getSelectedPixels() {
    return new Point[0];
  }

  @Override
  public void drawOverlay(Graphics2D g2, Point offset) {
    if (startPoint != null && endPoint != null) {
      Point min = Util2D.min(startPoint, endPoint);
      Point max = Util2D.max(startPoint, endPoint);
      g2.setColor(Color.BLACK);
      g2.setStroke(
        new BasicStroke(
          1.0f,
          BasicStroke.CAP_SQUARE,
          BasicStroke.JOIN_MITER,
          10.0f,
          new float[] { 6.0f, 6.0f },
          10.0f
        )
      );
      g2.drawRect(offset.x + min.x, offset.y + min.y, max.x - min.x, max.y - min.y);
    }
  }
}
