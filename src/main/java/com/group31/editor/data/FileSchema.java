package com.group31.editor.data;

import com.group31.editor.canvas.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

public class FileSchema implements Serializable {

  private String name;
  private String lastSaved;
  private FileHistory history;
  private byte[][] canvas;
  public Dimension dimension;

  /**
   * FileSchema()
   *   FileSchema stores project information including Canvas, dimension, history and other project information
   * @author jennin16
   * @param name Name of the new project
   * @param x Width of canvas
   * @param y Height of canvas
   */
  public FileSchema(String name, Integer x, Integer y) {
    this.name = name;
    dimension = new Dimension(x, y);
    this.clearChanges();
  }

  /**
   * FileSchema.getDetailsJSON()
   *   Returns the project details in JSON format
   * @author jennin16
   * @return A lovely JSON formatted string :)
   */
  public String getDetailsJSON() {
    return (
      "{\"name\":\"" +
      name +
      "\",\"x\":" +
      dimension.getWidth() +
      ",\"y\":" +
      dimension.getHeight() +
      "}"
    );
  }

  /**
   * FileSchema.updateProjectLocation(Path)
   *   Updates the default
   * @param path
   * @throws FileNotFoundException
   */
  public void updateProjectLocation(Path path) throws FileNotFoundException {
    if (Files.exists(path)) lastSaved =
      path.toAbsolutePath().toString(); else throw new FileNotFoundException();
  }

  public void updateProjectLocation(Path path, Boolean futureFile)
    throws FileNotFoundException {
    if (Files.exists(path) || futureFile) lastSaved =
      path.toAbsolutePath().toString(); else throw new FileNotFoundException();
  }

  /**
   * FileSchena.getProjectLocation()
   * @return Current project location as Path
   */
  public Path getProjectLocation() {
    return Paths.get(lastSaved);
  }

  /**
     * FileSchema.saveCanvas(Canvas)
     *  This method saves a given canvas by converting it to a byte array and storing it in the canvas field.
    @author jennin16
    @throws IOException if there is an error when writing the canvas to a byte array.
    */
  public void saveCanvas() throws IOException {
    Canvas cv = Canvas.getInstance();
    // Convert the BufferedImage to a byte array
    ArrayList<byte[]> buffer = new ArrayList<byte[]>();
    for (int i = 0; i < Canvas.getLayerCount(); i++)
      buffer.add(ImageTools.toByteArray(cv.getLayer(i)));
    buffer.toArray(new byte[buffer.size()][]);

    this.canvas = buffer.toArray(new byte[buffer.size()][]);
  }

  /**
   * FileSchema.getCanvas()
   *  Converts the stored byte array to a BufferedImage for loading into a Canvas
   * @return A BufferedImage based on the FileSchema's last canvas (byte array)
   * @throws IOException
   */
  public BufferedImage[] getCanvas() throws IOException {
    // Convert the 2d byte array to a BufferedImage
    ArrayList<BufferedImage> canvas = new ArrayList<BufferedImage>();
    for (byte[] image : this.canvas)
      canvas.add(ImageTools.toBufferedImage(image));
    return canvas.toArray(new BufferedImage[canvas.size()]);
  }

  public void clearChanges() {
    history = new FileHistory();
  }

  public FileHistory getHistory() {
    return history;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getDetailsJSON();
  }
}
