package org.toolchild.suffering.gameobject.tile;

import java.awt.image.BufferedImage;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;

/**
 * {@link Tile} to run on and stuff.
 * 
 * @author Bob
 *
 */
public class Grass extends Tile {
  /**
   * 
   * Standard {@link Tile} constructor.
   *
   */
  public Grass(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages, boolean isSolid) {
    super(x, y, width, height, id, handler, bufferedImages, isSolid);
  }

}
