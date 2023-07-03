package com.group31.editor.data;

import com.group31.editor.util.Logger;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public interface FileType {
  /**
   * importData(Path, Canvas)
   *   Import image data to file
   * @param path A Path to a location on disk
   * @param canvas An initialised Canvas
   * @return Boolean success value
   */
  public static BufferedImage importData(Path path)
    throws FileNotFoundException, IOException {
    if (!Files.exists(path)) throw new FileNotFoundException();
    Logger.log(Logger.FILE_OP.READ, path);
    return ImageIO.read(path.toFile());
  }

  /**
   * exportData(Canvas)
   *   Export image data to file
   * @param path A Path to a location on disk
   * @param canvas An initialised Canvas
   * @return Boolean success value
   */
  public void exportData(Path path, BufferedImage image)
    throws FileNotFoundException, IOException;
}
