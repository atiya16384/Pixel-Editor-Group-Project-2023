package com.group31.editor.ui.options;

import com.group31.editor.ui.guide.Guidable;

import javax.swing.*;

public class OpacitySlider extends JSlider implements Guidable {
  public OpacitySlider(int i, int i1, int i2) {
    super(i, i1, i2);
  }

  @Override
  public String getGuideText() {
    return "Opacity: Make an image more or less visible by increasing or decreasing the opacity using the slider provided on the left of the pixel editor.";
  }
}
