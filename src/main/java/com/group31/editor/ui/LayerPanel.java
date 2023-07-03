package com.group31.editor.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.Layers;
import com.group31.editor.libs.SvgImageIcon;
import com.group31.editor.ui.guide.Guidable;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LayerPanel extends javax.swing.JPanel implements ActionListener {
    private CanvasLayerButton button;
    private boolean hidden = false;
    private int layerIndex;
    private final ImageIcon displayedIcon = SvgImageIcon.createIconFromSvg("popup/displayed.svg", new Dimension(10, 10));
    private final ImageIcon hiddenIcon = SvgImageIcon.createIconFromSvg("popup/hidden.svg", new Dimension(10, 10));
    private JButton hideButton = new JButton(displayedIcon);
    private JButton deleteButton = new JButton(SvgImageIcon.createIconFromSvg("popup/delete.svg", new Dimension(10, 10)));

    public LayerPanel(int layerIndex) {
        this.button = new CanvasLayerButton(layerIndex);
        this.layerIndex = layerIndex;
        this.hideButton.addActionListener(this);
        this.hideButton.setActionCommand("hide");
        this.deleteButton.addActionListener(this);
        this.deleteButton.setActionCommand("delete");
        add(this.button);
        add(this.hideButton);
        if (Layers.size() > 1)
            add(this.deleteButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "hide":
                if (hidden) {
                    hidden = false;
                    this.hideButton.setIcon(displayedIcon);
                    Layers.showLayer(layerIndex);
                } else {
                    hidden = true;
                    this.hideButton.setIcon(hiddenIcon);
                    Layers.hideLayer(layerIndex);
                }
                break;
            case "delete":
                Canvas.getInstance().deleteLayer(layerIndex);
                com.group31.editor.ui.event.LayerManager.setChange();
                break;
        }

        updateUI();
        Canvas.getInstance().updateCanvas();
    }

    public void draw() {
        this.button.draw();
        updateUI();
    }
}
