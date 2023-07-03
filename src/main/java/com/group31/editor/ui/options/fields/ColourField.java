package com.group31.editor.ui.options.fields;

import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.ColourButton;
import com.group31.editor.util.ColourDocumentFilter;
import com.group31.editor.util.PrefixNavigationFilter;
import com.group31.editor.util.TextChangeListener;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColourField extends JPanel {

  private final JTextField hexField;
  private final ColourButton colourButton;

  public ColourField(String title, Supplier<Colour> colourSupplier, Consumer<Color> colourConsumer) {
    JLabel fieldTitle = new JLabel(title);
    hexField =
            new JTextField(
                    String.format(
                            "#%06X",
                            colourSupplier.get().getRGB() & 0x00FFFFFF
                    ),
                    7
            );
    hexField.setNavigationFilter(new PrefixNavigationFilter(1, hexField));

    colourButton = new ColourButton(" ", colourSupplier.get());

    this.add(fieldTitle);
    this.add(hexField);
    this.add(colourButton);

    colourButton.addActionListener(e -> {
      Color colour = JColorChooser.showDialog(this, "Colour Selector", Color.black);

      if (colour != null) {
        try {
          colourConsumer.accept(colour);
          colourButton.setBackground(colour);
          hexField.setText(
                  String.format(
                          "#%06X",
                          colourSupplier.get().getRGB() & 0x00FFFFFF
                  )
          );
        } catch (NullPointerException a) {
          // does nothing if user colour not selected (user cancels in Color Chooser dialog)
        }
      }
    });
    // adds an instance of listener to colourInput text field
    hexField
            .getDocument()
            .addDocumentListener(
                    (TextChangeListener) e -> {
                      if (hexField.getText() != null && hexField.hasFocus()) {
                        try {
                          // converts HEX input to RGB value and sets the global colour variable
                          var newColour = Color.decode(hexField.getText());
                          colourConsumer.accept(newColour);
                          colourButton.setBackground(newColour);
                        } catch (NumberFormatException a) {
                          // does nothing if the colourField input cannot be interpreted
                        }
                      }
                    }
            );
  }

  public void updateColour(Colour newColour) {
    colourButton.setBackground(newColour);
    var newHex =
            String.format(
                    "#%06X",
                    newColour.getRGB() & 0x00FFFFFF
            );
    hexField.setText(newHex);
  }

}
