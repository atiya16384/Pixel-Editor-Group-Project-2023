package com.group31.editor.ui.headless;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.error.CanvasLimitation;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
public class GridLines extends JComponent{

    private static int numColumns;
    private static int numRows;
    private static int cellWidth;
    private static int cellHeight;
    
    public GridLines(int numColumns, int numRows){
        GridLines.numColumns=numColumns;
        GridLines.numRows=numRows;
    }

    public void setNumColumns(int numColumns) {
        GridLines.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return GridLines.numColumns;
    }

    public void setNumRows(int numRows) {
        GridLines.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return GridLines.numRows;
    }

    private void calculateDimensions() {
    
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = Canvas.getInstance().getWidth() / numColumns; 
        cellHeight = Canvas.getInstance().getHeight() / numRows;
    }

    public static void onDraw(){
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        BufferedImage output = new BufferedImage(
            Canvas.getInstance().getWidth(),      
            Canvas.getInstance().getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = output.createGraphics();

        int width = Canvas.getInstance().getWidth();
        int height = Canvas.getInstance().getHeight(); 

        //  we draw a single rectangle
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                Canvas.getInstance().paintComponents(g2);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight);
            }
        }

        // We iterate through each column and draw a row by multiplying a number of columns by the cellwidth (which is constant) 
        for (int i = 1; i < numColumns; i++) {
            Canvas.getInstance().paintComponents(g2);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(i * cellWidth, 0, i * cellWidth, height);
        }
        // We iterate through each row and draw a column by multiplying the number of rows by the cellHeight (which is constant)
        for (int i = 1; i < numRows; i++) {
            Canvas.getInstance().paintComponents(g2);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(0, i * cellHeight, width, i * cellHeight);
        } 
        

    }
}