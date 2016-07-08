package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gfx.Sprite;
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
  public Grass(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites, boolean isSolid) {
    super(x, y, width, height, id, handler, sprites, isSolid);
  }

  @Override
  public void render(Graphics2D graphics2d) {
    graphics2d.drawImage(this.sprites[0].getImage(), this.x, this.y, this.width, this.height, null);
  }

}
