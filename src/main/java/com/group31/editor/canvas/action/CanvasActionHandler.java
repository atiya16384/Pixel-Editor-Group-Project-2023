package com.group31.editor.canvas.action;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.data.HistoryManager;
import com.group31.editor.ui.UIFrame;
import com.group31.editor.ui.BottomPanel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Objects;

public class CanvasActionHandler implements MouseListener, MouseMotionListener {

  private UIFrame uiFrame;
  private final Canvas canvas = Canvas.getInstance();

  public CanvasActionHandler(UIFrame uiFrame) {
    this.uiFrame = uiFrame;

    this.canvas.addMouseMotionListener(this);
    this.canvas.addMouseListener(this);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    Point p = canvas.translateToDrawArea(e.getX(), e.getY());
    fireActionOnSelectedTool(p, CanvasAction.CLICK);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    Point p = canvas.translateToDrawArea(e.getX(), e.getY());
    fireActionOnSelectedTool(p, CanvasAction.PRESS);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    Point p = canvas.translateToDrawArea(e.getX(), e.getY());
    fireActionOnSelectedTool(p, CanvasAction.RELEASE);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // Point p = canvas.translateToDrawArea(e.getX(), e.getY());
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // Point p = canvas.translateToDrawArea(e.getX(), e.getY());
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Point p = canvas.translateToDrawArea(e.getX(), e.getY());
    if (!Objects.nonNull(p)) return;
    // log(e, p, CanvasAction.DRAG.name());
    fireActionOnSelectedTool(p, CanvasAction.DRAG);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    Point p = canvas.translateToDrawArea(e.getX(), e.getY());
    if (!Objects.nonNull(p)) return;
    fireActionOnSelectedTool(p, CanvasAction.MOUSE_MOVE);
    mouseCoordsOnMove(p);
  }

  private void fireActionOnSelectedTool(Point p, CanvasAction action) {
    if (Objects.isNull(p)) return;

    var transformAction = this.canvas.getTransformAction();
    var tool = this.uiFrame.getSelectedEditorTool();
    if (transformAction != null) {
      transformAction.onCanvasAction(
        new CanvasActionEvent(
          action,
          p
        )
      );
    } else if (tool != null) {
      var changeType = com.group31.editor.data.FileHistory.toolToAction(tool);
      tool.onCanvasAction(
        new CanvasActionEvent(
          action,
          p
        )
      );
      if (action == CanvasAction.RELEASE || action == CanvasAction.CLICK)
        HistoryManager
          .getInstance()
          .recordChange(Canvas.getInstance().cleanCache(), changeType);
    }
  }
  
  private void mouseCoordsOnMove(Point p) {
    uiFrame.getBottomPanel().changeMouseCoords(p);
  }
}
