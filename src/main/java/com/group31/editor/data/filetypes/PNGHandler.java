package com.group31.editor.data.filetypes;

import com.group31.editor.data.FileType;
import com.group31.editor.util.Logger;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class PNGHandler implements FileType {

  public void exportData(Path path, BufferedImage image)
    throws FileNotFoundException, IOException {
    java.io.File target = path.toFile();
    boolean iores = ImageIO.write(image, "PNG", target);
    if (!iores) throw new IOException("ImageIO.write() wrote 0 bytes with FALSE return");
    if (!Files.exists(path)) Logger.log(Logger.FILE_OP.OVERWRITE, path); else Logger.log(
      Logger.FILE_OP.SAVE,
      path
    );
  }

  @Override
  public String toString() {
    return ".png";
  }
}
