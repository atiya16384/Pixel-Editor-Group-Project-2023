package com.group31.editor.canvas;

import com.group31.editor.canvas.error.*;
import com.group31.editor.canvas.selection.Selection;
import com.group31.editor.canvas.selection.SelectionListener;
import com.group31.editor.canvas.transform.ClipboardTransformAction;
import com.group31.editor.canvas.transform.MoveTransformAction;
import com.group31.editor.canvas.transform.TransformAction;
import com.group31.editor.canvas.transform.TransformActionListener;
import com.group31.editor.tool.CanvasDrawTool;
import com.group31.editor.tool.util.Colour;
import com.group31.editor.ui.UIFrame;
import com.group31.editor.ui.guide.Guidable;
import com.group31.editor.util.*;

import io.sentry.Sentry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JPanel;


public class Canvas extends JPanel implements Guidable { // implements Scrollable {

  private final TransparencyLayer transparencyLayer = new TransparencyLayer(5);
  private Graphics2D g2;
  private static volatile Canvas instance;
  private BufferedImage drawCache;
  private Dimension dimensions;
  private Selection selection;
  private TransformAction transformAction;

  private final List<SelectionListener> selectionListenerList = new ArrayList<>();
  private final List<TransformActionListener> transformActionListenerList = new ArrayList<>();

  private static boolean readyState = false;
  private boolean tprncyLyrEnabled = true;
  private boolean gridLinesEnabled = false;

  private double scale = 1.0;

