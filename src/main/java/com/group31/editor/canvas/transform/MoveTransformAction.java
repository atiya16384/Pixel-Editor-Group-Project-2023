package com.group31.editor.canvas.transform;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasAction;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.canvas.selection.LassoSelection;
import com.group31.editor.data.HistoryManager;
import com.group31.editor.data.FileHistory.Actions;
import com.group31.editor.util.Util2D;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics2D;

public class MoveTransformAction implements TransformAction {

  private final Canvas canvas = Canvas.getInstance();

  private BufferedImage subject;
  private Point mouseDelta;
  private Point currentPoint;

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    if (!(canvas.getSelection() != null && (ev.action() == CanvasAction.PRESS || ev.action() == CanvasAction.DRAG || ev.action() == CanvasAction.RELEASE))) return;
    var selection = canvas.getSelection();
    var startPoint = selection.getStartPoint();

    switch (ev.action()) {
      case PRESS -> {
        if (!selection.isSelected(ev.point().x, ev.point().y)) return;

        var bounds = selection.getBounds();
        subject = canvas.getCanvas().getSubimage(
                startPoint.x,
                startPoint.y,
                bounds.width,
                bounds.height);
        if (selection instanceof LassoSelection lassoSelection) {
          lassoSelection.lassoifyImage(subject);
          canvas.setPixelRegion(
                  startPoint,
                  selection.getEndPoint(),
                  new Color(255, 255, 255, 0),
                  lassoSelection::isSelected);
        } else {
          canvas.setPixelBlock(
                  startPoint,
                  selection.getEndPoint(),
                  new Color(255, 255, 255, 0));
        }
        mouseDelta = Util2D.translate(startPoint, ev.point(), true);
        currentPoint = ev.point();
        canvas.updateCanvas();
      }
      case DRAG -> {
        currentPoint = ev.point();
        canvas.updateCanvas();
      }
      case RELEASE -> {
        if (!(subject != null && mouseDelta != null && currentPoint != null)) return;

        BufferedImage output = new BufferedImage(
                canvas.getCanvas().getWidth(),
                canvas.getCanvas().getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = output.createGraphics();
        g2.drawImage(canvas.getLayer(Canvas.selectedLayer()), 0, 0, null);
        g2.drawImage(subject,
                currentPoint.x + mouseDelta.x,
                currentPoint.y + mouseDelta.y,
                null);
        g2.dispose();
        canvas.replaceLayer(Canvas.selectedLayer(), output);
        HistoryManager
          .getInstance()
          .recordChange(Canvas.getInstance().cleanCache(), Actions.CLIPBOARD);

        canvas.setTransformAction(null);
      }
    }
    if (mouseDelta != null) {
      var delta = Util2D.translate(Util2D.translate(ev.point(), mouseDelta), startPoint, true);
      selection.moveSelection(delta.x, delta.y);
    }
  }

  @Override
  public void drawOverlay(Graphics2D g2, Point offset) {
    if (!(subject != null && mouseDelta != null && currentPoint != null)) return;

    g2.drawImage(subject,
            offset.x + currentPoint.x + mouseDelta.x,
            offset.y + currentPoint.y + mouseDelta.y,
            null);
  }
}
