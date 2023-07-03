package com.group31.editor.canvas.transform;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.canvas.selection.LassoSelection;
import com.group31.editor.canvas.selection.Selection;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;

public class ClipboardTransformAction implements TransformAction {

  private final Canvas canvas = Canvas.getInstance();

  private final BufferedImage subject;
  private Point currentPoint;
  private boolean paste = false;

  public ClipboardTransformAction(Selection selection, boolean cut) {
    var startPoint = selection.getStartPoint();
    var bounds = selection.getBounds();
    subject = canvas.getCanvas().getSubimage(
            startPoint.x,
            startPoint.y,
            bounds.width,
            bounds.height);
    if (selection instanceof LassoSelection lassoSelection) {
      lassoSelection.lassoifyImage(subject);
      if (cut) {
        canvas.setPixelRegion(
                startPoint,
                selection.getEndPoint(),
                new Color(255, 255, 255, 0),
                lassoSelection::isSelected);
      }
    } else if (cut) {
      canvas.setPixelBlock(
              startPoint,
              selection.getEndPoint(),
              new Color(255, 255, 255, 0));
    }
    canvas.setSelection(null);
  }

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    if (!paste) return;

    switch (ev.action()) {
      case MOUSE_MOVE -> {
        currentPoint = ev.point();
        canvas.updateCanvas();
      }
      case CLICK -> {
        if (currentPoint == null) return;

        BufferedImage output = new BufferedImage(
                canvas.getCanvas().getWidth(),
                canvas.getCanvas().getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = output.createGraphics();
        g2.drawImage(canvas.getLayer(Canvas.selectedLayer()), 0, 0, null);
        g2.drawImage(subject,
                currentPoint.x,
                currentPoint.y,
                null);
        g2.dispose();
        canvas.replaceLayer(Canvas.selectedLayer(), output);

        canvas.setTransformAction(null);
      }
    }
  }

  @Override
  public void drawOverlay(Graphics2D g2, Point offset) {
    if (currentPoint == null) return;

    g2.drawImage(subject,
            offset.x + currentPoint.x,
            offset.y + currentPoint.y,
            null);
  }

  public void enablePasting() {
    paste = true;
    canvas.fireTransformActionListeners();
  }

  public boolean isPasting() {
    return paste;
  }
}
