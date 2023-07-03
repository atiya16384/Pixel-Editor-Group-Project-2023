package com.group31.editor.ui;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.data.ProjectHandler;
import com.group31.editor.ui.event.LayerManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;

public class CanvasLayerButton extends javax.swing.JButton implements ActionListener {
    private final int layerIndex;
    private Integer[] size = this.calcScaledSize();
    // private boolean active = false;

    public CanvasLayerButton(int layerIndex) {
        this.layerIndex = layerIndex;
        setContentAreaFilled(false);
        addActionListener(this);
        
        this.draw();
        setBorder(BorderFactory.createLoweredSoftBevelBorder());
    }

    public void setIsSelected(boolean isSelected) { //TODO: active button
        if (isSelected) setBackground(new Color(130, 137, 141)); else setBackground(null);
    }

    public void draw() {
        setSize(new Dimension(size[0], size[1]));
        
        BufferedImage image = Canvas.getInstance().getLayer(layerIndex);
        BufferedImage output = new BufferedImage(size[0], size[1], image.getType());
        Graphics2D g2 = output.createGraphics();
        // g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, size[0], size[1], 0, 0, image.getWidth(), image.getHeight(), null);
        g2.dispose();

        setIcon(new javax.swing.ImageIcon(output));
        setBorder(BorderFactory.createLoweredSoftBevelBorder());
        updateUI();
    }

    private Integer[] calcScaledSize() { // bad place to put, need to move - image scaling math each history event
        Dimension canvasDimensions = ProjectHandler.getInstance().getCanvasSize();
        Integer[] scale = new Integer[2]; 

        try {
            if (canvasDimensions.width > 120) {
                scale[0] = 120;
                scale[1] = (scale[0] * canvasDimensions.height) / canvasDimensions.width;
            }
            if (scale[1] > 120) {
                scale[1] = 120;
                scale[0] = (scale[1] * canvasDimensions.width) / canvasDimensions.height;
            }
        } catch (ArithmeticException e) {
            scale[0] = 120;
            scale[1] = 120;
        } catch (NullPointerException e) {
            scale[0] = 120;
            scale[1] = 120;
        }

        return scale;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Canvas.getInstance().changeLayer(layerIndex);
        LayerManager.setChange();
    }

    public void update() {
        this.draw();
        updateUI();
    }
}
