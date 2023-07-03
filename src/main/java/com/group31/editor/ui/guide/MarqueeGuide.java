package com.group31.editor.ui.guide;

import com.group31.editor.ui.MarqueePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class MarqueeGuide extends MarqueePanel implements AWTEventListener {

  private final JLabel guideLabel;

  public MarqueeGuide() {
    super(30, 3);
    setWrap(true);
    setWrapAmount(20);

    guideLabel = new JLabel("");
    add(guideLabel);

    Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_MOTION_EVENT_MASK);
  }

  public void setGuideText(String text) {
    guideLabel.setText(text);
  }

  @Override
  public void eventDispatched(AWTEvent awtEvent) {
    if (!(awtEvent instanceof MouseEvent ev)) return;

    var source = ev.getSource();
    while (!(source instanceof Guidable guidable) && (source instanceof Container container) && container.getParent() != null) {
      source = container.getParent();
    }
    if (source instanceof Guidable guidable) {
      setGuideText(guidable.getGuideText());
    } else {
      setGuideText("");
    }
  }
}
