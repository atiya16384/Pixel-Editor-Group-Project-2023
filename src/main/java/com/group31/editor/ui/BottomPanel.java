package com.group31.editor.ui;

import com.group31.editor.ui.guide.MarqueeGuide;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

public class BottomPanel extends JPanel{

    private UIFrame uiFrame;
    private JLabel coordinates;
    private JLabel dimensions;
    private MarqueeGuide guide;

    /** 
     * BottomPanel(UIFrame, integer, height)
     *      Creates an instance of the bottom panel, passing in the width and height as parameter     
     * @param uiFrame UI Frame which the panel is added to 
     * @param width Width of the canvas
     * @param height Height of the canvas
     */
    public BottomPanel(UIFrame uiFrame, int width, int height){
        this.uiFrame = uiFrame;
        
        setPreferredSize(new Dimension(1000,30));
        setLayout(new GridLayout(3,1));
        // setLayout(new GridLayout(2,2));
        
        coordinates = new JLabel("  Coordinates:");
        dimensions = new JLabel("  Dimensions: " + String.valueOf(width) + ", " + height);
        guide = new MarqueeGuide();
        guide.setPreferredSize(new Dimension(1000, 20));

        // JSlider zoomSlider = new JSlider(50, 1024);
        // zoomSlider.setValue(200);
        // zoomSlider.addChangeListener(new ChangeListener() {
            // @Override
            // public void stateChanged(ChangeEvent e) {
                // int value = zoomSlider.getValue();
                // Canvas cv = Canvas.getInstance();
                // cv.apply(new Dimension(value, value));
                // cv.revalidate();
            // }
        // });

        this.add(coordinates);
        this.add(dimensions);
        this.add(guide);

        uiFrame.getJFrame().add(this, BorderLayout.PAGE_END);
    }

    /** changeMouseCoords
     *      Updates the coordinates based on mouse movement
     * @author mumtazga
     * @param p contains current mouse coordinates
     */
    public void changeMouseCoords(Point p){
        String xCoord = String.valueOf(Math.round(p.getX()));
        String yCoord = String.valueOf(Math.round(p.getY()));
        coordinates.setText("  Coordinates (X,Y): " + xCoord + ", " + yCoord);
    }
}
