package com.group31.editor.tool;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.action.CanvasActionEvent;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.ColourOptionsPanel;

import java.awt.Color;

public class ColourPipetteTool extends CanvasUtilityTool {
    private Color colour;
    public ColourPipetteTool() {
        super("Colour Pipette", "colourPipette", new ColourOptionsPanel("Colour Pipette"));
    }

    @Override
    public void onCanvasAction(CanvasActionEvent e) {
        switch (e.action()) {
            case CLICK -> {
                colour = new Colour(Canvas.getInstance().getPixel((int) e.point().getX(), (int) e.point().getY()));
                Colour.setActiveColour(colour);
            }
        }
    }

    @Override
    public String getGuideText() {
        return "Colour Pipette: Allows you to quickly and easily grab the colour of any pixel on the screen and copy it in hex form, by simply clicking on a specific item.";
    }
}


