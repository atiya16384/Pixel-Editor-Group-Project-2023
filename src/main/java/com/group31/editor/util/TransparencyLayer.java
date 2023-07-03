package com.group31.editor.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransparencyLayer {

  private final TexturePaint paint;

  public TransparencyLayer(int squareSize) {
    BufferedImage bi = new BufferedImage(squareSize*2, squareSize*2, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bi.createGraphics();
    g2.setColor(Color.white);
    g2.fillRect(0, 0, squareSize*2, squareSize*2);
    g2.setColor(new Color(0, 0, 0, 127));
    g2.fillRect(0, squareSize, squareSize, squareSize);
    g2.fillRect(squareSize, 0, squareSize, squareSize);
    Rectangle anchor = new Rectangle(0, 0, squareSize*2, squareSize*2);
    paint = new TexturePaint(bi, anchor);
  }

  public void draw(Graphics2D g2, int width, int height, Point offset) {
    g2.setPaint(paint);
//    TODO: find out if its actually drawing the dimensions of the rect or dimensions-stroke (see what drawRect does...)
    g2.fillRect(offset.x, offset.y, width, height);
  }
}
