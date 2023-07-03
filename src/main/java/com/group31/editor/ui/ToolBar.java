package com.group31.editor.ui;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.tool.ActionTool;
import com.group31.editor.tool.CanvasUtilityTool;
import com.group31.editor.tool.Tool;
import com.group31.editor.tool.brush.BrushTool;
import com.group31.editor.tool.brush.EraserTool;
import com.group31.editor.tool.selection.LassoSelectionTool;
import com.group31.editor.tool.selection.RectSelectionTool;
import com.group31.editor.tool.shapes.EllipseTool;
import com.group31.editor.tool.shapes.LineTool;
import com.group31.editor.tool.shapes.RectangleTool;
import com.group31.editor.tool.shapes.TriangleTool;
import com.group31.editor.tool.BGFillTool;
import com.group31.editor.tool.ColourPipetteTool;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.Objects;

public class ToolBar extends JPanel {

  public static final Tool[] tools = {
          new BrushTool(),
          new EraserTool(),
          new BGFillTool(),
          new RectSelectionTool(),
          new LassoSelectionTool(),
          new LineTool(),
          new RectangleTool(),
          new EllipseTool(),
          new TriangleTool(),
          new ColourPipetteTool()
  };

  private final UIFrame uiFrame;

  private final ArrayList<ToolBarButton> buttons;

  public ToolBar(UIFrame uiFrame) {
    this.uiFrame = uiFrame;
    this.buttons = new ArrayList<ToolBarButton>();

    setPreferredSize(new Dimension(100, 500));
    setBounds(400, 700, 200, 500);
    setLayout(new GridLayout(12, 2));

    for (Tool tool : tools) {
      ToolBarButton thisButton = createEditorToolButton(tool);
      this.buttons.add(thisButton);
      add(thisButton);
    }

    uiFrame.getJFrame().add(this, BorderLayout.LINE_END);
  }

  private ToolBarButton createEditorToolButton(Tool tool) {
    var button = new ToolBarButton(tool.hashCode(), tool.getName(), tool.getIcon(), tool.getGuideText());
    button.addActionListener(e -> {
      var currentTool = uiFrame.getSelectedEditorTool();
      if (currentTool != null) {
        Objects.requireNonNull(getButtonByTool(currentTool)).setIsSelected(false);
      }
      if (tool instanceof CanvasUtilityTool) {
        button.setIsSelected(true);
        uiFrame.setSelectedEditorTool((CanvasUtilityTool) tool);
        try {
          changeCursor(tool.getCursorImage(), tool.getName());
        } catch (Exception err) {
          Canvas.getInstance().setCursor(Cursor.getDefaultCursor());
        }
      } else if (tool instanceof ActionTool) {
        ((ActionTool) tool).onAction();
      }
    });
    return button;
  }

  private ToolBarButton getButtonByTool(Tool tool) {
    for (ToolBarButton button : buttons) if (
      Objects.equals(button.getKey(), tool.hashCode())
    ) return button;
    return null;
  }

  /**
   * changeCursor()
   * 
   * This method changes the cursor to the respective cursor corresponding to the current tool
   * @author mumtazga
   * @param newCursor image for the tool being used
   * @param cursorName name of the tool that is being used
   */
  private void changeCursor(Image newCursor, String cursorName) {
    Toolkit tlkit = Toolkit.getDefaultToolkit();
    Point point = new Point(WIDTH, HEIGHT);
    Image newImage = newCursor.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
    Cursor cursor = tlkit.createCustomCursor(newImage, point, cursorName);
    Canvas.getInstance().setCursor(cursor);
  }
}
