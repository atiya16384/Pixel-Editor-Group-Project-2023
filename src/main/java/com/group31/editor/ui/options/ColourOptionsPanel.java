package com.group31.editor.ui.options;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.options.fields.ColourField;

import javax.swing.*;

public class ColourOptionsPanel extends OptionsPanel{
    private final ColourField colourField;

    public ColourOptionsPanel(String toolName) {
        super();
        // create and add Options Title to panel
        JLabel optionsTitle = new JLabel(toolName + " Options");
        this.add(optionsTitle);

        // create and add colour options to colour panel
        colourField = new ColourField(
                "Colour",
                Colour::getActiveColour,
                Colour::setActiveColour);
        Colour.addActiveColourListener(colour -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    colourField.updateColour(colour);
                }
            });
        });
        this.add(colourField);
    }
}
