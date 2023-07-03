package com.group31.editor.data.filetypes;

import com.group31.editor.data.FileType;
import com.group31.editor.util.Logger;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class BMPHandler implements FileType {

  public void exportData(Path path, BufferedImage image)
    throws FileNotFoundException, IOException {
    FileOutputStream fileOutput = new FileOutputStream(path.toString());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedImage source = new BufferedImage(
      image.getWidth(),
      image.getHeight(),
      BufferedImage.TYPE_INT_RGB
    );
    source
      .createGraphics()
      .drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    Logger.log(
      "Convereted ARGB image to RGB, attempting to convert to bytes and write to file",
      Logger.LOG_TYPE.FILE
    );
    boolean iores = ImageIO.write(source, "BMP", out);
    if (!iores) throw new IOException("ImageIO.write() wrote 0 bytes with FALSE return");
    byte[] result = out.toByteArray();
    fileOutput.write(result);
    fileOutput.close();
    if (!Files.exists(path)) Logger.log(Logger.FILE_OP.OVERWRITE, path); else Logger.log(
      Logger.FILE_OP.SAVE,
      path
    );
  }

  @Override
  public String toString() {
    return ".bmp";
  }
}
