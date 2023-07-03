package com.group31.editor.tool.brush;

import com.group31.editor.tool.util.BrushProfile;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.StrokeOptionsPanel;

import java.awt.Color;
import java.awt.Point;

public class EraserTool extends BrushTool {

  public EraserTool() {
    super("Eraser", "rubber", new StrokeOptionsPanel("Eraser"));
  }

  @Override
  protected void draw(Point p) {
    int brush = BrushProfile.active;

    if (lastToolUpdate != brush)
      this.regen(brush);

    for (var i = 0; i < this.resizeCache; i++) {
      for (var j = 0; j < this.resizeCache; j++) {
        Color pixel = new Color(this.cachedProfile.getRGB(i, j), true);
        var x = (int) Math.round(p.getX() + i - (this.resizeCache / 2));
        var y = (int) Math.round(p.getY() + j - (this.resizeCache / 2));
        if (
          (pixel.getRed() == 0) &&
          (pixel.getBlue() == 0) &&
          (pixel.getGreen() == 0) &&
          (canvas.isOnCanvas(x, y))
        ) {
          canvas.setPixel(x, y, new Colour(255, 255, 255, 0));
        }
      }
    }
  }

  @Override
  public String getGuideText() {
    return "Eraser: Click and drag the image to erase pixels within the eraser stroke. You are able to increase or decrease the thickness of the eraser.";
  }
}
