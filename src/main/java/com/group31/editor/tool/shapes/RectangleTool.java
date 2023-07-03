package com.group31.editor.tool.shapes;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.DrawOptionsPanel;
import com.group31.editor.ui.options.ShapeOptionsPanel;

import java.awt.*;

public class RectangleTool extends ShapeTool {

  public RectangleTool() {
    super(
        "Rectangle",
        "rectangle",
        new ShapeOptionsPanel("Rectangle"));
  }

  @Override
  protected void drawShape(Graphics2D g2, Point min, Point max, Point offset) {
    var bottomUp = min.y > max.y;
    var rect = new Rectangle(
            offset.x + min.x,
            offset.y + (!bottomUp ? min.y : max.y),
            max.x - min.x,
            !bottomUp ? max.y - min.y : min.y - max.y);
    g2.setColor(Colour.getActiveFillColour());
    g2.fill(rect);
    g2.setColor(Colour.getActiveColour());
    g2.setStroke(new BasicStroke(1.0f));
    g2.draw(rect);
  }

  @Override
  public String getGuideText() {
    return "Rectangle Tool: Click and drag the shape inwards/outwards to decrease/increase the size, up/down/left/right to change orientation of shape and release when finished.";
  }
}
