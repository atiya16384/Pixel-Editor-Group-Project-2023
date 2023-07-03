package com.group31.editor.tool.shapes;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.DrawOptionsPanel;
import com.group31.editor.ui.options.ShapeOptionsPanel;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class EllipseTool extends ShapeTool {

  public EllipseTool() {
    super(
        "Ellipse",
        "circle",
        new ShapeOptionsPanel("Ellipse"));
  }

  @Override
  protected void drawShape(Graphics2D g2, Point min, Point max, Point offset) {
    var bottomUp = min.y > max.y;
    var ellipse = new Ellipse2D.Float(
            offset.x + min.x,
            offset.y + (!bottomUp ? min.y : max.y),
            max.x - min.x,
            !bottomUp ? max.y - min.y : min.y - max.y);
    g2.setColor(Colour.getActiveFillColour());
    g2.fill(ellipse);
    g2.setColor(Colour.getActiveColour());
    g2.setStroke(new BasicStroke(1.0f));
    g2.draw(ellipse);
  }

  @Override
  public String getGuideText() {
    return "Ellipse Tool: Click and drag the shape inwards/outwards to decrease/increase the size, up/down/left/right to change orientation of shape and release when finished.";
  }
}
