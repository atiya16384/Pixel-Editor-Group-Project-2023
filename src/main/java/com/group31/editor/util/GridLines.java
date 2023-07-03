package com.group31.editor.util;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

public class GridLines {

    public GridLines(Graphics2D g2, int width, int height, Point offset){ 
        int boxWidth = 10;
        //set the colour and the stroke
        g2.setColor(new Color(0, 0, 0, 127));
        g2.setStroke(new BasicStroke(1.0f));

        //set the number of vertical and horizontal lines
        int verticalLines = Math.floorDiv(width,boxWidth);
        int horizontalLines = Math.floorDiv(height, boxWidth);
        
        //draw all the vertical lines
        for(int i=0; i<verticalLines+1; i++){
            g2.drawLine(i*boxWidth+offset.x, offset.y, i*boxWidth+offset.x, height+offset.y);
        }
        //draw all the horizontal lines
        for(int i=0; i<horizontalLines+1; i++){
            g2.drawLine(offset.x, i*boxWidth+offset.y, width+offset.x, i*boxWidth+offset.y);
        }       
    }
}