  // Main hook, hijack g2 and draw
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.g2 = (Graphics2D) g; // Capture g2
    this.g2.scale(this.scale, this.scale);
    drawCanvas();
  }

  /**
   * Canvas(Dimension) #1
   *   Initialises a new Canvas object, requires initial dimensions for JPanel
   * @author jennin16
   * @param canvasDimensions A Dimension, likely from FileSchema
   */
  public Canvas(Dimension canvasDimensions) throws CanvasLimitation {
    super();
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Initialising Canvas",
      "canvas"
    );
    if (instance != null) throw new CanvasLimitation();
    this.dimensions = canvasDimensions;
    Logger.log("Ensuring default Colour initialised", Logger.LOG_TYPE.CANVAS);
    Colour.getActiveColour();
    Colour.getActiveFillColour();
    Logger.log("Instance created", Logger.LOG_TYPE.CANVAS, Logger.COLOUR_SET.SUCCESS);
    setBackground(new Color(130, 137, 141));
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Canvas ready",
      "canvas"
    );
  }
  public Canvas() throws CanvasLimitation {
    this(new Dimension(1,1));
  }

  public static Canvas getInstance() {
    return getInstance(null);
  }

  public static Canvas getInstance(Dimension canvasDimensions) {
    if (instance == null) {
      synchronized (Canvas.class) {
        if (instance == null) {
          try {
            if (canvasDimensions == null) instance = new Canvas();
            else instance = new Canvas(canvasDimensions);
          } catch (CanvasLimitation e) {
            Sentry.captureException(e); // Instance retrieval should never overwrite an instance
            System.exit(10);
          }
        }
      }
    }
    return instance;
  }


  // Canvas Management
  private void cacheChange(int x, int y) {
    if (new Color(drawCache.getRGB(x, y), true).getAlpha() == 0) drawCache.setRGB(
      x,
      y,
      Layers.getLayer().getRGB(x, y)
    );
  }

  /**
   * Canvas.updateCanvas()
   *  Updates the Canvas JPanel with the current BufferedImage
   * @param image
   * @returns BufferedImage copy
   */
  public static BufferedImage copyBI(BufferedImage image) {
    java.awt.image.ColorModel colourModel = image.getColorModel();
    boolean isAlphaPremultiplied = colourModel.isAlphaPremultiplied();
    return new BufferedImage(
      colourModel,
      image.copyData(null),
      isAlphaPremultiplied,
      null
    );
  }

  @Override
  public String getGuideText() {
    if (transformAction != null) {
      if (transformAction instanceof MoveTransformAction) {
        return "Move: Click and drag your selection to move it around on the canvas.";
      } else if (transformAction instanceof ClipboardTransformAction cta && cta.isPasting()) {
        return "Clipboard: Move the clipboard preview around and click on the canvas where you want to paste it.";
      }
    }
    var currentTool = UIFrame.getInstance().getSelectedEditorTool();
    if (currentTool != null) {
      return currentTool.getGuideText();
    }
    return "Canvas: Select a tool on the right to get started on your masterpiece!";
  }


  /**
   * Canvas.updateView(Double, Integer[])
   *   Updates the Canvas view with the scale set and the center of the view at the given co-ordinates
   * @param scale A Double value indiciating the scale of the (viewed) Canvas
   * @param coord The co-ordinates to focus on (center the view)
   */
  @Deprecated
  public void updateView(Double scale, Point coord) {} // TODO: release 3 thing ??

  /**
   * Canvas.newCanvas(Dimension)
   *   Prepares the canvas for painting
   * @author jennin16
   * @param canvasDimensions A Dimension object indiciating canvas size
   * @throws InUseCanvasException when g2 or BufferedImage is initialised in instance
   */
  public synchronized void newCanvas(Dimension canvasDimensions)
    throws InUseCanvasException {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Creating",
      "canvas"
    );
    if (canvasDimensions == null) throw new IllegalArgumentException("Canvas was null.");
    if (g2 != null) throw new InUseCanvasException();
    if (Layers.getLayer() != null) throw new InUseCanvasException();
    readyState = true;

    this.dimensions = canvasDimensions;
    Layers.createLayer(this.dimensions);
    setSize(this.dimensions);
    this.drawCache =
      new BufferedImage(
        (int) this.dimensions.getWidth(),
        (int) this.dimensions.getHeight(),
        BufferedImage.TYPE_INT_ARGB
      );

    for (int x = 0; x < this.dimensions.getWidth(); x++)
      for (int y = 0; y < this.dimensions.getHeight(); y++) {
        var transparent = new Color(255, 255, 255, 0).getRGB();
        Layers.getLayer().setRGB(x, y, transparent);
        drawCache.setRGB(x, y, transparent);
      }

    readyState = true;
    Logger.log(
      "BufferedImage created, Canvas ready",
      Logger.LOG_TYPE.CANVAS,
      Logger.COLOUR_SET.SUCCESS
    );
    updateUI();
    setReady();
  }

  /**
   * loadCanvas(BufferedImage)
   *   Loads a BufferedImage
   * @author jennin16
   * @param canvas A previously utilised BufferedImage
   * @throws InUseCanvasException
   */
  public void loadCanvas(BufferedImage canvas) throws InUseCanvasException {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Loading",
      "canvas"
    );
    Logger.log(
      "Trying to load a BufferedImage into the Canvas instance",
      Logger.LOG_TYPE.CANVAS
    );
    if (readyState) throw new InUseCanvasException(
      "Cannot load canvas while a canvas is loaded."
    );
    this.dimensions = new Dimension(canvas.getWidth(), canvas.getHeight());
    setSize(this.dimensions);
    updateUI();
    Layers.clean();
    Layers.createLayer(dimensions, canvas);
    cleanCache();
    Logger.log(
      "Loaded given BufferedImage (canvas BMP data) into Canvas",
      Logger.LOG_TYPE.CANVAS,
      Logger.COLOUR_SET.SUCCESS
    );
    setReady();
  }

  public void loadCanvas(BufferedImage[] canvas) throws InUseCanvasException {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Loading",
      "canvas"
    );
    Logger.log(
      "Trying to load a BufferedImage into the Canvas instance",
      Logger.LOG_TYPE.CANVAS
    );
    if (readyState) throw new InUseCanvasException(
      "Cannot load canvas while a canvas is loaded."
    );

    try {
      this.dimensions = new Dimension(canvas[0].getWidth(), canvas[0].getHeight());
    } catch (Exception e) {
      throw new RuntimeException("Failed to read BufferedImage data");
    }

    setSize(this.dimensions);
    updateUI();
    Layers.clean();
    for (BufferedImage layer : canvas) {
      Layers.createLayer(dimensions, layer);
    }
    cleanCache();
    Logger.log(
      "Loaded BufferedImage array (canvas BMP data) into Canvas",
      Logger.LOG_TYPE.CANVAS,
      Logger.COLOUR_SET.SUCCESS
    );
    setReady();
  }

  public void loadLayer(BufferedImage layer) {
    Layers.replaceLayer(getLayerCount(), layer);
  }

  public void replaceLayer(Integer layer, BufferedImage layerImage) {
    Layers.replaceLayer(layer, layerImage);
  }
  
  /**
   * Canvas.cleanCache()
   *  Cleans the drawCache and returns the BufferedImage for the inverse change to apply
   * @author jennin16
   * @return The cache before being erased
   */
  public BufferedImage cleanCache() {
    BufferedImage storedCache = copyBI(drawCache);
    drawCache =
      new BufferedImage(
        (int) this.dimensions.getWidth(),
        (int) this.dimensions.getHeight(),
        BufferedImage.TYPE_INT_ARGB
      );
    return storedCache;
  }

  /**
   * Canvas.getDrawCache()
   * @author jennin16
   * @return The current drawCache
   */
  public BufferedImage getDrawCache() {
    return drawCache;
  }

  /**
   * cleanCanvas()
   *   Clears g2, current dimensions and the BufferedImage
   *   This disposes of the current Graphics2D instance, ALL layers, cache and global dimensions
   * @author jennin16
   */
  public void cleanCanvas() {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Cleaning",
      "canvas"
    );
    Logger.log(
      "Setting canvas to default state",
      Logger.LOG_TYPE.CANVAS,
      Logger.COLOUR_SET.WARNING
    );
    Layers.clean();
    this.cleanCache();
    this.dimensions = null;
    this.g2 = null;
    readyState = false;
  }

    /**
   * setReady()
   *   Sets the Canvas state as ready
   * @author jennin16
   */
  private void setReady() {
    SentryReporting.leaveABreadcrumb(
      SentryReporting.BREADCRUMB_TYPE.DEFAULT,
      "Canvas ready",
      "canvas"
    );
    readyState = true;
    Logger.log(
      "Canvas is marked ready",
      Logger.LOG_TYPE.CANVAS,
      Logger.COLOUR_SET.SUCCESS
    );
  }

  public void toggleTransparency() {
    tprncyLyrEnabled = !tprncyLyrEnabled;
    updateCanvas();
  }

  public void drawGridlines() {
    gridLinesEnabled = !gridLinesEnabled;
    updateCanvas();
  }

  /**
  * Canvas.updateCanvas()
  *   Forces an update of the Canvas pane, including re-rendering G2D content and repainting the AWT pane.
  */
  public void updateCanvas() {
    if (readyState) drawCanvas();
    repaint();
  }

  protected void drawCanvas() {
    if (g2 == null)
      g2.drawString("Canvas is not ready.", 0, 0);
    var offset = getDrawOffset();
    if (tprncyLyrEnabled)
      transparencyLayer.draw(g2, this.dimensions.width, this.dimensions.height, offset);
    g2.drawImage(this.getCanvas(), offset.x, offset.y, null);
    if (gridLinesEnabled)
      new GridLines(g2, this.dimensions.width, this.dimensions.height, offset);
    if (selection != null) {
      selection.drawOverlay(g2, offset);
    }
    if (transformAction != null) {
      transformAction.drawOverlay(g2, offset);
    }
    var selectedTool = UIFrame.getInstance().getSelectedEditorTool();
    if (selectedTool instanceof CanvasDrawTool drawTool){
      drawTool.drawOverlay(g2, offset);
    }
  }

  /**
   * Canvas.getCanvas()
   * @author jennin16
   * @return The current BufferedImage
   */
  public BufferedImage getCanvas() {
    return Layers.getLayers();
  }

  public BufferedImage getLayer() {
    return Layers.getLayer();
  }

  public BufferedImage getLayer(Integer layer) {
    return Layers.getLayer(layer);
  }

  public void changeLayer(Integer layer) {
    if (!readyState) throw new CanvasNotReady();
    Layers.selectLayer(layer);
    updateCanvas();
  }

  public static Integer selectedLayer() {
    return Layers.getSelectedLayer();
  }
  
  public void addLayer() {
    if (!readyState) throw new CanvasNotReady();
    this.changeLayer(Layers.createLayer(dimensions));
    updateCanvas();
  }

  public void deleteLayer(int layer) {
    if (!readyState) throw new CanvasNotReady();
    if ((Layers.getSelectedLayer() == layer) && (layer > 0))
      this.changeLayer(layer - 1);
    if (getLayerCount() < 1)
      return;
    Layers.deleteLayer(layer);
    updateCanvas();
  }

  public static Integer getLayerCount() { return Layers.size(); }

  public TransformAction getTransformAction() {
    return this.transformAction;
  }

  public void setTransformAction(TransformAction action) {
    this.transformAction = action;
    fireTransformActionListeners();
  }

  public void fireTransformActionListeners() {
    this.transformActionListenerList.forEach(l -> l.transformActionChanged(this.transformAction));
  }

  // Image Editing

  /**
   * Canvas.getPixel(Integer, Integer)
   *   Gets the pixel RGB at X/Y co-ord
   * @author jennin16
   * @param x Integer x coordinate
   * @param y Integer y coordinate
   * @returns A int RGB value
   */
  public int getPixel(int x, int y) {
    if (!readyState) throw new CanvasNotReady();
    try {
      return Layers.getLayer().getRGB(x, y);
    } catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  public int[] getPixelBlock(int startX, int startY, int width, int height) {
    if (!readyState) throw new CanvasNotReady();
    try {
      return Layers.getLayer().getRGB(startX, startY, width, height, null, 0, 1);
    } catch (ArrayIndexOutOfBoundsException e) {
      return new int[]{};
    }
  }

  /**
   * Canvas.setPixel(Integer, Integer)
   *   Sets the pixel RGB at X/Y co-ord
   * @author jennin16
   * @param x Integer x coordinate
   * @param y Integer y coordinate
   * @param rgb A color
   * @param skipCache Should the pixel be saved into the drawCache
   * @param skipCanvasUpdate Doesn't apply change to canvas
   */
  public void setPixel(
    int x,
    int y,
    int rgb,
    boolean skipCache,
    boolean skipCanvasUpdate
  ) {
    if (!readyState) throw new CanvasNotReady();
    if (!skipCache) cacheChange(x, y);
    if (!skipCanvasUpdate) {
      Layers.getLayer().setRGB(x, y, rgb);
      updateCanvas();
    }
  }
  public void setPixel(int x, int y, Color col, boolean skipCache) {
    setPixel(x, y, col.getRGB(), skipCache, false);
  }

  public void setPixel(int x, int y, Color col) {
    setPixel(x, y, col.getRGB(), false, false);
  }

  /**
   * Canvas.fillBg(Color)
   *   Sets whole canvas to a single colour
   * @author jennin16
   * @param col A java.awt.Color
   */
  public void fillBg(Color col) {
    if (!readyState) throw new CanvasNotReady();
    drawCache = copyBI(Layers.getLayer());
    setPixelBlock(
      new Point(0, 0),
      new Point(this.dimensions.width, this.dimensions.height),
      col
    );
    updateCanvas();
  }

  /**
   * Canvas.setPixelBlock(Integer[], Integer[])
   *   Sets a square of pixels between two sets of X/Y co-ordinates
   * @author jennin16
   * @param start Integer array of X,Y co-ordinates
   * @param end Integer array of X,Y co-ordinates
   */
  public void setPixelBlock(Point start, Point end, Color col) {
    setPixelRegion(start, end, col, (p) -> true);
  }

  /**
   * Canvas.setPixelRegion(Integer[], Integer[], Predicate<Point>)
   *   Sets a region of pixels between two sets of X/Y co-ordinates if include predicate fulfilled.
   * @author jennin16
   * @param start Integer array of X,Y co-ordinates
   * @param end Integer array of X,Y co-ordinates
   * @param include Predicate<Point> test function if pixel is included in region
   */
  public void setPixelRegion(Point start, Point end, Color col, Predicate<Point> include) {
    if (!readyState) throw new CanvasNotReady();
    for (
            int x = (int) Math.round(start.getX());
            x < (int) Math.round(end.getX());
            x++
    ) {
      for (
              int y = (int) Math.round(start.getY());
              y < (int) Math.round(end.getY());
              y++
      ) {
        if (include.test(new Point(x, y))) {
          cacheChange(x, y);
          Layers.getLayer().setRGB(x, y, col.getRGB());
        }
      }
    }
  }


  // Tool Integration

  public Selection getSelection() {
    return selection;
  }

  public void setSelection(Selection selection) {
    this.selection = selection;
    fireSelectionListeners();
    Canvas.getInstance().updateCanvas();
  }

  public void fireSelectionListeners() {
    this.selectionListenerList.forEach(l -> l.selectionChanged(this.selection));
  }

  /**
   * isOnCanvas(int, int)
   *  Takes x and y coordinates as input.
   * @author lucatk
   * @param x
   * @param y
   * @see PIXEL-EDITOR-D
   * @return true if (x, y) pair is on our canvas, otherwise false
   */
  public boolean isOnCanvas(int x, int y) {
    return (
      x >= 0 &&
      x < this.dimensions.width &&
      y >= 0 &&
      y < this.dimensions.height
    );
  }

  /**
   * getDrawOffset()
   * @author lucatk
   * @return java.awt.Point of draw area co-ordinates
   */
  public Point getDrawOffset() {
    var x = (this.getWidth() - this.dimensions.width) / 2;
    var y = (this.getHeight() - this.dimensions.height) / 2;
    return new Point(x, y);
  }

  /**
   * translateToDrawArea(int, int)
   *  Translates the given coordinate pair in UI space to coordinates in our canvas space.
   * @author lucatk
   * @param x
   * @param y
   * @return java.awt.Point of translated draw area coordinates
   */
  public Point translateToDrawArea(int x, int y) {
    var offset = getDrawOffset();
//    TODO: figure out if scale leads to inaccuracies due to int cast (esp. when scale is not integer)
    var tx = (int) ((x - offset.getX() * this.scale) / this.scale);
    var ty = (int) ((y - offset.getY() * this.scale) / this.scale);

    if (
      tx < 0 || ty < 0 || tx > (this.dimensions.width - 1) || ty > (this.dimensions.height - 1)
    ) return null;

    return new Point(tx, ty);
  }

  public double getScale() {
    return this.scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
    updateCanvas();
  }

  public void addSelectionListener(SelectionListener l) {
    this.selectionListenerList.add(l);
  }

  public void removeSelectionListener(SelectionListener l) {
    this.selectionListenerList.remove(l);
  }

  public void addTransformActionListener(TransformActionListener l) {
    this.transformActionListenerList.add(l);
  }

  public void removeTransformActionListener(TransformActionListener l) {
    this.transformActionListenerList.remove(l);
  }

}

