package com.group31.editor.tool.selection;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.canvas.selection.LassoSelection;
import com.group31.editor.tool.CanvasUtilityTool;
import com.group31.editor.ui.options.SelectionOptionsPanel;

public class LassoSelectionTool extends CanvasUtilityTool {

  private final Canvas canvas = Canvas.getInstance();

  /**
   * LassoSelectionTool()
   *   LassoSelectionTool is a tool that selects an area of the canvas. The selected area will be represented as a polygon.
   * @author mchaleto, lucatk
   */
  public LassoSelectionTool() {
    super("Lasso Selection", "lassoSelection", new SelectionOptionsPanel());
  }

  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    switch (ev.action()) {
      case PRESS -> {
        if (canvas.getSelection() instanceof LassoSelection selection) {
          if (selection.isFinished()) {
            canvas.setSelection(null);
          } else {
            selection.addPoint(ev.point());
          }
        } else if (canvas.getSelection() != null) {
          canvas.setSelection(null);
        } else {
          canvas.setSelection(new LassoSelection(ev.point()));
        }
      }
      case MOUSE_MOVE -> {
        if (
          canvas.getSelection() instanceof LassoSelection selection &&
          !selection.isFinished()
        ) {
          selection.setMousePosition(ev.point());
        }
      }
    }
  }

  @Override
  public String getGuideText() {
    return "Lasso Selection: Continuously click using the lasso to select a polygon shape.";
  }
}
