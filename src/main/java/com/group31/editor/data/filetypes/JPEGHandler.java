package com.group31.editor.data.filetypes;

import com.group31.editor.data.FileType;
import com.group31.editor.util.Logger;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

// these should probably be revamped from ImageIO to a nicer Image processing library

public class JPEGHandler implements FileType {

  public void exportData(Path path, BufferedImage image)
    throws FileNotFoundException, IOException {
    this.exportData(path, image, 1);
  }

  public void exportData(Path path, BufferedImage image, float quality)
    throws FileNotFoundException, IOException {
    FileOutputStream fileOutput = new FileOutputStream(path.toString());

    // convert image from ARGB to RGB, JPEG does not support Alpha channel
    BufferedImage source = new BufferedImage(
      image.getWidth(),
      image.getHeight(),
      BufferedImage.TYPE_INT_RGB
    );
    source
      .createGraphics()
      .drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    Logger.log(
      "Convereted ARGB image to RGB, attempting to use available JPG writer with custom params option to write to file",
      Logger.LOG_TYPE.FILE
    );

    // find an image writer and configure it
    Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");
    if (!imageWriters.hasNext()) throw new NoSuchMethodError("No writers found");
    ImageWriter writer = (ImageWriter) imageWriters.next();
    ImageOutputStream ios = ImageIO.createImageOutputStream(fileOutput);
    writer.setOutput(ios);
    ImageWriteParam param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(quality);

    // write the image
    writer.write(null, new IIOImage(source, null, null), param);

    // clean
    ios.close();
    fileOutput.close();
    writer.dispose();
    if (!Files.exists(path)) Logger.log(Logger.FILE_OP.OVERWRITE, path); else Logger.log(
      Logger.FILE_OP.SAVE,
      path
    );
  }

  @Override
  public String toString() {
    return ".jpg";
  }
}
