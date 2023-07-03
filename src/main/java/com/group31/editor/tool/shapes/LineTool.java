package com.group31.editor.tool.shapes;

import com.group31.editor.tool.util.Colour;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;

public class LineTool extends ShapeTool {

  public LineTool() {
    super(
        "Line",
        "line",
        new com.group31.editor.ui.options.ShapeOptionsPanel("Line"));
  }

  @Override
  protected void drawShape(Graphics2D g2, Point min, Point max, Point offset) {
    // set color to selected color
    g2.setColor(Colour.getActiveColour());
    // set stroke
    g2.setStroke(new BasicStroke(1.0f));

    g2.drawLine(offset.x + min.x,
            offset.y + min.y,
            offset.x + max.x,
            offset.y + max.y);
  }

  @Override
  public String getGuideText() {
    return "Line Tool: Click and drag the line inwards/outwards to decrease/increase the size and change direction.";
  }
}
