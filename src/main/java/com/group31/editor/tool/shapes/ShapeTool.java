package com.group31.editor.tool.shapes;


import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.data.ProjectHandler;
import com.group31.editor.tool.CanvasDrawTool;
import com.group31.editor.ui.options.OptionsPanel;
import com.group31.editor.util.Util2D;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class ShapeTool extends CanvasDrawTool {

  private Point startPoint;
  private Point endPoint;

  protected ShapeTool(String name, String iconName, OptionsPanel optionsPanel) {
    super(name, iconName, optionsPanel);
  }

  public Point getStartPoint() {
    return this.startPoint;
  }

  public Point getEndPoint() {
    return this.endPoint;
  }

  public void setStartPoint(Point startPoint) {
    this.startPoint = startPoint;
  }

  public void setEndPoint(Point endPoint) {
    this.endPoint = endPoint;
    com.group31.editor.canvas.Canvas.getInstance().updateCanvas();
  }

  private void _drawShape(Graphics2D g2, Point offset) {
    if (startPoint == null || endPoint == null)
      return;

    // draw shape using offset
    drawShape(
            g2,
            Util2D.min(startPoint, endPoint),
            Util2D.max(startPoint, endPoint),
            offset);
  }

  protected abstract void drawShape(Graphics2D g2, Point min, Point max, Point offset);

  private void drawToCanvas() {
    // if (startPoint == null || endPoint == null)
    //   return;

    //buffered image of objects drawn to screen
    //can call output g2

    java.awt.Dimension dimensions = ProjectHandler.getInstance().getCanvasSize();
    BufferedImage output = new BufferedImage(
            dimensions.width,      
            dimensions.height,
            BufferedImage.TYPE_INT_ARGB
    );
    
    // we add the graphic
    Graphics2D g2 = output.createGraphics();
    //canvas.getWidth or canvas.getHeight
    //using instance of canvas 
    g2.drawImage(canvas.getLayer(Canvas.selectedLayer()), 0, 0, null);
    _drawShape(g2, new Point());
    g2.dispose();
    canvas.replaceLayer(Canvas.selectedLayer(), output);
  }
  
  //parameters need to be the same, to render the same image, instead of changing value

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    switch (ev.action()) {
      case PRESS -> setStartPoint(ev.point());
      case DRAG -> setEndPoint(ev.point());
      case RELEASE -> {
        setEndPoint(ev.point());
        drawToCanvas();
        setStartPoint(null);
        setEndPoint(null);
      }
    }
  }

  @Override
  public void drawOverlay(Graphics2D g2, Point offset) {
    _drawShape(g2, offset);
  }
}
