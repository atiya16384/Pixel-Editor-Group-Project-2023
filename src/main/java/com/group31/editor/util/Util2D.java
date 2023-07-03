package com.group31.editor.util;

import java.awt.Point;
import java.awt.Polygon;

public class Util2D {

  //  row-wise max
  public static Point max(Point a, Point b) {
    if (a.x > b.x) {
      return a;
    } else if (a.x == b.x && a.y > b.y) {
      return a;
    } else {
      return b;
    }
  }

  //  row-wise min
  public static Point min(Point a, Point b) {
    if (a.x < b.x) {
      return a;
    } else if (a.x == b.x && a.y <= b.y) {
      return a;
    } else {
      return b;
    }
  }

  public static Point translate(Point a, Point b) {
    return translate(a, b, false);
  }

  public static Point translate(Point a, Point b, boolean subtract) {
    return translate(a, subtract ? -b.x : b.x, subtract ? -b.y : b.y);
  }

  public static Point translate(Point a, int dx, int dy) {
    var translated = new Point(a);
    translated.translate(dx, dy);
    return translated;
  }

  public static Polygon translate(Polygon p, Point b) {
    var translated = new Polygon(p.xpoints, p.ypoints, p.npoints);
    translated.translate(b.x, b.y);
    return translated;
  }
}
