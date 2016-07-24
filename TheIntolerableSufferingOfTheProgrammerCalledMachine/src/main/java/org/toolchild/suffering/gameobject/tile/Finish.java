package org.toolchild.suffering.gameobject.tile;

import java.awt.image.BufferedImage;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.Player;

/**
 * When the {@link Player} touches this {@link tile} the level ends.
 * 
 * @author Bob
 *
 */
public class Finish extends Tile {

  /**
   * Standard {@link Tile} constructor.
   * 
   * @param x
   * @param y
   * @param width
   * @param height
   * @param id
   * @param handler
   * @param bufferedImages
   * @param isSolid
   */
  public Finish(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages, boolean isSolid) {
    super(x, y, width, height, id, handler, bufferedImages, isSolid);
  }

}
