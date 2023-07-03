package com.group31.editor.tool.selection;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasAction;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.canvas.selection.RectSelection;
import com.group31.editor.tool.CanvasUtilityTool;
import com.group31.editor.ui.options.SelectionOptionsPanel;

public class RectSelectionTool extends CanvasUtilityTool {

  private final Canvas canvas = Canvas.getInstance();

  /**
   * RectSelectionTool()
   *   RectSelectionTool is a tool that selects an area of the canvas. The area is selected as a rectangle.
   * @author mchaleto, lucatk
   */
  public RectSelectionTool() {
    super("Rectangle Selection", "rectSelection", new SelectionOptionsPanel());
  }

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    switch (ev.action()) {
      case PRESS -> {
        if (
          canvas.getSelection() != null &&
          (
            !(canvas.getSelection() instanceof RectSelection selection) ||
            selection.getEndPoint() != null
          )
        ) {
          canvas.setSelection(null);
        } else {
          canvas.setSelection(new RectSelection(ev.point()));
        }
      }
      case DRAG, RELEASE -> {
        if (canvas.getSelection() instanceof RectSelection selection) {
          if (ev.action() == CanvasAction.RELEASE && ev.point().equals(selection.getStartPoint())) {
            canvas.setSelection(null);
          } else {
            selection.setEndPoint(ev.point());
          }
        }
      }
    }
  }

  @Override
  public String getGuideText() {
    return "Rectangle Selection: Drag and release the rectangle tool to select a rectangle shape.";
  }
}
