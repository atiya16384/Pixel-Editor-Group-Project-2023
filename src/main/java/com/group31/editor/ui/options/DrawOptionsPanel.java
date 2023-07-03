package com.group31.editor.ui.options;

import com.group31.editor.tool.util.BrushProfile;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.tool.util.Thickness;
import com.group31.editor.ui.options.fields.ColourField;
import com.group31.editor.util.TextChangeListener;
import com.group31.editor.libs.StretchIcon;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

public class DrawOptionsPanel extends OptionsPanel {

  private final ColourField colourField;

  private JTextField opacityField, thicknessField;
  private JPanel opacityPanel;
  private OpacitySlider opacitySlider;
  private JSlider thicknessSlider;
  private JLabel opacityTitle;
  private JLabel thicknessTitle, labelTitle;
  private JPanel thicknessPanel, profilePanel;

  public DrawOptionsPanel(String toolName) {
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

    opacityPanel = new JPanel();
    opacityPanel.setPreferredSize(new Dimension(200,40));
    opacityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    //create and add Opacity Title to panel
    opacityTitle = new JLabel("Opacity", JLabel.LEFT);
    this.add(opacityTitle);
    this.add(opacityPanel);

    opacitySlider = new OpacitySlider(0,100,100);
    opacitySlider.setPreferredSize(new Dimension(140,30));
    opacityField = new JTextField(String.valueOf(Math.round((Colour.getActiveColour().getAlpha() / 255.0) * 100)), 3);
    opacityField.setHorizontalAlignment(SwingConstants.CENTER);

    opacityPanel.add(opacitySlider);
    opacityPanel.add(opacityField);

    opacitySlider.addChangeListener(e -> {
      JSlider source = (JSlider) e.getSource();

      if(source.getValueIsAdjusting()) {
        int a = (int)Math.round(255 * (opacitySlider.getValue() / 100.0));
        // set opacityField text as opacitySlider value
        opacityField.setText(String.valueOf(opacitySlider.getValue()));
        // gets the current colour instance values and sets the global colour variable with the slider input as the alpha value
        var activeColour = Colour.getActiveColour();
        Colour.setActiveColour(
                activeColour.getRed(),
                activeColour.getGreen(),
                activeColour.getBlue(),
                a);
      }
    });


    // ********* THICKNESS PANEL ********* //

    // create and add panel that holds thickness options

    thicknessPanel = new JPanel();
    thicknessTitle = new JLabel("Thickness", JLabel.LEFT);
    thicknessPanel.setPreferredSize(new Dimension(200,40));
    this.add(thicknessTitle);
    this.add(thicknessPanel);

    // create and add thickness options to thickness panel

    thicknessSlider = new JSlider(1,200,Thickness.getActiveThickness());
    thicknessSlider.setPreferredSize(new Dimension(140,30));
    thicknessPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    thicknessField = new JTextField(String.valueOf((int)Math.round(Thickness.getActiveThickness())), 3);
    thicknessField.setHorizontalAlignment(SwingConstants.CENTER);
    thicknessPanel.add(thicknessSlider);
    thicknessPanel.add(thicknessField);

    // ********* BRUSH PROFILES PANEL ********* //

    // create and add panel that holds colour options
    labelTitle = new JLabel("Brush Profiles", JLabel.LEFT);
    profilePanel = new JPanel();
    this.add(labelTitle);
    this.add(profilePanel);

    // create and add colour options to colour panel
    profilePanel.setLayout(new GridLayout(5, 5));

    for (int i = 0; i < 9; i++) {
      final int currentProfile = i;
      JButton thisButton = new JButton(new ImageIcon(DrawOptionsPanel.class.getResource("/brushProfiles/profile_" + (i+1) + ".png")));
      thisButton.setSize(20, 20);
      thisButton.addActionListener(__ -> { BrushProfile.active = currentProfile+1; });
      profilePanel.add(thisButton);
    }

    opacityField
            .getDocument()
            .addDocumentListener(
                    new TextChangeListener() {
                      @Override
                      public void update(DocumentEvent e) {
                        if (opacityField.getText() != null && opacityField.hasFocus()) {
                          try {
                            if (Integer.parseInt(opacityField.getText()) >= 0 && Integer.parseInt(opacityField.getText()) <= 100){
                              // gets the current colour instance values and sets the global Colour variable with the text field input as the alpha value
                              Colour.setActiveColour(Colour.getActiveColour().getRed(), Colour.getActiveColour().getGreen(), Colour.getActiveColour().getBlue(), 255 * (Integer.parseInt(opacityField.getText()) / 100));
                              // Change slider value based on input
                              opacitySlider.setValue(Integer.parseInt(opacityField.getText()));
                            }
                            else {
                              // if input is not an int between 0 and 100 set opacity percentage to 100
                              opacitySlider.setValue(100);
                            }
                          } catch (NumberFormatException a) {
                            // if the opacityField input cannot be interpreted set opacity percentage to 100
                            opacitySlider.setValue(100);
                          }
                        }
                      }
                    }
            );

    thicknessSlider.addChangeListener(e -> {  
      JSlider source = (JSlider) e.getSource();
        if(source.getValueIsAdjusting()) {
          // set opacityField text as opacitySlider value
          thicknessField.setText(String.valueOf(thicknessSlider.getValue()));
          Thickness.setActiveThickness(source.getValue());
        }
    });
  }

}
