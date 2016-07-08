package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gfx.Sprite;
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
   * @param sprites
   * @param isSolid
   */
  public Finish(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites, boolean isSolid) {
    super(x, y, width, height, id, handler, sprites, isSolid);
  }


  @Override
  public void render(Graphics2D graphics2d) {
    graphics2d.drawImage(this.sprites[0].getImage(), this.x, this.y,this.width, this.height, null);
  }

}
