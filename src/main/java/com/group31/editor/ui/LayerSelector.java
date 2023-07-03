package com.group31.editor.ui;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.ui.event.LayerManager;
import com.group31.editor.ui.guide.Guidable;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class LayerSelector extends JPanel implements Scrollable, Guidable {
    public static LayerManager watcher; 

    private JPanel layerPanel = new JPanel();

    public LayerSelector() {
        setLayout(new BorderLayout());
        setSize(this.getPreferredScrollableViewportSize());

        JButton newLayerButton = new JButton("New Layer");
        newLayerButton.addActionListener(__ -> {
            Canvas.getInstance().addLayer();
            LayerManager.setChange();
            this.regenerateComponents();
        });
        newLayerButton.setSize(new Dimension(10, 10));
        add(newLayerButton, BorderLayout.NORTH);

        layerPanel = new JPanel(new GridLayout(50, 1));
        this.regenerateComponents();
        add(layerPanel, BorderLayout.CENTER);
        updateUI();

        if (watcher == null) {
            watcher = new LayerManager(this);
            watcher.start();
        } else {
            watcher.updateSelector(this);
        }
    }

    private void regenerateComponents() {
        layerPanel.removeAll();
        for (int layer = 0; layer < Canvas.getLayerCount(); layer++) {
            LayerPanel thisPanel = new LayerPanel(layer);
            layerPanel.add(thisPanel);
        }
        this.refresh();
    }

    public void refresh() {
        try {
            for (Component c : layerPanel.getComponents())
                ((LayerPanel)c).draw();
        } catch (NullPointerException err) {
            this.regenerateComponents();
            this.refresh();
        }
        updateUI();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 128;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 128;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(190, 1000);
    }

    @Override
    public String getGuideText() {
        return "Layers: Click the layer icon to stack the elements one on top of the other. Layers allows you to separate different elements of an image.";
    }
}
