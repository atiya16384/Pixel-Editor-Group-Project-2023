package com.group31.editor.tool.util;

import com.group31.editor.canvas.selection.SelectionListener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Colour extends java.awt.Color {

  private static volatile Colour activeColour;
  private static volatile Colour activeFillColour;

  private static final List<Consumer<Colour>> activeColourListenerList = new ArrayList<>();
  private static final List<Consumer<Colour>> activeFillColourListenerList = new ArrayList<>();

  public Colour(int r, int g, int b, int a) {
    super(r, g, b, a);
  }

  public Colour(Color color) {
    super(color.getRGB());
  }

  public Colour(int rgb) {
    super(rgb);
  }

  public static void setActiveColour(int r, int g, int b, int a) {
    setActiveColour(new Colour(r, g, b, a));
  }

  public static void setActiveColour(Color color) {
    setActiveColour(new Colour(color));
  }

  public static void setActiveColour(Colour colour) {
    activeColour = colour;
    activeColourListenerList.forEach(c -> c.accept(colour));
  }
  public static Colour getActiveColour() {
    if (activeColour == null) {
      synchronized (Colour.class) {
        if (activeColour == null) {
          activeColour = new Colour(0, 0, 0, 255);
        }
      }
    }
    return activeColour;
  }

  public static void setActiveFillColour(int r, int g, int b, int a) {
    setActiveFillColour(new Colour(r, g, b, a));
  }

  public static void setActiveFillColour(Color color) {
    setActiveFillColour(new Colour(color));
  }

  public static void setActiveFillColour(Colour colour) {
    activeFillColour = colour;
    activeFillColourListenerList.forEach(c -> c.accept(colour));
  }

  public static Colour getActiveFillColour() {
    if (activeFillColour == null) {
      synchronized (Colour.class) {
        if (activeFillColour == null) {
          activeFillColour = new Colour(0, 0, 0, 255);
        }
      }
    }
    return activeFillColour;
  }

  public static void addActiveColourListener(Consumer<Colour> consumer) {
    activeColourListenerList.add(consumer);
  }

  public static void addActiveFillColourListener(Consumer<Colour> consumer) {
    activeFillColourListenerList.add(consumer);
  }
}
