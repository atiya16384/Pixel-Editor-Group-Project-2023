package com.group31.editor.data;

import java.awt.image.BufferedImage;

public abstract class ImageTools {

  /**
   * ImageConverter.toByteArray(BufferedImage)
   *  Converts a BufferedImage to a byte array
   * @param image
   * @returns byte[] of BufferedImage
   * @throws java.io.IOException
   */
  public static byte[] toByteArray(BufferedImage image)
    throws java.io.IOException {
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    javax.imageio.ImageIO.write(image, "png", baos);
    return baos.toByteArray();
  }

  /**
   * ImageConverter.toBufferedImage(byte[])
   *  Converts a byte array to a BufferedImage
   * @param image
   * @returns BufferedImage
   * @throws java.io.IOException
   */
  public static BufferedImage toBufferedImage(byte[] image)
    throws java.io.IOException {
    return javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(image));
  }

  public static BufferedImage biCombine(BufferedImage bi1, BufferedImage bi2) {
    BufferedImage output = new BufferedImage(
      Math.max(bi1.getWidth(), bi2.getWidth()),
      Math.max(bi1.getHeight(), bi2.getHeight()),
      BufferedImage.TYPE_INT_ARGB
    );
    java.awt.Graphics g = output.getGraphics();
    g.drawImage(bi1, 0, 0, null);
    g.drawImage(bi2, 0, 0, null);
    g.dispose();
    return output;
  }
}
