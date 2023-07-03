package com.group31.editor.canvas.selection;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.util.Util2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LassoSelection implements Selection {

  private List<Point> points;
  private Polygon polygon;
  private Point mousePosition;

  public LassoSelection(Point startPoint) {
    points = new ArrayList<>(List.of(startPoint));
  }

  public void addPoint(Point point) {
    if (points.size() > 1 && !isFinished()) {
      if (point.distance(points.get(0)) <= 5) {
        finish();
      } else {
        var sp = points.get(points.size() - 1);
        for (int i = 1; i < points.size(); i++) {
          var p0 = points.get(i - 1);
          var p1 = points.get(i);
          if (
            !p1.equals(sp) &&
            Line2D.linesIntersect(p0.x, p0.y, p1.x, p1.y, sp.x, sp.y, point.x, point.y)
          ) {
            finish();
            break;
          }
        }
      }
    }
    if (!isFinished()) {
      points.add(point);
    }
    Canvas.getInstance().updateCanvas();
  }

  public void setMousePosition(Point point) {
    this.mousePosition = point;
    Canvas.getInstance().updateCanvas();
  }

  private void finish() {
    points.add(points.get(0));
    polygon = new Polygon(getXPoints(), getYPoints(), points.size());
    Canvas.getInstance().fireSelectionListeners();
  }

  public boolean isFinished() {
    return polygon != null;
  }

  private int[] getXPoints() {
    return getXPoints(new Point());
  }

  private int[] getXPoints(Point offset) {
    return points
      .stream()
      .map(point -> Util2D.translate(point, offset))
      .mapToInt(point -> point.x)
      .toArray();
  }

  private int[] getYPoints() {
    return getYPoints(new Point());
  }

  private int[] getYPoints(Point offset) {
    return points
      .stream()
      .map(point -> Util2D.translate(point, offset))
      .mapToInt(point -> point.y)
      .toArray();
  }

  public void moveSelection(int dx, int dy) {
    polygon.translate(dx, dy);
  }

  @Override
  public Point getStartPoint() {
    if (polygon == null) return null;
    return polygon.getBounds().getLocation();
  }

  @Override
  public Point getEndPoint() {
    if (polygon == null) return null;
    return Util2D.translate(getStartPoint(), polygon.getBounds().width, polygon.getBounds().height);
  }

  @Override
  public Rectangle getBounds() {
    if (polygon == null) return null;
    return polygon.getBounds();
  }

  @Override
  public boolean isSelected(int x, int y) {
    if (polygon == null) return false;
    return polygon.contains(x, y);
  }

  @Override
  public Point[] getSelectedPixels() {
    return new Point[0];
  }

  @Override
  public void drawOverlay(Graphics2D g2, Point offset) {
    if (!points.isEmpty()) {
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
      if (isFinished()) {
        g2.drawPolygon(Util2D.translate(polygon, offset));
      } else {
        g2.drawPolyline(getXPoints(offset), getYPoints(offset), points.size());

        if (this.mousePosition != null) {
          var lp = Util2D.translate(points.get(points.size() - 1), offset);
          var mp = Util2D.translate(mousePosition, offset);
          g2.drawLine(lp.x, lp.y, mp.x, mp.y);
        }
      }
    }
  }

  public boolean isSelected(Point p) {
    return isSelected(p.x, p.y);
  }

  public void lassoifyImage(BufferedImage image) {
    var transparent = new Color(255, 255, 255, 0).getRGB();
    var startPoint = getStartPoint();
    for (
            int x = 0;
            x < image.getWidth();
            x++
    ) {
      for (
              int y = 0;
              y < image.getHeight();
              y++
      ) {
        if (!isSelected(Util2D.translate(startPoint, x, y))) {
          image.setRGB(x, y, transparent);
        }
      }
    }
  }
}
