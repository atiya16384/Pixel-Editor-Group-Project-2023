package com.group31.editor.ui.options;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.fields.ColourField;

import javax.swing.*;

public class ShapeOptionsPanel extends OptionsPanel {

  private final ColourField fillColourField;

  public ShapeOptionsPanel(String toolName) {
    super();

    this.add(new JLabel(toolName + " Options"));

    // create and add colour options to colour panel
    fillColourField = new ColourField(
            "Fill Colour",
            Colour::getActiveFillColour,
            Colour::setActiveFillColour);
    Colour.addActiveFillColourListener(colour -> SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        fillColourField.updateColour(colour);
      }
    }));
    this.add(fillColourField);
  }

}
