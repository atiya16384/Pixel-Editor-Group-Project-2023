package com.group31.editor.canvas;

import java.util.ArrayList;

import com.group31.editor.data.ImageTools;
import com.group31.editor.util.SentryReporting;
import com.group31.editor.ui.headless.PopUpMessage;

import java.awt.image.BufferedImage;

public abstract class Layers {
    private static int currentLayer = 0;
    private static volatile ArrayList<BufferedImage> layers = new ArrayList<BufferedImage>();
    private static volatile ArrayList<Boolean> layerStatus = new ArrayList<Boolean>();
    /**
     * Layers.getLayer(Integer)
     * @since 0.2.380
     * @author jennin16
     * @param layer The integer of the layer to get
     * @returns BufferedImage of the layer requested
     */
    protected static BufferedImage getLayer() {
        return getLayer(currentLayer);
    }
    protected static BufferedImage getLayer(Integer layer) {
        try { return layers.get(layer); }
        catch (java.lang.IndexOutOfBoundsException e) { return null; }        
    }

    /**
     * Layers.createLayer(java.awt.Dimension, BufferedImage)
     *  Creates a new layer with the given dimensions and image
     * @since 0.2.380
     * @author jennin16
     * @param dimensions Size of the layer to create
     * @param image Image to initially assign the layer     
     * @returns Integer (position) of the layer created
     */
    protected static Integer createLayer(java.awt.Dimension dimensions) {
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.DEFAULT,
            "Created new layer",
            "canvas/layer"
        );
        layers.add(new BufferedImage((int)dimensions.getWidth(), (int)dimensions.getHeight(), BufferedImage.TYPE_INT_ARGB));
        layerStatus.add(true);
        return layers.size() - 1;
    }
    protected static Integer createLayer(java.awt.Dimension dimensions, BufferedImage image) {
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.DEFAULT,
            "Created new layer",
            "canvas/layer"
        );
        layers.add(image);
        layerStatus.add(true);
        return layers.size() - 1;
    }

    /**
     * Layers.clean()
     *  Deletes all layers
     * @since 0.2.380
     * @author jennin16
     */
    protected static void clean() {
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.INFO,
            "Deleted all layers",
            "canvas/layer"
        );
        layers.clear();
        layerStatus.clear();
    }

    public static Integer size() { return layers.size(); }

    /**
     * Layers.selectLayer(Integer)
     *  Selects a layer to be used for drawing
     * @since 0.2.380
     * @author jennin16
     * @param layer The integer of the layer to set as default
     * @returns A boolean to indicate success of the operation
     */
    protected static boolean selectLayer(Integer layer) {
        SentryReporting.layerBreadcrumb(Canvas.selectedLayer(), layer);
        currentLayer = layer;
        try {
            if (layerStatus.get(layer) == false) layerStatus.set(layer, true);
            currentLayer = layer;
            layers.get(layer); // validation
            return true;
        } catch (java.lang.IndexOutOfBoundsException e) {
            return false;
        } catch (java.lang.NullPointerException e) {
            new PopUpMessage (
                "Project issue",
                "You have opened a v0.2 project, we recommend you Save As the project to convert it to v0.3+ to avoid issues.",
                "popup/icons8-low-risk-500.svg"
            ).openGetResult();
            return false;
        }
    }

    protected static boolean replaceLayer(Integer layer, BufferedImage image) {
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.INFO,
            "Replaced a layer",
            "canvas/layer"
        );
        try {
            if (layerStatus.get(layer) == false) layerStatus.set(layer, true);
            layers.set(layer, image);
            return true;
        } catch (java.lang.IndexOutOfBoundsException e) {
            return false;
        } catch (java.lang.NullPointerException e) {
            new PopUpMessage (
                "Project issue",
                "You have opened a v0.2 project, you should Save As the project to convert it to v0.3+ to continue.",
                "popup/icons8-low-risk-500.svg"
            ).openGetResult();
            return false;
        }
    }

    protected static Integer getSelectedLayer() {
        return currentLayer;
    }

    /**
     * Layers.deleteLayer(int)
     *  Removes a layer from the canvas gracefully
     * @since 0.3.478
     * @author jennin16
     * @returns BufferedImage of the removed layer
     */
    protected static BufferedImage deleteLayer(int layer) {
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.INFO,
            "Deleted a layer",
            "canvas/layer"
        );
        BufferedImage out = layers.get(layer);
        layers.remove(layer);
        layerStatus.remove(layer);
        return out;
    }

    /**
     * Layers.getLayers()
     *  Combines all layers into one image
     * @since 0.2.380
     * @author jennin16
     * @returns BufferedImage of all layers combined
     */
    protected static BufferedImage getLayers() {
        BufferedImage out = new BufferedImage((int)layers.get(0).getWidth(), (int)layers.get(0).getHeight(), BufferedImage.TYPE_INT_ARGB);
        // for (BufferedImage layer : layers) {
        for (int i = 0; i < layers.size(); i++) {
            BufferedImage layer = layers.get(i);
            if (layerStatus.get(i) == false) continue;
            if (layer == null) break;
            else
                out = ImageTools.biCombine(out, layer);
        }
        return out;
    }

    public static void showLayer(int layerIndex) {
        layerStatus.set(layerIndex, true);
    }
    public static void hideLayer(int layerIndex) {
        layerStatus.set(layerIndex, false);
    }
    public static Boolean getLayerVisibility(int layerIndex) {
        return layerStatus.get(layerIndex);
    }
}
