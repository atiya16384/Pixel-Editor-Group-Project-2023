package com.group31.editor.ui.options;

import com.group31.editor.ui.LayerSelector;
import com.group31.editor.ui.UIFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class OptionsPanel extends JPanel {

  public static final OptionsPanel empty = new EmptyOptionsPanel();

  private UIFrame uiFrame;
  private JScrollPane layerScroll;
  private final static LayerSelector layerSelector = new LayerSelector();

  public OptionsPanel() {
    super();
  }

  public void addToFrame(UIFrame uiFrame) {
    this.uiFrame = uiFrame;
    this.setPreferredSize(new Dimension(200, uiFrame.getJFrame().getHeight()));
    this.layerScroll = new JScrollPane(layerSelector);
    this.layerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    this.add(layerScroll);
    this.uiFrame.getJFrame().add(this, BorderLayout.LINE_START);
  }

  public void removeFromFrame() {
    this.remove(layerScroll);
    this.layerScroll = null;
    this.uiFrame.getJFrame().remove(this);
    this.uiFrame = null;
  }
}
