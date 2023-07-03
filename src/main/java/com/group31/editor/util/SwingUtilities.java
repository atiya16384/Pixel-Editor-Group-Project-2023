package com.group31.editor.util;

import com.group31.editor.libs.StretchIcon;
import java.awt.Image;
import java.awt.Toolkit;

public class SwingUtilities {

  public static StretchIcon loadStretchIconFromResource(String path) {
    return loadStretchIconFromResource(path, "");
  }

  public static StretchIcon loadStretchIconFromResource(String path, String description) {
    java.net.URL imgURL = SwingUtilities.class.getResource("/" + path);
    if (imgURL != null) {
      return new StretchIcon(imgURL, description);
    } else {
      Logger.log("Couldn't find file: " + path, Logger.LOG_TYPE.ERROR);
      return null;
    }
  }

  public static Image loadImageFromResource(String path) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    java.net.URL imgURL = SwingUtilities.class.getResource("/" + path);
    if (imgURL != null) {
      return toolkit.getImage(imgURL);
    } else {
      Logger.log("Couldn't find file: " + path, Logger.LOG_TYPE.ERROR);
      return null;
    }
  }
}
