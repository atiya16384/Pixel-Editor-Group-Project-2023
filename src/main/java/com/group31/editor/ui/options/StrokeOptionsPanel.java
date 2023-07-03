package com.group31.editor.ui.options;

import com.group31.editor.tool.util.BrushProfile;
import com.group31.editor.tool.util.Thickness;
import com.group31.editor.util.TextChangeListener;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.FlowView;

import java.awt.*;

public class StrokeOptionsPanel extends OptionsPanel{
    private JTextField thicknessField;
    private JLabel thicknessTitle, labelTitle;
    private JPanel thicknessPanel, profilePanel;
    private JSlider thicknessSlider;
    private Icon[] profileIcons = new Icon[10];
    private ArrayList<JButton> profileButtons = new ArrayList<JButton>();
    private int i;
    private String namesIcon[] = {
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_1.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_2.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_3.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_4.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_5.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_6.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_7.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_8.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_9.png")
                    .toString()
                    .substring(5),
            StrokeOptionsPanel.class.getResource("/brushProfiles/profile_10.png")
                    .toString()
                    .substring(5),
    };

    public StrokeOptionsPanel(String toolName) {
        super();
        // create and add Options Title to panel
        JLabel optionsTitle = new JLabel(toolName + " Options");
        this.add(optionsTitle);

        // ********* THICKNESS PANEL ********* //

        // create and add panel that holds thickness options

        thicknessPanel = new JPanel();
        thicknessTitle = new JLabel("Thickness", JLabel.LEFT);
        thicknessPanel.setPreferredSize(new Dimension(200,40));
        this.setLayout(new FlowLayout());
        this.add(thicknessTitle);
        this.add(thicknessPanel);

        // create and add thickness options to thickness panel

        thicknessSlider = new JSlider(1,200,Thickness.getActiveThickness());
        thicknessSlider.setPreferredSize(new Dimension(140,30));
        thicknessField = new JTextField(String.valueOf((int)Math.round(Thickness.getActiveThickness())), 3);
        thicknessField.setHorizontalAlignment(SwingConstants.CENTER);
        thicknessPanel.add(thicknessSlider);
        thicknessPanel.add(thicknessField);

        // ********* BRUSH PROFILES PANEL ********* //

        // create and add panel that holds colour options
        labelTitle = new JLabel("Brush Profiles", JLabel.LEFT);
        this.add(labelTitle);

        profilePanel = new JPanel();
        this.add(profilePanel);

        // create and add colour options to colour panel
        profilePanel.setLayout(new GridLayout(5, 5));

        for (int i = 0; i < namesIcon.length; i++) {
            final int currentProfile = i;
            profileIcons[i] = new ImageIcon(namesIcon[i]);
      
            profileButtons.add(new JButton(profileIcons[i]));
            profileButtons.get(i).setSize(5, 5);
            profilePanel.add(profileButtons.get(i));
      
            profileButtons.get(i).addActionListener(__ -> { BrushProfile.active = currentProfile+1; });
            //System.out.println("Created button: " + Integer.valueOf(i).toString());
        }

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
