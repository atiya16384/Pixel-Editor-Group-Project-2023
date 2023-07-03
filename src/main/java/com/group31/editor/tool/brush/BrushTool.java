package com.group31.editor.tool.brush;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.tool.CanvasDrawTool;
import com.group31.editor.tool.util.BrushProfile;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.DrawOptionsPanel;
import com.group31.editor.ui.options.OptionsPanel;
import com.group31.editor.util.Logger;
import com.group31.editor.tool.util.Thickness;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class BrushTool extends CanvasDrawTool {

  protected final Canvas canvas = Canvas.getInstance();
  protected int lastToolUpdate = -1;
  protected int resizeCache = -1;
  protected BufferedImage cachedProfile;

  public BrushTool() {
    super("Brush", "brush", new DrawOptionsPanel("Brush"));
  }
  public BrushTool(String name, String iconName, OptionsPanel optionsPanel) {
    super(name, iconName, optionsPanel);
  }

  protected void regen(int brush) {
    URL location = null;
    BufferedImage img;

    this.resizeCache = (int) Math.round(
      42 * Thickness.getActiveThickness() / 40
    );

    this.cachedProfile = new BufferedImage(
      this.resizeCache,
      this.resizeCache,
      BufferedImage.TYPE_INT_RGB
    );

    Graphics2D g2d = cachedProfile.createGraphics();
    g2d.addRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY
      )
    );
    
    location = BrushTool.class.getResource("/brushProfiles/profile_"+brush+".png");
    try {
      if (location == null) return;
      img = ImageIO.read(location);
      g2d.drawImage(img, 0, 0, this.resizeCache, this.resizeCache, null);
    } catch (IOException e) {
      Logger.log("Could not load brush profile " + location, Logger.LOG_TYPE.ERROR);
    }
  }

  protected void draw(Point p) {
    int brush = BrushProfile.active;

    if (lastToolUpdate != brush)
      this.regen(brush);

    for (var i = 0; i < this.resizeCache; i++) {
      for (var j = 0; j < this.resizeCache; j++) {
        Color pixel = new Color(cachedProfile.getRGB(i, j), true);
        var x = (int) Math.round(p.getX() + i - (this.resizeCache / 2));
        var y = (int) Math.round(p.getY() + j - (this.resizeCache / 2));
        Color current = new Color(canvas.getPixel(x,y));
        if (
          (pixel.getRed() == 0) &&
          (pixel.getBlue() == 0) &&
          (pixel.getGreen() == 0) &&
          (canvas.isOnCanvas(x, y)) &&
          ((current.getRed() != pixel.getRed()) && (current.getBlue() != pixel.getBlue()) && (current.getGreen() != pixel.getGreen()))
        ) {
          canvas.setPixel(x, y, Colour.getActiveColour());
        }
      }
    }
  }

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    switch (ev.action()) {
      case PRESS, DRAG -> draw(ev.point());
    }
  }

  @Override
  public String getGuideText() {
    return "Brush: Click and drag the brush to create simple drawings, with different thickness and brush profiles found on the left of the pixel editor.";
  }
}

