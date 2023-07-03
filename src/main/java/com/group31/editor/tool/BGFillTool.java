package com.group31.editor.tool;

import com.group31.editor.libs.Java2sFloodFill;
import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.data.HistoryManager;
import com.group31.editor.data.FileHistory.Actions;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.ColourOptionsPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BGFillTool extends CanvasUtilityTool {

  /**
   * BGFillTool()
   * This constructor will create a new Background fill tool object.
   * The name and iconName of the tool are initialized.
   * @author mchaleto
   */
  public BGFillTool() {
    super("Bucket Fill", "fill", new ColourOptionsPanel("Bucket Fill"));
  }
  @Override
  public void onCanvasAction(CanvasActionEvent ev) {
    switch (ev.action()) {
      case CLICK -> {
        int x = (int) ev.point().getX();
        int y = (int) ev.point().getY() +25;
        if(canvas.getPixel(x,y) != Colour.getActiveColour().getRGB()){
          Java2sFloodFill.floodFill(canvas.getLayer(), x, y, canvas.getPixel(x, y), Colour.getActiveColour().getRGB());
          canvas.updateCanvas();
          HistoryManager
            .getInstance()
            .recordChange(Canvas.getInstance().cleanCache(), Actions.FILL);
        }
      }
    }
  } //Click event from mouse to change background

  @Override
  public String getGuideText() {
    return "Bucket Fill: Simply click the bucket on a region in the canvas to change the area's background colour.";
  }
}
