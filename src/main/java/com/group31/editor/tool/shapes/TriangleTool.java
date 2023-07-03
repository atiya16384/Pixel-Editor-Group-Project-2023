package com.group31.editor.tool.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.DrawOptionsPanel;
import com.group31.editor.ui.options.ShapeOptionsPanel;

public class TriangleTool extends ShapeTool {

  public TriangleTool() {
    super(
            "Triangle",
            "triangle",
            new ShapeOptionsPanel("Triangle"));
  }

  @Override
  protected void drawShape(Graphics2D g2, Point min, Point max, Point offset) {
    var rtl = this.getStartPoint().equals(max);
    int[] x = {
            offset.x + min.x,
            offset.x + max.x,
            offset.x + min.x + ((max.x - min.x) / 2)
    };
    int[] y = {
            offset.y + (!rtl ? min.y : max.y),
            offset.y + (!rtl ? min.y : max.y),
            offset.y + (!rtl ? max.y : min.y)
    };
    var polygon = new Polygon(x, y, 3);
    g2.setColor(Colour.getActiveFillColour());
    g2.fill(polygon);
    g2.setColor(Colour.getActiveColour());
    g2.setStroke(new BasicStroke(1.0f));
    g2.draw(polygon);
  }

  @Override
  public String getGuideText() {
    return "Triangle Tool: Click and drag the shape inwards/outwards to decrease/increase the size, up/down/left/right to change orientation of shape and release when finished.";
  }

}
